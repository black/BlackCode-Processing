/**
 * This Processing package implements the KLT (Kanade-Lucas-Tomasi) Feature Tracker  
 * algorithm on the GPU. 
 * It was ported from the C++ code written by Christopher Zach, available at this URL: 
 * http://cs.unc.edu/~cmzach/opensource.html
 * @author Andres Colubri
 * @version 0.9.2
 *
 * proGPUKLT Copyright (c) 2008 Andres Colubri
 *   
 * Original C++ GPU-KLT Copyright (c) 2008 University of North Carolina at Chapel Hill
 * 
 * proGPUKLT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * proGPUKLT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.

 * You should have received a copy of the GNU Lesser General Public License along
 * with GPU-KLT. If not, see <http://www.gnu.org/licenses/>.
 */

package codeanticode.progpuklt;

/**
 * 
 * This class encapsulates a feature track, i.e., all the (x, y) positions that the 
 * feature point has adopted during its existence up to the current frame.
 * @author Andres Colubri
 *
 */

public class FeatureTrack
{
	/**
	 * 
	 * Default constructor
	 * 
	 */
	public FeatureTrack()
	{
	    length = 0;
	    restarted = false;	    
		x = new float[MAX_TRACK_LENGTH];
		y = new float[MAX_TRACK_LENGTH];
	}
	
	/**
	 * 
	 * Sets track length to zero.
	 * 
	 */
	public void clear()
	{
		length = 0;
	}

	/**
	 * 
	 * Sets restarted field to true.
	 * 
	 */
	public void restart()
	{
		restarted = true;
	}	
	
	/**
	 * 
	 * Adds a new position to the track.
	 * @param x float
	 * @param y float
	 *
	 */
	public void add(float x, float y)
	{
		if (length < MAX_TRACK_LENGTH)
		{
		    this.x[length] = x;
		    this.y[length] = y;		
		    length++;
		}
		else
		{
			this.x[MAX_TRACK_LENGTH - 1] = x;
			this.y[MAX_TRACK_LENGTH - 1] = y;			
		}
	}
	
	public int length;
	public boolean restarted;
    public float[] x;	
    public float[] y;
    
    static public int MAX_TRACK_LENGTH = 10000; 
}
