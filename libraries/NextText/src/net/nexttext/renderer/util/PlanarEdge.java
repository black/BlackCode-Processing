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

import net.nexttext.FastMath;
import net.nexttext.GeometricException;

/**
 * PlanarEdge. 
 */
public class PlanarEdge
{
	private PlanarVertex orig;
	private PlanarEdge  twin;
	private PlanarEdge  next;
	private PlanarEdge  prev;
	double angle = -1;
	boolean realedge;
	
	/**
	 * Constructor.
	 * @param orig origin
	 * @param realedge true if it is real
	 */
	PlanarEdge(PlanarVertex orig, boolean realedge)
	{
		this.orig = orig;
		this.realedge = realedge;
	}
	
	/**
	 * Check if the edge is real.
	 * @return true if real, false if not.
	 */
	public boolean isRealEdge()
	{
		return realedge;
	}
	
	/**
	 * Get the origin.
	 * @return the origin.
	 */
	public PlanarVertex getOrigin()
	{
		return orig;
	}
	
	/**
	 * Calculated the angle from this edge to the given edge (counter clockwise), the result is in the interval [0;2*PI).
	 * 
	 * @param edge
	 * @return the angle
	 */
	public double angleCounterClockWise(PlanarEdge edge)
	{
		if(this == edge)
			throw new RuntimeException("You are trying to find the angle after adding an edge...");
		//float myangle = FastMath.atan2(getDX(), getDY());
		double e_angle = edge.getAngle(); //FastMath.atan2(edge.getDX(), edge.getDY());
		while(e_angle < getAngle())
			e_angle += FastMath.TWO_PI;
		while(e_angle > getAngle()+FastMath.TWO_PI)
			e_angle -= FastMath.TWO_PI;
		if(e_angle == getAngle())
		{
			// We have the same angle, then the unreal edges are on the outer rim, hence the angle is 2 PI and not 0,  
			if(!isRealEdge())
				e_angle += FastMath.TWO_PI;
		}
		return e_angle - getAngle();
	}
	
	/**
	 * Get the X distance.
	 * @return x distance
	 */
	double getDX()
	{
		return twin.orig.point.x - orig.point.x;
	}
	
	/**
	 * Get the Y distance.
	 * @return y distance.
	 */
	double getDY()
	{
		return twin.orig.point.y - orig.point.y;
	}
	
	/**
	 * Get the angle.
	 * @return the angle.
	 */
	double getAngle()
	{
		return angle;
	}
	
	/**
	 * Get the destination vertex.
	 * @return the destination vertex.
	 */
	public PlanarVertex getDestination()
	{
		return twin.orig;
	}
	
	/**
	 * Get the next edge.
	 * @return the next edge.
	 */
	PlanarEdge getNext()
	{
		return next;
	}
	
	/**
	 * Set the next edge.
	 * @param next
	 */
	void setNext(PlanarEdge next)
	{
		this.next = next;
		next.prev = this;
	}

	/**
	 * Get the previous edge.
	 * @return the previous edge
	 */
	PlanarEdge getPrev()
	{
		return prev;
	}
	
	/**
	 * Get the twin edge.
	 * @return the twin edge
	 */
	PlanarEdge getTwin()
	{
		return twin;
	}
	
	/**
	 * Set the twin edge.
	 * @param twin
	 */
	void setTwin(PlanarEdge  twin)
	{
		this.twin = twin;
		twin.twin = this;
		
		angle = FastMath.atan2(getDY(), getDX());
		twin.angle = FastMath.atan2(twin.getDY(), twin.getDX());
		
		float limit = FastMath.FLT_EPSILON*4;
		double anglediff = FastMath.abs(angleCounterClockWise(twin) - FastMath.PI);
		if(anglediff > limit)
		{
			throw new GeometricException("Two twins do not have opposite angles: "+angleCounterClockWise(twin)+" != "+FastMath.PI+" : ("+anglediff+" > "+limit+")");
		}
	}
}
