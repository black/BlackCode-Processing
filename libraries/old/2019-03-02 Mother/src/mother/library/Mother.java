/**
 * Mother
 * A library for live visuals performance with Processing sketches
 * http://www.onar3d.com/mother
 *
 * Copyright (c) 2008-2014 Ilias Bergstrom http://www.onar3d.com
 *
 * Mother is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mother.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * @author      Ilias Bergstrom http://www.onar3d.com
 * @modified    11/04/2014
 * @version     1.6.0 (3)
 */

package mother.library;

import processing.core.*;
import processing.opengl.*;
import oscP5.*;
import mpe.config.FileParser;

import javax.media.opengl.*;
import javax.swing.JOptionPane;
//import javax.media.opengl.glu.*;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.text.NumberFormat;
import java.util.*;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import foetus.*;

/*
BUILDING RELEASE:
o	First build the Foetus plugin running the Ant script in the processing-Foetus project. Copy Foetus.jar to the processing-Mother/lib folder overwriting the previous one.
o	MAKE sure I refresh all files in the libs folder in my workspace, to their latest versions!
o	Make sure a jdk is used in Eclipse.
o	Run the Ant script for Mother. This uses the files in the above libs folder.
o	Build Mother.zip manually, to include documentation pdf.
*/

public class Mother implements Runnable {
	public final static String VERSION = "1.6.0";
	
	private int 				m_osc_send_port;
	private int 				m_osc_receive_port;
	private String 				m_IP;
	private SynthLoader 		m_SynthLoader;
	private SynthContainer 		m_SynthContainer;
	private String 				m_Synth_Folder;
	private int 				m_Width;
	private int 				m_Height;
	private FileParser 			fp;
	private PrintWriter 		output;
	private boolean 			m_FullScreen;
	private boolean 			m_WriteImage = false;
	private float 				m_FrameRate = 30f;
	private String	 			m_ImageFolder;
	private float 				m_SpeedFraction;
	private boolean 			firstProfiledFrame; // Frames-per-second computation
	private int 				profiledFrameCount;
	private long 				startTimeMillis;
	private boolean 			m_Stereo;
	private boolean		 		m_Billboard;
	//private boolean				m_UseSpout;
	static 	boolean 			first_run = true; 	// For debugging crash with registered methods.
	private ArrayList<Message> 	m_MessageStack;
	private PGraphics 			m_synthOutputStack = null;
	private Operations 			m_Operations;
	private PApplet 			r_Parent;
	
	public String 			GetIP() 							{ return m_IP;}
	public int				GetOSCSendPort()					{ return m_osc_send_port; }
	public int				GetOSCReceivePort()					{ return m_osc_receive_port; }
	public SynthLoader		GetSynthLoader() 					{ return m_SynthLoader; }
	public SynthContainer	GetSynthContainer() 				{ return m_SynthContainer; }	
	public boolean 			GetWriteImage() 					{ return m_WriteImage; }
	public void 			SetWriteImage(boolean writeImage)	{ this.m_WriteImage = writeImage;	}
	public float 			getSpeedFraction()					{ return m_SpeedFraction;	}
	public boolean			getBillboardFlag()					{ return m_Billboard; }
	public PApplet 			GetParent() 						{ return r_Parent; }
	
	public int getChildWidth() {
		if(m_Stereo)
			return m_Width/2;
		else
			return m_Width;
	}
	
	public int getChildHeight()	{ return m_Height; }
	
	public PGraphics getOutputImage() { return m_synthOutputStack; }
	
	// SPOUT
	//Spout spout;
	
	//Class spoutClass;
	//Object spout;
	
//	Spout m_Spout;
	
