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
 * this class implements the Hist interfeace, you can use this class to manage color histograms.
 * 
 * @author  Giovanni Tarducci,Alessio Barducci 
 * 
 */
public class Histogram implements Hist {
	
	private int i;
	private int bit;
	private int bins;
	private int[] colors;
	
	/**
	 * class constructor.
	 * 
	 * @param bitPerComp
	 * 			number of bit per color (RGB) mode 
	 * 			e.g.: bitPerComp = 2 mean you choosed 6 bits per pixel, that is: four possible values for each component,
	 * 			otherwise bitPerComp = 4 means you choosed 12 bit per pixel (16 possible values for each component).
	 * 			(be carefull, a 4 bit per component choose, for a source image source size of 300x300 pixels will ask you
	 * 			 more than 1 GB of ram for the integral histogram's propagation method).
	 * 			usually you can achive your scope with: bitPerComp = 2.
	 */
	public Histogram(int bitPerComp)
	{
		bit    = bitPerComp;
		bins   = 1 << bit;
		colors = new int[bins*bins*bins];
	}
	
	/**
	 *
	 * @param bin 
	 * @return int
	 */
	public int  getBinValue(int bin)
	{
		return colors[bin];
	}
	
	/**
	 * 
	 * @return int
	 */
	public int  getBinsPerComp()
	{
		return bins;
	}
	
	/**
	 * copy source histogram's bins onto the current histogram.
	 * 
	 * @param srcHist
	 * 			source histogram
	 */
	public void copy(Hist srcHist)
	{
		for(i = 0; i < bins*bins*bins; i++)
			this.colors[i] = srcHist.getBinValue(i);
	}
	/**
	 * 
	 * update the histogram adding the pixelColor value in the right bin
	 * 
	 * @param pixelColor
	 */
	public void sumPixel(int pixelColor)
	{
		this.colors[ ( (pixelColor >> 16 & 0xFF) >> (8 - bit) ) +
	                 ( (pixelColor >> 8  & 0xFF) >> (8 - bit) )*bins +
	                 ( (pixelColor       & 0xFF) >> (8 - bit) )*bins*bins ]++;
	}
	
	/**
	 * compute an wavefront propagation step.
	 * 
	 * @param leftHist,upHist,up_LeftHist
	 * 			source histograms used for propagation step
	 * @param currentPixel
	 * 			source image's pixel of the current point
	 */
	public void sumHists(Hist leftHist,Hist upHist,Hist up_leftHist,int currentPixel)
	{
		for(i = 0; i < bins*bins*bins; i++)
			this.colors[i] = leftHist.getBinValue(i) + upHist.getBinValue(i) - up_leftHist.getBinValue(i);
	    this.colors[ ( (currentPixel >> 16 & 0xFF) >> (8 - bit) ) +
	                 ( (currentPixel >> 8  & 0xFF) >> (8 - bit) )*bins +
	                 ( (currentPixel       & 0xFF) >> (8 - bit) )*bins*bins ]++;
	}
	
	/**
	 * normalize the current histogram and store the output in outputHist.
	 * 
	 * @param outputHist
	 * 			normalization's output
	 * @param norm
	 * 			normalize factor
	 */
	public void normalize(float[] outputHist,int norm)
	{
		for(i = 0; i < bins*bins*bins; i++)
			outputHist[i] = ((float)this.colors[i] / norm);
	}
	
	/**
	 * use the current histogram ("this" operator) to store the (color) histogram of a source image.
	 *  
	 * @param imgWidth
	 * @param imgHeight
	 * @param imgPixels
	 * 			the pixels's array of the source image
	 */
	public void createFromImage(int imgWidth,int imgHeight,int[] imgPixels)
	{
		for(i = 0; i < imgWidth*imgHeight; i++)
		    this.sumPixel(imgPixels[i]);
	}
}
