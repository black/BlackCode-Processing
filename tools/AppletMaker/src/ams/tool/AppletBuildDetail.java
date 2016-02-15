package ams.tool;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import processing.app.*;
import processing.core.*;
import processing.mode.java.*;
import processing.mode.java.preproc.*;

/**
 * This class extends Processing's JavaBuild class to simplify access to 
 * some of the processes e.g. compilation.
 * 
 * @author Peter Lager
 *
 */
public class AppletBuildDetail extends JavaBuild implements ABconstants  {

	protected boolean foundSize;
	protected String renderer;

	protected int sketchWidth = PApplet.DEFAULT_WIDTH;
	protected int sketchHeight = PApplet.DEFAULT_HEIGHT;
	protected String description = "";

	protected File appletFolder;

	protected boolean separateJARs = false;
	protected boolean signApplet = false;
	
	protected int templateToUse = TEMPLATE_151;

	protected boolean exported = false;
	protected boolean signed = false;

	/**
	 * Create an AppletBuildDetail object for the given sketch
	 * @param sketch the sketch we are using the tool on.
	 */
	public AppletBuildDetail(Sketch sketch) {
		super(sketch);
		// If the applet folder does not exist create it
		appletFolder = new File(sketch.getFolder(), "applet");
		if(!appletFolder.exists())
			appletFolder.mkdir();
	}

	
	/**
	 * @return the appletFolder
	 */
	public File getAppletFolder() {
		return appletFolder;
	}


	/**
	 * Get the sketch from the editor reference
	 * @return
	 */
	public Sketch getSketch(){
		return sketch;
	}
	
	/**
	 * @return the sketchWidth
	 */
	public int getSketchWidth() {
		return sketchWidth;
	}

	/**
	 * @param sketchWidth the sketchWidth to set
	 */
	public void setSketchWidth(int sketchWidth) {
		this.sketchWidth = sketchWidth;
	}

	/**
	 * @return the sketchHeight
	 */
	public int getSketchHeight() {
		return sketchHeight;
	}

