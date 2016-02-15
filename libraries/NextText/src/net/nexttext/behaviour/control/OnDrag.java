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

import processing.core.PVector;
import net.nexttext.Book;
import net.nexttext.Locatable;
import net.nexttext.TextObject;
import net.nexttext.behaviour.Action;
import net.nexttext.behaviour.standard.DoNothing;
import net.nexttext.behaviour.control.Condition;
import net.nexttext.input.*;

/**
 * A Condition which is true when the TextObject is being dragged by the mouse.
 *
 * <p>It implements the Locatable interface which returns the location of the
 * mouse offset by the vector between the mouse and the TextObject when the
 * drag started.  If the TextObject is moved to this position it is like
 * dragging it with the mouse.  The position is that of the last TextObject
 * which was dragged, so it's only appropriate to use this Locatable in the
 * true condition of OnDrag.  </p>
 */
/* $Id$ */
public class OnDrag extends Condition implements Locatable {
  
    private MouseDefault mouse;
    private int buttonToCheck;
    private boolean dragging;
    private PVector dragOffset;
    private TextObject lastDragged;
    private TextObject currDragged;

    /**
     * Creates an OnDrag which performs the given Action when the mouse 
     * button 1 is pressed.
     *
     * @param trueAction the Action to perform when the mouse button 1 is pressed
     */
    public OnDrag(Action trueAction) {
        this(MouseDefault.LEFT, trueAction, new DoNothing());
    }
    
    /**
     * Creates an OnDrag which performs one of the given Actions, depending
     * on whether or not the mouse button 1 is pressed.
     *
     * @param trueAction the Action to perform when the mouse button 1 is pressed
     * @param falseAction the Action to perform when the mouse button 1 is released
     */
    public OnDrag(Action trueAction, Action falseAction) {
        this(MouseDefault.LEFT, trueAction, falseAction);
    }
    
    /**
     * Creates an OnDrag which performs the given Action when the selected
     * mouse button is pressed.
     *
     * @param buttonToCheck the mouse button to consider
     * @param trueAction the Action to perform when the selected mouse button is pressed
     */
    public OnDrag(int buttonToCheck, Action trueAction) {
        this(buttonToCheck, trueAction, new DoNothing());
    }
    
    /**
     * Creates an OnDrag which performs one of the given Actions, depending
     * on whether or not the selected mouse button is pressed.
     *
     * @param buttonToCheck the mouse button to consider
     * @param trueAction the Action to perform when the selected mouse button is pressed
     * @param falseAction the Action to perform when the selected mouse button is released
     */
    public OnDrag(int buttonToCheck, Action trueAction, Action falseAction) {
    	super(trueAction, falseAction);
        this.mouse = Book.mouse;
        this.buttonToCheck = buttonToCheck;
        dragging = false;
        dragOffset = new PVector();
    }

    /** 
     * Checks whether or not the selected mouse button is pressed over the given TextObject.
     * 
     * @param to the TextObject to consider
     * 
     * @return the outcome of the condition
     */
    public boolean condition(TextObject to) {
    	if (mouse.isPressed(buttonToCheck)) {
    		if (to.getBoundingPolygon().contains(mouse.getX(), mouse.getY())) {
    			if (!dragging) {
    				// lock the mouse to the TextObject
    				dragging = true;
    				dragOffset = new PVector(mouse.getX(), mouse.getY());
    				dragOffset.sub(to.getPositionAbsolute());
    				currDragged = to;
    			}
    		}
        } else {
            dragging = false;
            lastDragged = currDragged;
            currDragged = null;
        	dragOffset = new PVector();
        }
    	
        return (dragging && (currDragged == to));
    }

    /**
     * Gets the target position of the dragged TextObject, if it would follow
     * the mouse.
     * 
     * @return the target position of the TextObject
     */
    public PVector getLocation() {
        if (dragging) {
        	PVector ret = new PVector(mouse.getX(), mouse.getY());
            ret.sub(dragOffset);
            return ret;
        } else {
            return lastDragged.getPositionAbsolute();
        }
    }
}