	public Mother(PApplet parent) {
		r_Parent = parent; 
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#setup()
	 */
	public void setup()	{
		if(first_run) {
			r_Parent.registerMethod("dispose", this);
			r_Parent.registerMethod("pre", this);
			r_Parent.registerMethod("post", this);
			
			Runtime.getRuntime().addShutdownHook(new Thread(this));
			
			first_run=false;
		}
				
		r_Parent.size(m_Width, m_Height, PConstants.P3D);
		
		r_Parent.frameRate(m_FrameRate / m_SpeedFraction);

		m_SynthLoader 		= new SynthLoader(m_Synth_Folder);
		m_SynthContainer	= new SynthContainer();
		m_MessageStack 		= new ArrayList<Message>();
		m_Operations		= new Operations(this, m_Synth_Folder);
		
		// -Djava.library.path="${workspace_loc}/resources;${env????_var:PATH}"
		//if(m_UseSpout) {
//			m_Spout = new Spout(r_Parent, "Processing Mother", m_Width, m_Height);
			/*
			try {
				spoutClass = Class.forName("Spout");
			 
				spout = spoutClass.newInstance();
			 
				Method initializeMethod = spoutClass.getMethod("Initialize", new Class[] { PApplet.class });
			 
				initializeMethod.invoke(spout, r_Parent);
			 
				Method initSenderMethod = spoutClass.getMethod("initSender", new Class[] { String.class, Integer.TYPE, Integer.TYPE });
				
				initSenderMethod.invoke(spout, "Processing Mother", m_Width, m_Height);
			}
			catch(Exception e) {
				System.out.println("CRASH WHEN SPOUTING 1: " + e.getMessage());
			}
			*/
			
			//spout = new Spout(r_Parent);
			//spout.initSender("Processing Mother", m_Width, m_Height);
		//}
		
		listenToOSC();

		// Is this still necessary in Processing 2.0?
//		PGraphicsOpenGL pgl = (PGraphicsOpenGL) r_Parent.g;
//		PGL 			gl 	= pgl.beginPGL();
//		GL2 			gl2	= gl.gl.getGL2();
//		gl2.setSwapInterval(1); // set vertical sync on
//		pgl.endPGL();
	}

	public void pre() {
		//System.out.println("pre.");
	}

	public void post() {
		//System.out.println("post.");
	}

	public void dispose() {
	 
	}
	
	public void close() {
	
	}
	
	/*
	 * 
	 */
	private void PreDrawChildUpdate(PApplet child) {
		child.mouseX 		= r_Parent.mouseX;
		child.mouseY 		= r_Parent.mouseY;
		child.mousePressed 	= r_Parent.mousePressed;
		child.keyPressed 	= r_Parent.keyPressed;
		child.key 			= r_Parent.key;
		child.sketchPath 	= m_Synth_Folder;
	}

	// //For testing (getting opengl state)
	// IntBuffer arg1 = IntBuffer.allocate(1);
	// opengl.glGetIntegerv(GL.GL_BLEND_DST, arg1);
	// println(arg1.get(0));
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {		
		ChildWrapper 	current 		= null;
		ChildWrapper 	currentChild	= null;
		PGraphics 		previousChild	= null;
		PGraphics 		previous		= null;
		PGraphicsOpenGL pgl 			= (PGraphicsOpenGL) r_Parent.g;
		PGL 			gl 				= pgl.beginPGL();
		GL2 			gl2 			= ((PJOGL)gl).gl.getGL2();
	
		int width 	= r_Parent.width;
		int height 	= r_Parent.height;
		
		if(m_synthOutputStack == null)
			m_synthOutputStack = r_Parent.createGraphics(width, height, r_Parent.OPENGL);

		dealWithMessageStack(); // Dealing with message stack
		
		// Set The Clear Color To Black
		gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		m_synthOutputStack.beginDraw();
		m_synthOutputStack.clear();
		m_synthOutputStack.endDraw();
		
		ArrayList<PGraphics> olderSiblings = new ArrayList<PGraphics>();
		
		synchronized (m_SynthLoader) {		
			for (int i = 0; i < m_SynthContainer.Synths().size(); i++) {
				current = (ChildWrapper) m_SynthContainer.Synths().get(i);
	
				PreDrawChildUpdate(current.Child());
				callRegisteredMethod(current, "pre");

				gl2.glEnable(GL.GL_BLEND);
				r_Parent.pushMatrix();
				r_Parent.pushStyle();
				gl2.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
				
				if(i>0) {
					olderSiblings.add(previous);
				}
								
				current.foetusField.olderSiblings.clear();
				for(int s_i = 0; s_i<olderSiblings.size(); s_i++) {
					current.foetusField.olderSiblings.add(olderSiblings.get(s_i));
				}
				
				current.foetusField.incoming = m_synthOutputStack;				
				
				current.draw(m_Stereo);
				
				if(current.Synths().size()>0) {					
					for (int kid_i = 0; kid_i < current.Synths().size(); kid_i++) {
						currentChild  = (ChildWrapper)current.Synths().get(kid_i);

						// If it is the first sub-synth, give it the output of the parent.
						// If not, give it the output of the previous sibling.
						if(kid_i == 0)
							currentChild.foetusField.incoming = current.foetusField.outgoing;
						else
							currentChild.foetusField.incoming = previousChild;
							
						currentChild.draw(m_Stereo); // draw the effect
						
						previousChild = currentChild.foetusField.outgoing;
					}
	
					// Now that I have the output of the child, 
					// I draw it into the PGrapics of the parent synth:
					current.foetusField.startDrawing();
					current.Child().clear();
					current.Child().tint(255, 255.0f*currentChild.foetusField.GetAlpha());
					current.Child().blendMode(currentChild.foetusField.GetBlendMode());
					
					/*
					 * This I only need if I don't fix the GlowBlur effect thing.
					 * Really it should preserve transparency.
					 * Which right now it doesn't do.
					 * Also, it only does something if ADD blending is used.
					 */
					if(previous != null) {
						current.Child().image(	previous, 0, 0, width, height);
					}
					
					current.Child().image(currentChild.foetusField.outgoing, 0, 0, width, height);
					current.foetusField.endDrawing();
				}
								
				m_synthOutputStack.beginDraw();
				m_synthOutputStack.tint(255, 255.0f*current.foetusField.GetAlpha());
				m_synthOutputStack.blendMode(current.foetusField.GetBlendMode());				  
				m_synthOutputStack.image(current.foetusField.outgoing, 0, 0, width, height);
				/*
				 * This is a compromise for the moment, and only expected to be used 
				 * with Processing 2.0.2, in 2.0.3 it looks wrong I think.
				 * I should implement a solution that deals with repeated blending,
				 * when I find the time.
				 */
				//m_synthOutputStack.image(current.foetusField.outgoing, 0,	0, width, height);
				m_synthOutputStack.endDraw();
				
				previous = current.foetusField.outgoing;
				
				callRegisteredMethod(current, "draw");
				
				gl2.glPopAttrib();
				r_Parent.popStyle();
				r_Parent.popMatrix();
				gl2.glDisable(GL.GL_BLEND);

				callRegisteredMethod(current, "post");
				
				//System.gc();
			}

			r_Parent.image(m_synthOutputStack, 0, 0, width, height);
		}

		// float m = millis(); // For timing image recording
		handleImageRecording();
		// System.out.println(millis()-m);

		pgl.endPGL();
		
		//if(m_UseSpout) {
//			m_Spout.sendTexture();
			/*try {
				Method sendtextureMethod = spoutClass.getMethod("sendTexture", new Class[] {});
				
				sendtextureMethod.invoke(spout);
			}
			catch(Exception e) {
				System.out.println("CRASH WHEN SPOUTING 2");
			}
			*/
		//}
		
	    // SEND A SHARED TEXTURE HERE
	    //spout.sendTexture();
		
		printFrameRate();
	}

	/*
	protected void finalize() {
		System.out.println("FINALIZING");
		output.flush(); // Write the remaining data
		output.close(); // Finish the file
	}
	*/

	public void keyPressed() {
		PApplet child;
		Method keyMethod;

		switch (r_Parent.key) {
		case 'r':
			m_WriteImage = !m_WriteImage;
			break;
		}

		// For forwarding keyPressed messages to synths. Not really necessary though as all
		// communication is supposed to be over OSC.

		for (int i = 0; i < m_SynthContainer.Synths().size(); i++) {
			child = ((ChildWrapper) m_SynthContainer.Synths().get(i)).Child();

			// Handling messages to synths
			try {
				child.keyEvent 	= r_Parent.keyEvent;
				child.key 		= r_Parent.key;
				child.keyCode 	= r_Parent.keyCode;

				keyMethod = child.getClass().getMethod("keyPressed", new Class[] {});
				keyMethod.invoke(child, new Object[] {});
			}
			catch (Exception e)	{
				r_Parent.println("CRASH keyPressed" + e.getMessage());
			}
		}
	}

	public void motherAcceptMessage(Date time, OSCMessage message) {
		Message m;

		synchronized (m_MessageStack) {
			m 			= new Message();
			m.time 		= time;
			m.message 	= message;

			m_MessageStack.add(m);

			if (m_MessageStack.size() > 5000) {
				System.out.println("Clearing message stack");
				m_MessageStack.clear();
			}
		}
	}

	protected void sendPicWritingStartedMessage() {
		OSCPortOut sender;
		try	{
			// (m_IP, m_osc_send_port)
			InetAddress ip = InetAddress.getByName(m_IP);
			sender = new OSCPortOut(ip, m_osc_send_port);

			ArrayList<Float> list = new ArrayList<Float>();

			list.add(m_FrameRate);

			Object args[] = new Object[list.size()];

			for (int i = 0; i < list.size(); i++) {
				args[i] = list.get(i);
			}

			OSCMessage msg = new OSCMessage("/Mother_Pic_Writing_Started/", args);

			sender.send(msg);
		}
		catch (UnknownHostException e1)	{
			e1.printStackTrace();
		}
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		r_Parent.redraw();
	}

	protected void sendNextFrameMessage() {
		OSCPortOut sender;
		try	{
			// (m_IP, m_osc_send_port)
			InetAddress ip = InetAddress.getByName(m_IP);
			sender = new OSCPortOut(ip, m_osc_send_port);

			ArrayList<Float> list = new ArrayList<Float>();

			list.add(m_FrameRate);

			Object args[] = new Object[list.size()];

			for (int i = 0; i < list.size(); i++) {
				args[i] = list.get(i);
			}

			OSCMessage msg = new OSCMessage("/Mother_Next_Frame/", args);

			sender.send(msg);
		}
		catch (UnknownHostException e1)	{
			e1.printStackTrace();
		}
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		r_Parent.redraw();
	}

	public void init() {
		// Useless initializations, unless the program doesn't fint the .ini file at all...
		m_Width 		= 640;
		m_Height 		= 480;
		m_FullScreen 	= true;
		//m_UseSpout 		= false;
		
		// For OSC
		m_IP 				= "127.0.0.1";
		m_osc_receive_port 	= 7005;
		m_osc_send_port 	= 5432;
		
		m_Synth_Folder 		= "X:\\Lumia Synths";

		// Loading setup values from .ini file
		
	    if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
	    	 loadIniFile(r_Parent.sketchPath("data/mother" + ".ini")); // Mac
	    }
	    else { 
	        loadIniFile(r_Parent.sketchPath("data//mother" + ".ini")); // Windows  	
	    }		

		if (r_Parent.frame != null && m_FullScreen == true)	{
			r_Parent.frame.removeNotify();// make the frame not displayable
			r_Parent.frame.setResizable(false);
			r_Parent.frame.setUndecorated(true);
			r_Parent.println("frame is at " + r_Parent.frame.getLocation());
			r_Parent.frame.addNotify();
		}
	}

	
//	static public void main(String args[]) {
//		int 		pos_X;
//		int 		pos_Y;
//
//		FileParser fp = new FileParser("data//mother" + ".ini");
//
//		// parse ini file if it exists
//		if (fp.fileExists()) {
//			if (fp.getIntValue("FullScreen") == 1) {
//				int outputScreen = fp.getIntValue("outputScreen");
//
//				GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//				GraphicsDevice devices[] = environment.getScreenDevices();
//				String location;
//
//				Rectangle virtualBounds = new Rectangle();
//
//				if (devices.length > outputScreen) { 
//					// we have a 2nd display/projector
//
//					GraphicsConfiguration[] gc = devices[outputScreen].getConfigurations();
//
//					if (gc.length > 0) {
//						virtualBounds = gc[0].getBounds();
//					}
//
//					location = "--location=" + virtualBounds.x + "," + virtualBounds.y;
//
//					pos_X = virtualBounds.x;
//					pos_Y = virtualBounds.y;
//				}
//				else {
//					// leave on primary display
//					location = "--location=0,0";
//
//					pos_X = 0;
//					pos_Y = 0;
//				}
//
//				PApplet.main(new String[] { location, "--hide-stop", /* display, */"Mother" });
//			}
//			else {
//				PApplet.main(new String[] { "Mother" });
//			}
//		}
//	}

