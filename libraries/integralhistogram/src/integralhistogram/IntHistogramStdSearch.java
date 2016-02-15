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
 * this class implements the TargHistSearch interface, you can use this class 
 * to search a target image onto a source one and get the search result over a result image.
 * 
 * @example image_sample
 * @example video_sample
 * @author  Giovanni Tarducci,Alessio Barducci  
 * 
 */
public class IntHistogramStdSearch implements IntHistSearch {
	
	  
	 /**
	  * we propose here a standard implementation of the intHistSearch interface method.
	  * 
	  * 
	  * @param intHists
	  * @param sWidth
	  * @param sHeight
	  * @param intersect
	  * @param output
	  * @param imgTarget
	  * @param tWidth
	  * @param tHeight
	  * @param normTargHist
	  * @param scale
	  * @param tolerance
	  * @return
	  */
	public boolean intHistSearch(Hist[] intHists,int sWidth,int sHeight,HistIntersection intersect,SearchOutput output,
									int tWidth,int tHeight,int scale,float tolerance)
	{
		int rectHeight,rectWidth;
		int minW,minH;
		int i,j;
		boolean match = false; 

		if((int)(sWidth / scale)>1) minW  = (int)(sWidth / scale);else minW  = 2;
		if((int)(tHeight*minW / tWidth)>1) minH  = (int)(tHeight*minW / tWidth);else minH = 2;
		
		for(j = 0; j < sHeight; j += 2)
		{
			for(i = 0; i < sWidth; i += 2)
			{ 
				rectWidth  = minW - 1;
				rectHeight = minH - 1;
				
				for(;((rectWidth + i) < sWidth) && ((rectHeight + j) < sHeight);)
				{
					if(intersect.intersection(intHists[(j + rectHeight)*sWidth + (i + rectWidth)],intHists[(j + rectHeight)*sWidth + i],
					   intHists[(j*sWidth) + (i + rectWidth)],intHists[(j*sWidth) + i],tolerance,rectHeight*rectWidth)) 
					{
						output.histSearchOut(j,i,rectWidth,rectHeight);
						match = true;
					} 
					
					rectWidth += minW;
					rectHeight = (int)(tHeight*rectWidth / tWidth);
				}  
			}
		}
		
		return match;
	}
	
}