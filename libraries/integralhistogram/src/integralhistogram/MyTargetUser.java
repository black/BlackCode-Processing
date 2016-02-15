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

import processing.core.PImage;

/**
 * With this class we extended the IntHistUser abstract class. Use this class to output .xml files or to output 
 * on a result image in the standard way (i.e: everytime a target region matches, plot it on the result image).
 * 
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public class MyTargetUser extends TargetUser {
	
	/**
	 * create a target handler
	 *  
	 * @param imgTarg
	 * @param bitPerComp
	 * @param imgScale
	 * @param threshold
	 */
	public MyTargetUser(PImage imgTarg,int bitPerComp,int imgScale,float threshold)
	{
		super(imgTarg,bitPerComp,imgScale,threshold);
	}
	
	/**
	 * 
	 * @param bitPerComp
	 * @return Hist
	 */
	protected Hist getNewHist(int bitPerComp)
	{
		return new Histogram(bitPerComp);
	}
	
	/**
	 * called if you choosed the image output mode 
	 * 
	 * @param imgResPixels
	 * @param imgResWidth
	 * @param imgResHeight
	 * @return SearchOutput
	 */
	protected SearchOutput getNewImgSearchOut(int[] imgResPixels,int imgResWidth,int imgResHeight)
	{
		return new ImgSearchOut(imgResPixels,imgResWidth);
	}
	
	/**
	 * called if you choosed the file output mode
	 * 
	 * @param fileName
	 * @return SearchOutput
	 */
	protected SearchOutput getNewFileSearchOut(String fileName)
	{
		return new XMLSearchOut();
	}
	
	/**
	 * 
	 * @param normaHist
	 * @return HistDistances
	 */
	protected HistDistance getNewHistDistance(float[] normaHist)
	{
		return new HistogramDistance(normaHist);
	}
}
