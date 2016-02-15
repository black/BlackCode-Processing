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
 * this class implements the TargHistSearch interface. 
 * Use this class to search a target image onto a source one.
 * 
 * @author  Giovanni Tarducci,Alessio Barducci  
 * 
 */
public class IntHistogramSearch implements IntHistSearch {
	
	  
	 /**
	  * we propose here a possible implementation of the intHistSearch interface method.
	  * 
	  * @param intHists
	  * 		historams array (yet processed by the integral histogram propagation method)
	  * @param sWidth
	  * 		source image width
	  * @param sHeight
	  * 		source image height
	  * @param intersect
	  * 		HistInterseciton objcet (will handle the core intersection step)
	  * @param output
	  * 		Search Output object (will handle the results's output)
	  * @param tWidth
	  * 		target image width
	  * @param tHeight
	  * 		target image height
	  * @param scale
	  * 		scale to search for
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

		if((int)(tWidth / scale)>1) minW  = (int)(tWidth / scale);else minW  = 2;
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
