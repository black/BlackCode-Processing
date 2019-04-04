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
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.Event;
import processing.event.MouseEvent;

/**
 * This class provides all the key methods needed to respond to mouse 
 * events from a GView controlled and render the view. <br>
 * 
 * The user should create their own class that inherits from this class
 * and override the updtae() method and any of the mouse???() methods
 * they want to use.
 * 
 * @author Peter Lager
 *
 */
public class GViewListener implements PConstants {

	// This will be initialised when added to the GView control 
	private GAbstractView gview = null;

	// Mouse event created from Processing's event, 'corrected' 
	// to work for the view. This is the last one processed
	private MouseEvent lastEvent = null;

	protected GViewListener() { }
	
	/**
	 * Makes sure we have a view before using it.
	 * @return true if this listener is associated with a view else false
	 */
	final boolean hasView() {
		if(gview == null) {
//			System.out.println("This listener is not associated with a view");
			return false;
		}
		return true;
	}
	
	/**
	 * This method is called by the GView control when this listener is added to it.
	 * @param view the GView this listener is for.
	 */
	final void setView(GAbstractView view) {
		gview = view;
	}

	/**
	 * Get the PGrahics canvas for this viewer. The returned value should be cast 
	 * to Graphics3D is view is 3D (i.e. uses P3D)
	 * @return the graphics context
	 */
	final public PGraphics getGraphics() {
		return hasView() ? gview.getGraphics() : null;
	}

	/**
	 * Get the PApplet used to create the view.
	 * @return the PApplet container for the view
	 */
	final public PApplet getPApplet() {
		return hasView() ? gview.getPApplet() : null;
	}

	/**
	 * Get the platform-native event object. This might be the java.awt event
	 * on the desktop, though if you're using OpenGL on the desktop it'll be a
	 * NEWT event that JOGL uses. Android events are something else altogether.
	 * Bottom line, use this only if you know what you're doing, and don't make
	 * assumptions about the class type.
	 * @return the platform-native event object.
	 */
	final public Object getNative() {
		return lastEvent != null ? lastEvent.getNative() : null;
	}

	/**
	 * @return the width of the view
	 */
	final public int width() {
		return hasView() ? gview.width() : 0;
	}

	/**
	 * @return the height of the view
	 */
	final public int height() {
		return hasView() ? gview.height() : 0;
	}

	/**
	 * @return true if view is 3D (i.e. uses P3D)
	 */
	final public boolean is3D() {
		return hasView() ? gview.is3D : false;
	}

	/**
	 * @return the event flavor. It is always Event.MOUSE
	 */
	final public int getFlavor() {
		return Event.MOUSE;
	}
	
	/**
	 * @return the count for the mouse wheel
	 */
	final public int count() {
		return hasView() ? gview.count : 0;
	}

	/**
	 * @return the mouse X position for the previous event
	 */
	final public int pmouseX() {
		return hasView() ? gview.pmouseX : 0;
	}

	/**
	 * @return the mouse Y position for the previous event
	 */
	final public int pmouseY() {
		return hasView() ? gview.pmouseY : 0;
	}

	/**
	 * @return the mouse X position for the current event
	 */
	final public int mouseX() {
		return hasView() ? gview.mouseX : 0;
	}

	/**
	 * @return the mouse Y position for the current event
	 */
	final public int mouseY() {
		return hasView() ? gview.mouseY : 0;
	}

	/**
	 * @return the mouse button
	 */
	final public int button() {
		return hasView() ? gview.button : 0;
	}

	/**
	 * @return true if the mouse is over the view area?
	 */
	final public boolean isMouseOver() {
		return hasView() ? gview.isOver : false;
	}

	/**
	 * @return true if a mouse button is being pressed?
	 */
	final public boolean isMousePressed() {
		return hasView() ? gview.mousePressed : false;
	}

	/**
	 * @return the modifier keys 
	 */
	final public int getModifiers() {
		return hasView() ? gview.modifiers : 0;
	}

	/**
	 * @return true if the shift key is down else false
	 */
	final public boolean isShiftDown() {
		return hasView() ? gview.isShiftDown() : false;
	}

	/**
	 * @return true if the ctrl key is down else fals
	 */
	final public boolean isControlDown() {
		return hasView() ? gview.isControlDown() : false;
	}

	/**
	 * @return true if the meta key is down else fals
	 */
	final public boolean isMetaDown() {
		return hasView() ? gview.isMetaDown() : false;
	}

	/**
	 * @return true if the alt key is down else fals
	 */
	final public boolean isAltDown() {
		return hasView() ? gview.isAltDown() : false;
	}
	
	/**
	 * Use this to get the last event processed. Useful for those methods not 
	 * included in this class.
	 * @return the mouse event
	 */
	final public MouseEvent getEvent() {
		return lastEvent;
	}

	/**
	 * This is called by the view control with the mouse event generated by
	 * Processing. It creates a new event where the mouse position has been
	 * changed to that relative to the view rather than the sketch window. <br>
	 * Also the action to be used in this event; generally this will be the
	 * same as the original event but the ENTER and EXIT events now relate 
	 * to the view area rather than the sketch window. <br>
	 * 
	 * @param event the Processing event corrected for the associated view position
	 */
	final void processEvent(MouseEvent event) {
		lastEvent = event;
		mouseEvent(event);
		switch(event.getAction()) {
		case MouseEvent.PRESS:
			mousePressed();
			break;
		case MouseEvent.RELEASE:
			mouseReleased();
			break;
		case MouseEvent.CLICK:
			mouseClicked();
			break;
		case MouseEvent.DRAG:
			mouseDragged();
			break;
		case MouseEvent.MOVE:
			mouseMoved();
			break;
		case MouseEvent.ENTER:
			mouseEntered();
			break;
		case MouseEvent.EXIT:
			mouseExited();
			break;
		case MouseEvent.WHEEL:
			mouseWheel();
			break;
		}
	}

	/**
	 * This method should be overridden in the child class
	 * @param event the mouse event corrected for the associated view position
	 */
	public void mouseEvent(MouseEvent event) { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseClicked() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseDragged() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseMoved() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mousePressed() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseReleased() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseWheel() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseEntered() { }

	/**
	 * This method should be overridden in the child class
	 */
	public void mouseExited() {	}

	/**
	 * This method should be overridden in the child class
	 */
	public void update() { }

	/**
	 * This method will cause the update() method to be executed at the next frame.
	 */
	final public void invalidate() {
		if(gview != null) {
			gview.bufferInvalid = true;
		}
	}
	
	/**
	 * This method will stop the update() method being executed until the next call
	 * to invalidate().
	 */
	final public void validate() {
		if(gview != null) {
			gview.bufferInvalid = false;
		}
	}

}
