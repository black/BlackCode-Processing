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
 * generic interface for an histograms distance's computing class.
 * histogram distance is the heart of the search process and you will decide the matching politic by implementing this class.
 * (we used the histograms bins's distance, check it out, we called it: HistogramDistance)
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public interface HistDistance {
	
	/**
	 * 
	 * @param binIntersectionValue
	 * 		current bin's intersection value (computed with the Integral Histogram method)
	 * @param bin
	 * 		current bin
	 * @return float
	 * 		return the updated distance
	 */
	public float computeDistance(float binIntersectionValue,int bin);
	
	/**
	 * use init() method to initialize your varibles (this method have to be called at every new histogram intersection test) 
	 * 
	 */
	public void init();
}
