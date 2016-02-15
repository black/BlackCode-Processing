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

import java.io.*;

/**
 * this class implements SearchOutput. You can use it via MyTargetUser2.
 * 
 * @author Giovanni Tarducci, Alessio Barducci
 *
 */
public class TXTSearchOut implements SearchOutput {
	
	private BufferedWriter out;
	private String fileName;
	private float timePosition;
	
	public TXTSearchOut()
	{
		timePosition = -1;
	}
	
	/**
	 * 
	 * @param i
	 * 		  current source image row
	 * @param j
	 * 		  current source image row
	 * @param targRectWidth
	 * 		  current Target Region Widht
	 * @param targRectHeight
	 * 		  current Target Region Height
	 */
	public void histSearchOut(int i,int j,int targRectWidth,int targRectHeight)
	{
		try
		{	
			out.write("\n" + i + "," + j + "," + (i + targRectHeight) + "," + (j + targRectWidth));
			
			out.flush();
		}
		catch(Exception e)
		{
			System.err.println("pheraps you didn't called the startOutputFile() method");
		}
	}
	
	public void finalizeOutput()
	{
		timePosition = -1;
	}
	
	/**
	 * 
	 * @param fileNm
	 */
	public void startOutputFile(String fileNm)
	{
		try
		{  		
			if(fileNm != null)
			{	
				fileName = fileNm;
				
				out = new BufferedWriter(new FileWriter("/" + fileName + ".txt"));
				
				out.write("-- " + fileName + " --");
			} 
			
		}
		catch(IOException e)
		{
			System.err.println("an error occurred while attempting to open the file");
		}
		
	}
	
	public void startOutput()
	{
		try
		{
			out.write("\n\n~ " + timePosition + "\n");
			out.flush();
		}
		catch(IOException e)
		{
			System.err.println("an error occurred while attempting to writo onto the file");
		}
	}
	
	/**
	 * 
	 * @param timePosotion
	 */
	public void updateOutputFile(float timePos)
	{
		timePosition = timePos;
	}
	
	public void endOutputFile()
	{
		try
		{   
			out.close();
			
		}
		catch(IOException e)
		{
			System.err.println("an error occurred while attempting to close the file");
		}
	}
	
}
