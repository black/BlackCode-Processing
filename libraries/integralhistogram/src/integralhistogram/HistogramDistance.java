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
 * this class implements HistDistance. You can use it via MyTargetUser and MyTargetUser2.
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public class HistogramDistance implements HistDistance {
	
	private float[] normHist;
	private float distance;
	
	/**
	 * Set the local reference to the normalized histogram of the target image; used during the distance compute.
	 * 
	 * @param targetImgNormHist
	 * 			target image's normalized histogram
	 */
	public HistogramDistance(float[] targetImgNormHist)
	{
		normHist = targetImgNormHist;
		init();
	}
	
	/**
	 * called during the intersection process. Our distance compute is based on the difference between the target region's bin and
	 * the normalized target histogram's bin.
	 * 
	 * @param intersection
	 * 			current intersection value
	 * @param bin
	 * 			current bin
	 * @return float
	 * 			return the updated distance value
	 */
	public float computeDistance(float intersection,int bin)
	{
		distance += ((intersection - normHist[bin])>0)?(intersection - normHist[bin]):-(intersection - normHist[bin]);
		
		return distance;
	}
	
	/**
	 * init the distance value
	 */
	public void init()
	{
		distance = 0;
	}
}
