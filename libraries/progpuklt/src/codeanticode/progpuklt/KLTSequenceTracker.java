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

import processing.core.*;
import java.util.*;

class KLTSequenceTracker
{
    KLTSequenceTracker(KLTSequenceTrackerConfig config)
    {
        this.config = config;
    	trackWithGain = config.trackWithGain;
        detector = new KLTDetector(config.minDistance);
        tracker = null;
    	trackerWithGain = null;
         
    	pyrCreatorA = new PyramidWithDerivativesCreator();
    	pyrCreatorB = new PyramidWithDerivativesCreator();
    	
        pyrCreator0 = pyrCreatorA;
        pyrCreator1 = pyrCreatorB;
    }

    void allocate(int width, int height, int nLevels,
                  int featuresWidth, int featuresHeight,
                  int kernelSize, boolean explicitLod)
    {
        allocate(width, height, nLevels, featuresWidth, featuresHeight, 
        		 2*featuresWidth, 2*featuresHeight, kernelSize, explicitLod);
    }
    
    void allocate(int width, int height, int nLevels,
                  int featuresWidth, int featuresHeight,
                  int pointListWidth, int pointListHeight,
                  int kernelSize, boolean explicitLod)
    {
        this.width = width;
        this.height = height;
        this.featuresWidth = featuresWidth;
        this.featuresHeight = featuresHeight;
        this.pointListWidth = pointListWidth;
        this.pointListHeight = pointListHeight;

        pyrCreatorA.allocate(width, height, nLevels, kernelSize);
        pyrCreatorB.allocate(width, height, nLevels, kernelSize);

    	if (trackWithGain)
    	{
    	    trackerWithGain = new KLTTrackerWithGain(config.nIterations, config.nLevels,
    	                                             config.levelSkip, config.windowWidth);
    	    trackerWithGain.allocate(width, height, featuresWidth, featuresHeight);
    	    trackerWithGain.setBorderMargin(config.trackBorderMargin);
    	    trackerWithGain.setConvergenceThreshold(config.convergenceThreshold);
    	    trackerWithGain.setSSDThreshold(config.SSDThreshold);
    	}
    	else
    	{
    	    tracker = new KLTTracker(config.nIterations, config.nLevels,
    	                             config.levelSkip, config.windowWidth);
    	    tracker.allocate(width, height, featuresWidth, featuresHeight);
    	    tracker.setBorderMargin(config.trackBorderMargin);
    	    tracker.setConvergenceThreshold(config.convergenceThreshold);
    	    tracker.setSSDThreshold(config.SSDThreshold);
    	}
    	
    	detector.allocate(width, height, pointListWidth, pointListHeight);
        corners = new float[pointListWidth * pointListHeight * 3];
    }
    
    void deallocate()
    {
        pyrCreatorA.deallocate();
    	pyrCreatorB.deallocate();
    	
    	if (tracker != null)
    	{
    	    tracker.deallocate();
    	}
    	else
    	{
    	    trackerWithGain.deallocate();
    	}

    	detector.deallocate();
    }

    void setBorderMargin(float margin)
    {
        if (tracker != null)
            tracker.setBorderMargin(margin);
        else
            trackerWithGain.setBorderMargin(margin);

        detector.setBorderMargin(margin);
    }

    void setConvergenceThreshold(float thr)
    {
        if (tracker != null)
            tracker.setConvergenceThreshold(thr);
        else
            trackerWithGain.setConvergenceThreshold(thr);
    }

    void setSSDThreshold(float thr)
    {
        if (tracker != null)
            tracker.setSSDThreshold(thr);
        else
            trackerWithGain.setSSDThreshold(thr);
    }

    int detect(int texID, KLTTrackedFeature[] dest)
    {
    	pyrCreator1.buildPyramidForGrayscaleImage(texID);
    	return detect(dest); 
    }
        
    int detect(int[] image, KLTTrackedFeature[] dest)
    {
    	pyrCreator1.buildPyramidForGrayscaleImage(image);
    	return detect(dest); 
    }
    
