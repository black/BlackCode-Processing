/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package org.gamecontrolplus.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * Objects of this class are separate windows which can be used to hold
 * M4P GUI components or used for drawing or both combined.
 * <br><br>
 * A number of examples are included in the library and can be found
 * at www.lagers.org.uk
 * 
 * 
 * @author Peter Lager
 *
 */
public abstract class MWindow extends PApplet implements MConstants, MConstantsInternal {

	/**
	 * Factory method to create and start a new window. The renderer
	 * must be JAVA2D, P2D or P3D otherwise this method returns null.
	 * 
	 * @param title text to appear in frame title bar
	 * @param px horizontal position of top-left corner
	 * @param py vertical position of top-left corner
	 * @param w width of drawing surface
	 * @param h height of surface
	 * @param r renderer must be JAVA2D, P3D or P3D
	 * @return the window created (in case the user wants its.
	 */
	public static MWindow getWindow(PApplet app, String title, int px, int py, int w, int h, String renderer){
		M4P.registerSketch(app);
		MWindow g3w = null;
		if(renderer.equals(JAVA2D))
			g3w = new MWindowAWT(title, w, h);
		else if(renderer.equals(P2D))
			g3w = new MWindowNEWT(title, w, h, false);
		else if(renderer.equals(P3D)){
			g3w = new MWindowNEWT(title, w, h, true);
		}
		if(g3w != null){
			String spath = "--sketch-path=" + M4P.sketchWindow.sketchPath();
			String loc = "--location=" + px + "," + py;
			String className = g3w.getClass().getName();
			String[] args = { spath, loc, className };
			M4P.registerWindow(g3w);
			PApplet.runSketch(args, g3w);
		}
		return g3w; 
	}

	protected int actionOnClose = KEEP_OPEN;

	public MWinData data;

	protected final int w, h;
	protected final String title;
	protected String renderer_type;

	protected MWindow(String title, int w, int h) {
		super();
		this.title = title;
		this.w = w;
		this.h = h;
		registerMethods();
	}

	/**
	 * Register this window for pre, draw, post, mouseEvent and
	 * keyEvent methods. 
	 */
	protected void registerMethods(){
		registerMethod("pre", this);
		registerMethod("draw", this);
		registerMethod("post", this);
		registerMethod("mouseEvent", this);
		registerMethod("keyEvent", this);
	}

	/**
	 * Unregister this window for pre, draw, post, mouseEvent and
	 * keyEvent methods.
	 * This method is called when the window closes.
	 */
	protected void unregisterMethods(){
		unregisterMethod("pre", this);
		unregisterMethod("draw", this);
		unregisterMethod("post", this);
		unregisterMethod("mouseEvent", this);
		unregisterMethod("keyEvent", this);
	}

	/**
	 * To provide a unique fields for this window create a class that inherits
	 * from MWinData with public access fields. Then use this method to associate
	 * the data with this window.
	 * @param data
	 */
	public void addData(MWinData data){
		this.data = data;
	}
	
	/**
	 * Add a control to this window, ignoring duplicates.
	 * 
	 * @param control control to be added
	 */
	protected void addToWindow(MAbstractControl control){
		// Avoid adding duplicates
		if(!toAdd.contains(control) && !windowControls.contains(control))
			toAdd.add(control);
	}

	/**
	 * Remove a control to this window.
	 * 
	 * @param control control to be removed
	 */
	protected void removeFromWindow(MAbstractControl control){
		toRemove.add(control);
	}

	/**
	 * Set the colour scheme to be used by all controls on this window.
	 * @param cs colour scheme e.g. M4P.GREEN_SCHEME
	 */
	void setColorScheme(int cs){
		for(MAbstractControl control : windowControls)
			control.setLocalColorScheme(cs);
	}

	/**
	 * Set the alpha level for all controls on this window. <br>
	 * 0 = fully transparent <br>
	 * 255 = fully opaque <br>
	 * Controls are disabled when alpha gets below M4P.ALPHA_BLOCK (128)
	 * 
	 * @param alpha 0-255 inclusive
	 */
	void setAlpha(int alpha){
		for(MAbstractControl control : windowControls)
			control.setAlpha(alpha);
	}

