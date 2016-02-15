package it.ppm.codebookmodel;

/**
 *@author Federico Bartoli
 *@author Mattia Masoni

*/
/**
 * This class represent the pixel'cordinate of the image.
 */

public class Point {

	private int x;
	private int y;
	
	/**
	 * Create a new object Point and set to zero x and y cordinates.
	 *
	 */
	public Point(){
		
		setX(0);
		setY(0);
		
	}
	/**
	 * Create a new object Point and set the cordinates.
	 * @param x x cordinate  of the new point
	 * @param y y cordinate  of the new point
	 */
	public Point(int x, int y){
		
		setX(x);
		setY(y);
	
	}
	/**
	 * 	Returns x cordinate of the point.
	 * @return x cordinate of the point.
	 */
	public int getX(){

		return x;
	
	}
	/**
	 * 	Returns y cordinate of the point.
	 * @return y cordinate of the point.
	 */
	public int getY(){

		return y;	
	
	}	
	/**
	 * Set value of x cordinate of the point. 
	 * @param x a new x cordinate of the point.	
	 */
	public void setX(int x){
	
		this.x = x;
	
	}
	/**
	 * Set value of y cordinate of the point. 
	 * @param y a new y cordinate of the point	
	 */
	public void setY(int y){
	
		this.y = y;
	
	}

}
