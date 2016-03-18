/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript;

/**
 * Immutable representation of a Vector. So all methods 
 * return a new Vector object.
 * 
 * @author Peter Lager
 *
 */
public final class Vector {
	
	public static Vector ZERO_VECTOR = new Vector();
	public static Vector X_VECTOR = new Vector(1,0,0);
	public static Vector Y_VECTOR = new Vector(0,1,0);
	public static Vector Z_VECTOR = new Vector(0,0,1);
	
	public final double x;
	public final double y;
	public final double z;

	/**
	 * Create the zero vector [0,0,0]
	 */
	public Vector(){
		x = y = z = 0;
	}

	/**
	 * Create the vector [x,y,z]
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Create a copy of the vector v
	 */
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	/**
	 * v1 + v2
	 */
	public static Vector add(Vector v1, Vector v2) {
		return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	/**
	 * v1 - v2
	 */
	public static Vector sub(Vector v1, Vector v2) {
		return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	/**
	 * v1 * v2
	 */
	public static Vector mult(Vector v1, Vector v2) {
		return new Vector(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
	}

	/**
	 * v1 * scalar
	 */
	public static Vector mult(Vector v1, double s) {
		return new Vector(v1.x * s, v1.y * s, v1.z * s);
	}

	/**
	 * v1 / scalar
	 */
	public static Vector div(Vector v1, double s) {
		return new Vector(v1.x / s, v1.y / s, v1.z / s);
	}

	/**
	 * this vector + v
	 */
	public Vector add(Vector v){
		return new Vector(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * this vector - v
	 */
	public Vector sub(Vector v){
		return new Vector(x - v.x, y - v.y, z - v.z);
	}

	/**
	 * this vector / scalar
	 */
	public Vector div(double s){
		return new Vector(x/s, y/s, z/s);
	}

	
	/**
	 * this vector * scalar
	 */
	public Vector mult(double s){
		return new Vector(x*s, y*s, z*s);
	}

	/**
	 * the magnitude (size) of this vector
	 */
	public double mag(){
		return Math.sqrt(x*x + y*y + z*z);
	}

	/**
	 * the normalized version of this vector
	 */
	public Vector norm(){
		double mag = Math.sqrt(x*x + y*y + z*z);
		return new Vector(x/mag, y/mag, z/mag);
	}

	/**
	 * The dot product between v1 and  v2
	 */
	public static double dot(Vector v1, Vector v2) {
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}

	/**
	 * The cross product between v1 and  v2
	 */
	public static Vector cross(Vector v1, Vector v2) {
		double crossX = v1.y * v2.z - v2.y * v1.z;
		double crossY = v1.z * v2.x - v2.z * v1.x;
		double crossZ = v1.x * v2.y - v2.x * v1.y;
		return new Vector(crossX, crossY, crossZ);
	}

	/**
	 * Linear interpretation between 2 vectors. The parametric 
	 * variable will be in the range >=0 and <=1
	 * 
	 */
	public static Vector lerp(Vector v1, Vector v2, double t) {
		double lerpX = v1.x + (v2.x - v1.x) * t;
		double lerpY = v1.y + (v2.y - v1.y) * t;
		double lerpZ = v1.z + (v2.z - v1.z) * t;
		return new Vector(lerpX, lerpY, lerpZ);
	}

	/**
	 * Get the angle between two vectors in radians. If either vector
	 * is of size 0 (zero) then it returns zero.
	 */
	public static double angleBetween(Vector origin, Vector v1, Vector v2) {
		if(v1.equals(ZERO_VECTOR) || v2.equals(ZERO_VECTOR))
			return 0.0;
		double v1x = v1.x, v1y = v1.y, v1z = v1.z; 
		double v2x = v2.x, v2y = v2.y, v2z = v2.z; 
		if(!origin.equals(ZERO_VECTOR)){
			v1x -= origin.x; v1y -= origin.y; v1z -= origin.z;
			v2x -= origin.x; v2y -= origin.y; v2z -= origin.z;
		}
		double dot = v1x * v2x + v1y * v2y + v1z * v2z;
		double v1mag = Math.sqrt(v1x * v1x + v1y * v1y + v1z * v1z);
		double v2mag = Math.sqrt(v2x * v2x + v2y * v2y + v2z * v2z);
		if(v1mag == 0 || v2mag == 0) return 0;
		// This should be a number between -1 and 1, since it's "normalized"
		double amt = dot / (v1mag * v2mag);
		// But if it's not due to rounding error, then we need to fix it
		if (amt <= -1) {
			return Math.PI;
		} else if (amt >= 1) {
			return 0;
		}
		return Math.acos(amt);
	}
	
	/**
	 * Calculate the hashcode based on all 3 coordinate values
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Compare 2 vectors for equality
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
	
	
	@Override
	public String toString(){
		return "[" + x + ", " + y + ", " + z +"]";
	}
}
