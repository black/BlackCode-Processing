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

import net.nexttext.TextObjectGlyph;
import net.nexttext.property.PVectorListProperty;
import net.nexttext.property.PVectorProperty;

import java.util.Iterator;

import processing.core.PVector;

/**
 * A DForm which reverts TextObject to its original shape.
 *
 * <p>Different ways of reforming the glyphs are provided, which given
 * different visual effects.  </p>
 * 
 * <p>The current ActionResult returned specifies that a Reform action never
 * terminates, it sends a true event once it's reformed. We probably want 
 * to change it so that it can terminate. If needs be, the Reform could be put
 * into a Repeat behaviour. </p>
 */
/* $Id$ */
public class Reform extends DForm {
    
    public static final int STYLE_LINEAR = 1;
    public static final int STYLE_EXPONENTIAL = 2;    
    
    int style = STYLE_LINEAR;
    
    float linearSpeed = 0.05f;
    float exponentialSpeed = 2000;

    /**
     * Constructs a default Reform of linear style with a default speed of 0.05.
     */
    public Reform() {
    }
    
    /**
     * Constructs a custom Reform with given style and appropriate speed.
     *
     * <p>In exponential style, smaller values give faster reforms, the default 
     * value is 2000.</p>
     *
     * <p>In linear style, smaller values give slower reforms, the default 
     * value is 0.05</p>.
     *
     * @param speed the speed value according to the chosen style
     * @param style the type of reformation (linear or exponential)
     */
    public Reform(float speed, int style) {
        
        if (style == STYLE_LINEAR) {
            this.style = STYLE_LINEAR;
            linearSpeed = speed;
            
        } else if (style == STYLE_EXPONENTIAL) {
            this.style = STYLE_EXPONENTIAL;
            exponentialSpeed = speed;
            
        }
    }

    public ActionResult behave(TextObjectGlyph to) {         
        // Traverse the control points of the glyph, determine the distance
        // from its current location to the origin and move it part way there.
    	PVectorListProperty cPs = getControlPoints(to);
        Iterator<PVectorProperty> i = cPs.iterator();
        
        boolean done = true;
        
        // if the glyph is not deformed, don't waste time reforming it
        if (!to.isDeformed())
            return new ActionResult(false, false, false);
        
        while (i.hasNext()) {
        	PVectorProperty cP = i.next();
        	PVector cV = cP.get();
        	PVector oV = cP.getOriginal();

        	PVector offset = PVector.sub(oV, cV);

            // In order not to produce gratuitous property change events, if
            // the offset is short, nothing is done.
            if (offset.mag() < 0.1f) continue;

            // The reform algorithm is very slow when the points are close, so
            // once we reach a distance of 0.8 we just snap it back to its
            // original.

            if (offset.mag() > 0.8f) {
                done = false;

                if (style == STYLE_EXPONENTIAL) {                    
                    offset.mult(1 - (float)Math.pow(Math.E, - offset.mag()/exponentialSpeed));
                } else {
                    offset.mult(linearSpeed);
                }
            }            
            cV.add(offset);
            cP.set(cV);
        }
        if ( done ) {
        	to.setDeformed(false);
            return new ActionResult(true, true, false);
        }
        
        return new ActionResult(false, true, false);
    }

    public float getExponentialSpeed() {
        return exponentialSpeed;
    }

    /**
     * Sets the speed of the reform when using the exponential style.
     * @param exponentialSpeed an appropriate speed for exponential style
     */
    public void setExponentialSpeed(float exponentialSpeed) {
        this.exponentialSpeed = exponentialSpeed;
    }

    public float getLinearSpeed() {
        return linearSpeed;
    }

    /**
     * Sets the speed of the reform when using the linear style.
     * @param linearSpeed an appropriate speed for linear style
     */
    public void setLinearSpeed(float linearSpeed) {
        this.linearSpeed = linearSpeed;
    }

    /**
     * Linear style of reformation doesn't deform glyph shape.
     */
    public void setStyleLinear() {
        style = STYLE_LINEAR;
    }

    /**
     * Exponential style reformation preserves deformations longer.
     */
    public void setStyleExponential() {
        style = STYLE_EXPONENTIAL;
    }
}