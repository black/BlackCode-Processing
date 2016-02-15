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
 * generic interface for an exaustive histogram based search class.
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public interface IntHistSearch {
	
	/**
	 * search a target image onto a source image i.e onto an integral histogram.
	 * print the search's results using a SearchOut interface's implementation object.
	 * you can play on how you will manage the search scales and the file managing method.
	 * (remember that you will have to call the propIntHist() method before this one in an example source)
	 * 
	 * @param intHists
	 * 			histograms's array previusly processed by the propIntHist() method
	 * @param sWidth
	 * 			source image width
	 * @param sHeight
	 * 			source image height
	 * @param intersect
	 * 			istance of Intersection's implementation class that you will call during the search process, to compute
	 * 			the distance between a target region and the integral histogram
	 * @param output
	 * 			Intersect interface's implementation class instance
	 * @param imgTarget
	 * 			target image's pixels
	 * @param tWidth
	 * 			target image width
	 * @param tHeight
	 * 			target image height
	 * @param normTargHist
	 * @param scale
	 * 			minimun scale to search for (here comes your choose on how to interpet the concept of scale)
	 * @param tolerance
	 *			threshold to mark a target region as good (used by the "Intersection's implementation" instance)
	 * @return boolean
	 * 			true if at least one match was found
	 */
	boolean intHistSearch(Hist[] intHists,int sWidth,int sHeight,HistIntersection intersect,SearchOutput output,
						   int tWidth,int tHeight,int scale,float tolerance);
}