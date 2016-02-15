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
import net.nexttext.property.PVectorProperty;

/**
 * Stop an object from moving by setting its velocity and angular velocity to 0.
 */
/* $Id$ */
public class Stop extends PhysicsAction {

    public ActionResult behave(TextObject to) {

    	PVectorProperty velocity = getVelocity(to);
        velocity.set(new PVector());

        NumberProperty angVel = getAngularVelocity(to);
        angVel.set(0);

        return new ActionResult(true, true, false);
    }
}
