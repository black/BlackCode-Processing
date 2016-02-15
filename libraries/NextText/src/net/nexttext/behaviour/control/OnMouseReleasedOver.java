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

import net.nexttext.TextObject;
import net.nexttext.behaviour.Action;
import net.nexttext.input.MouseDefault;

/**
 * A Condition which is true when a mouse button is released over a TextObject
 * i.e. a single true result is returned if the button was down and got released
 * on top of the given TextObject.
 */
/* $Id$ */
public class OnMouseReleasedOver extends OnMouseDepressed {
    
    private boolean isPressed;
    private boolean wasPressed;
    
    /**
     * Creates an OnMouseReleasedOver which performs the given Action when the 
     * mouse button 1 is released.
     *
     * @param trueAction the Action to perform when the mouse button 1 is released
     */
    public OnMouseReleasedOver(Action trueAction) {
        this(MouseDefault.LEFT, trueAction);
    }
    
    /**
     * Creates an OnMouseReleasedOver which performs the given Action when the 
     * selected mouse button is released.
     *
     * @param buttonToCheck the mouse button to consider
     * @param trueAction the Action to perform when the selected mouse button is released
     */
    public OnMouseReleasedOver(int buttonToCheck, Action trueAction) {
        super(buttonToCheck, trueAction);
    
        isPressed = false;
        wasPressed = false;
    }
    
    /**
     * Checks whether or not the selected mouse button is released over the TextObject.
     * 
     * @param to the TextObject to consider
     * 
     * @return the outcome of the condition
     */
    public boolean condition(TextObject to) {
        wasPressed = isPressed;
        isPressed = super.condition(to);
        
        if (wasPressed && !isPressed && to.getBoundingPolygon().contains(mouse.getX(), mouse.getY())) {
            return true;
        }
        return false;
    }
}
