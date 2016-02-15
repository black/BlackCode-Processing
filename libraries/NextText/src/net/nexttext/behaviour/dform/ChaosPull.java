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

import java.util.Iterator;

import processing.core.PVector;

import net.nexttext.Locatable;
import net.nexttext.PLocatableVector;
import net.nexttext.TextObjectGlyph;
import net.nexttext.behaviour.TargetingAction;
import net.nexttext.property.PVectorListProperty;
import net.nexttext.property.PVectorProperty;

/** 
 * ChaosPull is similar to {@link Pull} except that the control points get into a chaotic state when
 * they reach the target.
 *
 * TODO: add parameters.
 */
/* $Id$ */
public class ChaosPull extends DForm implements TargetingAction{

    Locatable target;
    int chaosStrength;
      
    /**
     * Constructor.
     * @param target location of the attraction point that pulls the vertices
     * @param chaosStrength strength of the attraction
     */
    public ChaosPull( Locatable target, int chaosStrength ) {
        this.target = target;        
        this.chaosStrength = chaosStrength;
    }
    
    /**
     * Constructor creates a ChaosPull at the target with a default strength of 1200.
     * @param target
     */
    public ChaosPull( Locatable target ) {
        this(target, 1200);
    }
    
    /**
     * Constructor creates a ChaosPull at x,y.
     * @param x
     * @param y
     * @param chaosStrength
     */
    public ChaosPull ( float x, float y, int chaosStrength ) {
    	this(x, y, 0, chaosStrength);
    }
   
    /**
     * Constructor creates a ChaosPull at x,y with a default strength of 1200.
     * @param x
     * @param y
     */
    public ChaosPull ( float x, float y ) {
    	this(x, y, 0, 1200);
    }
    
    /**
     * Constructor creates a ChaosPull at x,y,z with a default strength of 1200.
     * @param x
     * @param y
     * @param z
     */
    public ChaosPull ( float x, float y, float z ) {
    	this(x, y, z, 1200);
    }

    /**
     * Constructor creates a ChaosPull at x,y,z.
     * @param x
     * @param y
     * @param z
     * @param chaosStrength
     */
    public ChaosPull ( float x, float y, float z, int chaosStrength ) {
    	this(new PLocatableVector(x, y, z), chaosStrength);
    }
       
    /* (non-Javadoc)
     * @see net.nexttext.behaviour.dform.DForm#behave(net.nexttext.TextObjectGlyph)
     */
    public ActionResult behave(TextObjectGlyph to) {    
        
        // Get the position of the target relative to the TextObject.
    	PVector toAbsPos = to.getPositionAbsolute();
    	PVector targetV = target.getLocation();
        targetV.sub(toAbsPos);

        // Traverse the control points of the glyph, determine the distance
        // from it to the target and move it part way there.
        PVectorListProperty cPs = getControlPoints(to);
        Iterator<PVectorProperty> i = cPs.iterator();
        while (i.hasNext()) {
            
        	PVectorProperty cP = i.next();
        	PVector p = cP.get();
            
        	PVector offset = new PVector(targetV.x, targetV.y, targetV.z);
            offset.sub(p);
            
            float pullForce = chaosStrength/(offset.mag()+25);
            offset.mult( pullForce / offset.mag() );
            
            p.add(offset);
            cP.set(p);  
        }
        
        return new ActionResult(false, false, false);
    }

    public int getChaosStrength() {
        return chaosStrength;
    }

    /**
     * Sets the 'strength' of the chaosPull, stronger chaosPull results in 
     * larger deformations and faster pulling.
     * 
     * <p>The default value is 1200.</p>
     */
    public void setChaosStrength(int chaosStrength) {
        this.chaosStrength = chaosStrength;
    }
    
    public void setTarget( float x, float y ) {
    	setTarget(x, y, 0);
    }
    
    public void setTarget( float x, float y, float z ) {
    	setTarget(new PLocatableVector(x, y, z));
    }
    
    public void setTarget( PVector target ) {
    	setTarget(new PLocatableVector(target));
    }
    
    public void setTarget( Locatable target ) {
        this.target = target;
    }    
}

