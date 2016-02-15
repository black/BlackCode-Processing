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
import net.nexttext.TextObject;
import net.nexttext.property.NumberProperty;

/**
 * Applies a constant downwards acceleration to an object.
 */
/* $Id$ */
public class Gravity extends PhysicsAction {

    public Gravity ( float strength ) {
        properties().init( "Strength", new NumberProperty(strength) );
    }
    
    public ActionResult behave( TextObject to) {
        
    	PVector acc = new PVector(0, ((NumberProperty)properties().get("Strength")).get() );
        applyAcceleration(to, acc);
        
        return new ActionResult(false, false, false);
    }
}
