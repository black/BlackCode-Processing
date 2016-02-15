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
 * use this class to compute the integral histogram's intersection method.
 * 
 * @example image_sample
 * @example video_sample
 * @author  Giovanni Tarducci,Alessio Barducci 
 * 
 */
public class HistogramIntersection implements HistIntersection {
	
	private HistDistance histDistance;
	
	/**
	 * compute intersection between target image's normalized histogram and actual target region
	 * 
	 * @return boolean
	 * 			false if the match for the actual target region failed
	 * @param currentHist,leftHist,upHist,up_leftHist
	 * 			histograms defined by the target region's boundary
	 * @param targetImgNormHisto
	 * 			target image's normalized histogram
	 * @param tolerance
	 * 			threshold to accept the actual target region as valid
	 * @param norm
	 * 			normalize factor
	 */
	public boolean intersection(Hist currentHist,Hist leftHist,Hist upHist,Hist up_leftHist,float tolerance,int norm)
	{
		float intersection = 0;
	    int bins,i;
	    
	    bins = currentHist.getBinsPerComp();
	    
	    histDistance.init();
	    
	    for(i = 0; i < bins*bins*bins; i++)
	    {
	    	intersection = ((float)(currentHist.getBinValue(i) -
	    							   leftHist.getBinValue(i) - 
	    							     upHist.getBinValue(i) +
	                               up_leftHist.getBinValue(i)) / norm);

	    	if(histDistance.computeDistance(intersection,i) > tolerance)
	    		return false;
	    	
	    }
	    return true;
	}
	
	/**
	 * set the HistDistance object 
	 * 
	 * @param histDist
	 */
	public void setHistDistance(HistDistance histDist)
	{
		histDistance = histDist;
	}
	
}