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

package net.nexttext;

import java.awt.Rectangle;
import java.awt.Shape;

import processing.core.PVector;

/**
 * A locatable which responds with a random location.
 *
 * <p>The location returned will be inside the given shape.  The bounds of the
 * shape are checked on each call, so mutable shapes will be properly
 * supported.  </p>
 *
 * <p>Because of the way getLocation() is implemented, very thin shapes with
 * large bounding boxes may cause it to lock up in a loop trying to find a
 * point inside.  </p>
 */
/* $Id$ */
public class RandomLocation implements Locatable  {

    Shape shape;

    public RandomLocation(Shape shape) {
        this.shape = shape;
    }

    public PVector getLocation() {

        float x,y;
        do {
            Rectangle bounds = shape.getBounds();
            x = (float)(bounds.x + Math.random() * bounds.width);
            y = (float)(bounds.y + Math.random() * bounds.height);
        } while (!shape.contains(x, y));

        return new PVector(x, y);
    }
}
