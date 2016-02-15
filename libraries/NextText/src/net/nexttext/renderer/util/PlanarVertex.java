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

import java.util.logging.Logger;

import processing.core.PVector;

/**
 * PlanarVertex. 
 */
public class PlanarVertex
{
//    private static final Logger logger = Logger.getLogger(PlanarVertex.class
//            .getName());
    
	int index;
	PVector point;
	private PlanarEdge arb_outgoing;
	
	/**
	 * Constructor
	 * @param i index
	 * @param p	point
	 */
	PlanarVertex(int i, PVector p)
	{
		this.index = i;
		this.point = p;
	}
	
	/**
	 * Add an outgoing edge.
	 * @param outedge
	 */
	void addOutgoingEdge(PlanarEdge outedge)
	{
		if(arb_outgoing == null)
		{
			// First one to leave the vertex
			arb_outgoing = outedge;
			outedge.getTwin().setNext(outedge);
			//outedge.prev = outedge.twin;
		}
		else
		{
			// Bind to it's neighbor at this vertex
			PlanarEdge neighbor = clockWiseOf(outedge);
			
			neighbor.getPrev().setNext(outedge);
			outedge.getTwin().setNext(neighbor);
		}
		
		// Bind to its neighbor at the destination vertex (only if it already has outgoing edges
		if(outedge.getTwin().getOrigin().arb_outgoing == null)
		{
			outedge.getTwin().getOrigin().arb_outgoing = outedge.getTwin();
			outedge.setNext(outedge.getTwin());
		}
		else
		{
			PlanarEdge neighbor = outedge.getTwin().getOrigin().clockWiseOf(outedge.getTwin());
			neighbor.getPrev().setNext(outedge.getTwin());
			outedge.setNext(neighbor);
		}
	}

	
	/**
	 * Returns the first outgoing edge clockwise of the given edge (can be the edge it self).
	 * 
	 * @param edge
	 * @return edge
	 */
	private PlanarEdge clockWiseOf(PlanarEdge edge)
	{
		// Find the
		PlanarEdge result = arb_outgoing;
		PlanarEdge next = result; 
		double angle = next.angleCounterClockWise(edge);
		do
		{
			if(next.getOrigin() != this)
				throw new RuntimeException("We get an edge that does not orginate from this vertex !!!");
			next = next.getTwin().getNext();
			double nangle = next.angleCounterClockWise(edge);
			
			// Always prefer the same type as 'edge.isRealEdge()'
			if(nangle == angle && result != next)
			{
				// If we have the same angle, we must get the one that is topologically most counter-clockwise
				if(next.getTwin().getNext() == result)
				{
					result = next;
				}
				else if(next.isRealEdge() && !result.isRealEdge())
				{
					result = next;
				}
				else if(result.getTwin().getNext() == next)
				{
					// ok, the result is good
				}
				else
				{
//					logger.warning("Error: (nangle == angle && "+result+" != "+next+")");
				}
			}
			else if(nangle < angle)
			{
				result = next;
				angle = nangle;
			}
		}
		while(next != arb_outgoing); // Only one round
		/*
		logger.info("\nEdge: "+FastMath.atan2(edge.getDX(), edge.getDY()));
		logger.info("Result: "+FastMath.atan2(result.getDX(), result.getDY()));
		logger.info("Angle: "+angle);
		*/
		return result;
	}
	
	/**
	 * Returns the edge from this vertex to the given, or null
	 * if none exists.
	 * 
	 * @param v end vertex
	 * @return the edge between passed vexter and this one, or null if none exists.
	 */
	PlanarEdge getEdge(PlanarVertex v)
	{
		if(arb_outgoing == null)
			return null;
		PlanarEdge next = arb_outgoing; 
		do
		{
			if(next.getTwin().getOrigin() == v)
				return next;
			next = next.getTwin().getNext();
		}
		while(next != arb_outgoing); // Only one round
		return null;
	}

	/**
	 * Get the vertex's index.
	 * @return index
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Get first edge.
	 * @return edge
	 */
	PlanarEdge getFirstEdge()
	{
		return arb_outgoing;
	}
	
	@Override
	public String toString()
	{
		return "[indx:"+index+",("+point.x+","+point.y+")]";
	}
	
	/**
	 * Get point.
	 * @return point
	 */
	public PVector getPoint()
	{
		return point;
	}
	
	/**
	 * Outpot edges.
	 */
	public void printEdges()
	{
		if(arb_outgoing == null)
		{
//			logger.info("I HAVE NOT EDGES !");
			return;
		}
		PlanarEdge next = arb_outgoing; 
		do
		{
//			logger.info("Edge:"+next);
			next = next.getTwin().getNext();
		}
		while(next != arb_outgoing); // Only one round
	}
}
