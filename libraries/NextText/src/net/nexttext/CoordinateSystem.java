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

import java.awt.Polygon;

import processing.core.PVector;

/**
 * A CoordinateSystem describe a set of three Axes positioned at some arbitrary 
 * origin in space.
 * 
 * <pre>
 *  (origin)_ _ _ _ x
 *    		|\
 *    		| \
 *    		|  \z
 *    		y
 * </pre>
 */
/* $Id$ */
public class CoordinateSystem {
   
    private PVector origin;
    private Axes axes;
    
    /**
     * Creates a "default" CoordinateSystem where the origin is (0,0,0) and each
     * axes is a unit vector.
     */
    public CoordinateSystem() {       
        this.origin = new PVector();
        this.axes = new Axes();
    }
    
    /**
     * Creates a CoordinateSystem with the specified origin and rotation
     */
    public CoordinateSystem( PVector origin, float rotation ) {

        this.origin = origin;        
        this.axes = new Axes();
        axes.rotate( rotation );        
    }
    
    /**
     * Creates a CoordinateSystem with the specified origin and rotation, then 
     * transforms it by the specified parent system.
     * 
     * XXXBUG This description is not explicit enough.  
     */
    public CoordinateSystem( PVector origin, float rotation, CoordinateSystem parentSystem ) {
        
        this.origin = origin;
        
        this.axes = new Axes();
        axes.rotate( rotation );
        
        // transform this system by the parent system, ie the origin and
        // axes are now expressed in terms of the parent system.
        
        this.origin = parentSystem.transform( this.origin );
        this.axes = parentSystem.axes.transform( this.axes );    
    }

    /**
     * Returns a copy of the origin vector.
     */
    public PVector getOrigin() {
        return new PVector(origin.x, origin.y, origin.z);
    }
    
    /**     
     * Takes a vector local to this coordinate system and returns an equivalent
     * vector transformed "out of" this system such that the vector is now 
     * relative to the parent system.
     * 
     * <p>Transforms a vector local to this coordinate system by aligning it with
     * this system's axes and translating by this system's origin. </p>  
     * 
     * @param inV the input vector; the vector will remain unchanged.
     * @return the equivalent vector relative to the parent system.  
     */
    public PVector transform( PVector inV ) {
        
    	PVector outV = axes.transform( inV );        
        outV.add( origin );
        return outV;        
    }
    
    /**
     * Transforms a polygon out of this coordinate system.
     *
     * <p>Polygons are 2D, so the transformation is done with the assumption
     * that all Z values are 0.  </p>
     *
     * @return a new Polygon object.
     */
    public Polygon transform(Polygon inPoly) {
        Polygon outPoly = new Polygon();
        for (int i = 0; i < inPoly.npoints; i++) {
            float x = axes.transformX(inPoly.xpoints[i], inPoly.ypoints[i], 0);
            float y = axes.transformY(inPoly.xpoints[i], inPoly.ypoints[i], 0);
            outPoly.addPoint((int)(x + origin.x), (int)(y + origin.y));
        }
        return outPoly;
    }

    /**
     * Transforms a vector from the parent system such that it is now relative to
     * this system.
     * 
     * @param inV the input vector; the vector will remain unchanged.
     * @return the transformed vector
     */
    public PVector transformInto( PVector inV ) {
        
    	PVector outV = new PVector( inV.x, inV.y, inV.z );
        outV.sub( this.origin );       
        outV = axes.transformInto( outV ); 
        return outV;
    }
    
    public String toString() {
        return "Origin: " + this.origin + "\n" + axes.toString();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    
    /**
     * This inner class represents the X/Y/Z axes as one unit. 
     */
    class Axes {
        
    	PVector xAxis;
    	PVector yAxis;
    	PVector zAxis;
        
        /**
         * Creates default Axes using right-handed coordinates.
         */
        public Axes() {            
            // xAxis is pointing left
            this.xAxis = new PVector(1.0f , 0.0f, 0.0f);
            // yAxis is pointing downwards            
            this.yAxis = new PVector(0.0f , 1.0f, 0.0f);
            // positive Z values go "inside" the screen             
            this.zAxis = new PVector(0.0f , 0.0f, 1.0f);
        }
        
        public Axes( PVector xAxis, PVector yAxis, PVector zAxis ) {            
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            this.zAxis = zAxis;
        }
        
        /**
         * 2D Rotation (ie: around the Z-axis). 
         * 
         * <p>Rotates both xAxis and yAxis by the same amount</p>.
         */
        public void rotate( float radians ) {
        	// because the screen coordinate are mirrored along the X axis (ie: a 
            // negative Y means going out of the screen), we must first negate angle
            // to get clockwise rotation.  
            // the result will be a rotation that is looks clockwise on screen.
            radians = -radians;
            
            // rotate the x axis
            xAxis.set(xAxis.x * (float)Math.cos(radians) + xAxis.y * (float)Math.sin(radians),
            		  - xAxis.x * (float)Math.sin(radians) + xAxis.y * (float)Math.cos(radians),
            		  0);
            
            // rotate the y axis
            yAxis.set(yAxis.x * (float)Math.cos(radians) + yAxis.y * (float)Math.sin(radians),
          		  - yAxis.x * (float)Math.sin(radians) + yAxis.y * (float)Math.cos(radians),
          		  0);
        }
        
        /**
         * Outbound vector transformation.
         */
        public PVector transform( PVector inV ) {
            return new PVector( (inV.x*xAxis.x + inV.y*yAxis.x + inV.z*zAxis.x),
    							(inV.x*xAxis.y + inV.y*yAxis.y + inV.z*zAxis.y),
    							(inV.x*xAxis.z + inV.y*yAxis.z + inV.z*zAxis.z) );        
        }
        
        // Perform outbound transformations without using vector objects.
        // These methods save the additional overhead of creating multiple
        // Vector3 objects.  They are used in the calculation of bounding
        // polygons, which is done quite often.  A separate method is used for
        // each axis because methods can only have a single return value.
        float transformX(float x, float y, float z) {
            return (x * xAxis.x + y * yAxis.x + z * zAxis.x);
        }

        float transformY(float x, float y, float z) {
            return (x * xAxis.y + y * yAxis.y + z * zAxis.y);
        }

        float transformZ(float x, float y, float z) {
            return (x * xAxis.z + y * yAxis.z + z * zAxis.z);
        }

        /**
         * Inbound vector transformation.
         */
        public PVector transformInto( PVector inV ) {
            return new PVector( inV.dot(xAxis), 
                    			inV.dot(yAxis), 
                    			inV.dot(zAxis) );        
        }
        
        /**
         * Axes to Axes outbound transform.
         */
        public Axes transform( Axes axes ) {            
        	PVector xA = axes.transform( xAxis );
        	PVector yA = axes.transform( yAxis );
        	PVector zA = axes.transform( zAxis );
            return new Axes(xA, yA, zA);
        }
        
        /**
         * Axes to Axes inbound transform.
         */        
        public Axes transformInto( Axes axes ) {           
        	PVector xA = axes.transformInto( xAxis );
        	PVector yA = axes.transformInto( yAxis );
        	PVector zA = axes.transformInto( zAxis );
            return new Axes(xA, yA, zA);
        }
        
        public String toString() {
            return "X axis: " + xAxis + "\n" +
            	   "Y axis: " + yAxis + "\n" +
            	   "Z axis: " + zAxis;                	
        }
    }   
}
