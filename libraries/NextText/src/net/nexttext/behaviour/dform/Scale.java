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

import java.awt.Rectangle;

import java.util.Iterator;

import processing.core.PVector;

/**
 * A DForm which scales the size of a TextObject.
 *
 */
/* $Id$ */
public class Scale extends DForm {
    
    private float scale;
    
    /**
     * @param scale is amount the object's size will increase, as a multiplier. 
     */
    public Scale(float scale) {
        this.scale = scale;        
    }

    public ActionResult behave(TextObjectGlyph to) {
        // Determine the center of to, in the same coordinates as the control
        // points will be.
    	PVector toAbsPos = to.getPositionAbsolute();
        Rectangle bb = to.getBoundingPolygon().getBounds();
        PVector center = new PVector((float)bb.getCenterX(), (float)bb.getCenterY());
        center.sub(toAbsPos);

        // Traverse the control points of the glyph, applying the
        // multiplication factor to each one, but offset from the center, not
        // the position.
        PVectorListProperty cPs = getControlPoints(to);
        Iterator<PVectorProperty> i = cPs.iterator();
        while (i.hasNext()) {
        	PVectorProperty cP = i.next();
            // Get the vector from the center of the glyph to the control point.
        	PVector p = cP.get();
            p.sub(center);
            // Scale the control point by the appropriate factor
            p.mult(scale);
            // Return p to the original coordinates
            p.add(center);            
            cP.set(p);
        }
        return new ActionResult(true, true, false);       
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}