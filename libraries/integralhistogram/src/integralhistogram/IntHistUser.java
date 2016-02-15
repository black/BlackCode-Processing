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

import processing.core.PImage;

/**
 * you can extend this abstract class to test your interfaces's implementation classes.
 * 
 * @author  Giovanni Tarducci,Alessio Barducci 
 * 
 */
public abstract class IntHistUser {
	
	private Hist[] hists;//interface
	private IntHistProp integralHisto;//interface
	private HistIntersection intersect;//interface
	private IntHistSearch findImage;//interface
	
	private PImage imgSource;
	protected int bitPerComp;
	
	/**
	 * create the integral histogram
	 * 
	 * @param imgSrc
	 * @param bit
	 */
	public IntHistUser(PImage imgSrc,int bit)
	{
		imgSource  = imgSrc;
		bitPerComp = bit;
		
		hists  = getNewHistogramArray(imgSource.width,imgSource.height);//Factor Method
  
		integralHisto = getNewIntHistProp();//Factor Method
		integralHisto.propIntHist(hists,imgSource.width,imgSource.height,imgSource.pixels);
		
		intersect = getNewHistIntersection();//Factor Method
		
	}
	
	/**
	 * search for a target object onto the target's source image
	 * 
	 * @param target
	 * @return
	 * 		true if at least a match was done
	 */
	public boolean imageSearch(TargetUser target)
	{
		boolean targFound;
		
		PImage imgTarget = target.getTargImage();
		
		findImage  = getNewIntHistSearcher();//Factor Method
		
		intersect.setHistDistance(target.getHistDistance());
		
		target.startOutput();
		
		targFound  = findImage.intHistSearch(hists,imgSource.width,imgSource.height,intersect,target.getOutHandler(),
												imgTarget.width,imgTarget.height,target.getScale(),target.getTolerance());
		
		target.finalizeOutput();
		
		return targFound;
	}
	
	protected abstract Hist[] getNewHistogramArray(int imgSrcWidth,int imgSrcHeight);
	
	protected abstract IntHistProp getNewIntHistProp();
	
	protected abstract HistIntersection getNewHistIntersection();
	
	protected abstract IntHistSearch getNewIntHistSearcher();
	
}