    int detect(KLTTrackedFeature[] dest)
    {
    	int nFeatures = featuresWidth * featuresHeight;
        int nDetectedFeatures;

    	nDetectedFeatures = detector.detectCorners(config.minCornerness, pyrCreator1.textureID());
        
    	// To avoid buffer overflows.
    	nDetectedFeatures = PApplet.min(nDetectedFeatures, pointListWidth * pointListHeight);    	
    	detector.extractCorners(nDetectedFeatures, corners);
    	
    	if (nDetectedFeatures >= nFeatures)
    	{
    	    sortCorners(nDetectedFeatures);
    	    nDetectedFeatures = nFeatures;
    	}

    	shuffleCorners(nDetectedFeatures);
    	
    	if (trackWithGain)
    	{
    	    for (int i = 0; i < nFeatures; ++i)
    	        corners[3 * i + 2] = 1.0f;
    	    trackerWithGain.provideFeaturesAndGain(corners);
    	}
    	else
    	    tracker.provideFeatures(corners);

        for (int i = 0; i < nDetectedFeatures; ++i)
    	{
    	    dest[i].status = 1;
    	    dest[i].pos[0] = corners[3 * i + 0];
    	    dest[i].pos[1] = corners[3 * i + 1];
    	    dest[i].gain   = corners[3 * i + 2];
    	}

        for (int i = nDetectedFeatures; i < nFeatures; ++i) dest[i].status = -1;
        
        return nDetectedFeatures;
    }
    
    int redetect(int texID, KLTTrackedFeature[] dest)
    {
        int nPresentFeatures = track(texID, dest);
    	return redetect(dest, nPresentFeatures);
    }
    
    int redetect(int[] image, KLTTrackedFeature[] dest)
    {
        int nPresentFeatures = track(image, dest);
    	return redetect(dest, nPresentFeatures);
    }
    
    int redetect(KLTTrackedFeature[] dest, int nPresentFeatures)
    {
    	int nFeatures = featuresWidth * featuresHeight;
    	int nNewFeatures = 0;
    	
    	for (int i = 0; i < nFeatures; ++i)
    	{
    	    corners[3 * i + 0] = dest[i].pos[0];
    	    corners[3 * i + 1] = dest[i].pos[1];
        }

    	nNewFeatures = detector.detectCorners(config.minCornerness, pyrCreator1.textureID(), nFeatures, corners);
        //To avoid overflow during readback
    	nNewFeatures = PApplet.min(nNewFeatures, pointListWidth * pointListHeight);
    	detector.extractCorners(nNewFeatures, corners);
    	
    	if (nNewFeatures >= nFeatures - nPresentFeatures)
    	{
    	    // Sort wrt. the cornerness value, take only the most distinctive corners.
    	    sortCorners(nNewFeatures);
    	    nNewFeatures = nFeatures - nPresentFeatures;

    	    shuffleCorners(nNewFeatures);
        }

    	int k = 0;
    	for (int i = 0; i < nFeatures && k < nNewFeatures; ++i)
    	{
    	    if (dest[i].status < 0)
    	    {
    	        // Empty slot to fill.
    	        dest[i].status = 1;
    	        dest[i].pos[0] = corners[3 * k + 0];
    	        dest[i].pos[1] = corners[3 * k + 1];
    	        dest[i].gain   = corners[3 * k + 2];
    	        ++k;
    	    }
    	}

    	for (int i = 0; i < nFeatures; ++i)
    	{
    	    corners[3 * i + 0] = dest[i].pos[0];
    	    corners[3 * i + 1] = dest[i].pos[1];
    	    corners[3 * i + 2] = 1.0f;
    	}

    	if (trackWithGain)
    	    trackerWithGain.provideFeaturesAndGain(corners);
    	else
    	    tracker.provideFeatures(corners);
    	
    	return nNewFeatures;
    }
    
    int track(int texID, KLTTrackedFeature[] dest)
    {
    	pyrCreator1.buildPyramidForGrayscaleImage(texID);
    	return track(dest);
    }
    
    int track(int[] image, KLTTrackedFeature[] dest)
    {
        pyrCreator1.buildPyramidForGrayscaleImage(image);
        return track(dest);
    }

