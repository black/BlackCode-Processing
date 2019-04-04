/*
  Part of the G4P library for Processing 
  	http://www.lagers.org.uk/g4p/index.html
	http://sourceforge.net/projects/g4p/files/?source=navbar

  Copyright (c) Peter Lager 2019

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

package g4p_controls;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

/**
 * A GViewPeasyCam control is a 3D view controlled by a PeasyCam object. <br>
 * 
 * There are some limitations
 * <ul>
 * <li> Only mouse ENTERED, EXITED and CLICKED events will be reported to listeners. </li>
 * <li> You cannot rotate this control </li>
 * </ul>
 * 
 * @author Peter Lager
 */
public class GViewPeasyCam extends GAbstractView  {

	protected PeasyCam pcam;

	/**
	 * Create a 3D view object controlled with PeasyCam on the main sketch surface. <br>
	 * 
	 * The camera will look at the default [0,0,0] position
	 * 
	 * @param theApplet the main sketch
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 * @param dist the distance for the camera
	 */
	public GViewPeasyCam(PApplet theApplet, float p0, float p1, float p2, float p3, float dist) {
		this(theApplet, p0, p1, p2, p3, new PVector(), dist);
	}
	
	/**
	 * Create a 3D view object controlled with PeasyCam on the main sketch surface. <br>
	 * 
	 * @param theApplet the main sketch
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 * @param lookAt where the camera is looking at
	 * @param dist the distance for the camera
	 */
	public GViewPeasyCam(PApplet theApplet, float p0, float p1, float p2, float p3, PVector lookAt, float dist) {
		super(theApplet, p0, p1, p2, p3);
		view = winApp.createGraphics((int)width, (int)height, P3D);
		is3D = true;
		pcam = new PeasyCam(theApplet, view, lookAt.x, lookAt.y, lookAt.z, dist);
		pcam.setViewport((int)x, (int)y, (int)width, (int)height);

		// Initialise the view
		view.beginDraw();
		view.background(0, 48);
		view.endDraw();

		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		cursorOver = CROSS;
		// Must register control
		G4P.registerControl(this);
	}

	/**
	 * Examine the mouse event created by Processing and see if it needs to
	 * be processed by this control. <br>
	 * Do not call this method directly, it it called automatically by G4P 
	 * at the  appropriate time.
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		isOver = (currSpot >= 0);
		// If no listener then we can forget the rest. There will be need
		// to change mouse cursor because it does not respond to mouse events
		if(viewListener == null) return;

		if(isOver || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;
		
		switch(event.getAction()){
		case MouseEvent.ENTER:	// action = 6
			// This is when the mouse enters the applications client area not the control
			if(isOver) {
				pisOver = isOver = true;
				viewListener.processEvent(convertEvent(event, MouseEvent.ENTER, ox, oy));
			}
			break;
		case MouseEvent.EXIT: // action = 7
			// This is when the mouse leaves the applications client area not the control
			if(isOver) {
				pisOver = isOver = false;;
				viewListener.processEvent(convertEvent(event, MouseEvent.EXIT, ox, oy));
			}
			break;
		case MouseEvent.CLICK:		// action = 3
			if(isOver) {
				viewListener.processEvent(convertEvent(event, MouseEvent.CLICK, ox, oy));				
			}
			break;
		case MouseEvent.MOVE:	// action = 5
		case MouseEvent.DRAG:	// action = 4
			// First see if we have entered or exited the control
			if(pisOver != isOver) {
				pisOver = isOver;
				if(isOver) { // [mx,my] is over the view so perform move after enter
					viewListener.processEvent(convertEvent(event, MouseEvent.ENTER, ox, oy));
				}
				else { // [mx,my] is not over the view so ignore move event
					viewListener.processEvent(convertEvent(event, MouseEvent.EXIT, ox, oy));	
				}
			}
			break;
		}
	}

	public PeasyCam getPeasyCam() {
		return pcam;
	}

	/**
	 * Move the control to the given position based on the mode. <br>
	 * 
	 * The position is not constrained to the screen area. <br>
	 * 
	 * The current control mode determines whether we move the
	 * corner or the center of the control to px,py <br>
	 * 
	 * @param px the horizontal position to move to
	 * @param py the vertical position to move to
	 */
	public void moveTo(float px, float py){
		moveTo(px, py, G4P.control_mode);
	}

	/**
	 * Move the control to the given position based on the mode. <br>
	 * 
	 * Unlike when dragged the position is not constrained to the 
	 * screen area. <br>
	 * 
	 * The mode determines whether we move the corner or the center 
	 * of the control to px,py <br>
	 * 
	 * @param px the horizontal position to move to
	 * @param py the vertical position to move to
	 * @param mode the control mode 
	 */
	public void moveTo(float px, float py, GControlMode mode){
		GAbstractControl p = parent;
		if(p != null){
			px -= p.width/2.0f;
			py -= p.height/2.0f;
		}
		switch(mode){
		case CORNER:
		case CORNERS:
			cx += (px - x);
			cy += (py - y);
			x = cx - width/2.0f;
			y = cy - height/2.0f;
			break;
		case CENTER:
			cx = px;
			cy = py;
			x = cx - width/2.0f;
			y = cy - height/2.0f;
			break;
		}
		pcam.setViewport((int)x, (int)y, (int)width, (int)height);
	}

	/* disable rotation */
	public void setRotation(float angle){ }

	/* disable rotation */
	public void setRotation(float angle, GControlMode mode){ }

	public void setEnabled(boolean enable){
		enabled = enable;
		pcam.setActive(enable);
	}
	
	public void setAlpha(int alpha){
		alpha = Math.abs(alpha) % 256;
		if(alphaLevel != alpha){
			alphaLevel = alpha;
			available = (alphaLevel >= ALPHA_BLOCK);
			pcam.setActive(available);
			bufferInvalid = true;
		}
	}

	public void setAlpha(int alpha, boolean includeChildren){
		setAlpha(alpha);
	}
}
