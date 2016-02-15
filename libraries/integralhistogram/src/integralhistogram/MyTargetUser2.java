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
 * With this class we extended the IntHistUser abstract class. Use this class to output .txt files or to output 
 * on a result image in the custom way (i.e: compute the average of the target regions that matches, plot it on the result image).
 * 
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public class MyTargetUser2 extends TargetUser {
	
	/**
	 * create a target handler
	 * 
	 * @param imgTarg
	 * @param bitPerComp
	 * @param imgScale
	 * @param threshold
	 */
	public MyTargetUser2(PImage imgTarg,int bitPerComp,int imgScale,float threshold)
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
	 * 
	 * @param imgResPixels
	 * @param imgResWidth
	 * @param imgResHeight
	 * @return SearchOutput
	 */
	protected SearchOutput getNewImgSearchOut(int[] imgResPixels,int imgResWidth,int imgResHeight)
	{
		return new AvgImgSearchOut(imgResPixels,imgResWidth);
	}
	
	/**
	 * 
	 * @param fileName
	 * @return SearchOutput
	 */
	protected SearchOutput getNewFileSearchOut(String fileName)
	{
		return new TXTSearchOut();
	}
	
	/**
	 * 
	 * @param normaHist
	 * @return HistDistance
	 */
	protected HistDistance getNewHistDistance(float[] normaHist)
	{
		return new HistogramDistance(normaHist);
	}
}
