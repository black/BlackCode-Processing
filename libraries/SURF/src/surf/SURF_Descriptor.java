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
 * A standard implementation of the Descriptor interface. The constructor has an
 * Integral Image representation of an image and an ArrayList of Interest
 * Points. SURF_Descriptor uses the Integral Image to compute Haar wavelet
 * responses and extracts 64-dimensional descriptor vector based on sums of
 * these wavelet responses.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public class SURF_Descriptor implements Descriptor {

	private ArrayList<Interest_Point> interest_points;
	@SuppressWarnings("unused")
	private Integral_Image integralImage;
	private HAAR_Convolution haarConvolution;

	/**
	 * Constructor of SURF_Descriptor. The constructor has an Integral Image
	 * representation of an image and an ArrayList of Interest Points.
	 * SURF_Descriptor uses the Integral Image to compute Haar wavelet responses
	 * and extracts 64-dimensional descriptor vector based on sums of these
	 * wavelet responses.
	 * 
	 * @param interest_points
	 *            ArrayList of Interest_Point you want to describe.
	 * @param integralImage
	 *            Integral_Image of a initial PImage you want to SURF.
	 */
	public SURF_Descriptor(ArrayList<Interest_Point> interest_points,
			Integral_Image integralImage) {
		this.interest_points = interest_points;
		this.integralImage = integralImage;
		this.haarConvolution = new My_HAAR_Convolution(integralImage);
	}

	/*
	 * This schema helps to understand how the method works.
	 * 
	 *  --------------------------------> x
	 * |
	 * |   x1,y1 --- --- --- --- x2,y2
	 * |        |   |   |   |   |
	 * |        |---|---|---|---|
	 * |        |   |   |   |   |
	 * |        |---|---|---|---|
	 * |        |   |   |   |   |
	 * |        |---|---|---|---|
	 * |        |   |   |   |   |
	 * |   x4,y4 --- --- --- --- x3,y3
	 * |
	 * v
	 * y
	 * 
	 */
	public void generateAllDescriptors() {

		float[] ValueDescriptor = new float[64];
		Interest_Point currentIP;
		int x1, y1, x2, y4;
		;
		int xf, yf;

		for (int i = 0; i < interest_points.size(); i++) {

			for (int count = 0; count < 64; count++) {
				ValueDescriptor[count] = 0;
			}

			currentIP = (Interest_Point) interest_points.get(i);

			x1 = (int) (currentIP.getX() - currentIP.getScale() * 10);
			y1 = (int) (currentIP.getY() - currentIP.getScale() * 10);
			x2 = (int) (currentIP.getX() + currentIP.getScale() * 10);
			y4 = (int) (currentIP.getY() + currentIP.getScale() * 10);

			/*
			 * Calculation of points (set the column with xf you scroll by line
			 * by yf) for each subregion of the square x1y1.x2y2.x3y3.x4y4 and
			 * calculating of the convolution with the HAAR Wavelets on the same
			 * points in order to determine the descriptor for a single
			 * Interest_Point.
			 */
			for (int j = 0; j < 20; j++) {

				xf = (int) x1 + (j + 1) * (x2 - x1) / 20;

				for (int k = 0; k < 20; k++) {

					yf = (int) y1 + (k + 1) * (y4 - y1) / 20;

					double gaussValue = gaussian(xf - currentIP.getX(), yf
							- currentIP.getY(), 3.3f * currentIP.getScale());
					float DxToWeight = haarConvolution.makeConvolutionDx(xf,
							yf, 2*currentIP.getScale());
					float DyToWeight = haarConvolution.makeConvolutionDy(xf,
							yf, 2*currentIP.getScale());

					ValueDescriptor[(int) (4 * Math.floor(j / 5) + 16 * Math
							.floor(k / 5))] += gaussValue * DxToWeight;
					ValueDescriptor[(int) (4 * Math.floor(j / 5) + 16
							* Math.floor(k / 5) + 1)] += gaussValue
							* DyToWeight;
					ValueDescriptor[(int) (4 * Math.floor(j / 5) + 16
							* Math.floor(k / 5) + 2)] += Math.abs(gaussValue
							* DxToWeight);
					ValueDescriptor[(int) (4 * Math.floor(j / 5) + 16
							* Math.floor(k / 5) + 3)] += Math.abs(gaussValue
							* DyToWeight);
				}

			}

			normalizeDescriptor(ValueDescriptor);
			currentIP.setDescriptorOfTheInterestPoint(ValueDescriptor);

		}

	}

	/*
	 * Calculates the value of a Gaussian center in x and y and with the
	 * specified sigma. G(x,y,s) = k(s) * exp(value(x,y,s));
	 * 
	 * @param x
	 *            x-coordinate.
	 * @param y
	 *            y-coordinate.
	 * @param sigma
	 *            Gaussian width.
	 * @return The Gaussian value at (x,y) with sigma.
	 */
	private double gaussian(float x, float y, float sigma) {
		double k = 1 / (Math.sqrt(2 * Math.PI * Math.pow(sigma, 2)));
		double value = -(Math.pow(x, 2) + Math.pow(y, 2))
				/ (2 * Math.pow(sigma, 2));
		double exp = Math.exp(value);
		return k * exp;
	}

	/*
	 * Make the descriptor a unit vector.
	 * 
	 * @param ValueDescriptor
	 *            float[] of descriptor components to normalize.
	 */
	private void normalizeDescriptor(float[] ValueDescriptor) {

		float norm = 0;

		for (int k = 0; k < 64; k++) {
			norm += Math.pow(ValueDescriptor[k], 2);
		}
		norm = (float) Math.sqrt(norm);

		for (int k = 0; k < 64; k++) {
			ValueDescriptor[k] = ValueDescriptor[k] / norm;
		}

	}

}