	/**
	 * Execute any draw handler for this window.
	 */
	public void draw() {
		pushMatrix();
		if(drawHandlerObject != null){
			try {
				drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this, data });
			} catch (Exception e) {
				MMessenger.message(EXCP_IN_HANDLER,  
						new Object[] {drawHandlerObject, drawHandlerMethodName, e} );
			}
		}
		popMatrix();
	}

	/**
	 * Execute any pre handler associated with this window and its controls
	 */
	public void pre(){
		if(preHandlerObject != null){
			try {
				preHandlerMethod.invoke(preHandlerObject, 
						new Object[] { this, data });
			} catch (Exception e) {
				MMessenger.message(EXCP_IN_HANDLER, 
						new Object[] {preHandlerObject, preHandlerMethodName, e} );
			}
		}
		if(MAbstractControl.controlToTakeFocus != null && MAbstractControl.controlToTakeFocus.getPApplet() == this){
			MAbstractControl.controlToTakeFocus.setFocus(true);
			MAbstractControl.controlToTakeFocus = null;
		}
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & PRE_METHOD) == PRE_METHOD)
				control.pre();
		}
	}

	/**
	 * Execute any post handler associated with this window and its controls. <br>
	 * Add/remove any controls request by user, this is done here outside the drawing 
	 * phase to prevent crashes.
	 */
	public void post() {
		if(postHandlerObject != null){
			try {
				postHandlerMethod.invoke(postHandlerObject, 
						new Object[] { this, data });
			} catch (Exception e) {
				MMessenger.message(EXCP_IN_HANDLER, 
						new Object[] {postHandlerObject, postHandlerMethodName, e} );
			}
		}
		if(M4P.cursorChangeEnabled){
			if(MAbstractControl.cursorIsOver != null && MAbstractControl.cursorIsOver.getPApplet() == this)
				cursor(MAbstractControl.cursorIsOver.cursorOver);			
			else 
				cursor(M4P.mouseOff);
		}
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & POST_METHOD) == POST_METHOD)
				control.post();
		}
		// =====================================================================================================
		// =====================================================================================================
		//  This is where components are removed or added to the window to avoid concurrent access violations 
		// =====================================================================================================
		// =====================================================================================================
		synchronized (this) {
			// Dispose of any unwanted controls
			if(!toRemove.isEmpty()){
				for(MAbstractControl control : toRemove){
					// If the control has focus then lose it
					if(MAbstractControl.focusIsWith == control)
						control.loseFocus(null);
					// Clear control resources
					control.buffer = null;
					if(control.parent != null){
						control.parent.children.remove(control);
						control.parent = null;
					}
					if(control.children != null)
						control.children.clear();
					control.palette = null;
					control.jpalette = null;
					control.eventHandlerObject = null;
					control.eventHandlerMethod = null;
					control.winApp = null;
					windowControls.remove(control);
					System.gc();			
				}
				toRemove.clear();
			}
			if(!toAdd.isEmpty()){
				for(MAbstractControl control : toAdd)
					windowControls.add(control);
				toAdd.clear();
				Collections.sort(windowControls, M4P.zorder);
			}
		}
	}
	
	/**
	 * Execute any mouse event handler associated with this window and its controls
	 */
	public void mouseEvent(MouseEvent event) {
		if(mouseHandlerObject != null){
			try {
				mouseHandlerMethod.invoke(mouseHandlerObject, new Object[] { this, data, event });
			} catch (Exception e) {
				MMessenger.message(EXCP_IN_HANDLER,
						new Object[] {mouseHandlerObject, mouseHandlerMethodName, e} );
			}
		}
		for(MAbstractControl control : windowControls){
			if((control.registeredMethods & MOUSE_METHOD) == MOUSE_METHOD)
				control.mouseEvent(event);
		}
	}

	/**
	 * Execute any key event handler associated with this window and its controls
	 */
	public void keyEvent(KeyEvent event) {
		if(keyHandlerObject != null){
			try {
				keyHandlerMethod.invoke(keyHandlerObject, new Object[] { this, data, event });
			} catch (Exception e) {
				MMessenger.message(EXCP_IN_HANDLER,
						new Object[] {keyHandlerObject, keyHandlerMethodName, e} );
			}
		}
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & KEY_METHOD) == KEY_METHOD)
				control.keyEvent(event);
		}			
	}

	/**
	 * Attempt to add the 'draw' handler method. 
	 * The default event handler is a method that returns void and has two
	 * parameters PApplet and MWinData
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addDrawHandler(Object obj, String methodName){
		try{
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] {PApplet.class, MWinData.class } );
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (Exception e) {
			drawHandlerObject = null;
			MMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class<?>[] { PApplet.class, MWinData.class } } );
		}
	}

	/**
	 * Attempt to add the 'pre' handler method. 
	 * The default event handler is a method that returns void and has two
	 * parameters GWinApplet and MWinData
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addPreHandler(Object obj, String methodName){
		try{
			preHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] {PApplet.class, MWinData.class } );
			preHandlerObject = obj;
			preHandlerMethodName = methodName;
		} catch (Exception e) {
			preHandlerMethod = null;
			MMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class<?>[] { PApplet.class, MWinData.class } } );
		}
	}

	/**
	 * Attempt to add the 'mouse' handler method. 
	 * The default event handler is a method that returns void and has three
	 * parameters GWinApplet, MWinData and a MouseEvent
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addMouseHandler(Object obj, String methodName){
		try{
			mouseHandlerMethod = obj.getClass().getMethod(methodName, 
					new Class<?>[] {PApplet.class, MWinData.class, MouseEvent.class } );
			mouseHandlerObject = obj;
			mouseHandlerMethodName = methodName;
		} catch (Exception e) {
			mouseHandlerObject = null;
			MMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class<?>[] { PApplet.class, MWinData.class, MouseEvent.class } } );
		}
	}

	/**
	 * Attempt to add the 'key' handler method. 
	 * The default event handler is a method that returns void and has three
	 * parameters GWinApplet, MWinData and a KeyEvent
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addKeyHandler(Object obj, String methodName){
		try{
			keyHandlerMethod = obj.getClass().getMethod(methodName, 
					new Class<?>[] {PApplet.class, MWinData.class, KeyEvent.class } );
			keyHandlerObject = obj;
			keyHandlerMethodName = methodName;
		} catch (Exception e) {
			keyHandlerObject = null;
			MMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class<?>[] { PApplet.class, MWinData.class, KeyEvent.class } } );
		}
	}

	/**
	 * Attempt to add the 'post' handler method. 
	 * The default event handler is a method that returns void and has two
	 * parameters GWinApplet and MWinData
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addPostHandler(Object obj, String methodName){
		try{
			postHandlerMethod = obj.getClass().getMethod(methodName, 
					new Class<?>[] {PApplet.class, MWinData.class } );
			postHandlerObject = obj;
			postHandlerMethodName = methodName;
		} catch (Exception e) {
			postHandlerObject = null;
			MMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class<?>[] { PApplet.class, MWinData.class } } );
		}
	}

	/**
	 * Attempt to create the on-close-window event handler for this MWindow. 
	 * The default event handler is a method that returns void and has a single
	 * parameter of type MWindow (this will be a reference to the window that is
	 * closing) <br/>
	 * 
	 * The handler will <b>not be called</> if the setActionOnClose flag is set 
	 * to EXIT_APP <br/>
	 * If the flag is set to CLOSE_WINDOW then the handler is called when the window
	 * is closed by clicking on the window-close-icon or using either the close or 
	 * forceClose methods. <br/>
	 * If the flag is set to KEEP_OPEN the window can only be closed using the
	 * forceClose method. In this case the handler will be called.
	 * 
	 * 
	 * @param obj the object to handle the on-close-window event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addOnCloseHandler(Object obj, String methodName){
		try{
			closeHandlerObject = obj;
			closetHandlerMethodName = methodName;
			closetHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { PApplet.class, MWinData.class } );
		} catch (Exception e) {
			MMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class<?>[] { MWindow.class } } );
			closeHandlerObject = null;
			closetHandlerMethodName = "";
		}
	}

	/**
	 * This will close the window provided the action-on-close flag is CLOSE_WINDOW
	 * otherwise the window remains open.
	 */
	public abstract void close();

	/**
	 * This will close the window provided the action-on-close flag is CLOSE_WINDOW
	 * or KEEP_OPEN otherwise the window remains open.
	 */
	public abstract void forceClose();

	/**
	 * This sets what happens when the users attempts to close the window. <br>
	 * There are 3 possible actions depending on the value passed. <br>
	 * MWindow.KEEP_OPEN - ignore attempt to close window (default action) <br>
	 * MWindow.CLOSE_WINDOW - close this window, if it is the main window it causes the app to exit <br>
	 * MWindow.EXIT_APP - exit the app, this will cause all windows to close. <br>
	 * @param action the required close action
	 */
	public abstract void setActionOnClose(int action);

	public void settings() {
		size(w, h, renderer_type);
	}

	/**
	 * Set up the 'window' listeners
	 */
	protected abstract void initListeners();

	public void setup(){
		surface.setTitle(title); // does not like this in settings	
		initListeners();
	}

	/**
	 * This method is executed when the window closes. It will call the user defined
	 * on-close-handler method set with 
	 */
	public void performCloseAction(){
		if(closeHandlerObject != null){
			try {
				closetHandlerMethod.invoke(closeHandlerObject, 
						new Object[] { this, data });
			} catch (Exception e) {
				MMessenger.message(EXCP_IN_HANDLER, 
						new Object[] {preHandlerObject, preHandlerMethodName, e} );
			}
		}
	}

	/**
	 * Window adapter class for the JAVA2D renderer
	 * 
	 * @author Peter Lager
	 */
	protected class WindowAdapterAWT extends WindowAdapter {
		MWindow window = null;
		
		public WindowAdapterAWT(MWindow window){
			this.window = window;
		}
		
		public void windowClosing(WindowEvent evt) {
			switch(actionOnClose){
			case EXIT_APP:
				System.exit(0);
				break;
			case CLOSE_WINDOW:
				noLoop();
				unregisterMethods();
				performCloseAction();
				dispose();
				M4P.deregisterWindow(window);
				break;
			}
		}
	}

	/**
	 * Window adapter class for the P2D and P3D renderers
	 * 
	 * @author Peter Lager
	 */
	protected class WindowAdapterNEWT extends com.jogamp.newt.event.WindowAdapter {

		MWindow window = null;
		
		public WindowAdapterNEWT(MWindow window){
			this.window = window;
		}
		public void windowGainedFocus(com.jogamp.newt.event.WindowEvent arg0) {
			focused = true;
			focusGained();
		}

		public void windowLostFocus(com.jogamp.newt.event.WindowEvent arg0) {
			focused = false;
			focusLost();
		}

		public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent arg0) {
			switch(actionOnClose){
			case EXIT_APP:
				noLoop();
				unregisterMethods();
				// Next two come from processing.opengl PSurfaceJOGL.java
				dispose();
				exitActual();
				break;
			case CLOSE_WINDOW:
				noLoop();
				unregisterMethods();
				performCloseAction();
				// Next one comes from processing.opengl PSurfaceJOGL.java
				dispose();
				M4P.deregisterWindow(window);
				break;
			}
		}

	}

	public LinkedList<MAbstractControl> windowControls = new LinkedList<MAbstractControl>();
	// These next two lists are for controls that are to be added or remove since these
	// actions must be performed outside the draw cycle to avoid concurrent modification
	// exceptions when changing windowControls
	public LinkedList<MAbstractControl> toRemove = new LinkedList<MAbstractControl>();
	public LinkedList<MAbstractControl> toAdd = new LinkedList<MAbstractControl>();

	/** The object to handle the pre event */
	protected Object preHandlerObject = null;
	/** The method in preHandlerObject to execute */
	protected Method preHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String preHandlerMethodName;

	/** The object to handle the post event */
	protected Object postHandlerObject = null;
	/** The method in postHandlerObject to execute */
	protected Method postHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String postHandlerMethodName;

	/** The object to handle the draw event */
	protected Object drawHandlerObject = null;
	/** The method in drawHandlerObject to execute */
	protected Method drawHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String drawHandlerMethodName;

	/** The object to handle the key event */
	protected Object keyHandlerObject = null;
	/** The method in keyHandlerObject to execute */
	protected Method keyHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String keyHandlerMethodName;

	/** The object to handle the mouse event */
	protected Object mouseHandlerObject = null;
	/** The method in mouseHandlerObject to execute */
	protected Method mouseHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String mouseHandlerMethodName;

	/** The object to handle the window closing event */
	protected Object closeHandlerObject = null;
	/** The method in closeHandlerObject to execute */
	protected Method closetHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String closetHandlerMethodName;

	protected boolean is3D;
}
