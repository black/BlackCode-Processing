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

import processing.core.PVector;
import net.nexttext.Locatable;
import net.nexttext.PLocatableVector;
import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.TargetingAction;
import net.nexttext.property.PVectorProperty;

/**
 * Move an object by the given vector amount.
 *
 * <p>The vector amount is added to its position.  The value of the Locatable
 * is checked each time behave() is called, so a variable amount can be
 * used.</p>
 */
/* $Id$ */
public class MoveBy extends AbstractAction implements TargetingAction {

    protected Locatable offset;

    public MoveBy(float x, float y) {
    	this(new PVector(x, y));
    }
    
    public MoveBy(Locatable offset) {
    	this.offset = offset;
    }

    public MoveBy(PVector offset) {
    	this.offset = new PLocatableVector(offset);
    }
    
    public ActionResult behave(TextObject to) {
    	PVectorProperty posProp = getPosition(to);
       	posProp.add(offset.getLocation());
        return new ActionResult(false, false, false);
    }

    /**
     * Sets a target to approach.
     */
    public void setTarget( float x, float y ) {
    	setTarget(x, y, 0);
    }
    
    /**
     * Sets a target to approach.
     */
    public void setTarget( float x, float y, float z ) {
    	setTarget(new PLocatableVector(x, y, z));
    }
    
    /**
     * Sets a target to approach.
     */
    public void setTarget( PVector target ) {
    	setTarget(new PLocatableVector(target));
    }
    
    /**
     * Sets an offset to move by.
     */
    public void setTarget(Locatable offset) {
        this.offset = offset;
   }
}
