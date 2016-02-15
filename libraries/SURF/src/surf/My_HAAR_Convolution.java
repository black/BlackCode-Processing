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
 * A standard implementation of the HAAR_Convolution interface.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

/*
 *       -----------------------------------------------> x
 *       |       x1     x2    x3        x1          x3
 *       |     y1  -----------       y1  -----------
 *       |        |  B  |  W  |         |     W     |
 *       |        |     |     |         |     -1    |
 *       |        |  1  | -1  |      y2 |-----------|
 *       |        |     |     |         |     B     |
 *       |        |     |     |         |     1     |
 *       |     y3  -----------       y3  -----------
 *       |
 *       v
 *       y
 *           
 * Our Haar Wavelets approximation. The left filter computes the x-response, the right filter the y one.
 * Inside each region the wavelet value is specified.
 * Combined with the use of Integral Image, the Haar Convolution needs only 6 operations per filter.
 *
 */

public class My_HAAR_Convolution implements HAAR_Convolution {

	private Integral_Image integralImage;

	public My_HAAR_Convolution(Integral_Image integralImage) {
		this.integralImage = integralImage;
	}

	public float makeConvolutionDx(int x, int y, float _scale) {

		int x1, x2, x3, y1, y3;

		/*
		 * Calculation of points x1, x2, x3, y1, y2, y3. The center of the filter
		 * is at (x,y) and it is 2*_scale large.
		 */
		x1 = (int) (x - _scale);
		x2 = (int) x;
		x3 = (int) (x + _scale);
		y1 = (int) (y - _scale);
		y3 = (int) (y + _scale);

		return (integralImage.getIntegralSquare(x1, y1, x2, y3) - integralImage
				.getIntegralSquare(x2, y1, x3, y3));

	}

	public float makeConvolutionDy(int x, int y, float _scale) {

		int x1, x3, y1, y2, y3;

		/*
		 * Calculation of points x1, x2, x3, y1, y2, y3. The center of the filter
		 * is at (x,y) and it is 2*_scale large.
		 */
		x1 = (int) (x - _scale);
		x3 = (int) (x + _scale);
		y1 = (int) (y - _scale);
		y2 = (int) y;
		y3 = (int) (y + _scale);

		return (-integralImage.getIntegralSquare(x1, y1, x3, y2) + integralImage.getIntegralSquare(x1, y2, x3, y3));

	}

}