	public void callRegisteredMethod(ChildWrapper w, String parameter)	{
		try {
			Class params[] 	= new Class[1];
			params[0] 		= String.class;
			
			if(w!=null) {
				Method m = (Method) ((Class<? extends PApplet>) w.Child().getClass().getGenericSuperclass())
					.getDeclaredMethod("handleMethods", params);
			
				m.setAccessible(true);

				m.invoke(w.Child(), parameter);
			}
			 
//			Field sven;
//
//			sven = (Field) ((Class<? extends PApplet>) w.Child().getClass().getGenericSuperclass())
//					.getDeclaredField(fieldName);
//
//			sven.setAccessible(true);
//
//			RegisteredMethods regMethods = (RegisteredMethods) sven.get(w.Child());
//			regMethods.handle();
			
			/* EXAMPLE: 
			 * 
			 * C1 c1inst=new C1()
			 * Method m = c1inst.getClass().getDeclaredMethod("printing", null);
			 * m.setAccessible(true);
			 * m.invoke(t, null);
			 */
		}
		catch (Exception e)	{
			e.printStackTrace();
		}
	}

	private void dealWithMessageStack()	{
		synchronized (m_MessageStack) {
			Object[] args;
			OscMessage theOscMessage;
			OSCMessage m;
			for (int i = 0; i < m_MessageStack.size(); i++) {
				m = m_MessageStack.get(i).message;
				args = m.getArguments();

				theOscMessage = new OscMessage(m.getAddress(), args);

				oscEvent(theOscMessage);
			}

			// System.out.println("Message stack size: " + m_MessageStack.size());
			m_MessageStack.clear();
		}
	}

