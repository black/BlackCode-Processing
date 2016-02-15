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

import javax.media.opengl.*;

class KLTTrackerBase
 {
    KLTTrackerBase(int nIterations, int nLevels, int levelSkip, int windowWidth)
    {
    	gl = Util.gl;
        this.nIterations = nIterations; 
        this.nLevels = nLevels;
        this.levelSkip = (levelSkip > 0) ? levelSkip : (nLevels-1);
        this.windowWidth = windowWidth;
        width = 0;
        height = 0; 
        featureWidth = 0; 
        featureHeight = 0;
        margin = 5;
        convergenceThreshold = 0.01f; 
        SSDThreshold = 1000.0f;
    }

    void setBorderMargin(float margin) { this.margin = margin; }
    void setConvergenceThreshold(float thr) { convergenceThreshold = thr; }
    void setSSDThreshold(float thr) { SSDThreshold = thr; }

    void swapFeatureBuffers() 
    { 
    	RTTBuffer tmp = featuresBuffer0;
    	featuresBuffer0 = featuresBuffer1;
    	featuresBuffer1 = tmp;
    }

    int sourceFeatureTexID() { return featuresBuffer0.textureID(); }
    int targetFeatureTexID() { return featuresBuffer1.textureID(); }

    void activateTargetFeatures() { featuresBuffer1.activate(); }

    protected GL gl;
    protected int nIterations, nLevels, levelSkip, windowWidth;
    protected int width, height, featureWidth, featureHeight;
    protected float margin, convergenceThreshold, SSDThreshold;
    protected RTTBuffer featuresBuffer0, featuresBuffer1;
}
