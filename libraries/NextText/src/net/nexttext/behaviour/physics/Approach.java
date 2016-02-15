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

package net.nexttext.behaviour.physics;

import processing.core.PVector;
import net.nexttext.Locatable;
import net.nexttext.PLocatableVector;
import net.nexttext.TextObject;
import net.nexttext.behaviour.TargetingAction;
import net.nexttext.property.BooleanProperty;
import net.nexttext.property.NumberProperty;

/**
 * Approach applies an acceleration which tries to push the object in the direction of
 * its target.
 */
/* $Id$ */
public class Approach extends PhysicsAction implements TargetingAction {
    
    protected Locatable target;
    
    /**   
	 * @param speed controls the magnitude of the acceleration applied in the direction
	 * of the target
	 * @param hitRange this value is used as a radius around the target to 
	 * determine if Approach has reached its location.  A hitRange of 1 means 
	 * that Approach will not return done unless the object is right on target.  
     */
    public Approach( Locatable target, float speed, int hitRange ) {
        this.target = target;   
        if ( hitRange < 1 ) hitRange = 1;
        properties().init("Speed", new NumberProperty(speed));
        properties().init("HitRange", new NumberProperty( hitRange ));   
        properties().init("CanComplete", new BooleanProperty( true ));        
    }
    
    /**
     * @param canComplete indicates whether this action should ever complete.
     * If set to false, then textObjects will stop moving once they enter the hitRange,
     * but will start moving again if the object to be followed moves out of the hitRange.
     */
    public Approach( Locatable target, float speed, int hitRange, boolean canComplete ) {
        this(target, speed, hitRange);
        ((BooleanProperty)properties().get("CanComplete")).set(canComplete);        
    }
    
    /**
     * Creates an Approach action at a certain target with default speed of 10 and hitRange or 2.
     * @param target
     */
    public Approach( Locatable target ) {
    	this(target, 10, 2);
    }
    
    /**
     * Constructor creates a Approach at x,y.
     * @param x
     * @param y
     * @param speed
     * @param hitRange
     */
    public Approach ( float x, float y, float speed, int hitRange ) {
    	this(x, y, 0, speed, hitRange);
    }
   
    /**
     * Constructor creates a Approach at x,y with a default speed and hitRange.
     * @param x
     * @param y
     */
    public Approach ( float x, float y ) {
    	this(x, y, 0, 10, 2);
    }
    
    /**
     * Constructor creates a Approach at x,y,z with a default speed and hitRange.
     * @param x
     * @param y
     * @param z
     */
    public Approach ( float x, float y, float z ) {
    	this(x, y, z, 10, 2);
    }

    /**
     * Constructor creates a Approach at x,y,z.
     * @param x
     * @param y
     * @param z
     * @param speed
     * @param hitRange
     */
    public Approach ( float x, float y, float z, float speed, int hitRange ) {
    	this(new PLocatableVector(x, y, z), speed, hitRange);
    }    
    
    /**
     * Applies an acceleration towards the target, with a magnitude proportional
     * to the Speed property.
     * 
     * <p>Result is complete if it has reached its target. </p>
     */
    public ActionResult behave(TextObject to) {
         
        // get the vector from the abs position to the target               
    	PVector pos = to.getPositionAbsolute();
    	PVector dir = target.getLocation();
	 	dir.sub(pos);	 	
	 	
	 	// get the distance from the target as a scalar value
	 	float dist = dir.mag();
                
        if ( dist > ((NumberProperty)properties().get("HitRange")).get() ) {
            // apply an acceleration in the direction of the target                  
            dir.mult( (1 / dist) * ((NumberProperty)properties().get("Speed")).get() );
            
            applyAcceleration(to, dir);
            
            if (((BooleanProperty)properties().get("CanComplete")).get() ) 
                return new ActionResult(false, true, false);
            else
                return new ActionResult(false, false, false);
        }
        // the object is close enough to the target, we are done 
        else{
            if (((BooleanProperty)properties().get("CanComplete")).get() )                
                return new ActionResult(true, true, false);
            else
                return new ActionResult(false, false, false);
        }
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
     * Sets a target to approach.
     */
    public void setTarget(Locatable target) {
       	this.target = target;
    }
}
