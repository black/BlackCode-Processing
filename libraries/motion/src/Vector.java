/** 
* Vector.java - a small vector class
* @author  Yonas Sandbæk > rewritten from a class by Ali Eslami (www.arkitus.com)
* @version 1.3 
*/ 

package seltar.motion;

import java.lang.Math;

public class Vector {
  float len;
  float dir;
  
  
  
	// CONSTRUCTORS

  
	/** 
	* Constructor 
	* Sets Length and Direction to 0
	*/ 
  public Vector()
  {
  	// sets Length and Direction to 0
    len = 0;
    dir = 0;
  }

	/** 
	* Constructor 
	* Sets Length and Direction to given variables
	*/ 
  public Vector(float L, float D)
  {
  	// sets Length and Direction
  	len = L;
  	dir = D;
  }
  
	/** 
	* Constructor 
	* Sets Length and Direction to given Point
	*/ 
  public Vector(Point P)
  {
  	// sets a vector from a given Point
    setVelocity(P.x,P.y);
  }

	
	
	// GET VARIABLES
	
  
	/** 
	* Gets Direction
	* @return float Direction
	*/ 
  public float getDirection()
  {
  	// returns direction
  	return dir;
  }

  
	/** 
	* Gets Velocity
	* @return float Velocity
	*/ 
  public float getVelocity()
  {
  	// returns velocity
  	return len;
  }

	/** 
	* Gets Velocity-X
	* @return float Velocity-X
	*/ 
	public float getVX()
	{
		// returns velocity x
  	return getCoords().x;	
	}
	
	/** 
	* Gets Velocity-Y
	* @return float Velocity-Y
	*/ 
	public float getVY()
	{
		// returns velocity y
		return getCoords().y;	
	}
	
	/** 
	* Gets Velocity-Coordinates
	* @return float Velocity-Coordinates
	*/ 
  public Point getCoords()
  {
		// returns velocity coordinate
    Point P = new Point();
    P.x = len*(float)Math.cos(dir);
    P.y = len*(float)Math.sin(dir);
    return P;
  }

	
	
	// SET VARIABLES
	
	
	/** 
	* Sets Direction (radians)
	* @param D Direction in radians
	*/ 
  public void setDirection(float D)
  {
  	// set direction
    dir = D;
  }


	/** 
	* Sets Velocity
	* @param L Velocity
	*/ 
  public void setVelocity(float L)
  {
  	// set velocity
    len = L;
  }

	/** 
	* Sets Velocity
	* @param X Velocity-X
	* @param Y Velocity-Y
	*/ 
  public void setVelocity(float X, float Y)
  {
  	// set a vector to a given coordinate
    len = (float)(Math.sqrt(Math.pow(X,2)+Math.pow(Y,2)));
    dir = (float)(Math.atan2(Y,X));
  }
	
	
	// MATH 
	
  
	/** 
	* Adds a Vector (Velocity & Direction) to Vector v
	* @param V Vector V
	*/ 
  public Vector addVector(Vector V)
  {
  	// add a vector
    Vector R = new Vector();
    R.setVelocity(getVX()+V.getVX(),getVY()+V.getVY());
    return R;
  }
  
	/** 
	* Multiply Velocity & Direction by given variable
	* @param k Multiplier
	*/ 
  public Vector multiply(float k)
  {
  	// multiplys points with a given variable
  	return multiply(k,k);
  }
  
	/** 
	* Multiply Velocity & Direction by given variable
	* @param k X-Multiplier
	* @param j Y-Multiplier
	*/ 
  public Vector multiply(float k, float j)
  {
  	// multiplys points with two given variables
    Vector R = new Vector();
    R.setVelocity(getVX()*k,getVY()*j);
    return R;
  }
    
	/** 
	* Returns the dotproduct of this Vector and given Vector V 
	*/ 
  public float dot(Vector V)
  {
  	// returns the dot-product
    return getVX()*V.getVX() + getVY()*V.getVY();
  }
  
	/** 
	* Returns the normal of this Vector
	*/ 
  public Vector normal()
  {
  	// returns the normal-product
    Vector R = new Vector(-getVY(), getVX());
    return R;
  }
}
