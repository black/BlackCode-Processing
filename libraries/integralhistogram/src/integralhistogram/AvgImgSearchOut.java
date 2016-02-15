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
 * SearchOut implementation. Let you plot the average of all matching target region, on a Result image.
 * You can use it via MyTargetUser2.
 * 
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public class AvgImgSearchOut implements SearchOutput {
	
	private int[]   imgRes;
	private int     rWidth;
	private float   avgX1,avgY1,avgX2,avgY2;
	private boolean first;
	private int     rectCount;
	
	/**
	 *
	 * @param imgResult
	 * 			pixels'array for the result image
	 * @param imgResWidth
	 * 			result image width
	 */
	public AvgImgSearchOut(int[] imgResult,int imgResWidth)
	{	
		imgRes  = imgResult;
		rWidth  = imgResWidth;
		first   = true;
	}
	
	/**
	 * 
	 * @param row
	 * 			current source image row
	 * @param col
	 * 			current sourcce image column
	 * @param targRectWidth
	 * 			current Target Region Widht
	 * @param targRectHeight
	 * 			current Target Region Height
	 */
	public void histSearchOut(int row,int col,int targRectWidth,int targRectHeight)
	{	
		if(first)
		{
			avgX1 = (float)col;
			avgY1 = (float)row;
			avgX2 = (float)(col + targRectWidth);
			avgY2 = (float)(row + targRectHeight);
			
			first = false;
		}
		
		avgX1 = (avgX1*rectCount + col ) / (rectCount + 1);
		avgY1 = (avgY1*rectCount + row ) / (rectCount + 1);
		avgX2 = (avgX2*rectCount + (col + targRectWidth) ) / (rectCount + 1);
		avgY2 = (avgY2*rectCount + (row + targRectHeight) ) / (rectCount + 1);
		
		rectCount++;
	}
	
	/**
	 * plot the average target region on the Result image
	 */
	public void finalizeOutput()
	{
		int x,y;
		
		avgX2 = (int)avgX2;
		avgY2 = (int)avgY2;
		
		for(x = (int)avgY1; x < avgY2; x += 2)
			for(y = (int)avgX1; y < avgX2; y++)
				imgRes[rWidth*x + y] = (0xFD << 16)|0|(imgRes[rWidth*x + y] & 0xFF);
	}
	
	/**
	 * initialize average Target Region corners
	 */
	public void startOutput()
	{
		avgX1 = 0;
		avgY1 = 0;
		avgX2 = 0;
		avgY2 = 0;
		
		first = true;
	}
	
	public void startOutputFile(String foo){}
	public void updateOutputFile(float foo){}
	public void endOutputFile(){}
}