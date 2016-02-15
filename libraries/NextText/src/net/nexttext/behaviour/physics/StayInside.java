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

import java.awt.Rectangle;
import java.awt.Shape;

import processing.core.PVector;

import net.nexttext.TextObject;
import net.nexttext.property.NumberProperty;
import net.nexttext.property.PVectorProperty;

/**
 * StayInside tries to keep an object inside a Shape.  Most likely, you 
 * will want to combine this action with another one which moves the object 
 * inside the Shape first.
 */
/* $Id$ */
public class StayInside extends PhysicsAction {
    
    protected Shape shape;
    
    /**
     * @param shape an area to remain inside of.
     * @param jiggle is number of pixels to jiggle object to keep it inside. 
     */
    public StayInside(Shape shape, float jiggle) {
        this.shape = shape;
        properties().init("Jiggle", new NumberProperty(jiggle));
    }
    
    /**
     * This constructor sets jiggle to 3 by default.
     * 
     * @param shape an area to remain inside of. 
     */
    public StayInside(Shape shape) {
        this(shape, 3);
    }
     
    /**
     * Jiggles the object until it's fully contained inside the shape. Also
     * slows down the object's velocity if it's trying to move outside.
     * 
     * <p>The returned ActionResult will include an event each time the object
     * is jiggled to keep it inside.  </p>
     */
    public ActionResult behave(TextObject to) {

        Rectangle toBB = to.getBoundingPolygon().getBounds();
        
        if ( shape.intersects( toBB )) {

        	PVectorProperty posProp = getPosition(to);
        	PVectorProperty velProp = getVelocity(to);

            // Record which sides intersect
            boolean xLeft   = shape.intersects(toBB.getMinX(), toBB.getMinY(),
                                               1, toBB.getHeight());
            boolean xRight  = shape.intersects(toBB.getMaxX(), toBB.getMinY(),
                                               1, toBB.getHeight());
            boolean xTop    = shape.intersects(toBB.getMinX(), toBB.getMinY(),
                                               toBB.getWidth(), 1);
            boolean xBottom = shape.intersects(toBB.getMinX(), toBB.getMaxY(),
                                               toBB.getWidth(), 1);
            
            // If only one side intersects, then jiggle a bit.

            if (xLeft && !xRight) {
                jiggle(true, -1, posProp, velProp);
            } else if (xRight && !xLeft) {
                jiggle(true, 1, posProp, velProp); 
            } else if (xTop && !xBottom) {
                jiggle(false, -1, posProp, velProp);
            } else if (xBottom && !xTop) {
                jiggle(false, 1, posProp, velProp);
            }
            return new ActionResult(false, false, true);
        } else {
            return new ActionResult(false, false, false);
        }
    }

    //  Jiggle a word within the target.
    private void jiggle(boolean xAxis, int dir, PVectorProperty pos, PVectorProperty vel) {
        float jiggle = ((NumberProperty)properties().get("Jiggle")).get();
        // Move it back within the object
        pos.add( xAxis ? new PVector(dir * jiggle, 0) : new PVector(0, dir * jiggle) );
        // Scale back the velocity by max(half,jiggle) if it's moving out
        if (dir * (xAxis? vel.get().x : vel.get().y) < 0) {
            float velMove = -1 * (xAxis? vel.get().x : vel.get().y) / 2;
            if (Math.abs(velMove) > jiggle) { velMove = dir * jiggle; }
            vel.add(new PVector( xAxis? velMove : 0, xAxis? 0: velMove , 0));
        }
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
