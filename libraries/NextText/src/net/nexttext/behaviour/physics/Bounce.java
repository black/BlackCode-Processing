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

import java.awt.Polygon;

import processing.core.PVector;

import net.nexttext.TextObject;
import net.nexttext.property.PVectorProperty;

/**
 * This action performs collision response on two objects by moving them apart
 * and reflects their velocity.
 */
/* $Id$ */
public class Bounce extends PhysicsAction {
    
    private float elasticity;
    private float absorbEnergy;
    
    /**
     * @param elasticity The elasticity of collision
     * @param absorbEnergy Objects absorb energy from each other if non zero
     */
    public Bounce( float elasticity, float absorbEnergy ) {        
        this.elasticity = elasticity;
        this.absorbEnergy = absorbEnergy;
    }
           
    /**
     * Performs collision response on toA and toB by pushing them apart an 
     * reflecting their velocity.
     * 
     * <p>Objects will not collide if one is a child of the other. </p>
     */    
    public ActionResult behave(TextObject toA, TextObject toB) {
        
        // make sure one is not the parent of the other
    	if ( toA.getParent() == toB || toB.getParent() == toA ) {
	 	    return new ActionResult(false, false, false);
		}
    	
    	// ** COLLISION DETECTION ** 
    	
    	// the polygons used in the collision response
    	Polygon A = toA.getBoundingPolygon();
    	Polygon B = toB.getBoundingPolygon();
        	
    	
    	// get the minimum translation vector between A and B.  
    	PVector mtd = getVectorToSeparateAFromB(A, B);
    	  
        if ( mtd.mag() == 0 ) {
            // objects do not intersect, so do nothing.
	 	    return new ActionResult(false, false, false);
        }
        
        // ** COLLISION RESPONSE **
       
        // see if B has a velocity property
        PVectorProperty velPropB = getVelocity(toB);
        
        // if it doesn't, then B is not affected by a Physics action.  In this
        // case, treat it as an unmovable object.
        if ( velPropB == null ) {
            // move A the full distance
            translate( getPosition(toA), mtd );
            // reflect the velocity of A using the inversed mtd
            mtd.mult(-1);
            reflectVelocity( getVelocity(toA), mtd );
        }
        // otherwise, have each object move half the distance and reflect 
        // their velocities.
        else {
            // have each object move half the distance
            mtd.mult(0.5f);  
            translate( getPosition(toA), mtd );
            
            // to reflect velocity, we need the inverse mtd, so reflect
            // B's velocity now
            reflectVelocity( velPropB, mtd );
            
            // inverse the mtd for toB          
            mtd.mult(-1);
            translate( getPosition(toB), mtd );
            
            // now that mtd has been inversed, we can reflect A's velocity.
            reflectVelocity( getVelocity(toA), mtd );            
        }           
        return new ActionResult(false, false, true);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods
    
    /**
     * Moves translates a Position property by the given distance
     */
    protected void translate( PVectorProperty posProp, PVector distance ) {       
    	PVector pos = posProp.get();
        pos.add( distance );
        posProp.set( pos );
    }
    
    /**
     * Reflects a Velocity property using the Minimum Translation Distance to
     * calculate the plane of collision.
     */
    protected void reflectVelocity( PVectorProperty velProp, PVector mtd ) {
        // the unit normal to the plane of collision corresponds to the 
        // normalized minimum translation distance.
    	PVector N = new PVector( mtd.x*-1, mtd.y*-1 );
        N.normalize();
    
        // get the velocity vector	        
        PVector V = velProp.get();
    	
        // Only reflect the velocity if the object is travelling
        // into the collision.
        if ((PVector.angleBetween(N, V)) > (Math.PI / 2)) {
            
            // reflected velocity formula:
            // V - ( (1 + elasticity) * N.V )N
            float dot = N.dot(V);
            dot *= 1 + elasticity;
            N.mult(dot);
            V.sub(N);
            
            // finally, update velocity
            velProp.set(V);
        }
    }    
    
    /**
     * Returns the smallest vector which pushes A away from B.
     *
     * <p>Even though odds are we'll be dealing mostly with boxes, this method 
     * supports arbitrary convex polygons. In order to do so, we need to test
     * with the axis perpendicular to every edge of each polygon. </p>
     *
     * <p>Note that it returns a zero vector if A and B do not intersect. </p>
     */
    protected PVector getVectorToSeparateAFromB( Polygon A, Polygon B ) {
    	
    	PVector fromA = getVectorToSeparateAFromBInner( A, B );
    	PVector fromB = getVectorToSeparateAFromBInner( B, A );
		
		// change vector fromB to push A
		fromB.mult(-1);
		
		// XXXBug:
		// There seems to be a bug in the getVectorToSeperateAFromBInner 
		// (see Bugzilla #55)
		// The code below is a temporary patch to catch those cases and return 
		// the non-zero vector out of fromA and fromB.  
		// This prevents objects from overlapping, however the collision 
		// response is often excessive and doesn't look right, due to the
		// fact that vector returned may not be the smallest push
		// vector.
		if ( fromA.mag() == 0 && fromB.mag() != 0 ) {
		    return fromB;
		}		
		if ( fromB.mag() == 0 && fromA.mag() != 0 ) {
		    return fromA;
		}
		   
	    return fromA.mag() < fromB.mag() ? fromA : fromB ;
    }
    
    /**
     * Returns the smallest vector which pushes A away from B and is perpendicular to
     * one of A's edges.
     */
    protected PVector getVectorToSeparateAFromBInner( Polygon A, Polygon B ) {

    	PVector smallestVector = null;
	    	    
	    int numEdgesA = A.npoints;
    	for ( int i=0; i <= numEdgesA ; i++) {
    		// find the vector formed by each edge by going from the current 
    		// vertex to the next.
    		int vX = A.xpoints[i%numEdgesA] - A.xpoints[ (i+1)%numEdgesA ];
    		int vY = A.ypoints[i%numEdgesA] - A.ypoints[ (i+1)%numEdgesA ];
    		// find the axis perpendicular to this edge
    		PVector axis = new PVector( -vY, vX, 0 );
    		
    		//ignore zero-length edge
    		if (axis.mag() == 0) continue;
    		
    		axis.normalize();

     		// see if this axis separates the polygons.  if it doesn't, this
     		// method will return a vector representing the intersection
     		// projected on that specific axis
    		PVector pushVector = AxisSeparatesPolygons( axis, A, B );
     		
     		if ( (smallestVector == null) || 
     			 (pushVector.mag() < smallestVector.mag()) ) {
	     		smallestVector = pushVector;
     		}     		     		
    	}
 		
    	if ( smallestVector == null ) smallestVector = new PVector();    	
    	return smallestVector;	    
    }
   
 		
    /**
     * Return a vector to move Polygon A away from Polygon B on the given axis,
     * or the zero vector if they don't intersect.
     */
    protected PVector AxisSeparatesPolygons( PVector axis, Polygon A, Polygon B ) {
    	
    	// project each polygon onto the axis
    	Interval intervalA = calculateInterval( axis, A );
     	Interval intervalB = calculateInterval( axis, B );
      	
        if (   (intervalA.min > intervalB.max)
            || (intervalB.min > intervalA.max)) {
            return new PVector();
        }

        // The vector to return is parallel to the axis unit vector,
        PVector ret = new PVector(axis.x, axis.y, axis.z);

        // and scaled by the overlap, with the correct sign.
        if (intervalA.max > intervalB.max) {
            ret.mult(intervalB.max - intervalA.min);
        } else {
            ret.mult(intervalB.min - intervalA.max);
        }
        return ret;
    }
    
    /**
     * Projects a Polygon P onto axis N and returns the min/max interval of the
     * projection. 
     */
    protected Interval calculateInterval( PVector axis, Polygon P ) {
    	
    	// for each vertex
    	int numEdges = P.npoints;
    	
     	Interval interval = new Interval(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
    
    	for ( int i=0; i < numEdges ; i++) {
    		
    		// project the vertex into the axis
    		float vX = P.xpoints[i];
    		float vY = P.ypoints[i];

    		PVector vertex = new PVector( vX, vY, 0 );
      		
      		float dot = axis.dot( vertex );
      		 
    	 	if ( dot < interval.min ) interval.min = dot;
    		else if ( dot > interval.max ) interval.max = dot;
    	}	
    	return interval;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Inner classes
    
    protected class Interval {
	 	
	    float min;
	    float max;
	    
	    Interval(float min, float max) {
		 	this.min = min;
		 	this.max = max;   
	    }	    
    }
    
}
