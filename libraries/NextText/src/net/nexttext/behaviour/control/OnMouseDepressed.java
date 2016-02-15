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
import net.nexttext.input.MouseDefault;

/**
 * A Condition which is true when a mouse button is down and false when a mouse
 * button is up.
 */
/* $Id$ */
public class OnMouseDepressed extends Condition {
    
    Mouse mouse;
    int buttonToCheck;

    /**
     * Creates an OnMouseDepressed which performs the given Action when the mouse 
     * button 1 is pressed.
     *
     * @param trueAction the Action to perform when the mouse button 1 is pressed
     */
    public OnMouseDepressed(Action trueAction) {
        this(MouseDefault.LEFT, trueAction, new DoNothing());
    }
    
    /**
     * Creates an OnMouseDepressed which performs one of the given Actions, depending
     * on whether or not the mouse button 1 is pressed.
     *
     * @param trueAction the Action to perform when the mouse button 1 is pressed
     * @param falseAction the Action to perform when the mouse button 1 is released
     */
    public OnMouseDepressed(Action trueAction, Action falseAction) {
        this(MouseDefault.LEFT, trueAction, falseAction);
    }
    
    /**
     * Creates an OnMouseDepressed which performs the given Action when the selected
     * mouse button is pressed.
     *
     * @param buttonToCheck the mouse button to consider
     * @param trueAction the Action to perform when the selected mouse button is pressed
     */
    public OnMouseDepressed(int buttonToCheck, Action trueAction) {
        this(buttonToCheck, trueAction, new DoNothing());
    }
    
    /**
     * Creates an OnMouseDepressed which performs one of the given Actions, depending
     * on whether or not the selected mouse button is pressed.
     *
     * @param buttonToCheck the mouse button to consider
     * @param trueAction the Action to perform when the selected mouse button is pressed
     * @param falseAction the Action to perform when the selected mouse button is released
     */
    public OnMouseDepressed(int buttonToCheck, Action trueAction, Action falseAction) {
    	super(trueAction, falseAction);
        this.mouse = Book.mouse;
        this.buttonToCheck = buttonToCheck;
    }
    
    /**
     * Checks whether or not the mouse is over the given TextObject.
     * 
     * @param to the TextObject to consider
     * 
     * @return the outcome of the condition
     */
    public boolean condition(TextObject to) {
        return mouse.isPressed(buttonToCheck);
    }
}
