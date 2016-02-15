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

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A standard implementation of the Integral_Image interface. Creates the
 * integral image representation of supplied input image. Calculates pixel sums
 * over upright rectangular areas.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public class My_Integral_Image implements Integral_Image {

	private float[][] integralValues;
	private PImage img;

	/**
	 * Constructor of My_Integral_Image. The constructor receives the image and
	 * makes the conversion in its integral version. You allocate a matrix of
	 * integralValues of the same dimension of the given image. The Integral
	 * Image is full calculated as follows:
	 * <ul>
	 * 
	 * <li>At the first line, the direct sum of intensity (brightness) of pixels
	 * in each point represents the right value of the integral;</li>
	 * 
	 * <li>For each next line, the correct value is given by the sum of the
	 * current row and of the cell in the matrix of the same coordinates of the
	 * pixel immediately above the pixel being processed.</li>
	 * </ul>
	 */
	public My_Integral_Image(PImage img, PApplet parent) {

		this.img = img;
		integralValues = new float[img.height][img.width];
		for (int i = 0; i < img.height; i++) {

			float sumOfTheCurrentRow = 0;
			for (int j = 0; j < img.width; j++) {

				sumOfTheCurrentRow += parent.brightness(img.pixels[j + i
						* (img.width)]);
				if (i > 0) {

					integralValues[i][j] = integralValues[i - 1][j]
							+ sumOfTheCurrentRow;

				} else {

					integralValues[i][j] = sumOfTheCurrentRow;

				}
			}
		}
	}

	public float getIntegralValue(int x, int y) {

		if ((y < 0 || y >= img.height)
				|| (x < 0 || x >= img.width)) {

			return 0;

		} else {

			return integralValues[y][x];

		}

	}

	public float getIntegralSquare(int xA, int yA, int xD, int yD) {

		float A = getIntegralValue(xA, yA);
		float D = getIntegralValue(xD, yD);
		float B = getIntegralValue(xD, yA);
		float C = getIntegralValue(xA, yD);
		return A + D - (C + B);

	}

}
