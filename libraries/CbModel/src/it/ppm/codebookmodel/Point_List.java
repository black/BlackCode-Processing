package it.ppm.codebookmodel;

import java.util.Vector;

/**
 *@author Federico Bartoli
 *@author Mattia Masoni
*/

/**
 * This class represent a list of Point's point
 */
public class Point_List {

	private Vector<Point> Points;
	
	/**
	 * 	Create a new object Point_List. 
	 *
	 */
	public Point_List(){
	
		Points = new Vector<Point>();
	
	}
	public void addPoint(int x, int y){
	
		Points.addElement(new Point(x,y));
	
	}
	/**
	 * Set the cordinates of point to a certain index of list.
	 * @param index index at which the specified opint is to be setted.
	 * @param x a new x cordinate of the point.
	 * @param y a new y cordinate if the point.
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public void setPointAt(int index, int x, int y)throws ArrayIndexOutOfBoundsException{
		
		setXAt(index, x);
		setYAt(index, y);
	
	}
	/**
	 * 	Deletes the point at the specified index.	
	 * @param index index at which the specified Point is to be deleted.
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public void removeElementAt(int index)throws ArrayIndexOutOfBoundsException{
		
		Points.removeElementAt(index);
	
	}
	/**
	 * Returns the object Point at the specified index in the list.
	 * @param index index at which the specified Point is to be returned.
	 * @return the Point at the specified index in the list  
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public Point getPointAt(int index)throws ArrayIndexOutOfBoundsException{

		return Points.elementAt(index);
	
	}
	/**
	 * 	Returns the x cordinate of the Point at the specified index in the list.
	 * @param index index at which the specified Point is to be returned.
	 * @return  the x cordinate of the Point at the specified index in the list.
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public int getXAt(int index)throws ArrayIndexOutOfBoundsException {
	
		return Points.elementAt(index).getX();
	
	}
	/**
	 * 	Returns the y cordinate of the Point at the specified index in the list.
	 * @param index index at which the specified Point is to be returned.
	 * @return the y cordinate of the Point at the specified index in the list.
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public int getYAt(int index)throws ArrayIndexOutOfBoundsException{
	
		return Points.elementAt(index).getY();
	
	}
	/**
	 * Set the x cordinate of the Point at the specified index in the list.
	 * @param index index at which the specified Point is to be returned.
	 * @param x a new x cordinate of the point.
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public void setXAt(int index, int x)throws ArrayIndexOutOfBoundsException{
	
		Points.elementAt(index).setX(x);	
	
	}
	/**
	 * Set the y cordinate of the Point at the specified index in the list.
	 * @param index index at which the specified Point is to be returned.
	 * @param y a new y cordinate of the point.
	 * @throws ArrayIndexOutOfBoundsException Exception that may appears if the index is not valid.
	 */
	public void setYAt(int index, int y)throws ArrayIndexOutOfBoundsException{
	
		Points.elementAt(index).setY(y);
	
	}
	/**
	 * Returns the number of point belonging too the list.
	 * @return the number of point belonging too the list.
	 */
	public int size(){
	
		return Points.size();
	
	}
	/**
	 * Delete all point from the list.
	 *
	 */
	public void reset(){
	
		Points=new Vector<Point>();
	
	}

	
}
