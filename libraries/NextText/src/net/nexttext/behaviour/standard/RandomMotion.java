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
import net.nexttext.TextObject;
import net.nexttext.property.NumberProperty;
import net.nexttext.property.PVectorProperty;
import net.nexttext.behaviour.AbstractAction;

/**
 * Moves a TextObject randomly.
 */
/* $Id$ */
public class RandomMotion extends AbstractAction {
    
    /** 
     * Default constructor. Speed is 4 by default.
     */
    public RandomMotion() {
        init(4);
    }
    
    public RandomMotion(float speed) {
        init(speed);
    }

    private void init( float speed ) {
        properties().init("Speed", new NumberProperty(speed));
    }
    
    /**
     * Moves a TextObject randomly.
     */
    public ActionResult behave(TextObject to) {
    	PVectorProperty pos = getPosition(to);
        float rate = ((NumberProperty) properties().get("Speed")).get();
        pos.add(new PVector(rate * (float)(Math.random()-0.5), rate * (float)(Math.random()-0.5)));
        return new ActionResult(false, false, false);
    }
}
