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
 * generic interface for a search results's output class.
 * 
 * @author Giovanni Tarducci,Alessio Barducci
 * 
 */
public interface SearchOutput {
	
	/**
	 * method called during the search, usually everytime a target region matches
	 * 
	 * @param currentRow
	 * @param currentCol
	 * @param targRectWidth
	 * @param targRectHeight
	 */
	void histSearchOut(int currentRow,int currentCol,int targRectWidth,int targRectHeight);
	
	void startOutput();
	
	void finalizeOutput();
	
	//below: methods for file's handling
	void startOutputFile(String fileName);
	void updateOutputFile(float CurrentValue);
	void endOutputFile();
}