    int track(KLTTrackedFeature[] dest)
    {
    	int nFeatures = featuresWidth * featuresHeight;
        int nPresentFeatures = 0;
        
        if (trackWithGain)
    	{
    	    trackerWithGain.trackFeaturesAndGain(pyrCreator0.textureID(), pyrCreator1.textureID());
    	    trackerWithGain.readFeaturesAndGain(corners);
    	}
    	else
    	{
    	    tracker.trackFeatures(pyrCreator0.textureID(), pyrCreator1.textureID());
    	    tracker.readFeatures(corners);
    	}
        
    	// Update point tracks.
    	for (int i = 0; i < nFeatures; ++i)
    	{
    	    float X = corners[3 * i + 0];
    	    float Y = corners[3 * i + 1];
    	    float gain = corners[3 * i + 2];

    	    if (X >= 0)
    	    {
    	        dest[i].status = 0;
    	        dest[i].pos[0] = X;
    	        dest[i].pos[1] = Y;
    	        dest[i].gain   = gain;
    	        ++nPresentFeatures;
    	    }
    	    else
    	        dest[i].status = -1;
        }
    	
    	return nPresentFeatures;    	
    }
    
    void advanceFrame()
    {
        if (tracker != null)
            tracker.swapFeatureBuffers();
        else
            trackerWithGain.swapFeatureBuffers();

        PyramidWithDerivativesCreator tmp = pyrCreator0;
        pyrCreator0 = pyrCreator1;
        pyrCreator1 = tmp;
    }

    int getCurrentFrameTextureID() { return pyrCreator1.sourceTextureID(); }

    // Sorts the corners between 0 and maxCont using the QuickSort algorithm,
    // adapted from here: http://www.cs.princeton.edu/introcs/42sort/QuickSort.java.html
    void sortCorners(int maxCount)
    {
    	// shuffleCorners(maxCount) do we need this?
    	quicksortCorners(0, maxCount - 1);
    }

    protected void quicksortCorners(int left, int right)
    {
        if (right <= left) return;
        int i = partition(left, right);
        quicksortCorners(left, i-1);
        quicksortCorners(i+1, right);    	
    }
    
    // partition corners[left] to corners[right], assumes left < right
    protected int partition(int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (compareCorners(++i, right))      // find item on left to swap
                ;                                   // corners[3 * right + 2] acts as sentinel
            while (compareCorners(right, --j))      // find item on right to swap
                if (j == left) break;               // don't go out-of-bounds
            if (i >= j) break;                      // check if pointers cross
            
            swapCorners(i, j);                      // swap two elements into place
        }
        
        swapCorners(i, right);                      // swap with partition element
        
        return i;
    }
    
    // is corners[3 * i + 2] > corners[3 * j + 2] ?
    protected boolean compareCorners(int i, int j) 
    {
        return corners[3 * i + 2] > corners[3 * j + 2];
    }

    // exchange corners[3 * i + 0], corners[3 * i + 1], corners[3 * i + 2] with
    //          corners[3 * j + 0], corners[3 * j + 1], corners[3 * j + 2]
    protected void swapCorners(int i, int j) 
    {
        float temp0 = corners[3 * i + 0];
        float temp1 = corners[3 * i + 1];
        float temp2 = corners[3 * i + 2];
        
        corners[3 * i + 0] = corners[3 * j + 0]; 
        corners[3 * i + 1] = corners[3 * j + 1];
        corners[3 * i + 2] = corners[3 * j + 2];
        
        corners[3 * j + 0] = temp0; 
        corners[3 * j + 1] = temp1;
        corners[3 * j + 2] = temp2;
    }    
    
    // Shuffles the corners between 0 and maxCount using the unbiased Fisher-Yates shuffle  
    // algorithm (http://en.wikipedia.org/wiki/Fisher-Yates_shuffle).
    protected void shuffleCorners(int maxCount)
    {
    	Random rng = new Random();
    	int n = maxCount;
        while (n > 1) 
        {
            int k = rng.nextInt(n);  // 0 <= k < n.
            n--;                     // n is now the last pertinent index
            swapCorners(n, k);       // Swaps corners n and k (does nothing if b == k)    
        }    	
    }

    protected KLTSequenceTrackerConfig config;

    protected boolean trackWithGain;

    protected int width, height;
    protected int featuresWidth, featuresHeight;
    protected int pointListWidth, pointListHeight;

    protected PyramidWithDerivativesCreator pyrCreatorA;
    protected PyramidWithDerivativesCreator pyrCreatorB;
    protected KLTDetector detector;

    protected KLTTracker         tracker;
    protected KLTTrackerWithGain trackerWithGain;

    protected PyramidWithDerivativesCreator pyrCreator0;
    protected PyramidWithDerivativesCreator pyrCreator1;

    protected float[] corners;
}
