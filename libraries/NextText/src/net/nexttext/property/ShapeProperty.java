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

package net.nexttext.property;

import java.awt.Shape;

/**
 * A shape property of a TextObject or a Behaviour.
 *
 * <p>This is a property wrapper for java.awt.Shape.  </p>
 */
// The clones will share the same internal Shape objects.  This is necessary
// because Shape is an interface, so there is no way to know if the objects
// themselves implement Cloneable.  It is not a problem because Shapes cannot
// be modified, so changing the shape will replace the reference.  If the Shape
// objects change on their own there is no problem because sharing those
// changes seems like the correct behaviour.
/* $Id$ */
public class ShapeProperty extends Property {

    Shape original;
    Shape value;

	/**
	 * Creates a new ShapeProperty with a copy of the provided Shape.
	 */
    public ShapeProperty(Shape shape) {
        original = shape;
        value = shape;
    }

    /**
     * Do not modify the returned value, use set() to make changes instead.
     */
    public Shape get() {
        return value;
    }

    public void set(Shape shape) {        
        value = shape;
        firePropertyChangeEvent();
    }

    public void reset() {
        set(original);
    }
}
