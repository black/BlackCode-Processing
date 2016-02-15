/*
  This file is part of the ezjlibs project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezjlibs is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezjlibs is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezjlibs.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.util;

import java.awt.*;

/**
 * A Point with a time stamp (in frames).
 */
/* $Id: PointInTime.java 29 2008-03-13 13:06:35Z prisonerjohn $ */
public class PointInTime extends Point {
	private int birthFrame;
	
	/**
	 * Builds a PointInTime.
	 * 
	 * @param x the x-coord
	 * @param y the y-coord
	 * @param b the birth frame
	 */
	public PointInTime(int x, int y, int b) {
		this(new Point(x, y), b);
	}
	
	/**
	 * Builds a PointInTime.
	 * 
	 * @param pt the Point
	 * @param b the birth frame
	 */
	public PointInTime(Point pt, int b) {
		super(pt);
		this.birthFrame = b;
	}
	
	public int birthFrame() { return birthFrame; }
}
