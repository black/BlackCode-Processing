/** 
* Motion.java - a class for handling motion easier
* @author  Yonas Sandbæk
* @version 1.3 
*/ 

package seltar.motion;

import java.lang.Math;

public class Motion {
  public float Damp, Const, Spring; // Damping, Constant, Springiness
  public Point prevpos;
  public Point pos;
  public Vector v;
  
  
  
  // CONSTRUCTORS
  
  
	/** 
	* Constructor #1
	* @param X float X-position
	* @param Y float Y-position
	*/ 
  public Motion(float X, float Y) {
    prevpos = new Point(X,Y);
    pos = new Point(X,Y);
    v = new Vector(0,0);
	 	Damp = 0.95f;
	 	Const = 90.0f;
	 	Spring = 0.8f;
  }

	/** 
	* Constructor #2
	* @param X float X-position
	* @param Y float Y-position
	* @param VX float X-Velocity
	* @param VY float Y-Velocity
	*/ 
  public Motion(float X, float Y, float VX, float VY) {
    prevpos = new Point(X,Y);
    pos = new Point(X,Y);
    v = new Vector(new Point(VX,VY));
	 	Damp = 0.95f;
	 	Const = 90.0f;
	 	Spring = 0.8f;
  }
  
  
  
  // GET VARIABLES
  
	
	/** 
	* Gets Previous X-position
	* @return float Previous X
	*/ 
  public float getPX(){
  	// returns previous position x-coordinate
  	return prevpos.getX();
  }

	/** 
	* Gets Previous Y-position
	* @return float Previous Y
	*/ 
  public float getPY(){
  	// returns previous position y-coordinate
  	return prevpos.getY();
  }
  
  
	/** 
	* Gets current X-position
	* @return float Current X
	*/ 
  public float getX(){
  	// returns current position x-coordinate
  	return pos.getX();
  }

	/** 
	* Gets current Y-position
	* @return float Current Y
	*/ 
  public float getY(){
  	// returns current position y-coordinate
		return pos.getY();
  }
  
  
	/** 
	* Gets current velocity-X
	* @return float Velocity X
	*/ 
  public float getVX(){
  	// returns velocity x-coordinate
  	return v.getVX();
  }
  
	/** 
	* Gets current velocity-Y
	* @return float Velocity Y
	*/ 
  public float getVY(){
  	// returns velocity y-coordinate
  	return v.getVY();
  }

  
	/** 
	* Calculates angle from previous position to current position
	* @return float Angle
	*/ 
  public float calcAngle(){
  	// returns calculated angle from previous position to current position
  	return calcAngle(getPX(),getPY(),getX(),getY());
  }
  
	/** 
	* Gets angle from Vector v
	* @return float Angle
	*/ 
  public float getAngle(){
  	// returns angle
  	return v.getDirection();
  }

  
	/** 
	* Gets Constant divider
	* Used by followTo() & springTo()
	* @return float Constant
	*/ 
  public float getConstant(){
  	// returns constant
  	return Const;
  }
  
	/** 
	* Gets Damping
	* Used by springTo()
	* @return float Damping
	*/ 
  public float getDamping(){
  	// returns damping
  	return Damp;
  }
  
	/** 
	* Gets Springiness
	* Used by springTo()
	* @return float Springiness
	*/ 
  public float getSpring(){
  	// returns spring
  	return Spring;
  }
  
  
	/** 
	* Gets Distance from previous position to current position
	* @return float Distance
	*/ 
	public float getDistance(){
  	// returns distance from previous position to current position
  	return (float)Math.sqrt((getX()-getPX())*(getX()-getPX()) + (getY()-getPY())*(getY()-getPY()));
  }
  
	/** 
	* Gets Distance from current position to given position
	* @return float Distance
	*/ 
  public float getDistanceTo(float X, float Y){
  	// returns distance from current position to given position
  	return (float)Math.sqrt((X-getX())*(X-getX()) + (Y-getY())*(Y-getY()));
  }
  
  
    
  // SET VARIABLES
  
  
	/** 
	* Set current position to given Point
	* @param p Position
	*/ 
  public void setPos(Point p){
  	// set current position to given Point
  	pos.set(p.getX(),p.getY());
  }

	/** 
	* Set current position to given coordinate
	* @param X X-position
	* @param Y Y-position
	*/ 
  public void setPos(float X, float Y){
  	// set current position coordinate
  	pos.set(X, Y);
  }
  
	/** 
	* Set current X-position to given float
	* @param X X-position
	*/ 
  public void setX(float X){
  	// set current position x-coordinate
  	pos.set(X,pos.getY());
  }
  
	/** 
	* Set current Y-position to given float
	* @param Y Y-position
	*/ 
  public void setY(float Y){
  	// set current position y-coordinate
  	pos.set(pos.getX(),Y);
  }


	/** 
	* Set previous position to given Point
	* @param p Previous Position
	*/ 
  public void setPrevPos(Point p){
  	// set previous position Point
  	prevpos.set(p.getX(),p.getY());
  }
  
	/** 
	* Set previous position to given coordinate
	* @param X Previous X-Position
	* @param Y Previous Y-Position
	*/ 
  public void setPrevPos(float X, float Y){
  	// set previous position Point
  	prevpos.set(X, Y);
  }
  
	/** 
	* Set previous position to given float
	* @param PX Previous X-Position
	*/ 
  public void setPX(float PX){
  	// set previous position x-coordinate
  	prevpos.set(PX,prevpos.getY());
  }
  
