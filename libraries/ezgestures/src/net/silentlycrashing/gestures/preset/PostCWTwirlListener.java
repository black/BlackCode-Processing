/*
  This file is part of the ezGestures project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezGestures is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezGestures is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezGestures.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.gestures.preset;

import java.awt.*;
import net.silentlycrashing.gestures.*;
import processing.core.*;

/**
 * Listens for a clockwise twirl after the movement is completed.
 */
/* $Id: PostCWTwirlListener.java 31 2008-11-12 16:01:48Z prisonerjohn $ */
public class PostCWTwirlListener extends PostGestureListener implements CWTwirl {
	/**
	 * Builds a PostCWTwirlListener covering the entire canvas.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 */
	public PostCWTwirlListener(PApplet parent, GestureAnalyzer analyzer) {
		this(parent, analyzer, new Rectangle(0, 0, parent.width, parent.height));
	}
	
	/**
	 * Builds a bounded PostCWTwirlListener.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 * @param x the x-coordinate of the bounding Rectangle
	 * @param y the y-coordinate of the bounding Rectangle
	 * @param w the width of the bounding Rectangle
	 * @param h the height of the bounding Rectangle
	 */
	public PostCWTwirlListener(PApplet parent, GestureAnalyzer analyzer, int x, int y, int w, int h) {
		this(parent, analyzer, new Rectangle(x, y, w, h));
	}
	
	/**
	 * Builds a bounded PostCWTwirlListener.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 * @param bounds the bounding Rectangle
	 */
	public PostCWTwirlListener(PApplet parent, GestureAnalyzer analyzer, Rectangle bounds) {
		super(parent, analyzer, CW_PATTERN, bounds);
	}
}
