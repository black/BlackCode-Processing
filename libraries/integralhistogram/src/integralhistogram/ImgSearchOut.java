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
 * use this class to "print" the search's results over a result image.
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public class ImgSearchOut implements SearchOutput {
	
	private int[] imgRes;
	private int   rWidth;
	
	/**
	 * class constructor.
	 * 
	 * @param imgResult
	 * @param imgResWidth
	 */
	public ImgSearchOut(int[] imgResult,int imgResWidth)
	{	
		imgRes  = imgResult;
		rWidth  = imgResWidth;
	}
	
	/**
	 * color the result image's pixels between the specified boundaries 
	 * (see below: i,j,targRectWidth,targRectHeight)
	 * 
	 * @param i
	 * 			current source image's row
	 * @param j
	 * 			current source image's row
	 * @param targRectWidth
	 * 			current Target Region Width
	 * @param targRectHeight
	 * 			current Target Region Height
	 */
	public void histSearchOut(int i,int j,int targRectWidth,int targRectHeight)
	{
		int x,y;
		
		for(x = 0; x < targRectHeight; x += 2)
			for(y = 0; y < targRectWidth; y++)
			{
				if( ((imgRes[(y + j) + rWidth*(x + i)] >> 16) & 0xFF) != 0xFD)
					  imgRes[(y + j) + rWidth*(x + i)] = (0xFD << 16)|0|(imgRes[(y + j) + rWidth*(x + i)] & 0xFF);
				
			}
	}
	
	public void startOutput(){}
	public void finalizeOutput(){}
	public void updateOutputFile(float timePos){}
	public void startOutputFile(String foo){}
	public void endOutputFile(){}
}
