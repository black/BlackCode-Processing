/**
 * Bootstrap. Quickly initialize a Processing sketch with a template.
 *
 * Copyright 2013, Jonathan Acosta. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		JOnathan Acosta
 * @modified	April 2nd 2013
 * @version		1.0
 */

package poifox;

// processing
import processing.app.*;
import processing.app.tools.*;

// file io
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

// ui
import javax.swing.JOptionPane;

// util
import java.util.ArrayList;

/**
 * Bootstrap for Processing 2.0. Succeeds InitSketch 0.9
 * 
 * @author poifox
 * 
 */
public class Bootstrap implements Tool {

	/**
	 * Gets it from PDE
	 */
	private Editor editor;

	/**
	 * Read from the system config, for portability
	 */
	private String fs;

	/**
	 * default template Path
	 */
	private String defaultTemplate;

	/**
	 * Path to the sketchbook root
	 */
	private File sketchbookPath;

	/**
	 * Path to the templates folder inside the Sketchbook
	 */
	private File templatesFolder;

	/**
	 * Tool's data folder, here until next notice
	 */
	private File dataFolder;

	/**
	 * holds the list of all .txt templates available in templatesFolder
	 */
	private ArrayList<File> allTemplates;

	/**
	 * Returns the Menu Item string for the Processing "Tools" menu
	 * Required by the Tool Inteface
	 * @return the string "Bootstrap"
	 */
	public String getMenuTitle() {
		return "Bootstrap: init sketch";
	}

	/**
	 * Runs the first time the tool is run on 
	 * @param theEditor [description]
	 */
	public void init(Editor theEditor) {
		editor = theEditor;
		fs = System.getProperty("file.separator");
		defaultTemplate = "default.txt";
		initTemplatesRoot();
		allTemplates = new ArrayList<File>();
	}

