package mother.library;

import java.io.File;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import processing.core.PApplet;
import processing.core.PGraphics;
import foetus.*;

public class SynthLoader {
	ArrayList<URL> 				m_Visual_Synth_urls;
	URL[] 						m_Library_file_URLS;
	Hashtable<String, String> 	m_Visual_Synth_Names;
	String 						m_Synth_Folder;

	public URL[] 		get_Library_File_URLS() 	{return m_Library_file_URLS;}
	Hashtable<String, 	String> get_Synth_Names() 	{return m_Visual_Synth_Names;}
	public ArrayList<URL> get_Visual_Synth_urls() 	{return m_Visual_Synth_urls;}
	/**
	 * Constructor
	 * @param folder
	 */
	public SynthLoader(String folder) {
		m_Visual_Synth_Names 	= new Hashtable<String, String>();
		m_Synth_Folder 			= folder;

		PopulateSynthURLS();
		PopulateLibraryURLS();
	}

	/**
	 * Scans folder containing synths and stores URL for each
	 */
	private void PopulateSynthURLS() {
		String[] 	fileName;
		File 		oooClassPath 	= new File(m_Synth_Folder);
		File[] 		files 			= oooClassPath.listFiles();
		m_Visual_Synth_urls 		= new ArrayList<URL>();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				try	{
					fileName = files[i].getName().split("\\.");

					if ((fileName.length > 1) && 
							(fileName[fileName.length - 1].compareTo("jar") == 0)) {
						m_Visual_Synth_urls.add(files[i].toURI().toURL());
						m_Visual_Synth_Names.put(fileName[0], fileName[0]);
						System.out.println("Found Synth: " + fileName[0]);
					}
				}
				catch (MalformedURLException ex) {
					System.out.println("MalformedURLException: " + ex.getMessage());
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("Synth folder not found, or empty!");
		}
	}

	private void PopulateLibraryURLS() {
		String[] fileName;
		File oooClassPath;

		if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1)
			oooClassPath = new File(m_Synth_Folder + "/libraries"); // Mac
		else
			oooClassPath = new File(m_Synth_Folder + "//" + "libraries"); // Windows

		File[] files = oooClassPath.listFiles();

		ArrayList<URL> temp_Library_file_URLS = new ArrayList<URL>();

		// Check if Libraries folder exists. If not, create empty list.
		if (files != null) {
			try	{
				for (int i = 0; i < files.length; i++)	{
					fileName = files[i].getName().split("\\.");

					if(	(fileName.length > 1) && 
						(fileName[fileName.length - 1].compareTo("jar") == 0) ) {
						temp_Library_file_URLS.add(files[i].toURI().toURL());
						System.out.println("Found library: " + fileName[0]);
					}
				}
			}
			catch (MalformedURLException ex) {
				System.out.println("MalformedURLException: " + ex.getMessage());
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			m_Library_file_URLS = new URL[temp_Library_file_URLS.size()];

			for (int i = 0; i < temp_Library_file_URLS.size(); i++)	{
				m_Library_file_URLS[i] = temp_Library_file_URLS.get(i);
			}
		}
		else {
			m_Library_file_URLS = new URL[0];
		}
	}

	/**
	 * Loads a sketch from disk
	 * @param classPath
	 * @param className
	 * @return
	 */
	protected PApplet LoadSketch(String className) {   
		URL[] libraryURLS 	= m_Library_file_URLS;
		String classPath	= m_Synth_Folder;
		
		File dir1 = new File (".");
	    
		if(libraryURLS==null) {
			  System.out.println ("libraryURLS IS NULL!");
			  libraryURLS = new URL[0];
		}
		
		try {
	      System.out.println ("Current dir : " + dir1.getCanonicalPath());
	    }
	    catch(Exception e) {	    	 
	    
	    }		
	    
        File oooClassPath;
        
        // Weird, why are they the samee? :)
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1)
        	oooClassPath = new File(classPath + "//" + className + ".jar"); // Mac
        else 
        	oooClassPath = new File(classPath + "//" + className + ".jar"); // Windows

        URL[] toUse = new URL[1 + libraryURLS.length];
        
        try { 
	        for(int i = 0; i<libraryURLS.length; i++ ) {
	      		toUse[i] = libraryURLS[i];
	        }
	            
	        toUse[libraryURLS.length] = oooClassPath.toURI().toURL();
        	        	
        	URLClassLoader cl = new URLClassLoader( toUse, ClassLoader.getSystemClassLoader() );

        	PApplet toReturn = (PApplet)Class.forName(className, true, cl).newInstance(); 
        	
        	toReturn.noLoop();
        	
            return toReturn;
        } 
        catch (Exception ex) {
        	System.out.println("Loading child failed, probably jar file could not be found! " + 
        			ex.getMessage());
        }
        
        return null;
    } 
	
	/*
	 * 
	 */
	protected void InitChild(ChildWrapper cw, Mother parent)	{
		PApplet child = cw.Child();

//		Method[] methods 			= child.getClass().getMethods();
		Method[] declaredMethods 	= child.getClass().getDeclaredMethods();
		
		child.g = parent.GetParent().g;
				
		child.setSize(parent.getChildWidth(), parent.getChildHeight());

		/*
		 * With this, I'm hoping the child will run in a separate thread, 
		 * but its timer will not call the draw method.
		 * Instead, only one timer is running, the one in Mother.
		 */
		child.noLoop();

		/*
		 * try { for(int i = 0; i < methods.length; i++) { if(methods[i].getName().equals("init")) {
		 * methods[i].invoke(child, new Object[] {}); break; } } } catch(Exception e) {
		 * System.out.println("CRASH PApplet.init: " + e.getMessage()); }
		 */

		// initializeRegisteredMapField(cw);

		child.frameCount 	= parent.GetParent().frameCount;
		child.frameRate 	= parent.GetParent().frameRate;
		child.frame 		= parent.GetParent().frame;
// 		child.screen 		= parent.GetParent().screen;
		child.recorder 		= parent.GetParent().recorder;
		child.sketchPath 	= m_Synth_Folder;
		child.pixels 		= parent.GetParent().pixels;
		child.width 		= parent.getChildWidth();
		child.height 		= parent.getChildHeight();

		child.noLoop();

		Foetus foetusField;

		try	{
			for (int i = 0; i < declaredMethods.length; i++) {
				if (declaredMethods[i].getName().equals("initializeFoetus")) {
					declaredMethods[i].invoke(child, new Object[] {});

					break;
				}
			}

			foetusField = (Foetus) child.getClass().getDeclaredField("f").get(child);

			cw.setFoetusField(foetusField);

			foetusField.standalone = false;

			foetusField.setSpeedFraction(parent.getSpeedFraction());
		}
		catch (Exception e) {
			System.out.println("CRASH while initializing synth. Message: " + e.getMessage());
		}
		
		PGraphics kidPG = parent.GetParent().createGraphics(	parent.GetParent().width, 
				parent.GetParent().height, 
				parent.GetParent().OPENGL);

		cw.getFoetusField().outgoing = kidPG;
	}
}