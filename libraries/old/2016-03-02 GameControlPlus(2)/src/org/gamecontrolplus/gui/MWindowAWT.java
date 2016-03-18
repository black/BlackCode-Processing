package org.gamecontrolplus.gui;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class MWindowAWT  extends MWindow{

	protected MWindowAWT(String title, int w, int h) {
		super(title, w, h);
		is3D = false;
		renderer_type = JAVA2D;
	}

	public void draw() {
		super.draw();
		pushMatrix();
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & DRAW_METHOD) == DRAW_METHOD )
				control.draw();
		}		
		popMatrix();
	}

	/**
	 * Remove all existing window listeners and add our own custom listener.
	 */
	protected void initListeners(){
		Frame awtCanvas = ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).getFrame();
		for(WindowListener l : awtCanvas.getWindowListeners())
			awtCanvas.removeWindowListener(l);
		awtCanvas.addWindowListener(new WindowAdapterAWT(this));
	}

	/**
	 * This will close the window provided the action-on-close flag is CLOSE_WINDOW
	 * otherwise the window remains open.
	 */
	public void close(){
		if(actionOnClose == KEEP_OPEN || actionOnClose == EXIT_APP ) return;
		JFrame awtCanvas = (JFrame) ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).getFrame();
		awtCanvas.getToolkit().getSystemEventQueue().postEvent(new WindowEvent(awtCanvas, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * This will close the window provided the action-on-close flag is CLOSE_WINDOW
	 * or KEEP_OPEN otherwise the window remains open.
	 */
	public void forceClose(){
		if(actionOnClose == EXIT_APP) return;
		if(actionOnClose == KEEP_OPEN)
			setActionOnClose(CLOSE_WINDOW);
		JFrame awtCanvas = (JFrame) ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).getFrame();
		awtCanvas.getToolkit().getSystemEventQueue().postEvent(new WindowEvent(awtCanvas, WindowEvent.WINDOW_CLOSING));
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
		JFrame awtCanvas = (JFrame) ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).getFrame();
		if(action == KEEP_OPEN)
			awtCanvas.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		else
			awtCanvas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		actionOnClose = action;
	}
}
