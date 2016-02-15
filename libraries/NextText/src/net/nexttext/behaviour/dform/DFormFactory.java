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

package net.nexttext.behaviour.dform;

import net.nexttext.Book;
import net.nexttext.behaviour.AbstractBehaviour;
import net.nexttext.behaviour.Action;
import net.nexttext.behaviour.Behaviour;
import net.nexttext.behaviour.control.OnMouseDepressed;
import net.nexttext.behaviour.control.OnMousePressed;
import net.nexttext.behaviour.control.Repeat;
import net.nexttext.input.MouseDefault;

/**
 * The factory of DForm behaviours.
 */
/* $Id$ */
public class DFormFactory {
    
	/**
	 * Creates a ChaosPull behaviour that deforms when the left button is pressed and reform when not.
	 * @return behaviour
	 */
    public static Behaviour chaosPull() {
    	Action chaos = new ChaosPull(Book.mouse);
        Action reform = new Reform();
        Behaviour bhvr = new Behaviour(new OnMouseDepressed(MouseDefault.LEFT, chaos, reform));
        bhvr.setDisplayName("ChaosPull");  
        return bhvr;
    }
    
    /**
     * Creates a Pull behaviour that deforms when the left button is pressed and reform when not.
	 * @return behaviour
     */
    public static Behaviour pull() {
    	Action pull = new Pull(Book.mouse, 10, 2);
        Action reform = new Reform();
        Behaviour bhvr = new Behaviour(new OnMouseDepressed(MouseDefault.LEFT, pull, reform));
        bhvr.setDisplayName("Pull");  
        return bhvr;
    }

    /**
     * Creates a Reform behaviour.
	 * @return behaviour
     */
    public static Behaviour reform() {
        Action reform = new Reform();
        Behaviour bhvr = new Behaviour(reform);
        bhvr.setDisplayName("Reform");
    	return bhvr;
    }
    
    /**
     * Creates a Scale behaviour that scales when the left button is pressed and reforms when not.
     * @param mag scale factor
	 * @return behaviour
     */
    public static Behaviour scale(float mag) {
    	Action scale = new Scale(mag);
        Action reform = new Reform();
        Behaviour bhvr = new Behaviour(new OnMouseDepressed(MouseDefault.LEFT, scale, reform));
        bhvr.setDisplayName("Scale");
    	return bhvr;
    }

    /**
     * 
	 * @return behaviour
     */
    public static Behaviour throb() {         
        Behaviour throb = new Behaviour(new Repeat(new Throb(2, 100), 0));
        throb.setDisplayName("Throb");
        return throb;
    }

    public String toString() {
        return "DForm";
    }
}