	/** 
	* Set previous position to given float
	* @param PY Previous Y-Position
	*/ 
  public void setPY(float PY){
  	// set previous position y-coordinate
  	prevpos.set(prevpos.getX(),PY);
  }
  

	/** 
	* Set angle in Vector v
	* @param A Angle
	*/ 
  public void setAngle(float A){
  	// sets angle
  	v.setDirection(A);
  }
  

	/** 
	* Set velocity in Vector v
	* @param V Velocity
	*/ 
  public void setVelocity(float V){
  	// sets velocity
  	v.setVelocity(V);
  }
  
	/** 
	* Set velocity-coordinates in Vector v
	* @param VX X-Velocity
	* @param VY Y-Velocity
	*/ 
  public void setVelocity(float VX, float VY){
  	// sets velocity coordinate
  	v.setVelocity(VX,VY);
  }
  
	/** 
	* Set X-velocity in Vector v
	* @param VX X-Velocity
	*/ 
  public void setVX(float VX){
  	// sets velocity x
  	v.setVelocity(VX,v.getCoords().getY());
  }
  
	/** 
	* Set Y-velocity in Vector v
	* @param VY Y-Velocity
	*/ 
  public void setVY(float VY){
  	// sets velocity y
  	v.setVelocity(v.getCoords().getX(),VY);
  }
  
  
	/** 
	* Sets Constant divider
	* Used by followTo() & springTo()
	* @param C Constant
	*/ 
  public void setConstant(float C){
  	// sets constant variable
  	Const = C;
  }

	/** 
	* Sets Damping
	* Used by springTo()
	* @param D Damping
	*/ 
  public void setDamping(float D){ // (0-1)
  	// sets damping variable
  	Damp = D;
  }
  
	/** 
	* Sets Springiness
	* Used by springTo()
	* @param S Springiness
	*/ 
  public void setSpring(float S){ // (0-1)
  	// sets spring variable
  	Spring = S;
  }



  // CONSTRAIN
  
  
	/** 
	* Wraps Point pos and Point prevpos around given coordinates
	*/ 
  public void wrap(float x1, float y1, float x2, float y2)
  {
  	// wraps current position, previous position and resets velocity
  	float dx = x2-x1;
  	float dy = y2-y1;
  	if(getX()<x1){
  		 setPX(getPX()+dx);
  		 setX(getX()+dx);
  		 setVX(getX()-getPX());
  	}else if(getX()>x2){
  		 setPX(getPX()-dx);
  		 setX(getX()-dx);
  		 setVX(getX()-getPX());
  	}
  	
  	if(getY()<y1){
  		 setPY(getPY()+dy);
  		 setY(getY()+dy);
  		 setVY(getY()-getPY());
  	}else if(getY()>y2){
  		 setPY(getPY()-dy);
  		 setY(getY()-dy);
  		 setVY(getY()-getPY());
  	}
  }
  
	/** 
	* Constrain Point pos and Point prevpos to given coordinates
	*/ 
  public void constrain(float x1, float y1, float x2, float y2)
  {
  	// constrains current position, previous position
  	if(getX()<x1 || getX()>x2){
  		setX((getX() < x1) ? x1 : ((getX() > x2) ? x2 : getX()));
  		setPX(getX());
  	}
  	
  	if(getY()<y1 || getY()>y2){
  		setX((getY() < y1) ? y1 : ((getY() > y2) ? y2 : getY()));
  		setPY(getY());
  	}
  }
  
  
  
  // CALCULATIONS
  
  
	/** 
	* Calculates the angle between two given points
	* @return float Angle
	*/ 
  public float calcAngle(float x1, float y1, float x2, float y2){
  	// calculate angle between two points
  	float dx = x2 - x1;
		float dy = y2 - y1;
		
		float cx = 1.0f;
		float cy = 0.0f;
		
		return (float)(Math.acos(((dx*cx+dy*cy)/Math.sqrt(dx*dx+cy*cy))));
	}
  
  
  
  // MOTION
  
  
	/** 
	* Sets prevpos to pos and Moves pos according to Vector v (Velocity & Direction)
	*/ 
  public void move(){
  	// apply vector (velocity & direction) to position
  	setPrevPos(pos);
  	setPos(getX()+getVX(),getY()+getVY());
  }
  
	/** 
	* Sets Vector v Velocity & Direction
	* Moves in a given Angle with a given Speed
	*/ 
  public void moveDir(float Angle, float Speed){
  	// sets velocity to a given Angle with a given Speed
  	setVX((float)Math.sin(Angle)*Speed);
  	setVY((float)Math.cos(Angle)*Speed);
  }
  
	/** 
	* Sets Vector v Velocity & Direction
	* Move point towards given point dividing by Const
	*/ 
  public void followTo(float X, float Y){
  	// sets velocity to follow after a coordinate. uses Const as a divider
    setVX(((X-getX())/Const));
    setVY(((Y-getY())/Const));
  }
  
	/** 
	* Sets Vector v Velocity & Direction
	* Springs point towards given point dividing by Const, multiplied with Spring and Damp
	*/ 
  public void springTo(float X, float Y){
  	// sets velocity to follow after a coordinate. uses Const, Damp and Spring
    setVX(((X-getX())/Const)*Spring+getVX()*Damp);
    setVY(((Y-getY())/Const)*Spring+getVY()*Damp);
  }
} 