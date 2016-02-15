/** 
* Point.java - a small point class
* @author  Yonas Sandbæk > rewritten from a class by Ali Eslami (www.arkitus.com)
* @version 1.3 
*/ 

package seltar.motion;

import java.lang.Math;

public class Point {
  public float x, y;
  
	
	
	// CONSTRUCTORS
  
  
	/** 
	* Constructor 
	* Sets position to 0
	*/ 
  public Point()
  {
  	// sets coordinates to 0
    set(0,0);
  }
  
	/** 
	* Constructor 
	* Sets position to given coordinates
	*/ 
  public Point(float X, float Y)
  {
  	// sets coordinates
    set(X,Y);
  }
  
  
  
	// GET VARIABLES
	
	
	/** 
	* Gets X-coordinate
	*/ 
  public float getX()
  {
  	// returns x-coordinate
    return x;
  }
  
	/** 
	* Gets Y-coordinate
	*/ 
  public float getY()
  {
  	// returns y-coordinate
    return y;
  }
  
  
  
	// SET VARIABLES
	
	
	/** 
	* Sets coordinate to given Point
	* @param p New position
	*/ 
  public void set(Point p)
  {
  	// sets coordinates to given Point
    x = p.x;
    y = p.y;
  }
  
	/** 
	* Sets coordinate to given coordinate
	* @param X New X
	* @param Y New Y
	*/ 
  public void set(float X, float Y)
  {
  	// sets coordinates to given coordinates
    x = X;
    y = Y;
  }

	/** 
	* Sets X-coordinate to given X-coordinate
	* @param X New X
	*/ 
	public void setX(float X)
	{
  	// sets x-coordinates
		x = X;
	}
	
	/** 
	* Sets Y-coordinate to given Y-coordinate
	* @param Y New Y
	*/ 
	public void setY(float Y)
	{
  	// sets y-coordinates
		y = Y;
	}



	// MATH

	
	/** 
	* Add value to coordinates
	* @param k Add
	*/ 
  public Point add(float k)
  {
  	// adds a given variable to coordinates
  	return add(k,k);
  }

	/** 
	* Add values to given coordinate
	* @param k Add X
	* @param j Add Y
	*/ 
  public Point add(float k, float j)
  {
  	// adds two given variables to coordinates
    Point R = new Point();
    R.x = x+k;
    R.y = y+j;
    return R;
  }
  
	/** 
	* Multiply value to given coordinate
	* @param k Multiply
	*/ 
  public Point multiply(float k)
  {
  	// multiply a given variable to coordinates
  	return multiply(k,k);
  }
  
	/** 
	* Multiply values to given coordinate
	* @param k Multiply X
	* @param j Multiply Y
	*/ 
  public Point multiply(float k, float j)
  {
  	// multiply two given variables to coordinates
    Point R = new Point();
    R.x = x*k;
    R.y = y*j;
    return R;
  }
}