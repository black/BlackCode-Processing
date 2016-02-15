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
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A standard implementation of the SURF_Factory interface.
 * 
 * @author Alessandro Martini, Claudio Fantacci
 */

public class SURF implements SURF_Factory {

	private static SURF instance;
	private Integral_Image integralImage;
	private int imgWidth;
	private int imgHeight;
	private float balanceValue;
	private float threshold;
	private int octaves;
	private PApplet parent;

	/**
	 * Application of the Singleton Pattern. Returns an instance of the class
	 * making sure that it is the only one. The method is STATIC so that any
	 * subclass that extends Singleton retains ownership of uniqueness of the
	 * instance.
	 * 
	 * @param img
	 *            PImage that is converted into Integral Image before it's
	 *            assigned to SURF.
	 * @param balanceValue
	 *            Value used to calculate the Laplacian of Gaussian response.
	 * @param threshold
	 *            Value used to filter the Laplacian of Gaussian.
	 * @param octaves
	 *            Number of octaves that you want to compute.
	 * @param parent
	 *            PApplet where you display your video output.
	 * @return The single instance of SURF.
	 */
	public static SURF createInstance(PImage img, float balanceValue,
			float threshold, int octaves, PApplet parent) {

		if (instance == null) {

			instance = new SURF(img, balanceValue, threshold, octaves, parent);

		} else {

			instance.setIntegralImage(img);
			instance.setBalanceValue(balanceValue);
			instance.setOctaves(octaves);
			instance.setThreshold(threshold);

		}

		return instance;

	}

	/**
	 * Application of the Singleton Pattern. Returns an instance of the class
	 * making sure that it is the only one. The method is STATIC so that any
	 * subclass that extends Singleton retains ownership of uniqueness of the
	 * instance. Here, the Balance Value has a standard value of 0.9.
	 * 
	 * @param img
	 *            PImage that is converted into Integral Image before it's
	 *            assigned to SURF.
	 * @param threshold
	 *            Value used to filter the Laplacian of Gaussian.
	 * @param octaves
	 *            Number of octaves that you want to compute.
	 * @param parent
	 *            PApplet where you display your video output.
	 * @return The single instance of SURF.
	 */
	public static SURF createInstance(PImage img, float threshold, int octaves,
			PApplet parent) {

		if (instance == null) {

			instance = new SURF(img, threshold, octaves, parent);

		} else {

			instance.setIntegralImage(img);
			instance.setOctaves(octaves);
			instance.setThreshold(threshold);

		}

		return instance;

	}

	/*
	 * Constructor of SURF. The constructor is private to prevent any
	 * possibility to create an instance without the Singleton method.
	 */
	private SURF(PImage img, float balanceValue, float threshold, int steps,
			PApplet parent) {
		this.integralImage = new My_Integral_Image(img, parent);
		this.imgHeight = img.height;
		this.imgWidth = img.width;
		this.balanceValue = balanceValue;
		this.threshold = threshold;
		this.octaves = steps;
		this.parent = parent;
	}

	/*
	 * Constructor of SURF with a standard Balance Value (0.9). The constructor
	 * is private to prevent any possibility to create an instance without the
	 * Singleton method.
	 */
	private SURF(PImage img, float threshold, int steps, PApplet parent) {
		this.integralImage = new My_Integral_Image(img, parent);
		this.imgHeight = img.height;
		this.imgWidth = img.width;
		this.balanceValue = 0.9f;
		this.threshold = threshold;
		this.octaves = steps;
		this.parent = parent;
	}

	public Detector createDetector() {
		return new Fast_Hessian(integralImage, imgWidth, imgHeight,
				balanceValue, threshold, octaves, parent);
	}

	public Descriptor createDescriptor(ArrayList<Interest_Point> interest_points) {
		return new SURF_Descriptor(interest_points, integralImage);
	}

	public Integral_Image getIntegralImage() {
		return integralImage;
	}

	public void setIntegralImage(PImage img) {
		this.integralImage = new My_Integral_Image(img, parent);
	}

	public float getBalanceValue() {
		return balanceValue;
	}

	public void setBalanceValue(float balanceValue) {
		this.balanceValue = balanceValue;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

}
