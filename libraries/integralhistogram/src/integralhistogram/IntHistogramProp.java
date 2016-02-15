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
 * this class implements the IntHist interface, you can use this class 
 * to compute integral histogram method on your histograms's matrix.
 * 

 * @author  Giovanni Tarducci,Alessio Barducci  
 * 
 */
public class IntHistogramProp implements IntHistProp {
	
	/**
	 * implementation of the integral histogram's wavefront propagation method.
	 * 
	 * @param hists
	 * 			histograms array to work on
	 * @param imgWidth
	 * 			source image width
	 * @param imgHeight
	 * 			source image height
	 * @param pxlArray
	 * 			source image's pixels array
	 */
	public void propIntHist(Hist[] hists,int imgWidth,int imgHeight,int[] pxlArray)
	{
		int i,j;

		hists[0].sumPixel(pxlArray[0]);

		for(i = 1; i < imgWidth; i++)
		{
			hists[i].copy(hists[i - 1]);
			hists[i].sumPixel(pxlArray[i]);  
		}

		for (j = 1; j < imgHeight; j++)
		{
			hists[j*imgWidth].copy(hists[(j - 1)*imgWidth]);
			hists[j*imgWidth].sumPixel(pxlArray[j*imgWidth]);
		   
			for (i = 1; i < imgWidth; i++)
			{
				hists[j*imgWidth + i].sumHists(hists[j*imgWidth + (i - 1)],
											   hists[(j - 1)*imgWidth + (i)],
											   hists[(j - 1)*imgWidth + (i - 1)],
											   pxlArray[i + (j*imgWidth)]);                          
		   }
		}
	}
	
}
