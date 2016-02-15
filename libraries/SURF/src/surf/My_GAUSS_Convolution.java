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
 * A standard implementation of the GAUSS_Convolution interface.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

/* 
 * The filter Dxx moves in the picture. To compute the convolution just do 
 * 2*Area(BCFG)-Area(ABEF)-Area(CDGH) using the integral image. 
 * Synthetically E+D-(A+H)+3(B+G-(C+F)): we subtract the rectangle ADEH and sum 3 times 
 * the middle one. Required 8 image accesses at every step instead of filtersize * filtersize as usual.
 *
 *                            O---------------------->
 *                            |---------------------
 *                            |         0          |
 *                            A------B------C------D
 *                            | -1   |  +2  | -1   |
 *                            E------F------G------H 
 *                            |         0          |
 *                            |---------------------
 *                            |
 *                            v
 *                            
 * The filter Dxy moves in the picture. To compute the convolution just do 
 * Area(CDGH)-Area(ABEF)+Area(ILOP)-Area(MNQR) using the integral image. 
 * Synthetically B+E+C+H+I+P+N+Q-(A+F+D+G+L+O+M+R). Required 12 image accesses at every step instead of filtersize * filtersize as usual.
 * 
 *                            O---------------------->
 *                            |----------------------|
 *                            |  A------B  C------D  |
 *                            |  |  -1  |  |  +1  |  |
 *                            |  E------F  G------H  |
 *                            |                      |
 *                            |  I------L  M------N  |
 *                            |  |  +1  |  |  -1  |  |
 *                            |  O------P  Q------R  |
 *                            |----------------------|
 *                            |
 *                            v
 *                            
 * The filter Dyy moves in the picture. To compute the convolution just do 
 * 2*Area(CDEF)-Area(ABCD)-Area(EFGH) using the integral image. 
 * Synthetically B+G -(A+H)+3(C+F-(D+E)): we subtract the rectangle ABGH and sum 3 times 
 * the middle one. Required 8 image accesses at every step instead of filtersize * filtersize as usual.
 * 
 *                            O---------------------->
 *                            |------A------B------|
 *                            |      |  -1  |      |
 *                            |      C------D      |
 *                            |      |  +2  |      |
 *                            |      E------F      |
 *                            |      |  -1  |      |
 *                            |------G------H------|
 *                            |
 *                            v
 * 
 */

public class My_GAUSS_Convolution implements GAUSS_Convolution {

	private Integral_Image integralImage;

	public My_GAUSS_Convolution(Integral_Image integralImage) {
		this.integralImage = integralImage;
	}

	public float makeConvolutionDxx(int x, int y, int filtersize) {

		int x1, x2, x3, x4, y1, y2;
		int center, lobe_size1, lobe_size2;

		center = (filtersize - 1) / 2;
		lobe_size1 = filtersize / 3;
		lobe_size2 = 5 + 4 * (filtersize - 9) / 6;

		x1 = x - center;
		x4 = x + center;
		x2 = x - center + lobe_size1;
		x3 = x + center - lobe_size1;
		y1 = y - (lobe_size2 - 1) / 2;
		y2 = y + (lobe_size2 - 1) / 2;

		return (integralImage.getIntegralSquare(x2, y1, x3, y2) * 3 - integralImage
				.getIntegralSquare(x1, y1, x4, y2));

	}

	public float makeConvolutionDxy(int x, int y, int filtersize) {

		int x1, x2, x3, x4, y1, y2, y3, y4;
		int lobe_size1;

		lobe_size1 = filtersize / 3;

		x1 = x - lobe_size1;
		x4 = x + lobe_size1;
		x2 = x - 1;
		x3 = x + 1;
		y1 = y - lobe_size1;
		y4 = y + lobe_size1;
		y2 = y - 1;
		y3 = y + 1;

		return (integralImage.getIntegralSquare(x3, y1, x4, y2)
				+ integralImage.getIntegralSquare(x1, y3, x2, y4)
				- integralImage.getIntegralSquare(x1, y1, x2, y2) - integralImage
				.getIntegralSquare(x3, y3, x4, y4));

	}

	public float makeConvolutionDyy(int x, int y, int filtersize) {

		int x1, x2, y1, y2, y3, y4;
		int center, lobe_size1, lobe_size2;

		center = (filtersize - 1) / 2;
		lobe_size1 = filtersize / 3;
		lobe_size2 = 5 + 4 * (filtersize - 9) / 6;

		y1 = y - center;
		y4 = y + center;
		y2 = y - center + lobe_size1;
		y3 = y + center - lobe_size1;
		x1 = x - (lobe_size2 - 1) / 2;
		x2 = x + (lobe_size2 - 1) / 2;

		return (integralImage.getIntegralSquare(x1, y2, x2, y3) * 3 - integralImage
				.getIntegralSquare(x1, y1, x2, y4));

	}

}
