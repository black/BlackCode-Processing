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

/**
 * Generic interface for a Detector class.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public interface Detector {

	/**
	 * Detects all the Interest Points in a given image and then returns them as
	 * an ArrayList.
	 * 
	 * @return An ArrayList of Interest_Point
	 */
	public ArrayList<Interest_Point> generateInterestPoints();

}
