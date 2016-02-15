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
import processing.core.PApplet;
import java.awt.Rectangle;

/**
 * A Condition which is true when the mouse is on top of the PApplet and 
 * false when it is not. 
 */
/* $Id$ */
public class OnMouseOverApplet extends Condition {
   
    private Mouse mouse;
    private Rectangle bounds;
    
    /**
     * Creates an OnMouseOverApplet which performs the given Action when the mouse
     * is over the PApplet.
     *
     * @param p the parent PApplet
     * @param trueAction the Action to perform when the mouse is over the PApplet
     */
    public OnMouseOverApplet(PApplet p, Action trueAction) {
        this(p, trueAction, new DoNothing());
    }
    
    /**
     * Creates an OnMouseOverApplet which performs one of the given Actions, depending
     * on whether or not the mouse is over the PApplet.
     *
     * @param p the parent PApplet
     * @param trueAction the Action to perform when the mouse is over the PApplet
     * @param falseAction the Action to perform when the mouse is off the PApplet
     */
    public OnMouseOverApplet(PApplet p, Action trueAction, Action falseAction) {
    	super(trueAction, falseAction);
    	this.bounds = p.getBounds();
        this.mouse = Book.mouse;
    }

    /**
     * Checks whether or not the mouse is over the given PApplet.
     * 
     * @param to the TextObject to consider (not used)
     * 
     * @return the outcome of the condition
     */
    public boolean condition(TextObject to) {
        return bounds.contains(mouse.getX(), mouse.getY());
    }
}
