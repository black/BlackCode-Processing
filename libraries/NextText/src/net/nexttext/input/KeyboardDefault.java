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

import java.awt.event.KeyEvent;
import processing.core.PApplet;

/**
 * The KeyboardDefault is a Keyboard InputSource for Processing which is 
 * automatically updated as the sketch is running.
 */
/* $Id$ */
public class KeyboardDefault extends Keyboard {
	/**
	 * Builds a KeyboardDefault.
	 *
	 * @param p the parent PApplet the KeyboardDefault is added to
	 */
	public KeyboardDefault(PApplet p) {
	    p.registerKeyEvent(this);
	}
	
	/**
     * Handles a KeyEvent.
     * <p>Registered to be called automatically by the PApplet.</p>
     * 
     * @param event
     */
    public void keyEvent(KeyEvent event) {
        addEvent(new KeyboardEvent(event));
    }
}
