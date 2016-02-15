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
 * This is the basic Move; actions from the physics package will have no effect
 * unless this behaviour is applied to objects.
 *
 * <p>It implements basic mechanics, updating position and velocity, rotation,
 * and angular velocity, based on force, angular force (torque), and mass.  On
 * each frame the force and angular force are reset to zero.  In addition, each
 * Move has its own drag coefficient, which is applied to every object's
 * velocity on each frame.  </p>
 *
 * <p>Physics actions will typically add to the force on an object on each
 * frame.  </p>
 */
/* $Id$ */
public class Move extends PhysicsAction {

    public Move() {
        this(0, 0);
    }

    /**
     * New Move action with specified drag.
     *
     * <p>Drag is a fraction of the velocity by which it will be scaled back.
     * So a drag of 1 will cause objects to halt completely, and 0 will have no
     * effect.  </p>
     */
    public Move(float drag, float angularDrag) {
        properties().init("Drag", new NumberProperty(drag));
        properties().init("AngularDrag", new NumberProperty(angularDrag));
    }

    /**
     * Applies Euler motion to a TextObject.
     */
    public ActionResult behave(TextObject to) {

        // Determine the acceleration to apply to the object.
    	PVector acceleration = ((PVectorProperty) to.getProperty("Force")).get();
        acceleration.mult( 1 / getMass(to).get());

        // Update velocity based on the acceleration
        PVectorProperty velocity = getVelocity(to);
        velocity.add(acceleration);

        // Apply the drag to the velocity
        velocity.scalar(1 - ((NumberProperty) properties.get("Drag")).get());

        // Update position based on velocity
        getPosition(to).add(velocity.get());

        // Reset the force to zero for the next frame.
        ((PVectorProperty) to.getProperty("Force")).set(new PVector());


        // Determine angular acceleration
        float angAcc = (((NumberProperty) to.getProperty("AngularForce")).get()
                         / getMass(to).get());

        // Update angular velocity based on the accelaration
        NumberProperty angVel = getAngularVelocity(to);
        angVel.add(angAcc);

        // Apply drag to angular velocity
        float angDrag = ((NumberProperty) properties.get("AngularDrag")).get();
        angVel.set(angVel.get() * (1 - angDrag));

        // Update rotation based on angular velocity
        getRotation(to).add(angVel.get());

        // Reset the force to zero for the next frame.
        ((NumberProperty) to.getProperty("AngularForce")).set(0);
        
        return new ActionResult(false, false, false);
    }
}
