package net.nexttext.property;

import javax.management.ValueExp;

import processing.core.PVector;

/**
 * A PVector property of a TextObject or a Behaviour.
 * 
 * <p>Note that defensive copies of the current value and and original value of the
 * properties are always created when getting or setting the property.</p>  
 * 
 * <p>Also note that every wrapper method around vector arithmetic operations 
 * will fire a property change event.   Maybe we want to consider removing these
 * wrappers to lower the event overhead. </p>
 * 
 * @see PVector
 */
public class PVectorProperty extends Property {

	private PVector	original;
	private PVector value; 
	
	/**
	 * Constructor.  Creates a new PVectorProperty based on the object passed
	 * as a parameter.  The current and original values for that property will
	 * both be a copy of the parameter value.
	 */
	public PVectorProperty ( PVector value ) {
		this(value.x, value.y, value.z);
	}
	
	/**
	 * Creates a PVectorProperty from 3 floats.
	 */
	public PVectorProperty(float x, float y, float z) {
		original = new PVector (x, y, z);
		value = new PVector(x, y, z);		
	}

	/**
	 * Creates a PVectorProperty from 2 float, using 0 as the z coordinate.
	 */
	public PVectorProperty(float x, float y) {
        this(x, y, 0);
	}
	
    /**
     * Construct a property with specified original and future values.
     *
     * <p>The provided vectors are copied.  This function is used for the
     * special purpose of calculating an absolute position from the natively
     * stored relative positions.  </p>
     */
	public PVectorProperty(PVector original, PVector value ) {
		this.original = new PVector(original.x, original.y, original.z);
		this.value = new PVector(value.x, value.y, value.z);		
	}
	
	/**
	 * Returns a copy of the original value of this property 
	 */
	public PVector getOriginal() { 
        return new PVector( original.x, original.y, original.z );
	}
	
	/**
	 * Set the original value of the property.
	 */
	public void setOriginal( PVector newValue ) {
		original.x = newValue.x;
		original.y = newValue.y;
		original.z = newValue.z;	
	    firePropertyChangeEvent();
	}
	
	/**
	 * Returns the value of this property.
     *
     * @return A copy of the value of this property.
	 */
	public PVector get() {
	    return new PVector( value.x, value.y, value.z );
	}
	
	/**
	 * Sets the value of this property. The object passed as a newValue will 
	 * be copied before it is assigned to the property's value. 
	 */
	public void set( PVector newValue ) {
	    value.x = newValue.x;
	    value.y = newValue.y;
	    value.z = newValue.z;
	    firePropertyChangeEvent();
	}
	
    /**
     * Replaces the value of this property by its original value.
     */
    public void reset() {
        value = new PVector( original.x, original.y, original.z );
        firePropertyChangeEvent();
    }

    public float getX() {
        return value.x;
    }

    public float getY() {
        return value.y;
    }

    public float getZ() {
        return value.z;
    }

   	/** 
   	 * Wrapper around vector addition of Vector3 class
   	 */
   	public void add( PVector v1 ) {   		
   		value.add(v1);
   		firePropertyChangeEvent();
   	}
   	
   	/**
   	 * Wrapper around vector substraction of Vector3 class
   	 */
   	public void sub( PVector v1 ) {   		
   		value.sub(v1);
   		firePropertyChangeEvent();
   	}
   	
   	/**
   	 * Wrapper around vector cross product of Vector3 class
   	 */
   	public void cross( PVector v1 ) {   	 
   		value.cross(v1);	
   		firePropertyChangeEvent();
   	}

   	/**
   	 * Wrapper around vector matrix product of Vector3 class
   	 */
   	public void matrix( PVector v1 ) {   	 
        value.x = value.x * v1.x;
        value.y = value.y * v1.y;
        value.z = value.z * v1.z;
   		firePropertyChangeEvent();
   	}

   	/**
   	 * Wrapper around vector scalar product of Vector3 class
   	 */
   	public void scalar( float s ) {   	
   		value.mult(s);	
   		firePropertyChangeEvent();
   	}
   	
   	/**
   	 * Wrapper around vector dot product of Vector3 class
   	 */
   	public float dot( PVector v1 ) {
   		return value.dot(v1);	   		 
   	}
   	
   	/**
   	 * Wrapper around vector normalization of Vector3 class
   	 */
   	public void normalize() {   		
   		value.normalize();	
   		firePropertyChangeEvent();
   	}
	 
   	/**
   	 * Wrapper around vector rotation of Vector3 class
   	 */
   	public void rotate(float angle) {   		 
        // because the screen coordinate are mirrored along the X axis (ie: a 
        // negative Y means going out of the screen), we must first negate angle
        // to get clockwise rotation.  
        // the result will be a rotation that is looks clockwise on screen.
        angle = -angle;
        
        // rotate around the Z axis
        value.x = value.x * (float)Math.cos(angle) + value.y * (float)Math.sin(angle);
        value.y = - value.x * (float)Math.sin(angle) + value.y * (float)Math.cos(angle);
   	    
        firePropertyChangeEvent();
   	}

	/**
	 * Wrapper around toString() function of Vector3 class.
     */
    public String toString() {
        return "(" + original.toString() +
            ", " + value.toString() + ")";
	}

    // New Vector3 objects are created in case someone misuses the
    // Vector3Property by modifying the internal objects.
    public PVectorProperty clone() {
    	PVectorProperty that = (PVectorProperty) super.clone();
        that.original = new PVector(original.x, original.y, original.z);
        that.value = new PVector(value.x, value.y, value.z);
        return that;
    }	
}
