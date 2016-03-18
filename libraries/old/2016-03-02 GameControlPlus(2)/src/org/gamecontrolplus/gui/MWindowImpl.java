package org.gamecontrolplus.gui;

import java.util.Collections;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PMatrix;
import processing.core.PMatrix3D;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class MWindowImpl implements MConstants, MConstantsInternal {
	
	public LinkedList<MAbstractControl> windowControls = new LinkedList<MAbstractControl>();
	// These next two lists are for controls that are to be added or remove since these
	// actions must be performed outside the draw cycle to avoid concurrent modification
	// exceptions when changing windowControls
	public LinkedList<MAbstractControl> toRemove = new LinkedList<MAbstractControl>();
	public LinkedList<MAbstractControl> toAdd = new LinkedList<MAbstractControl>();

	
	PApplet app;
	PMatrix orgMatrix = null;
	
	public MWindowImpl(PApplet app){
		this.app = app;
		PMatrix mat = app.getMatrix();
		if(mat instanceof PMatrix3D)
			orgMatrix = mat;
		registerMethods();
	}
	
	protected void registerMethods(){
		app.registerMethod("pre", this);
		app.registerMethod("draw", this);
		app.registerMethod("post", this);
		app.registerMethod("mouseEvent", this);
		app.registerMethod("keyEvent", this);
	}

	protected void unregisterMethods(){
		app.unregisterMethod("pre", this);
		app.unregisterMethod("draw", this);
		app.unregisterMethod("post", this);
		app.unregisterMethod("mouseEvent", this);
		app.unregisterMethod("keyEvent", this);
	}

	protected void addToWindow(MAbstractControl control){
		// Avoid adding duplicates
		if(!toAdd.contains(control) && !windowControls.contains(control)){
			toAdd.add(control);
		}
	}
	
	protected void removeFromWindow(MAbstractControl control){
		toRemove.add(control);
	}
	
	void setColorScheme(int cs){
		for(MAbstractControl control : windowControls)
			control.setLocalColorScheme(cs);
	}

	void setAlpha(int alpha){
		for(MAbstractControl control : windowControls)
			control.setAlpha(alpha);
	}

	public void draw() {
		app.pushMatrix();
		if(orgMatrix != null)
			app.setMatrix(orgMatrix);
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & DRAW_METHOD) == DRAW_METHOD )
				control.draw();
		}		
		app.popMatrix();
	}

	/**
	 * The mouse method registered with Processing
	 * 
	 * @param event
	 */
	public void mouseEvent(MouseEvent event){
		for(MAbstractControl control : windowControls){
			if((control.registeredMethods & MOUSE_METHOD) == MOUSE_METHOD)
				control.mouseEvent(event);
		}
	}

	/**
	 * The key method registered with Processing
	 */	
	public void keyEvent(KeyEvent event) {
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & KEY_METHOD) == KEY_METHOD)
				control.keyEvent(event);
		}			
	}

	/**
	 * The pre method registered with Processing
	 */
	public void pre(){
		if(MAbstractControl.controlToTakeFocus != null && MAbstractControl.controlToTakeFocus.getPApplet() == app){
			MAbstractControl.controlToTakeFocus.setFocus(true);
			MAbstractControl.controlToTakeFocus = null;
		}
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & PRE_METHOD) == PRE_METHOD)
				control.pre();
		}
	}

	public void post() {
		//System.out.println("POST");
		if(M4P.cursorChangeEnabled){
			if(MAbstractControl.cursorIsOver != null && MAbstractControl.cursorIsOver.getPApplet() == app)
				app.cursor(MAbstractControl.cursorIsOver.cursorOver);			
			else 
				app.cursor(M4P.mouseOff);
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
//				System.out.println("Adding control to window - POST)");
				for(MAbstractControl control : toAdd)
					windowControls.add(control);
				toAdd.clear();
				Collections.sort(windowControls, M4P.zorder);
			}
		}
	}
}
