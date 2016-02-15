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
 * With this class we extended the IntHistUser abstract class. Use this class to search over the Source image, using the <code>scale</code> parameter
 * referred to the target image. i.e, via target handler (MyTargetUser or MyTargetUser2) you can set scale=10, the via the MyIntHistUser you will
 * search over the Source image, starting at 1/10 of the Target image. Otherwise, using MyIntHistUser2 you will start searching
 * at 1/10 of the Source image.
 * 
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public class MyIntHistUser extends IntHistUser {
	
	/**
	 * 
	 * @param imgSrc
	 * @param bit
	 */
	public MyIntHistUser(PImage imgSrc,int bit)
	{
		super(imgSrc,bit);
	}
	
	/**
	 * 
	 * @param imgSrcWidth
	 * @param imgSrcHeight
	 * @return
	 */
	protected Hist[] getNewHistogramArray(int imgSrcWidth,int imgSrcHeight)
	{
		int i,j;
		
		Hist[] hists  = new Histogram[imgSrcWidth*imgSrcHeight];
		
		for(i = 0; i < imgSrcHeight; i++)
			for(j = 0; j < imgSrcWidth; j++)
				hists[i*imgSrcWidth + j] = new Histogram(bitPerComp);
		
		return hists;
	}
	
	/**
	 * 
	 * @return IntHistProp
	 */
	protected IntHistProp getNewIntHistProp()
	{
		return new IntHistogramProp();
	}
	
	/**
	 * 
	 * @return HistIntersection
	 */
	protected HistIntersection getNewHistIntersection()
	{
		return new HistogramIntersection();
	}
	
	/**
	 * 
	 * @return IntHistSearch
	 */
	protected IntHistSearch getNewIntHistSearcher()
	{
		return new IntHistogramSearch();
	}
}

