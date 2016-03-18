package org.gamecontrolplus.gui;

import processing.core.PMatrix;

public class MWindowNEWT  extends MWindow {

	private PMatrix orgMatrix = null;

	protected MWindowNEWT(String title, int w, int h, boolean is3D) {
		super(title, w, h);
		this.is3D = is3D;
		renderer_type = is3D ? P3D : P2D;
	}

	public void setup(){
		super.setup();
		orgMatrix = getMatrix();
	}

	public void draw() {
		super.draw();
		if(is3D) hint(DISABLE_DEPTH_TEST);
		pushMatrix();
		setMatrix(orgMatrix);
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & DRAW_METHOD) == DRAW_METHOD )
				control.draw();
		}		
		popMatrix();
		if(is3D) hint(ENABLE_DEPTH_TEST);
	}

	/**
	 * Remove all existing window listeners added by Processing and add our own custom listener.
	 */
	protected void initListeners(){
		com.jogamp.newt.opengl.GLWindow newtCanvas = (com.jogamp.newt.opengl.GLWindow) surface.getNative();
		for(com.jogamp.newt.event.WindowListener l : newtCanvas.getWindowListeners())
			if(l.getClass().getName().startsWith("processing"))
				newtCanvas.removeWindowListener(l);
		newtCanvas.addWindowListener(new WindowAdapterNEWT(this));
	}

	/**
	 * This will close the window provided the action-on-close flag is CLOSE_WINDOW
	 * otherwise the window remains open.
	 */
	public void close(){
		if(actionOnClose == KEEP_OPEN || actionOnClose == EXIT_APP ) return;
		com.jogamp.newt.opengl.GLWindow newtCanvas = (com.jogamp.newt.opengl.GLWindow) surface.getNative();	
		newtCanvas.destroy();
	}

	/**
	 * This will close the window provided the action-on-close flag is CLOSE_WINDOW
	 * or KEEP_OPEN otherwise the window remains open.
	 */
	public void forceClose(){
		if(actionOnClose == EXIT_APP) return;
		if(actionOnClose == KEEP_OPEN)
			setActionOnClose(CLOSE_WINDOW);
		com.jogamp.newt.opengl.GLWindow newtCanvas = (com.jogamp.newt.opengl.GLWindow) surface.getNative();	
		newtCanvas.destroy();
	}

	/**
	 * This sets what happens when the users attempts to close the window. <br>
	 * There are 3 possible actions depending on the value passed. <br>
	 * GWindow.KEEP_OPEN - ignore attempt to close window (default action) <br>
	 * GWindow.CLOSE_WINDOW - close this window, if it is the main window it causes the app to exit <br>
	 * GWindow.EXIT_APP - exit the app, this will cause all windows to close. <br>
	 * @param action the required close action
	 */
	public void setActionOnClose(int action){
		com.jogamp.newt.opengl.GLWindow newtCanvas = (com.jogamp.newt.opengl.GLWindow) surface.getNative();	
		if(action == KEEP_OPEN)
			newtCanvas.setDefaultCloseOperation(com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode.DO_NOTHING_ON_CLOSE);
		else
			newtCanvas.setDefaultCloseOperation(com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
		actionOnClose = action;
	}

}