	private void printFrameRate() {
		if (!firstProfiledFrame) {
			if (++profiledFrameCount == 30)	{
				long endTimeMillis = System.currentTimeMillis();
				double secs = (endTimeMillis - startTimeMillis) / 1000.0;
				double fps = 30.0 / secs;
				// double ppf = tileSize * tileSize * 2;
				// double mpps = ppf * fps / 1000000.0;
				/*
				 * System.err.println("fps: " + fps + " polys/frame: " + ppf + " million polys/sec: " + mpps +
				 * " DrawElements calls/frame: " + (numDrawElementsCalls / 30));
				 */
				// System.err.println(vboEnabled);
				profiledFrameCount = 0;
//				numDrawElementsCalls = 0;
				startTimeMillis = System.currentTimeMillis();

				NumberFormat nf = NumberFormat.getInstance();

				nf.setMaximumFractionDigits(3);

				String number = nf.format(fps);

				// write the fps, in the top-left of the window
				r_Parent.frame.setTitle("Mother, " + number + "fps");

//				System.out.println(number);
			}
		}
		else {
			startTimeMillis = System.currentTimeMillis();
			firstProfiledFrame = false;
		}
	}

	private void handleImageRecording() {
		if (m_WriteImage) {
			// sendPicWritingStartedMessage();
			// noLoop();
			r_Parent.saveFrame(m_ImageFolder + "Mother-#####.png");
			// loop();
			// sendNextFrameMessage();
		}
	}

