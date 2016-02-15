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

package net.nexttext.behaviour.control;

import net.nexttext.Book;
import net.nexttext.TextObject;
import net.nexttext.behaviour.Action;
import net.nexttext.behaviour.standard.DoNothing;

import net.nexttext.input.Mouse;

/**
 * A Condition which is true when the mouse is on top of the TextObject and 
 * false when it is not. 
 */
/* $Id$ */
public class OnMouseOver extends Condition {
    
    private Mouse mouse;
    
    /**
     * Creates an OnMouseOver which performs the given Action when the mouse
     * is over the TextObject.
     *
     * @param trueAction the Action to perform when the mouse is over the TextObject
     */
    public OnMouseOver(Action trueAction) {
        this(trueAction, new DoNothing());
    }
    
    /**
     * Creates an OnMouseOver which performs one of the given Actions, depending
     * on whether or not the mouse is over the TextObject.
     *
     * @param trueAction the Action to perform when the mouse is over the TextObject
     * @param falseAction the Action to perform when the mouse is off the TextObject
     */
    public OnMouseOver(Action trueAction, Action falseAction) {
    	super(trueAction, falseAction);
        this.mouse = Book.mouse;
    }

    /**
     * Checks whether or not the mouse is over the given TextObject.
     * 
     * @param to the TextObject to consider
     * 
     * @return the outcome of the condition
     */
    public boolean condition(TextObject to) {
        return to.getBoundingPolygon().contains(mouse.getX(), mouse.getY());
    }
}
