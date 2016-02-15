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
 * generic interface for an integral histogram class.
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public interface IntHistProp {
	
	/**
	 * given an histograms array, compute on it the integral histogram.
	 * 
	 * @param hists
	 * 			histograms's array
	 * @param imgWidth
	 * 			source image width i.e histograms's matrix columns
	 * @param imgHeight
	 * 			source image height i.e histograms's matrix rows
	 * @param pxlArray
	 * 			source image pixels, used to fill your histograms using the methods of an Hist's implementation class
	 */
	void propIntHist(Hist[] hists,int imgWidth,int imgHeight,int[] pxlArray);
}
