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

package net.silentlycrashing.gestures;

import java.awt.*;
import net.silentlycrashing.util.*;
import processing.core.*;

/**
 * Listens for a matching gesture after the movement is completed.
 */
/* $Id: PostGestureListener.java 31 2008-11-12 16:01:48Z prisonerjohn $ */
public class PostGestureListener extends GestureListener {
	protected String activePattern;
	
	/**
	 * Builds a PostGestureListener covering the entire canvas.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 * @param pattern the move pattern to match for the Listener to be active
	 */
	public PostGestureListener(PApplet parent, GestureAnalyzer analyzer, String pattern) {
		this(parent, analyzer, pattern, new Rectangle(0, 0, parent.width, parent.height));
	}
	
	/**
	 * Builds a bounded PostGestureListener.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 * @param pattern the move pattern to match for the Listener to be active
	 * @param x the x-coordinate of the bounding Rectangle
	 * @param y the y-coordinate of the bounding Rectangle
	 * @param w the width of the bounding Rectangle
	 * @param h the height of the bounding Rectangle
	 */
	public PostGestureListener(PApplet parent, GestureAnalyzer analyzer, String pattern, int x, int y, int w, int h) {
		this(parent, analyzer, pattern, new Rectangle(x, y, w, h));
	}
	
	/**
	 * Builds a bounded PostGestureListener.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 * @param pattern the move pattern to match for the Listener to be active
	 * @param bounds the bounding Rectangle
	 */
	public PostGestureListener(PApplet parent, GestureAnalyzer analyzer, String pattern, Rectangle bounds) {
		super(parent, analyzer, bounds);
		activePattern = pattern;
	}

	/** 
	 * Checks that the gesture is starting in the correct bounds and sets the start point.
	 * 
	 * @param pt the first PointInTime of the gesture
	 */
	public void startListening(PointInTime pt) {
		reset();
		super.startListening(pt);
	}
	
	/** 
	 * Does nothing. This Listener waits until the gesture is complete to analyze it.
	 * 
	 * @param pt the current PointInTime of the gesture (unused)
	 */
	public void keepListening(PointInTime pt) {}
	
	/** 
	 * Checks if the regex pattern is matched and sets the Listener as active or not.
	 * 
	 * @param pt the last PointInTime of the gesture
	 */
	public void stopListening(PointInTime pt) {
		if (inBounds) {
			if (ga.matches(activePattern)) {
				activate();
			} else {
				deactivate();
			}
		}
	}
	
	/**
	 * Resets the GesureListener.
	 * <P>The offAction should be calling this when it is complete.</P>
	 */ 
	public void reset() {
		if (inBounds && active) {
			startPoint = null;
			deactivate();
		}
	}
}
