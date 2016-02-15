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

/*
 * This file is used to tesselate glyphs and is taken directly from the
 * jMonkeyEngine library.
 * 
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.nexttext.renderer.util;

import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;

/**
 * ClosedPolygon
 *
 * Created on 23. April 2006, 18:48
 * @author Pirx
 */
public class ClosedPolygon
{
	/** List of points forming the polygon. */
	private List<PVector> points = new ArrayList<PVector>();

	/**
	 * Add a point to the polygon.
	 * @param point
	 */
	public void addPoint(PVector point)
	{
		if(points.size() > 0)
		{
			PVector lastpoint = points.get(points.size() - 1);
			//if(lastpoint.equals(point))
			if((lastpoint.x == point.x) && (lastpoint.y == point.y))
			{
				//logger.info("Skipping duplicate point.");
				return;
			}
		}
		if(points.size() > 1)
		{
			// Check they are not on a straight line
			PVector p_1 = points.get(points.size()-2);
			PVector p_2 = points.get(points.size()-1);
			
			PVector v1 = new PVector(p_2.x, p_2.y, p_2.z);
			v1.sub(p_1);
			v1.normalize();
			
			PVector v2 = new PVector(point.x, point.y, point.z);
			v2.sub(p_2);
			v2.normalize();
			
			if(v1.equals(v2))
			{
				// Same direction, straight line, remove the last one in the vector
				//logger.info("REMOVING THE LAST ONE, TO AVOID STRAIGHT LINES");
				points.remove(points.size()-1);
			}
		}
		points.add(point);
	}

	/**
	 * Close the polygon.
	 */
	public void close()
	{
		if(points.size() > 3)
		{
			PVector first = points.get(0);
			PVector last = points.get(points.size()-1);
			//if(points.get(0).equals(points.get(points.size() - 1)))
			if ((first.x == last.x) && (first.y == last.y))
			{
				//logger.info("Removing last, duplicate point.");
				points.remove(points.size() - 1);
			}
		}
	}

	/**
	 * Get the list of points.
	 * @return the list of points
	 */
	public List<PVector> getPoints()
	{
		return points;
	}
	
	/**
	 * Check if the polygon is a hole.
	 * @return true if it is a hole, false if it is a filled shape
	 */
	public boolean isHole()
	{
		int size = points.size();
		int rightMostPoint = 0;
		for (int i = 0; i < size; i++)
		{
			if(points.get(i).x > points.get(rightMostPoint).x)
				rightMostPoint = i;
		}
		// Now we just need to see if the turn is right/left
		{
			PVector v1 = points.get((rightMostPoint - 1 + size) % size);
			PVector v2 = points.get(rightMostPoint);
			PVector v = points.get((rightMostPoint + 1) % size);
			double turnang = (v2.x - v1.x) * (v.y - v1.y) - (v.x - v1.x) * (v2.y - v1.y);
			
			//logger.info("turnang:"+turnang);
			return turnang > 0;
		}
	}

	/**
	 * Distance helper class.
	 */
	public static class Distance
	{
		public int inIndex;
		public int outIndex;
		public float sqrDist;

		public Distance(int inIndex, int outIndex, float sqrDist)
		{
			this.inIndex = inIndex;
			this.outIndex = outIndex;
			this.sqrDist = sqrDist;
		}
	}
}
