package ams.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

/**
 * Perform all the key generation, certification and signing in a worker
 * thread so that we can report progress.
 * 
 * @author Peter Lager
 *
 */
final class AppletSignWorker extends SwingWorker<Integer, ProgressText> implements ABconstants {

	private SignerDialog signer = null;
	private AppletBuildDetail ab;
	
	private SignerProperties sp;
	private File appletFolder;
	private String storeName, storePass, keyPass, alias;

	private int processError;
	private LinkedList<String> ptext = new LinkedList<String>();
	
	private Process p;

	/**
	 * Create the applet signing worker object
	 * 
	 * @param signer
	 * @param appBuild
	 */
	public AppletSignWorker(SignerDialog signer, AppletBuildDetail appBuild){
		this.signer = signer;
		sp = this.signer.getSignerProperties();
		ab = appBuild;
		appletFolder = ab.getAppletFolder();
		storePass = getRandomText(8);
		keyPass = getRandomText(8);
		alias = getRandomText(6);
		storeName = "my-key-store";
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int result = signApplet();
		ab.setSignApplet(result == 0);
		return result;
	}
	
	@Override
    protected void process(List<ProgressText> lines) {
        for (ProgressText line : lines) {
        	if(line.replace)
        		ptext.removeLast();
        	ptext.addLast(line.text);
        }
        signer.updateProgressDisplay(ptext);
   }
	
	@Override
	protected void done(){
		signer.doneWithSigning(processError);
	}
	
	/**
	 * Sign the applet
	 * @return 0 if applet signed successfully.
	 */
	private int signApplet() {
		File[] jarFiles = listJarFiles(appletFolder);
		// ====================================================================
		// Stage 1 generating key
		// ====================================================================
		publish(new ProgressText(false, "PROGRESS >->->"));
		String current = "Generating Key --- ";
		publish(new ProgressText(false, current + "WORKING"));
		processError = 0; // none yet
		processError = keygenStage(keygenArray);
		publish(new ProgressText(true, current + (processError == 0 ? "DONE" : "ERROR")));
		
		
		// ====================================================================
		// Stage 2 self certify
		// ====================================================================
		if(processError == 0){
			current = "Self certify --- ";
			publish(new ProgressText(false, current + "WORKING"));
			processError = selfcertStage(selfCertArray);
			publish(new ProgressText(true, current + (processError == 0 ? "DONE" : "ERROR")));	
		}

		// ====================================================================
		// Stage 3 sign jars
		// ====================================================================
		if(processError == 0){
			publish(new ProgressText(false, "Signing jars"));
			processError = jarsignStage(jarsignerArray, jarFiles);
		}
		return processError;
	}

	/**
	 * Generate the key using the executable keytool
	 * @param cmd CLI command to run (skeleton)
	 * @return 0 if successful non-zero otherwise.
	 */
	private int keygenStage(String[] cmd){
		int error = -1;
		cmd[0] = sp.get("keytool");
		cmd[3] = Messages.build(NAME, sp.get("CN"), sp.get("OU"), sp.get("O"), sp.get("L"), sp.get("ST"), sp.get("C"));
		cmd[5] = storeName;
		cmd[7] = storePass;
		cmd[9] = keyPass;
		cmd[11] = alias;
		try {
			p = Runtime.getRuntime().exec(cmd, new String[0], appletFolder);
			p.waitFor();
			error = p.exitValue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return error;
	}
	
	/**
	 * Self certify the key created in keyGenStage using the executable keytool
	 * @param cmd CLI command to run (skeleton)
	 * @return 0 if successful non-zero otherwise.
	 */
	private int selfcertStage(String[] cmd){
		int error = -1;
		cmd[0] = sp.get("keytool");
		cmd[3] = sp.get("DAYS");
		cmd[5] = storeName;
		cmd[7] = storePass;
		cmd[9] = keyPass;
		cmd[11] = alias;
		try {
			p = Runtime.getRuntime().exec(cmd, new String[0], appletFolder);
			p.waitFor();
			error = p.exitValue();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return error;
	}
	
	/**
	 * Sign all the jars in the applet folder.
	 * 
	 * @param cmd CLI command to run (skeleton)
	 * @param jars an array of jar files to sign
	 * @return 0 if successful non-zero otherwise.
	 */
	private int jarsignStage(String[] cmd, File[] jars) {
		String current;
		cmd[0] = sp.get("jarsigner");
		cmd[2] = storeName;
		cmd[4] = storePass;
		cmd[6] = keyPass;
		cmd[8] = alias;
		
		int jarerror, error = 0;
		for (File jarfile : jars) {
			current = " " + jarfile.getName()+ " --- ";
			publish(new ProgressText(false, current + "SIGNING"));
			//String cmdJar = cmd + " " + jarfile.getName() + " " + alias;
			cmd[7] = jarfile.getAbsolutePath().replace('\\','/');
			try {
				p = Runtime.getRuntime().exec(cmd, new String[0], appletFolder);
				p.waitFor();
//				InputStream is = p.getInputStream();
//				BufferedReader br = new BufferedReader(new InputStreamReader(is));
//				while (true) {
//					String s = br.readLine();
//					if (s == null)
//						break;
//					else
//						System.out.println(s);
//				}
//				br.readLine();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jarerror = p.exitValue();
			error = Math.min(error, jarerror);
			publish(new ProgressText(true, current + (processError == 0 ? "DONE" : "ERROR")));
		}
		return error;
	}
	
	/**
	 * Get a list of jar files in the specified folder
	 * @param folder the folder to search
	 * @return array of jar files
	 */
	private File[] listJarFiles(File folder) {
		return folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (!name.startsWith(".") && name.toLowerCase().endsWith(".jar") );
			}
		});
	}

	/**
	 * Used to create passwords and aliases for the keytool
	 * @param len the length of the random string required
	 * @return the random string
	 */
	public String getRandomText(int len) {
		char[] text = new char[len];
		for (int i = 0; i < len; i++) {
			int c = (int) (Math.random()  * chars.length());
			text[i] = chars.charAt(c);
		}
		return new String(text);
	}

}
