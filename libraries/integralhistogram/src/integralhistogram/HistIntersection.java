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
 * generic interface for integral histogram based intersection computing class.
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public interface HistIntersection {
	
	/**
	 * The histogram of a target region can be computed using the wavefront propagated integral histogram values at 
	 * the boundary points of the region. So As opposed to the conventional histogram computation,the integral histogram
	 * method does not repeat the histogram extraction for each possible region.
	 * This method has to be called suring the target search. As main parameters it takes the boundary histograms of the
	 * current target region, see below for the other parameters documentation.
	 *
	 * @param currentHist,leftHist,upHist,up_leftHist,targetImgNormHisto
	 * 			these are the boundary target region's points for the actual intersection computing
	 * @param targetImgNormHisto
	 * 			float array storing the normalized histogram of the target image
	 * @param tolerance
	 * 			threshold to determine if an intersection correspond to a match or not
	 * @param norm
	 * 			used to normalize the "boundary" histograms's bins values
	 * @return boolean
	 * 			true if there was a match for the current target region
	 */
	boolean intersection(Hist currentHist,Hist leftHist,Hist upHist,Hist up_leftHist,float tolerance,int norm);
	
	/**
	 * Set the HistDistance implementation object you want to use, in other words, set your matching politic.
	 * 
	 * @param histDistance
	 */
	void setHistDistance(HistDistance histDistance);
}
