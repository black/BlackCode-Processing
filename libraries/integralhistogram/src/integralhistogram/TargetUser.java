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
 * a class that extend TargetUser, represent your target handler i.e : you can manage a target image trought this class, setting the tolerance, the scales
 * the output image or file.
 * 
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public abstract class TargetUser {
	
	private Hist targetHist;		  //interface
	private SearchOutput searchOut;   //interface
	private HistDistance histDistance;//interface
	
	private PImage imgTarget;
	private String fileName;
	private int scale;
	private float tolerance;

	/**
	 * constructor
	 * 
	 * @param imgTarg
	 * @param bitPerComp
	 * @param imgScale
	 * @param threshold
	 */
	public TargetUser(PImage imgTarg,int bitPerComp,int imgScale,float threshold)
	{
		float[] normalized;
		int tWidth,tHeight;
		int bins;
		
		imgTarget  = imgTarg;
		tWidth     = imgTarget.width;
		tHeight    = imgTarget.height;
		scale      = imgScale;
		tolerance  = threshold;
		
		targetHist = getNewHist(bitPerComp);//Factor Method
		bins       = targetHist.getBinsPerComp();
		normalized = new float[bins*bins*bins];
		
		targetHist.createFromImage(tWidth,tHeight,imgTarget.pixels);
		targetHist.normalize(normalized,tWidth*tHeight);
		
		histDistance = getNewHistDistance(normalized);
		
	}
	
	/**
	 * 
	 * @param imgResult
	 */
	public void setOutputImage(PImage imgResult)
	{
		searchOut = getNewImgSearchOut(imgResult.pixels,imgResult.width,imgResult.height);//Factor Method
	}
	
	/**
	 * 
	 * @param fileNm
	 */
	public void setOutputFile(String fileNm)
	{
		fileName  = fileNm;
		searchOut = getNewFileSearchOut(fileName);//Factor Method
	}
	
	public void startOutput()
	{
		searchOut.startOutput();
	}
	
	public void finalizeOutput()
	{
		searchOut.finalizeOutput();
	}
	
	public void startOutputFile()
	{
		searchOut.startOutputFile(fileName);
	}
	
	public void updateOutputFile(float timePosition)
	{
		searchOut.updateOutputFile(timePosition);
	}
	
	public void endOutputFile()
	{
		searchOut.endOutputFile();
	}
	
	/**
	 * 
	 * @return PImage
	 */
	public PImage getTargImage()
	{
		return imgTarget;
	}
	
	/**
	 * 
	 * @return SearchOutput
	 */
	public SearchOutput getOutHandler()
	{
		return searchOut;
	}
	
	/**
	 * 
	 * @return HistDistance
	 */
	public HistDistance getHistDistance()
	{
		return histDistance;
	}
	
	/**
	 * 
	 * @return int
	 */
	public int getScale()
	{
		return scale;
	}
	
	/**
	 * 
	 * @return int
	 */
	public float getTolerance()
	{
		return tolerance;
	}
	
	
	protected abstract Hist getNewHist(int bitPerComp);
	
	protected abstract SearchOutput getNewImgSearchOut(int[] imgResPixels,int imgResWidth,int imgResHeight);
	
	protected abstract HistDistance getNewHistDistance(float[] normaHist);
	
	protected abstract SearchOutput getNewFileSearchOut(String fileName);
	
}