	private void listenToOSC() {
		try	{
			OSCPortIn receiver = new OSCPortIn(m_osc_receive_port);

			OSCListener listener = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					motherAcceptMessage(time, message);
				}
			};

			receiver.addListener("/Mother/*", listener);
			receiver.startListening();
		}
		catch (Exception e)	{
			r_Parent.println("Address already in use: Cannot bind");
			System.exit(0);
		}
	}

	/**
	 * Loads the Settings from the Client INI file
	 */
	private void loadIniFile(String fileString) {
		fp = new FileParser(fileString);

		// parse ini file if it exists
		if (fp.fileExists()) {
			m_IP = fp.getStringValue("IP");
			m_osc_receive_port = fp.getIntValue("osc_receive_port");
			m_osc_send_port = fp.getIntValue("osc_send_port");
			int[] localDim = fp.getIntValues("screenSize");
//			m_OutputScreen = fp.getIntValue("outputScreen");

			m_Width = localDim[0];
			m_Height = localDim[1];

			if (fp.getIntValue("FullScreen") == 1) {
				m_FullScreen = true;
			}
			else {
				m_FullScreen = false;
			}
			
			if (fp.getIntValue("UseCustomSynthFolder") == 1) {
				m_Synth_Folder = fp.getStringValue("SynthFolder");
			}
			else {
				if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1)
					m_Synth_Folder = r_Parent.sketchPath("data/Synths");  // Mac
		        else 
		        	m_Synth_Folder = r_Parent.sketchPath("data\\Synths"); // Windows
			}

			String frameRateString = fp.getStringValue("frameRate");

			m_FrameRate 	= Float.parseFloat(frameRateString);
			m_ImageFolder 	= fp.getStringValue("imagePath");

			String speedFractionString = fp.getStringValue("speedFraction");

			m_SpeedFraction = Float.parseFloat(speedFractionString);

			if (fp.getIntValue("stereo") == 1) {
				m_Stereo = true;
			}
			else {
				m_Stereo = false;
			}
			
			if (fp.getIntValue("billboard") == 1) {
				m_Billboard = true;
			}
			else {
				m_Billboard = false;
			}
			
			/*
			if (fp.getIntValue("videoMemorySharing") == 1) {
				m_UseSpout = true;
			}
			else {
				m_UseSpout = false;
			}
			*/
		}
	}

	/*
	 * public String getMenuTitle() { return "Mother 0.7"; }
	 * 
	 * public void init(Editor arg0) { // TODO Auto-generated method stub
	 * 
	 * }
	 */
	/*
	 * public void run() { main(null); }
	 */
	
	/*
	 * incoming osc message are forwarded to the oscEvent method.
	 */
	public void oscEvent(OscMessage theOscMessage) {
		String addrPattern 	= theOscMessage.addrPattern();
//		String typetag 		= theOscMessage.typetag();
		String[] splits 	= addrPattern.split("/");

		if (splits.length >= 2 && (splits[1].compareTo("Mother") == 0))	{
			synchronized (m_SynthLoader) {
				m_Operations.oscEvent(theOscMessage, splits);
			}
		}
		else {
		// Message not for mother
			// println("Unhandled OSC message: " + theOscMessage.addrPattern());
		}
	}
	
	public void run() {
		try {
			this.dispose();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			
		}
	}
}