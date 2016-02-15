/*
  Integral Histogram, a fast way to compute histograms of target regions.
  
  (c) 2009
  
  Integral Histogram is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  Integral Histogram is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with Integral Histogram; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */
package integralhistogram;

/**
 * generic interface for a color histogram's class.
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public interface Hist {
	
	/**
	 * return the content of the queried bin
	 * 
	 * @param bin
	 * @return int
	 */
	int  getBinValue(int bin);
	
	/**
	 * return the the number of bit per component (usually choosed by the user trought the implementation class's constructor) 
	 * 
	 * @return int
	 */
	int  getBinsPerComp();
	
	/**
	 * make a copy of an source histogram in the current histogram
	 * 
	 * @param srcHist
	 */
	void copy(Hist srcHist);
	
	/**
	 * update the current histogram with the source pixel value
	 * 
	 * @param pixelColor
	 */
	void sumPixel(int pixelColor);
	
	/**
	 * sum the surce histograms using the algorithm proposed by Fatih Porikli (http://www.merl.com)
	 * (you can check the algorithm in it's pure form in our "Histogram" implementation class)
	 * 
	 * @param leftHist
	 * @param upHist
	 * @param up_leftHist
	 * @param currentPixel
	 */
	void sumHists(Hist leftHist,Hist upHist,Hist up_leftHist,int currentPixel);
	
	/**
	 * normalize with "norm" factor, the current histogram, put the result histogram in "destination" array
	 *  
	 * @param destination
	 * @param norm
	 */
	void normalize(float[] destination,int norm);
	
	/**
	 * fill the current (color) histogram with the source image pixels, i.e crate an histogram of the source image
	 * 
	 * @param imgWidth
	 * @param imgHeight
	 * @param pixels
	 */
	void createFromImage(int imgWidth,int imgHeight,int[] pixels);
}
