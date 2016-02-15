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
 * This class encapsulates the parameters of the GPU-KLT algorithm.
 * @author Andres Colubri
 *
 */
public class FeatureTrackerParameters
{
	/**
	 * 
	 * Default constructor of the class. Sets the parameters to their default values.
	 * 
	 */
	public FeatureTrackerParameters()
	{
		this.nIterations = 10;
		this.nLevels     = 4;
		this.levelSkip   = 1;
		this.windowWidth = 7;		
		this.trackBorderMargin    = 20.0f;
		this.convergenceThreshold = 0.5f;
		this.SSDThreshold         = 80000.0f;
		this.trackWithGain        = true;
		this.minDistance   = 10;
		this.minCornerness = 50.0f;
		this.detectBorderMargin = trackBorderMargin;		
        this.kernelSize  = 1;
        this.explicitLod = false;
		this.nTrackedFrames = 10;
	}

	/**
	 * 
	 * This constructor allows to set the number nLevels of levels and disable/enable
	 * gain tracking.
	 * 
	 */	
	public FeatureTrackerParameters(int nLevels, boolean trackWithGain)
	{
		this.nIterations = 10;
		this.nLevels     = nLevels;
		this.levelSkip   = 1;
		this.windowWidth = 7;
		this.trackBorderMargin    = 20.0f;
		this.convergenceThreshold = 0.1f;
		this.SSDThreshold         = 1000.0f;
		this.trackWithGain        = trackWithGain;
		this.minDistance   = 10;
		this.minCornerness = 50.0f;
		this.detectBorderMargin = trackBorderMargin;		
        this.kernelSize  = 1;
        this.explicitLod = false;		
		this.nTrackedFrames = 10;	
	}
	
	/**
	 * Number of updates on the position to solve the actually non-linear minimization problem 
	 * (i.e. number of inner iterations). 
	 */
	public int nIterations; 
	/**
	 * Number of pyramid levels generated and used for tracking. 
	 */
	public int nLevels;
	/**
	 * After getting an initial estimate of the new feature position in the previous pyramid level,
	 * the next level used for refinement is level+levelSkip. reasonable values are 1 (all levels) 
	 * and nLevels-1 (2 levels). With levelSkip equal to 1 all pyramid levels are fully evaluated; 
	 * levelSkip = nLevels-1, only the coarsest and the full resolution level are used.
	 */
	public int levelSkip;
	/**
	 * Size of the window around the feature used for the position update. The used window is 
	 * windowWidth x windowWidth with the feature in the center.
	 */
	public int windowWidth;
	/**
	 * Features very close to the image border are discarded. You want to have at least 
	 * trackBorderMargin>=windowWidth/2 in order to have the feature completely within the image. 
	 */
	public float trackBorderMargin;
	/**
	 * If the position update after the inner iterations (after nIterations steps) has not converged 
	 * (i.e. the length of the update > convergenceThreshold in the final iteration), discard the 
	 * feature. It is measured in pixels.
	 */	
	public float convergenceThreshold;
	/**
	 * If the error function we are minimizing (with respect to the position update) is not small 
	 * enough (i.e. >SSDThreshold), discard the feature (no similarly looking patch found). 
	 */	
	public float SSDThreshold;
	/**
	 * Should simultaneous gain estimation be enabled (allows some variation in brightness in the 
	 * images) or not (faster tracking). 
	 */
	public boolean trackWithGain;
	/**
	 * Suppress corners/features that are two close. If two corners have distance < minDistance, 
	 * then the weaker one is suppressed. This enables a reasonable uniform distribution of features in the image. 
	 */
	public int minDistance;
	/**
	 * The minimum value of the cornerness required for a pixel to classify as corner. Highly textured areas have 
	 * larger cornerness. 
	 */
	public float minCornerness;
	/**
	 * Suppress corners very close to the image borders. Setting detectBorderMargin = trackBorderMargin is fine.
	 */	
	public float detectBorderMargin;
	/**
	 * Kernel size used in the pyramid with derivatives generation.
	 */
	public int kernelSize;

	/**
	 * Enables explicit LOD in the pyramid generation (w/out derivatives).
	 */
	public boolean explicitLod;
	/**
	 * Interval for feature re-detection.
	 */
	public int nTrackedFrames;
}