	/**
	 * @param sketchHeight the sketchHeight to set
	 */
	public void setSketchHeight(int sketchHeight) {
		this.sketchHeight = sketchHeight;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		if(description.length() == 0)
			getNarrative();
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the templateToUse
	 */
	public int getTemplateToUse() {
		return templateToUse;
	}

	/**
	 * @param templateToUse the templateToUse to set
	 */
	public void setTemplateToUse(int templateToUse) {
		this.templateToUse = templateToUse;
	}

	/**
	 * @return the signApplet
	 */
	public boolean isSignApplet() {
		return signApplet;
	}

	/**
	 * @param signApplet the signApplet to set
	 */
	public void setSignApplet(boolean signApplet) {
		this.signApplet = signApplet;
	}

	/**
	 * @return the separateJARs
	 */
	public boolean isSeparateJARs() {
		return separateJARs;
	}

	/**
	 * @param separateJARs the separateJARs to set
	 */
	public void setSeparateJARs(boolean separateJARs) {
		this.separateJARs = separateJARs;
	}

	/**
	 * @return the exported
	 */
	public boolean isExported() {
		return exported;
	}

	
	/**
	 * @return the signed
	 */
	boolean isSigned() {
		return signed;
	}


	/**
	 * @param signed the signed to set
	 */
	void setSigned(boolean signed) {
		this.signed = signed;
	}


	public boolean isRendererJava2D(){
		String[] sizeInfo =
			PdePreprocessor.parseSketchSize(sketch.getMainProgram(), false);
		foundSize = false;
		if (sizeInfo != null) {
			// get size
			try {
				if (sizeInfo[1] != null && sizeInfo[2] != null) {
					sketchWidth = Integer.parseInt(sizeInfo[1]);
					sketchHeight = Integer.parseInt(sizeInfo[2]);
					foundSize = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// parsing errors, whatever; ignored
			}
			// get renderer
			String sketchRenderer = sizeInfo[3];
			if (sketchRenderer != null) {
				if (sketchRenderer.equals("P2D") || sketchRenderer.equals("P3D") || sketchRenderer.equals("OPENGL")) {
					Base.showWarning("Invalid renderer", "Sketch must use the default Java2D renderer", (Exception) null);
					return false;
				}
			}
		}
		// May not bother with this bit just flag up tyhe problem in the dialog window
		if (!foundSize) {
			final String message =
				"The size of this applet could not automatically\n" +
				"be determined from your code. You'll have to edit\n" +
				"these in the Export Applet dialog window.";
			Base.showWarning("Could not find applet size", message, (Exception) null);
		}
		return true;
	}


	public boolean isInJavaMode(){
		if(!(mode instanceof JavaMode)){
			Base.showWarning("Invalid mode", "The sketch must be in JAVA mode.", (Exception) null);
			return false;
		}
		return true;
	}

	public boolean compileCode() {
		// Delete any existing applet folder with a new one.
		mode.prepareExportFolder(appletFolder);

		// Compile the code
		srcFolder = sketch.makeTempFolder();
		binFolder = sketch.makeTempFolder();
		String foundName = null;
		try {
			foundName = build(srcFolder, binFolder, true);
		} catch (SketchException e) {
			Base.showWarning("Compiler Error", "The sketch code has errors.", (Exception) null);
			return false;
		}

		// If an error was reported during compilation exit this function
		if (foundName == null) return false;

		// If name != exportSketchName, then that's weirdness
		// BUG unfortunately, that can also be a bug in the preproc :(
		if (!sketch.getName().equals(foundName)) {
			Base.showWarning("Error during export",
					"Sketch name is " + sketch.getName() + " but the\n" +
					"name found in the code was " + foundName + ".", (Exception) null);
			return false;
		}
		return true;
	}

	public void getNarrative(){
		String[] javadoc = PApplet.match(sketch.getCode(0).getProgram(), "/\\*{2,}(.*?)\\*+/");
		if (javadoc != null) {
			StringBuffer dbuffer = new StringBuffer();
			String found = javadoc[1];
			String[] pieces = PApplet.split(found, '\n');
			for (String line : pieces) {
				// if this line starts with * characters, remove 'em
				String[] m = PApplet.match(line, "^\\s*\\*+(.*)");
				dbuffer.append(m != null ? m[1] : line);
				// insert the new line into the html to help w/ line breaks
				dbuffer.append('\n');
			}
			description = dbuffer.toString();
		}
	}

	public boolean exportApplet() throws SketchException, IOException {
		exported = false; // assume we will fail

		// Add links to all the code
		StringBuffer sources = new StringBuffer();
		//for (int i = 0; i < codeCount; i++) {
		for (SketchCode code : sketch.getCode()) {
			sources.append("<a href=\"" + code.getFileName() + "\">" +
					code.getPrettyName() + "</a> ");
		}

		// Copy the source files to the target, since we like
		// to encourage people to share their code
		//	    for (int i = 0; i < codeCount; i++) {
		for (SketchCode code : sketch.getCode()) {
			try {
				File exportedSource = new File(appletFolder, code.getFileName());
				//Base.copyFile(code[i].getFile(), exportedSource);
				code.copyTo(exportedSource);

			} catch (IOException e) {
				e.printStackTrace();  // ho hum, just move on...
			}
		}
		// move the .java file from the preproc there too
		String preprocFilename = sketch.getName() + ".java";
		File preprocFile = new File(srcFolder, preprocFilename);
		if (preprocFile.exists()) {
			preprocFile.renameTo(new File(appletFolder, preprocFilename));
		} else {
			System.err.println("Could not copy source file: " + preprocFile.getAbsolutePath());
		}

		//	File skeletonFolder = mode.getContentFile("applet");
//		File skeletonFolder = new File(Base.getSketchbookToolsFolder(), "AppletTool/data");
//		public static File toolDataFolder = new File(Base.getSketchbookToolsFolder().getAbsolutePath(), "AppletUtility/data");
		
/*
		// Copy template specific items to applet folder
		if(templateToUse == TEMPLATE_151){
			// Check if the user already has their own loader image
			File loadingImage = new File(sketch.getFolder(), LOADING_IMAGE_151);
			if (!loadingImage.exists()) {
				loadingImage = new File(AppletUtility.toolDataFolder, LOADING_IMAGE_151);
			}
			Base.copyFile(loadingImage, new File(appletFolder, LOADING_IMAGE_151));
		}
		else if(templateToUse == TEMPLATE_TOOL){
			File nojavaImage = new File(AppletUtility.toolDataFolder, NO_JAVA_IMAGE);
			Base.copyFile(nojavaImage, new File(appletFolder, NO_JAVA_IMAGE));
			File jscriptFile = new File(AppletUtility.toolDataFolder, TOOL_JSCRIPT);
			Base.copyFile(jscriptFile, new File(appletFolder, TOOL_JSCRIPT));
		}
	*/
		/*
		 * Copy template specific items to applet folder
		 */
		switch(templateToUse){
		case TEMPLATE_TOOL:
			File nojavaImage = new File(AppletMaker.toolDataFolder, NO_JAVA_IMAGE);
			Base.copyFile(nojavaImage, new File(appletFolder, NO_JAVA_IMAGE));
			File jscriptFile = new File(AppletMaker.toolDataFolder, TOOL_JSCRIPT);
			Base.copyFile(jscriptFile, new File(appletFolder, TOOL_JSCRIPT));			
			break;
		case TEMPLATE_151:
		default:
			// Check if the user already has their own loader image
			File loadingImage = new File(sketch.getFolder(), LOADING_IMAGE_151);
			if (!loadingImage.exists()) {
				loadingImage = new File(AppletMaker.toolDataFolder, LOADING_IMAGE_151);
			}
			Base.copyFile(loadingImage, new File(appletFolder, LOADING_IMAGE_151));			
		}
		
		// Create new .jar file
		FileOutputStream zipOutputFile =
			new FileOutputStream(new File(appletFolder, sketch.getName() + ".jar"));
		ZipOutputStream zos = new ZipOutputStream(zipOutputFile);

		StringBuffer archives = new StringBuffer();
		archives.append(sketch.getName() + ".jar");

		// Add the manifest file
		addManifest(zos);

		HashMap<String,Object> zipFileContents = new HashMap<String,Object>();

		// add contents of 'library' folders
		for (Library library : getImportedLibraries()) {
			// May eventually have to change getAppletExports() to getApplicationExports(linux , 32)
			for (File exportFile : library.getAppletExports()) {
				String exportName = exportFile.getName();
				if (!exportFile.exists()) {
					System.err.println("File " + exportFile.getAbsolutePath() + " does not exist");
				} else if (exportFile.isDirectory()) {
					System.out.println("Ignoring sub-folder \"" + exportFile.getAbsolutePath() + "\"");
				} else if (exportName.toLowerCase().endsWith(".zip") || exportName.toLowerCase().endsWith(".jar")) {
					// This if statement is used to filter out OpenGL required jars
					if(!isLibraryForOpenGL(exportName)){
						//						System.out.println("ACCEPT: " + exportName);
						if (separateJARs) {
							Base.copyFile(exportFile, new File(appletFolder, exportName));
							archives.append("," + exportName);
						} else {
							String path = exportFile.getAbsolutePath();
							packClassPathIntoZipFile(path, zos, zipFileContents);
						}

					}
					//					else
					//						System.out.println("REJECT: " + exportName);
				} else {  // just copy the file over.. prolly a .dll or something
					Base.copyFile(exportFile, new File(appletFolder, exportName));
				}
			}
		}
		//		System.out.println("Exporting libraries END)");

		// Copy core.jar, or add its contents to the output .jar file
		File bagelJar = Base.isMacOS() ? Base.getContentFile("core.jar") : Base.getContentFile("core/library/core.jar");  // location for 2.0b7

		if (separateJARs) {
			Base.copyFile(bagelJar, new File(appletFolder, "core.jar"));
			archives.append(",core.jar");
		} else {
			String bagelJarPath = bagelJar.getAbsolutePath();
			packClassPathIntoZipFile(bagelJarPath, zos, zipFileContents);
		}

		if (sketch.hasCodeFolder()) {
			File[] codeJarFiles = sketch.getCodeFolder().listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.charAt(0) == '.') return false;
					if (name.toLowerCase().endsWith(".jar")) return true;
					if (name.toLowerCase().endsWith(".zip")) return true;
					return false;
				}
			});
			for (File exportFile : codeJarFiles) {
				String name = exportFile.getName();
				Base.copyFile(exportFile, new File(appletFolder, name));
				archives.append("," + name);
			}
		}

		// Add the data folder to the output .jar file
		addDataFolder(zos);

		// add the project's .class files to the jar
		// just grabs everything from the build directory
		// since there may be some inner classes
		// (add any .class files from the applet dir, then delete them)
		// TODO this needs to be recursive (for packages)
		addClasses(zos, binFolder);

		// close up the jar file
		zos.flush();
		zos.close();

		//

		// convert the applet template
		// @@sketch@@, @@width@@, @@height@@, @@archive@@, @@source@@
		// and now @@description@@

		/*
		 * Create the web page (index.html) to display the applet 
		 */
		File htmlOutputFile = new File(appletFolder, "index.html");
		// UTF-8 fixes http://dev.processing.org/bugs/show_bug.cgi?id=474
		PrintWriter htmlWriter = PApplet.createWriter(htmlOutputFile);

		InputStream is = null;
		// if there is an applet.html file in the sketch folder, use that
		File customHtml = new File(sketch.getFolder(), "applet.html");
		if (customHtml.exists()) {
			is = new FileInputStream(customHtml);
		}
		//	    for (File libraryFolder : importedLibraries) {
		//	      System.out.println(libraryFolder + " " + libraryFolder.getAbsolutePath());
		//	    }
		if (is == null) {
			switch(templateToUse){
			case TEMPLATE_TOOL:
				is = new FileInputStream(new File(AppletMaker.toolDataFolder, "template-tool.html"));	
				break;
			case TEMPLATE_151:
			default:
				is = new FileInputStream(new File(AppletMaker.toolDataFolder, "template-151.html"));	
			}
		}
		BufferedReader reader = PApplet.createReader(is);

		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.indexOf("@@") != -1) {
				StringBuffer sb = new StringBuffer(line);
				int index = 0;
				while ((index = sb.indexOf("@@sketch@@")) != -1) {
					sb.replace(index, index + "@@sketch@@".length(),
							sketch.getName());
				}
				while ((index = sb.indexOf("@@source@@")) != -1) {
					sb.replace(index, index + "@@source@@".length(),
							sources.toString());
				}
				while ((index = sb.indexOf("@@archive@@")) != -1) {
					sb.replace(index, index + "@@archive@@".length(),
							archives.toString());
				}
				while ((index = sb.indexOf("@@width@@")) != -1) {
					sb.replace(index, index + "@@width@@".length(),
							String.valueOf(sketchWidth));
				}
				while ((index = sb.indexOf("@@height@@")) != -1) {
					sb.replace(index, index + "@@height@@".length(),
							String.valueOf(sketchHeight));
				}
				while ((index = sb.indexOf("@@description@@")) != -1) {
					sb.replace(index, index + "@@description@@".length(),
							description);
				}
				line = sb.toString();
			}
			htmlWriter.println(line);
		}

		reader.close();
		htmlWriter.flush();
		htmlWriter.close();

		exported = true;
		return true;
	}

	protected boolean isLibraryForOpenGL(String name){
		name = name.toLowerCase();
		if(name.startsWith("gluegen-rt") || name.startsWith("jogl-all") && name.endsWith(".jar"))
			return true;
		else
			return false;
	}

}
