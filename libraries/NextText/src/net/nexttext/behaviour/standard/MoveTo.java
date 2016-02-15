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
import net.nexttext.property.NumberProperty;
import net.nexttext.property.PVectorProperty;

/**
 * Move an object to the location.
 */
/* $Id$ */
public class MoveTo extends AbstractAction implements TargetingAction {

    protected Locatable target;

    /**
     * Move a TextObject to a specified position.
     * @param x x target position
     * @param y y target position
     */
    public MoveTo(int x, int y) {
    	this(x, y, Long.MAX_VALUE);
    }
    
    /**
     * Move a TextObject to a specified position at a certain speed.
     * @param x x target position
     * @param y x target position
     * @param speed moving speed
     */
    public MoveTo(int x, int y, long speed) {
    	this(new PVector(x, y), speed);
    }
    
    /**
     * Move a TextObject to a target.
     * @param target locatable target
     */
    public MoveTo( Locatable target ) {
        this(target, Long.MAX_VALUE);
    }    
    
    /**
     * Move a TextObject to a target at a certain speed.
     * @param target locatable target
	 * @param speed The speed of the approach represented as the number of
	 * pixels to move in each frame.  Use a very large number for instant
	 * travel.
     */
    public MoveTo( Locatable target, long speed ) {
        this.target = target;
        properties().init("Speed", new NumberProperty(speed));
    }

    /**
     * Move a TextObject to a target.
     * @param target locatable target
     */
    public MoveTo( PVector target ) {
        this(target, Long.MAX_VALUE);
    } 
    
    /**
     * Move a TextObject to a specified position.
     * @param target position to move to
	 * @param speed The speed of the approach represented as the number of
	 * pixels to move in each frame.  Use a very large number for instant
	 * travel.
     */
    public MoveTo( PVector target, long speed ) {
        this.target = new PLocatableVector(target);
        properties().init("Speed", new NumberProperty(speed));
    }
    
    /**
     * Add a vector to the position to bring it closer to the target.
     *
     * <p>Result is complete if it has reached its target. </p>
     */
    public ActionResult behave(TextObject to) {
        float speed = ((NumberProperty)properties().get("Speed")).get();

        // get the vector from the position to the target
        PVector pos = to.getPositionAbsolute();
        
        // check if we are use a Locatable object or a PVector
        PVector newDir = target.getLocation();
	 	newDir.sub(pos);

        ActionResult result = new ActionResult(true, true, false);

	 	// Scale the vector down to the speed if needed.
        if (newDir.mag() > speed) {
            newDir.normalize();
            newDir.mult(speed);
            result.complete = false;
        }
        PVectorProperty posProp = getPosition(to);
        posProp.add(newDir);
        return result;
    }

    /**
     * Sets a target to approach.
     */
    public void setTarget(Locatable target) {
       	this.target = target;
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
}
