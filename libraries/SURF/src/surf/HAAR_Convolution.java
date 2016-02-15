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
 * Generic interface for a class that calculates the convolution of an image
 * with an Haar Filter.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public interface HAAR_Convolution {

	/**
	 * Computes the response in x-direction and returns the value of the (x,y)
	 * point for a filter proportional to the interest point detected scale.
	 * 
	 * @param x
	 * @param y
	 * @param _scale
	 *            Detected Interest Point scale.
	 * @return float Haar Convolution value in the x-direction for the specified
	 *         Interest Point.
	 */
	public float makeConvolutionDx(int x, int y, float _scale);

	/**
	 * Computes the response in y-direction and returns the value of the (x,y)
	 * point for a filter proportional to the interest point detected scale.
	 * 
	 * @param x
	 * @param y
	 * @param _scale
	 *            Detected Interest Point scale.
	 * @return float Haar Convolution value in the y-direction for the specified
	 *         Interest Point.
	 */
	public float makeConvolutionDy(int x, int y, float _scale);

}
