/*
  This file is part of the ezGestures project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezGestures is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezGestures is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezGestures.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.gestures;

import java.awt.event.*;

import processing.core.*;

/**
 * Records gestures made using the mouse for analysis.
 */
/* $Id: MouseGestureAnalyzer.java 32 2008-11-12 16:11:56Z prisonerjohn $ */
public class MouseGestureAnalyzer extends GestureAnalyzer {
	private int buttonToCheck;
	
	/**
	 * Builds a MouseGestureAnalyzer with default button and minimum offset.
	 * 
	 * @param parent the parent PApplet
	 */
	public MouseGestureAnalyzer(PApplet parent) {
		this(parent, PConstants.LEFT, 30);
	}
	
	/**
	 * Builds a MouseGestureAnalyzer with default minimum offset.
	 * 
	 * @param parent the parent PApplet
	 * @param button the mouse button to check
	 */
	public MouseGestureAnalyzer(PApplet parent, int button) {
		this(parent, button, 30);
	}
	
	/**
	 * Builds a MouseGestureAnalyzer.
	 * 
	 * @param parent the parent PApplet
	 * @param button the mouse button to check
	 * @param min the minimum offset
	 */
	public MouseGestureAnalyzer(PApplet parent, int button, int min) {
		super(parent, min);
		
		p.registerMouseEvent(this);
		buttonToCheck = button;
	}
	
	/**
     * Handles a MouseEvent.
     * <p>Registered to be called automatically by the PApplet.</p>
     * 
     * @param event the incoming MouseEvent
     */
	public void mouseEvent(MouseEvent event) {
		int eventButton = 0;
		int modifiers = event.getModifiers();
	    if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
	    	eventButton = PConstants.LEFT;
	    } else if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
	    	eventButton = PConstants.CENTER;
	    } else if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
	    	eventButton = PConstants.RIGHT;
	    }

		if (eventButton != buttonToCheck) {
			return;
		}
		
		switch (event.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				start(event.getPoint());
				break;
			case MouseEvent.MOUSE_RELEASED:
				stop(event.getPoint());
				break;
			case MouseEvent.MOUSE_CLICKED:
				break;
			case MouseEvent.MOUSE_DRAGGED:
				try {
					update(event.getPoint());
				} catch (NullPointerException e) {
					// XXX only happens on Windows, I think it's related to this NOBUTTON shit
					System.out.println("StartPoint is missing...");
				}
				break;
			case MouseEvent.MOUSE_MOVED:
				break;
		}
	}
}
