/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SignerDialog.java
 *
 * Created on Feb 22, 2013, 6:03:29 PM
 */

package ams.tool;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

import processing.app.Base;
import processing.core.PApplet;

/**
 *
 * @author Peter Lager
 */
public class SignerDialog extends javax.swing.JDialog  implements ABconstants {

	static final File signingDetailFile = new File(AppletMaker.toolDataFolder, "signer_details.txt");
	static final File cCodesFile = new File(AppletMaker.toolDataFolder, "ccodes.txt");

	protected AppletBuildDetail ab;
	protected SignerProperties sp;

	protected boolean ktFound, jsFound;

	// Colours used for jar signing tools
	protected static final Color green = new Color(204,255,204);
	protected static final Color pink = new Color(255,204,204);

	protected String KEYTOOL;
	protected String JARSIGNER;
	protected String BINFOLDER;
	

	/** 
	 * This constructor is used inside the IDE when testing. 
	 * 
	 */
	public SignerDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		finalizeGUI();
	}

	/**
	 * This is the constructor that will be used in the tool.
	 * 
	 * @param appletBuild
	 */
	public SignerDialog(AppletBuildDetail appletBuild){
		super((Dialog)null, "Applet JAR signer", true);
		initComponents();
		ab = appletBuild;
    	lblSketchName.setText(ab.getSketch().getName());
		finalizeGUI();
	}

	/**
	 * load country codes and signer details and use these to
	 * finish off the GHUI
	 * Finish off the GUI a
	 */
	private void finalizeGUI(){
		KEYTOOL 	= "keytool";
		JARSIGNER	= "jarsigner";
		BINFOLDER = "bin";
		if(Base.isWindows()){
			KEYTOOL += ".exe";
			JARSIGNER += ".exe";			
		}
		txaProgress.setVisible(false);
		// Get signer details
		sp = new SignerProperties();
		sp.loadFromTSV(signingDetailFile);
		// Get country codes
		String[] items = PApplet.loadStrings(cCodesFile);
		cbxC.setModel(new DefaultComboBoxModel(items));

		txfCN.setText(sp.get("CN"));
		txfOU.setText(sp.get("OU"));
		txfO.setText(sp.get("O"));
		txfL.setText(sp.get("L"));
		txfST.setText(sp.get("ST"));
		cbxC.setSelectedItem(sp.get("C"));
		txfDays.setText(sp.get("DAYS"));
		
		ktFound = new File(sp.get("keytool")).exists();
		jsFound = new File(sp.get("jarsigner")).exists();
		updateJDKtoolsInfo();
	}

	/**
	 * Report current progress on signing
	 * @param ptext
	 */
	void updateProgressDisplay(LinkedList<String> ptext){
		txaProgress.setText("");
		for(String aline : ptext)
			txaProgress.append("\n" + aline);
	}

	/**
	 * This is called when the signing process has finished
	 * @param error non-zero if signing failed
	 */
	void doneWithSigning(int error){
		if(error == 0){
			txaProgress.append("\n\nSigning was successful");
		}
		else {
			txaProgress.append("\n\n Signing failed.");
		}
		btnCancel.setText("CLOSE THIS WINDOW");
	}
	
	/**
	 * Enable or disable GUI controls for editing signer details
	 * @param enabled true to enable editing
	 */
	void enableChanges(boolean enabled){
		btnFindJDK.setEnabled(enabled);
		txfCN.setEnabled(enabled);
		txfDays.setEnabled(enabled);
		txfL.setEnabled(enabled);
		txfO.setEnabled(enabled);
		txfOU.setEnabled(enabled);
		txfST.setEnabled(enabled);
		cbxC.setEnabled(enabled);
	}

	// ================================================================================================================================================
	// ------------------------------------------------------------------------------------------------------------------------------------------------

	// New methods to help locate the JDK exes needed to sign the applet

	/**
	 * Search a directory tree and locate the BINFOLDER. Then look for the keytool
	 * and jarsigner executables inside the folder
	 * 
	 * @return true if both executables found
	 */
	private boolean findJDKtools(){
		File folder = selectFolder("Locate JDK folder");
		if(folder != null){
			File bin = getBinFolder(folder);
			if(bin != null){
				File ktFile = new File(bin, KEYTOOL);
				ktFound = ktFile.exists();
				if(ktFound)
					sp.put("keytool", ktFile.getAbsolutePath().replace('\\','/'));
				File jsFile = new File(bin, JARSIGNER);
				jsFound = jsFile.exists();
				if(jsFound)
					sp.put("jarsigner", jsFile.getAbsolutePath().replace('\\','/'));
			}
		}
		updateJDKtoolsInfo();
		return ktFound & jsFound;
	}
	
	// recursively search a directory tree for a folder called BINFOLDER
	private File getBinFolder(File folder){
		if(folder.getName().equalsIgnoreCase(BINFOLDER))
			return folder;
		File[] children = folder.listFiles(new FoldersOnlyFilter());
		for(File file : children){
			File possible = getBinFolder(file);
			if(possible != null)
				return possible;
		}
		return null;
	}
	
	/**
	 * Select a folder from the local file system.
	 * This will be used to locate the JDK folder
	 * 
	 * @param prompt the frame text for the chooser
	 * @return the absolute path name for the selected folder, or null if action 
	 * cancelled.
	 */
	protected File selectFolder(String prompt){
		File selectedFolder = null;
		// If MacOSX then force it to use the native file chooser so it can enter packaes
		if (Base.isMacOS() ) { // && PApplet.useNativeSelect != false) {
			FileDialog fileDialog = 
				new FileDialog((Dialog)null, prompt, FileDialog.LOAD);
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			fileDialog.setVisible(true);
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
			String filename = fileDialog.getFile();
			if (filename != null) {
				selectedFolder = (new File(fileDialog.getDirectory(), fileDialog.getFile()));
			}
		} else {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(prompt);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				selectedFolder = fileChooser.getSelectedFile();
			}
		}
		return selectedFolder;
	}
	
	private void updateJDKtoolsInfo(){	
		lblKeyTool.setBackground(ktFound ? green : pink);
		lblJarSigner.setBackground(jsFound ? green : pink);
		btnFindJDK.setEnabled(!(ktFound & jsFound));
		btnSign.setEnabled(ktFound & jsFound);
	}
	
	/**
	 * Get the current values from memory
	 */
	private void saveSignerDetails(){
		sp.put("CN", txfCN.getText());
		sp.put("OU", txfOU.getText());
		sp.put("O", txfO.getText());
		sp.put("L", txfL.getText());
		sp.put("ST", txfST.getText());
		sp.put("C", cbxC.getSelectedItem().toString());
		sp.put("DAYS", txfDays.getText());
		// keytool and jarsigner are saved when found
	}

	/**
	 * Make the signer's details available for the signing process
	 */
	SignerProperties getSignerProperties(){
		return sp;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSketchName = new javax.swing.JPanel();
        lblSketchName = new javax.swing.JLabel();
        pnlToolLocations = new javax.swing.JPanel();
        lblKeyTool = new javax.swing.JLabel();
        lblJarSigner = new javax.swing.JLabel();
        btnFindJDK = new javax.swing.JButton();
        pnlCertDetails = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txfCN = new javax.swing.JTextField();
        txfOU = new javax.swing.JTextField();
        txfO = new javax.swing.JTextField();
        txfL = new javax.swing.JTextField();
        txfST = new javax.swing.JTextField();
        cbxC = new javax.swing.JComboBox();
        txfDays = new javax.swing.JTextField();
        pnlControls = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaProgress = new javax.swing.JTextArea();
        btnSign = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(590, 370));
        setResizable(false);

        lblSketchName.setBackground(new java.awt.Color(0, 0, 0));
        lblSketchName.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblSketchName.setForeground(new java.awt.Color(255, 255, 255));
        lblSketchName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSketchName.setText("Sketch Name");
        lblSketchName.setOpaque(true);

        javax.swing.GroupLayout pnlSketchNameLayout = new javax.swing.GroupLayout(pnlSketchName);
        pnlSketchName.setLayout(pnlSketchNameLayout);
        pnlSketchNameLayout.setHorizontalGroup(
            pnlSketchNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSketchName, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        );
        pnlSketchNameLayout.setVerticalGroup(
            pnlSketchNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSketchName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblKeyTool.setBackground(new java.awt.Color(255, 204, 204));
        lblKeyTool.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKeyTool.setText("KEYTOOL");
        lblKeyTool.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKeyTool.setOpaque(true);

        lblJarSigner.setBackground(new java.awt.Color(255, 204, 204));
        lblJarSigner.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblJarSigner.setText("JARSIGNER");
        lblJarSigner.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblJarSigner.setOpaque(true);

        btnFindJDK.setText("Browse for JDK");
        btnFindJDK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindJDKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlToolLocationsLayout = new javax.swing.GroupLayout(pnlToolLocations);
        pnlToolLocations.setLayout(pnlToolLocationsLayout);
        pnlToolLocationsLayout.setHorizontalGroup(
            pnlToolLocationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToolLocationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblKeyTool, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblJarSigner, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFindJDK, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlToolLocationsLayout.setVerticalGroup(
            pnlToolLocationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToolLocationsLayout.createSequentialGroup()
                .addGroup(pnlToolLocationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKeyTool, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                    .addComponent(btnFindJDK)
                    .addComponent(lblJarSigner, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlCertDetails.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153), 2));

        jLabel3.setText("Company (CN)");

        jLabel4.setText("Unit (OU)");

        jLabel5.setText("Organisation (O)");

        jLabel6.setText("Location (L)");

        jLabel7.setText("State / Region (ST)");

        jLabel10.setText("Validity (Days)");

        jLabel8.setText("Country (C)");

        jLabel9.setBackground(new java.awt.Color(153, 153, 153));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Certificate Details");
        jLabel9.setOpaque(true);

        txfCN.setText("txfCN");

        txfOU.setText("txfOU");

        txfO.setText("txfO");

        txfL.setText("txfL");

        txfST.setText("txfST");

        cbxC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCActionPerformed(evt);
            }
        });

        txfDays.setText("txfDays");

        javax.swing.GroupLayout pnlCertDetailsLayout = new javax.swing.GroupLayout(pnlCertDetails);
        pnlCertDetails.setLayout(pnlCertDetailsLayout);
        pnlCertDetailsLayout.setHorizontalGroup(
            pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCertDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addGroup(pnlCertDetailsLayout.createSequentialGroup()
                        .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txfOU, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txfCN, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txfO, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txfL, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txfST, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(cbxC, 0, 185, Short.MAX_VALUE)
                            .addComponent(txfDays, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlCertDetailsLayout.setVerticalGroup(
            pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCertDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txfCN, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txfOU, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txfO, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txfL, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txfST, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cbxC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCertDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txfDays, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        txaProgress.setColumns(20);
        txaProgress.setEditable(false);
        txaProgress.setFont(new java.awt.Font("Tahoma", 0, 11));
        txaProgress.setRows(5);
        jScrollPane1.setViewportView(txaProgress);

        btnSign.setText("Sign Applet");
        btnSign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(btnSign, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnSign)
                .addGap(18, 18, 18)
                .addComponent(btnCancel))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSketchName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCertDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlToolLocations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlSketchName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlToolLocations, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCertDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void cbxCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxCActionPerformed
	}//GEN-LAST:event_cbxCActionPerformed

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
		dispose();
	}//GEN-LAST:event_btnCancelActionPerformed

	private void btnSignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignActionPerformed
		txaProgress.setVisible(true);
		enableChanges(false);				// prevent further changes
		btnSign.setEnabled(false);			// disable this button
		saveSignerDetails();				// Save current details
		sp.saveAsTSV(signingDetailFile);	// Store current details
		
		AppletSignWorker r = new AppletSignWorker(this, ab);
		r.execute();
	}//GEN-LAST:event_btnSignActionPerformed

	private void btnFindJDKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindJDKActionPerformed
		findJDKtools();		
	}//GEN-LAST:event_btnFindJDKActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				SignerDialog dialog = new SignerDialog(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnFindJDK;
    private javax.swing.JButton btnSign;
    private javax.swing.JComboBox cbxC;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblJarSigner;
    private javax.swing.JLabel lblKeyTool;
    private javax.swing.JLabel lblSketchName;
    private javax.swing.JPanel pnlCertDetails;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlSketchName;
    private javax.swing.JPanel pnlToolLocations;
    private javax.swing.JTextArea txaProgress;
    private javax.swing.JTextField txfCN;
    private javax.swing.JTextField txfDays;
    private javax.swing.JTextField txfL;
    private javax.swing.JTextField txfO;
    private javax.swing.JTextField txfOU;
    private javax.swing.JTextField txfST;
    // End of variables declaration//GEN-END:variables


}
