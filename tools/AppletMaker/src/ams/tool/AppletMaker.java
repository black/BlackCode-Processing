/**
 * you can put a one sentence description of your tool here.
 *
 * (C) 2013
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
 * @author		Peter Lager http://www.lagers.org.uk
 * @modified	12/25/2013
 * @version		##version##
 */

package ams.tool;

import java.io.File;

import processing.app.Base;
import processing.app.Editor;
import processing.app.tools.Tool;


/**
 * The Applet utility tool.
 * 
 * @author Peter Lager
 *
 */
public class AppletMaker implements Tool {

	// when creating a tool, the name of the main class which implements Tool
	// must be the same as the value defined for project.name in your build.properties

	public static Editor editor = null;
	public static File toolDataFolder = new File(Base.getSketchbookToolsFolder(), "AppletMaker/data");
//	public static File toolDataFolder = new File(Base.getSketchbookToolsFolder().getAbsolutePath(), "AppletMaker/data");

	/**
	 * The title to appear in the Processing menu.
	 */
	public String getMenuTitle() {
		return "Applet Export and Signer";
	}

	/**
	 * Initialise the tool by passing a reference to the editor
	 */
	public void init(Editor theEditor) {
		editor = theEditor;
	}

	/**
	 * Main entry point when the tool is run
	 */
	public void run() {
		System.out.println("===================================================");
		System.out.println("   AppletMaker V0.1.3 created by Peter Lager");
		System.out.println("===================================================");

		AppletBuildDetail ab = new AppletBuildDetail(editor.getSketch());

		// Make sure we are in JAVA mode, the renderer is JAVA2D and that it compiles
		if(!ab.isInJavaMode() || !ab.isRendererJava2D() || !ab.compileCode())
			return;
		// Everything set for export
		ExportDialog eform = new ExportDialog(ab);
		eform.setVisible(true);
		if(!ab.isExported()){
			System.out.println("Export aborted");
			return;
		}
	
		System.out.println("Applet has been exported");
		if(ab.isSignApplet()){
			SignerDialog sform = new SignerDialog(ab);
			sform.setVisible(true);
			// test to see if the applet was signed successfully
			if(ab.isSigned())
				System.out.println("Applet has been signed");		
		}

		Base.openFolder(ab.getAppletFolder());
	}



}



