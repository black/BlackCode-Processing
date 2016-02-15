/*
  Processing SURF, a Processing implementation of SURF: Speeded Up Robust Features,
  a novel scale - and rotation- invariant interest point detector and descriptor.
  
  (c) 2009
  
  Processing SURF is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  Processing SURF is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with Integral Histogram; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package surf;

/**
 * Generic interface for a class that converts an image to its integral version.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public interface Integral_Image {

	/**
	 * Given the coordinates of a point of the image, returns its integral
	 * value, ie the intensity of the rectangle whose vertices are the origin
	 * and (x, y).
	 * 
	 * @param x
	 * 			x-coordinate
	 * @param y
	 * 			y-coordinate
	 * @return The integral value of the given point.
	 */
	public float getIntegralValue(int x, int y);

	/**
	 * The method needs only A and D, i.e. the upper left and bottom right vertex coordinates.
	 * 
	 * @param xA
	 *            x-coordinate of vertex A.
	 * @param yA
	 *            y-coordinate of vertex A.
	 * @param xD
	 *            x-coordinate of vertex D.
	 * @param yD
	 *            y-coordinate of vertex D.
	 * @return The integral value of the square.
	 */
	public float getIntegralSquare(int xA, int yA, int xD, int yD);

}