	/**
	 * Does all the actual business logic of the tool.
	 */
	public void run() {
		loadTemplates();
		if ( allTemplates.size() > 0 ) {
			if ( allTemplates.size() > 1 ) {
				offerMenu();
			} else {
				initialize(allTemplates.get(0) );
			}
		} else {
			JOptionPane
			.showMessageDialog(
				editor.getContentPane(),
				(Object) "Looks like you don't have any templates!\n"
				+ "Please put some \".txt\" templates in your sketchbook here:\n"
				+ templatesFolder.getAbsolutePath(),
				getMenuTitle(), JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Presents the template selection menu.
	 * Called when there are more two or more templates in templatesFolder
	 */
	private void offerMenu() {
		Object[] templateNames = new Object[ allTemplates.size() ];
		for ( int i = 0 ; i < allTemplates.size(); i++ ) {
			templateNames[i] = allTemplates.get( i ).getName();
		}
		Object selectedTemplate;
		selectedTemplate = JOptionPane.showInputDialog(
			editor.getContentPane(),
			(Object) "Select the template to use:",
			getMenuTitle(),
			JOptionPane.QUESTION_MESSAGE,
			null,
			templateNames,
			templateNames[0]
			);
		if ( null == selectedTemplate ) {
			// flush allTemplates on cancel
			allTemplates.clear();
			return;
		}
		for ( int j = 0 ; j < allTemplates.size() ; j++ ) {
			if ( allTemplates.get(j).getAbsolutePath().endsWith(selectedTemplate.toString()) ) {
				initialize( allTemplates.get(j) );
				return;
			}
		}
		System.err.println("BAM! YOU SHOULD NOT BE LOOKING AT THIS MESSAGE!"
			+ "\nPlease report you saw this to jonathan@poifox.com.");
	}
	
	/**
	 * Populates the sketch text with the selected template file
	 * @param templateFile [description]
	 */
	private void initialize( File templateFile ) {
		String templateContent;
		templateContent = loadString( templateFile.getAbsolutePath() );
		// insert text goes to where the cursor is.
		editor.insertText( templateContent );
		editor.statusNotice("Sketch initialized with \""+ templateFile.getName() + "\". To work!" );
		if ( 1 == allTemplates.size() ) {
			System.out.println("Remember you can have any number of templates in:\n" 
				+ templatesFolder.getAbsolutePath() );
		}
		// always flush allTemplates after initializing
		allTemplates.clear();
	}
	
	/**
	 * Loads the list of available templates onto the allTemplates List
	 * @return [description]
	 */
	private void loadTemplates() {
		String filePath;
		File[] templates = templatesFolder.listFiles();
		for ( int i = 0; i < templates.length; i ++ ) {
			filePath = templates[i].getAbsolutePath();
			if ( filePath.endsWith(".txt") || filePath.endsWith(".TXT") ) {
				allTemplates.add( new File(filePath) );
			}
		}
	}

	/**
	 * Figures out where the templates root is and stores it in templatesFolder
	 * for later usage
	 */
	private void initTemplatesRoot() {
		sketchbookPath = new File(Preferences.get("sketchbook.path"));
		dataFolder = new File( sketchbookPath + fs + "tools" + fs + "Bootstrap" + fs + "data" );
		templatesFolder = new File( sketchbookPath.getAbsolutePath() + fs + "templates" );
		if ( ! templatesFolder.exists()) {
			createTemplatesFolder();
		}
	}

	/**
	 * Creates the templates folder inside of sketchbookPath if it's not there
	 */
	private void createTemplatesFolder() {
		JOptionPane
				.showMessageDialog(
						editor.getContentPane(),
						(Object) "The templates folder was not found"
								+ "\na new templates folder will be created for you in:\n"
								+ templatesFolder.getAbsolutePath(),
						getMenuTitle(), JOptionPane.INFORMATION_MESSAGE);
		if (templatesFolder.mkdir()) {
			boolean executable = templatesFolder.setExecutable(true);
			boolean readable = templatesFolder.setReadable(true);
			boolean writable = templatesFolder.setWritable(true);
			if ( executable && readable && writable ) {
				System.out.println("The templates folder was created successfully in your sketchbook");
				System.out.println("You can add .txt files inside to grow your templates collection");
			}
			// TODO when question is answered turn this on
			// try {
			// 	installDefaultTemplate();
			// 	editor.statusNotice("Installed default.txt");
			// } catch (Exception e) {
			// 	System.err.println("Error while installing default.txt template!");
			// }
		} else {
			JOptionPane
					.showMessageDialog(
							editor,
							(Object) "Dang! An error prevented the templates folder to be created.\n"
									+ "This is probably caused by wrong permissions"
									+ "set for your sketchbook folder; I'm sorry!",
							"Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Installs default template to templatesFolder if the default template is not there yet.
	 * Inactive for now as there's something in the API kinda funky
	 * @throws IOException [description]
	 */
	private void installDefaultTemplate() throws IOException {
		// String dataTemplate = templatesFolder.getAbsolutePath() + fs + defaultTemplate;
		// File installTemplate = new File(dataTemplate);
		// if ( ! installTemplate.exists() ) {
		// 	File defaultTemplate = new File( dataFolder.getAbsolutePath() + fs + defaultTemplate );
		// 	System.out.println(defaultTemplate.getAbsolutePath());
		// 	InputStream inStream = new FileInputStream( defaultTemplate.getAbsolutePath() );
		// 	OutputStream outStream = new FileOutputStream( installTemplate.getAbsolutePath() );
		// 	byte[] outBytes = new byte[1024];
		// 	int nLength;
		// 	BufferedInputStream buffer = new BufferedInputStream( inStream );
		// 	while ( ( nLength = buffer.read(outBytes) ) > 0 ) {
		// 		outStream.write( outBytes, 0, nLength );
		// 	}
		// 	inStream.close();
		// 	outStream.close();
		// }
	}

	/***********************************************************************************************
	 * This section is inherited from suggestions on the processing-templates repository
	 **********************************************************************************************/

	/**
	 * load a text file from the data folder or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public String loadString( String theFilename ) {
		InputStream is = null;
		if (theFilename.startsWith(File.separator)) {
			try {
				is = new FileInputStream(loadFile(theFilename));
			} catch (FileNotFoundException e) {
				System.err.println("ERROR @ loadString() " + e);
			}
		} else {
			is = getClass().getResourceAsStream(getPath(theFilename));
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		int buffer;
		String result = "";
		try {
			while ((buffer = br.read()) != -1) {
				result += (char) buffer;
			}
		} catch (Exception e) {
			System.err.println("ERROR @ loadString() " + e);
		}
		return result;
	}

	/**
	 * getPath will return the path to a file or folder inside the data folder
	 * of the tool or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public String getPath(String theFilename) {
		if (theFilename.startsWith("/")) {
			return theFilename;
		}
		return File.separator + "data" + File.separator + theFilename;
	}

	/**
	 * load a file from the data folder or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public File loadFile(String theFilename) {
		if (theFilename.startsWith(File.separator)) {
			return new File(theFilename);
		}
		String path = getClass().getResource(getPath(theFilename)).getPath();
		return new File(path);
	}

}
