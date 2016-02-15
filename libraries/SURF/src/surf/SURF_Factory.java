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

import java.util.ArrayList;
import processing.core.PImage;

/**
 * Basic interface for a class that provides the right Detector and Descriptor
 * class.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public interface SURF_Factory {

	/**
	 * Create the correct Detector.
	 * 
	 * @return A Detector object.
	 */
	public Detector createDetector();

	/**
	 * Create the correct Descriptor.
	 * 
	 * @param interest_points
	 *            ArrayList of Interest Point on which the descriptor will
	 *            compute the descriptors.
	 * 
	 * @return A Descriptor object.
	 */
	public Descriptor createDescriptor(ArrayList<Interest_Point> interest_points);

	/**
	 * Getter for the Integral Image.
	 * 
	 * @return The Integral Image used by SURF.
	 */
	public Integral_Image getIntegralImage();

	/**
	 * Setter for the Integral Image.
	 * 
	 * @param img
	 *            PImage that is converted into Integral Image before it's
	 *            assigned to SURF.
	 */
	public void setIntegralImage(PImage img);

	/**
	 * Getter for the Balance Value.
	 * 
	 * @return The Balance Value. Its default value is 0.9.
	 */
	public float getBalanceValue();

	/**
	 * Setter for the Balance Value.
	 * 
	 * @param balanceValue
	 *            Value used to calculate the Laplacian of Gaussian response.
	 */
	public void setBalanceValue(float balanceValue);

	/**
	 * Getter for the threshold.
	 * 
	 * @return The threshold value.
	 */
	public float getThreshold();

	/**
	 * Setter for the threshold.
	 * 
	 * @param threshold
	 *            Value used to filter the Laplacian of Gaussian.
	 */
	public void setThreshold(float threshold);

	/**
	 * Getter for the octaves.
	 * 
	 * @return Number of octaves computed in SURF.
	 */
	public int getOctaves();

	/**
	 * Setter for the octaves.
	 * 
	 * @param octaves
	 *            Number of octaves that you want to compute.
	 */
	public void setOctaves(int octaves);

}
