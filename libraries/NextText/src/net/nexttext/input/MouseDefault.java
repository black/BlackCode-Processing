/*
  This file is part of the NextText project.
  http://www.nexttext.net/

  Copyright (c) 2004-08 Obx Labs / Jason Lewis

  NextText is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 2 of the License, or (at your option) any later 
  version.

  NextText is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  NextText.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.nexttext.input;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import processing.core.PApplet;

/**
 * The MouseDefault is a Mouse InputSource for Processing which is 
 * automatically updated as the sketch is running.
 */
/* $Id$ */
public class MouseDefault extends Mouse {
	
	private int mX;
	private int mY;
	private boolean[] buttonPressStatus;
	
    /**
     * Builds a MouseDefault.
     * 
     * @param p the parent PApplet the MouseDefault is added to
     */
	public MouseDefault(PApplet p) {
		p.registerMouseEvent(this);
		
		buttonPressStatus = new boolean[3];
	}
	
	/**
     * Get the current x position of the mouse.
     */
    public int getX() {
    	return mX;
    }
    
    /**
     * Get the current y position of the mouse.
     */
    public int getY() {
    	return mY;
    }
    
    /**
     * Gets whether the specified mouse button is pressed.
     */
    public boolean isPressed(int button) {
        if (button == LEFT)
            return buttonPressStatus[0];
        else if (button == CENTER) 
            return buttonPressStatus[1];
        else if (button == RIGHT)
            return buttonPressStatus[2];
        else 
            return false;
    }
    
    /**
     * Sets whether the specified mouse button is pressed.
     */
    private void setPressed(int button, boolean state) {
        if (button == LEFT)
            buttonPressStatus[0] = state;
        else if (button == CENTER)
            buttonPressStatus[1] = state;
        else 
            buttonPressStatus[2] = state;
    }

    /**
     * Handles a MouseEvent.
     * <p>Registered to be called automatically by the PApplet.</p>
     * 
     * @param event
     */
	public void mouseEvent(MouseEvent event) {
		mX = event.getX();
		mY = event.getY();
		addEvent(new MouseInputEvent(event));
		
		int modifiers = event.getModifiers();
		int eventButton;
        if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
            eventButton = LEFT;
        } else if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
            eventButton = CENTER;
        } else if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
            eventButton = RIGHT;
        } else {
            return;
        }

		switch (event.getID()) {
			case MouseEvent.MOUSE_PRESSED:
			    setPressed(eventButton, true);
				break;
			case MouseEvent.MOUSE_RELEASED:
			    setPressed(eventButton, false);
			    break;
			case MouseEvent.MOUSE_CLICKED:
				break;
			case MouseEvent.MOUSE_DRAGGED:
				break;
			case MouseEvent.MOUSE_MOVED:
				break;
		}
	}
	
	/**
     * Handles a MouseEvent.
     * <p>Registered to be called automatically by the PApplet.</p>
     * 
     * @param event the incoming MouseEvent
     */
	/*
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
    }*/
}
