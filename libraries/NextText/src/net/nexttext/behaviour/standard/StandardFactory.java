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

package net.nexttext.behaviour.standard;

import net.nexttext.Book;
import net.nexttext.behaviour.AbstractBehaviour;
import net.nexttext.behaviour.Behaviour;
import net.nexttext.behaviour.control.OnDrag;
import net.nexttext.behaviour.control.Repeat;

/**
 * The factory of Standard behaviours.
 */
/* $Id$ */
public class StandardFactory {
	
    public static final AbstractBehaviour randomMotion() {        
        Behaviour rm = new Behaviour( new RandomMotion() );
        rm.setDisplayName("Random Motion");
        return rm;
    }

    
	public static AbstractBehaviour followMouse() {
        MoveTo moveTo = new MoveTo(Book.mouse, 1);
        Behaviour b = new Behaviour(new Repeat(moveTo, 0));
        b.setDisplayName("Follow Mouse");
        return b;
    }
    
    public static AbstractBehaviour draggable() {
        MoveTo moveTo = new MoveTo(Book.mouse, Long.MAX_VALUE);
        OnDrag onDrag = new OnDrag(new Repeat(moveTo, 0), new DoNothing());
        moveTo.setTarget(onDrag);
        Behaviour b = new Behaviour(onDrag);
        b.setDisplayName("Draggable");
        return b;
    }

    public String toString() { return "Standard"; }
}
