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

import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 * A GView control represents a rectangular area of the display window that can have a 
 * different renderer to the main sketch. For instance you might have a 3D area (P3D)
 * embedded in a 2D (P2D) sketch. <br>
 * The user must create a listener object (GViewListener) object to respond to mouse 
 * events and to update the view graphics.<br>
 * 
 * 
 * @author Peter Lager
 */
final public class GView extends GAbstractView {

	/**
	 * Create a view object. You cannot use P2D or P3D when the main sketch uses the 
	 * default JAVA2D. You can create JAVA2D view not matter what renderer the sketch uses.
	 * 
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 * @param renderer JAVA2D, P2D, P3D
	 */
	public GView(PApplet theApplet, float p0, float p1, float p2, float p3, String renderer) {
		super(theApplet, p0, p1, p2, p3);
		// Create the graphics for this view
		switch(renderer) {
		case P3D:
			is3D = true;
		case P2D:
		case JAVA2D:
			view = winApp.createGraphics((int)width, (int)height, renderer);
			break;
		default:
			view = winApp.createGraphics((int)width, (int)height, JAVA2D);
		}
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
		//		System.out.println("VW  " + currSpot + "  " + winApp.mouseX + " " + winApp.mouseY + " " + ox  + " " + oy);

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
		case MouseEvent.PRESS:		// action = 1
		case MouseEvent.CLICK:		// action = 3
		case MouseEvent.RELEASE:	// action = 2
			if(isOver) {
				viewListener.processEvent(convertEvent(event, event.getAction(), ox, oy));				
			}
			break;
		case MouseEvent.MOVE:	// action = 5
			// First see if we have entered or exited the control
			if(pisOver != isOver) {
				pisOver = isOver;
				if(isOver) { // [mx,my] is over the view so perform move after enter
					viewListener.processEvent(convertEvent(event, MouseEvent.ENTER, ox, oy));
					viewListener.processEvent(convertEvent(event, MouseEvent.MOVE, ox, oy));
				}
				else { // [mx,my] is not over the view so ignore move event
					viewListener.processEvent(convertEvent(event, MouseEvent.EXIT, ox, oy));	
				}
			}
			else if(isOver) {
				viewListener.processEvent(convertEvent(event, MouseEvent.MOVE, ox, oy));
			}
			break;
		case MouseEvent.DRAG:	// action = 4
			// First see if we have entered or exited the control
			if(pisOver != isOver) {
				pisOver = isOver;
				if(isOver) { // [mx,my] is over the view so perform drag after enter
					viewListener.processEvent(convertEvent(event, MouseEvent.ENTER, ox, oy));	
					viewListener.processEvent(convertEvent(event, MouseEvent.DRAG, ox, oy));
				}
				else { // [mx,my] is not over the view so ignore drag event
					viewListener.processEvent(convertEvent(event, MouseEvent.EXIT, ox, oy));	
				}
			}
			else if(isOver) {
				viewListener.processEvent(convertEvent(event, MouseEvent.DRAG, ox, oy));
			}
			break;
		case MouseEvent.WHEEL:	// action = 8
			if(isOver) {
				viewListener.processEvent(convertEvent(event, MouseEvent.WHEEL, ox, oy));
			}
			break;
		}
	}

}
