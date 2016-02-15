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

class KLTSequenceTrackerConfig
{
	KLTSequenceTrackerConfig()
	{
        nIterations = 10; // Iterations in the tracker
        nLevels = 4; // Levels used in the coarse-to-fine approach
        levelSkip = 3; // Levels skipped, reasonable values are 1 (all levels) and nLevels-1 (2 levels)
        windowWidth = 7; // Size of the floating window
        trackBorderMargin = 10.0f; // In pixels
        convergenceThreshold = 0.5f; // In pixels
        SSDThreshold = 80000.0f;
        trackWithGain = true;
        minDistance = 10; // Radius of non-max feature suppression
        minCornerness = 50.0f;
        detectBorderMargin = 10.0f; // In pixels
        kernelSize = 1;
        explicitLod = false;
    }

	KLTSequenceTrackerConfig(FeatureTrackerParameters params)
	{
        nIterations = params.nIterations;
        nLevels = params.nLevels;
        levelSkip = params.levelSkip;
        windowWidth = params.windowWidth;
        trackBorderMargin = params.trackBorderMargin;
        convergenceThreshold = params.convergenceThreshold;
        SSDThreshold = params.SSDThreshold;
        trackWithGain = params.trackWithGain;
        minDistance = params.minDistance;
        minCornerness = params.minCornerness;
        detectBorderMargin = params.detectBorderMargin;
        kernelSize = params.kernelSize;
        explicitLod = params.explicitLod;
    }
		
	int   nIterations, nLevels, levelSkip, windowWidth, kernelSize;
	float trackBorderMargin, convergenceThreshold, SSDThreshold;
	boolean  trackWithGain;
	boolean explicitLod;

	int   minDistance;
	float minCornerness, detectBorderMargin;
}
