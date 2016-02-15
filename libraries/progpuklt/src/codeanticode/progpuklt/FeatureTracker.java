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
import processing.opengl.*;

/**
 * 
 * This class encapsulates the GPU implementation of the KLT feature tracker, developed 
 * by Christopher Zach at the University of North Carolina at Chapel Hill.
 * Features are defined to be corners points (pixels with large intensity variations along
 * the x and y directions) found in the image, and the algorithm is able to follow these 
 * feature points through successive frames, until they disappear and are replaced by new
 * features.
 * This GPU implementation is substantially faster than a regular CPU version.
 * More information about the KLT tracker can be found in the following pages:<br>
 * <a href="http://cs.unc.edu/~cmzach/opensource.html">Christopher Zach's GPU-KLT page</a><br>
 * <a href="http://www.ces.clemson.edu/~stb/klt">CPU implementation of KLT by Stan Birchfield</a><br>
 * <a href="http://en.wikipedia.org/wiki/Lucas%E2%80%93Kanade_method">Wikipedia entry about the KLT algorithm</a><br> 
 * <a href="http://visual.ipan.sztaki.hu/pivweb/node5.html">Page about KLT tracker in computer vision tutorial</a><br> 
 * @author Andres Colubri
 * 
 */
public class FeatureTracker
{
    /**
     * 
     * Creates an instance of FeatureTracker to track nFeatures features on images with 
     * a resolution of imgWidth x imgHeight pixels. The actual number nFeatures that the
     * tracker uses is adjusted from the input value to the closest number that can
     * be expressed as a power-of-two integer. This adjusted value can be obtained by
     * calling the getNFeatures() method.
     * @param parent PApplet
     * @param imgWidth int 
     * @param imgHeight int  
     * @param nFeatures int
     * @see getNFeatures
     *  
     */
	public FeatureTracker(PApplet parent, int imgWidth, int imgHeight, int nFeatures)
	{
		init(parent, imgWidth, imgHeight, nFeatures, new FeatureTrackerParameters());
	}

    /**
     * 
     * Creates an instance of FeatureTracker to track nFeatures features on images with 
     * a resolution of imgWidth x imgHeight pixels. The actual number nFeatures that the
     * tracker uses is adjusted from the input value to the closest number that can
     * be expressed as a power-of-two integer. This adjusted value can be obtained by
     * calling the getNFeatures() method. Further tracker parameters can be 
     * set with the params object.
     * @param parent PApplet
     * @param imgWidth int 
     * @param imgHeight int  
     * @param nFeatures int
     * @param params FeatureTrackerParameters
     * @see getNFeatures 
     * @see FeatureTrackerParameters
     *    
     */
	public FeatureTracker(PApplet parent, int imgWidth, int imgHeight, int nFeatures,
			              FeatureTrackerParameters params)
	{
		init(parent, imgWidth, imgHeight, nFeatures, params);		
	}

    /**
     * 
     * Sets the maximum length of a feature track (in frames), i.e., how many post positions
     * will be stored for a given feature point until it disappears from the image. If a   
     * feature points exists longer than this value, the first mLength - 1 recorded positions
     * are kept, but the last one is constantly updated to be the current feature position,
     * thus discarding all the past positions between mLength - 1 and the current one.
     * This value cannot be updated unless the tracker object is re-created.     
     * @param mLength int
     *  
     */	
	static public void setMaxTrackLength(int mLength)
	{
	    FeatureTrack.MAX_TRACK_LENGTH = mLength;
	}

	/**
	 * 
	 * Track features in the image stored in the OpenGL texture with id texID. This is
	 * usually much faster than using the track() method with the PImage or int[] parameters,
	 * since those require transferring the image from CPU to GPU memory.  
	 * @param texID int
	 * 
	 */
	public void track(int texID)
	{
	    beginTracking();
	    runTracking(texID);
	    endTracking();	
	}

	/**
	 * 
	 * Track features in the image stored in the PImage object. 
	 * @param image PImage
	 * 
	 */	
	public void track(PImage image)
	{
		image.loadPixels();
	    track(image.pixels);
	}

	/**
	 * 
	 * Track features in the image stored in the pixels array. 
	 * @param pixels int[]
	 * 
	 */	
	public void track(int[] pixels)
	{
	    beginTracking();
	    runTracking(pixels);
	    endTracking();
	}
	
    /**
     * 
     * Stops the tracker.
     * 	
     */
	public void stop()
	{
        FrameBufferObject.disableFBORendering();
        tracker.deallocate();
	}
	
	/**
	 * 
	 * Returns the number of features being detected and tracked.
	 * @return int
	 * 
	 */
	public int getNFeatures()
	{
		return nFeatures;
	}

	/**
	 * 
	 * Returns the number of detected features when running the track method for the first time.
	 * @return int
	 * 
	 */
	public int getNDetectedFeatures()
	{
		return nDetectedFeatures;
	}

	/**
	 * 
	 * Returns the number of re-detected features. The re-detection occurs once every 
	 * FeatureTrackerParameters.nTrackedFrames frames. 
	 * @return int
	 * @see FeatureTrackerParameters
	 * 
	 */	
	public int getNNewFeatures()
	{
		return nNewFeatures;
	}	
	
	/**
	 * 
	 * Returns the number of present features being tracked. This number could be different
	 * than the number of originally detected features, because new feature points can
	 * appear (identified when re-detecting) or old points could disappear.
	 * @return int
	 * 
	 */
	public int getNPresentFeatures()
	{
		return nPresentFeatures;
	}

	/**
	 * 
	 * Returns the supported image width.
	 * @return int 
	 * 
	 */
	public int getWidth()
	{
	    return width;	
	}

	/**
	 * 
	 * Returns the supported image height.
	 * @return int 
	 * 
	 */	
	public int getHeight()
	{
	    return height;	
	}
	
	/**
	 * 
	 * Returns the maximum track length.
	 * @return int 
	 * 
	 */	
	public int getMaxTrackLength()
	{
	    return FeatureTrack.MAX_TRACK_LENGTH;
	}
	
	/**
	 * 
	 * Returns the current track length of feature i.
	 * @param i int
	 * @return int 
	 * 
	 */	
	public int getTrackLength(int i)
	{
	    return tracks[i].length;
	}

	/**
	 * 
	 * Returns true or false depending on weather feature i has been restarted or not.
	 * @param i int
	 * @return int 
	 * 
	 */	
	public boolean trackRestarted(int i)
	{
	    return tracks[i].restarted;
	}	
	
	/**
	 * 
	 * Returns the current x coordinate feature i.
	 * @param i int
	 * @return float 
	 * 
	 */	
	public float getPosX(int i)
	{
	    return tracks[i].x[tracks[i].length - 1];		
	}
	
	/**
	 * 
	 * Returns the current y coordinate feature i.
	 * @param i int
	 * @return float 
	 * 
	 */	
	public float getPosY(int i)
	{
	    return tracks[i].y[tracks[i].length - 1];		
	}
	
	/**
	 * 
	 * Returns the x coordinate of position of feature i at frame t.
	 * @param i int
	 * @param t int 
	 * @return float 
	 * 
	 */
	public float getTrackX(int i, int t)
	{
	    return tracks[i].x[t];
	}

	/**
	 * 
	 * Returns the y coordinate of position of feature i at frame t.
	 * @param i int
	 * @param t int 
	 * @return float 
	 * 
	 */	
	public float getTrackY(int i, int t)
	{
	    return tracks[i].y[t];		
	}

	/**
	 * 
	 * Returns the entire track of feature i.
	 * @param i int
	 * @return FeatureTrack
	 * @see FeatureTrack 
	 * 
	 */	
	public FeatureTrack getTrack(int i)
	{
	    return tracks[i];		
	}

	/**
	 * 
	 * Draws the tracks for all the feature points using lines connecting successive
	 * positions. The track are drawn inside a rectangle located at (0, 0) and covering
	 * the entire sketch screen. 
	 * 
	 */
	public void drawTracks()
	{
		drawTracks(0, 0, parent.width, parent.height);
	}

	/**
	 * 
	 * Draws the tracks for all the feature points using lines connecting successive
	 * positions. The track are drawn inside a rectangle located at (x, y) of size w x h.
	 * @param x float
	 * @param y float
	 * @param w float
	 * @param h float
	 * 
	 */	
	public void drawTracks(float x, float y, float w, float h)
	{
		int n;
		float x0, y0, x1, y1;
		for (int i = 0; i < nFeatures; ++i)
		{
			n = tracks[i].length;
			if (0 < n)
			{
				x0 = PApplet.map(tracks[i].x[0], 0, 1, x, x + w);
				y0 = PApplet.map(tracks[i].y[0], 0, 1, y, y + h);
			    for (int t = 1; t < tracks[i].length; t++)				
		        {
					x1 = PApplet.map(tracks[i].x[t], 0, 1, x, x + w);
					y1 = PApplet.map(tracks[i].y[t], 0, 1, y, y + h);
			    	parent.line(x0, y0, x1, y1);
			    	x0 = x1;
			    	y0 = y1;
		        }
			}
		}
	}

	/**
	 * 
	 * Draws the current positions of all the feature points using circles of diameter diam. 
	 * The features are drawn inside a rectangle located at (0, 0) and covering the entire 
	 * sketch screen.
	 * @param diam float 
	 * 
	 */
	public void drawFeatures(float diam)
	{
		drawFeatures(0, 0, parent.width, parent.height, diam);
	}
	
	/**
	 * 
	 * Draws the current positions of all the feature points using circles of diameter diam. 
	 * The features are drawn inside a rectangle located at (x, y) of size w x h.
	 * @param diam float
	 * @param x float
	 * @param y float
	 * @param w float
	 * @param h float  
	 * 
	 */
	public void drawFeatures(float x, float y, float w, float h, float diam)
	{ 
	    int n;
	    float xf, yf;
		for (int i = 0; i < nFeatures; i++)
		{
			n = tracks[i].length;
			if (0 < n)
			{
		        xf = PApplet.map(tracks[i].x[n - 1], 0, 1, x, x + w);
		        yf = PApplet.map(tracks[i].y[n - 1], 0, 1, y, y + h);
		        parent.ellipse(xf, yf, diam, diam);
			}
		}
	}
	
	protected void updateTracks()
	{
	    for (int i = 0; i < nFeatures; ++i)
	    {
	    	if (features[i].status != 0)
	    	{
	    		tracks[i].length = 0;
	    		tracks[i].restarted = true;
	    	}
	    	if (features[i].status >= 0) 
	    	{
	    		tracks[i].add(features[i].pos[0], features[i].pos[1]);
	    	    tracks[i].restarted = false;
	    	}
	    }	
	}

	// Generates a power-of-two box width and height so that width * height is closest to nfeat.
    protected void calculateNFeatures(int nfeat)
    {
        int w, h;
        float l = PApplet.sqrt(nfeat);
        for (w = 2; w < l; w *= 2);
        int n0 = w * w;
        int n1 = w * w / 2;
        if (PApplet.abs(n0 - nfeat) < PApplet.abs(n1 - nfeat)) h = w;
        else h = w / 2;
        
        featuresWidth = w;
        featuresHeight = h;
        nFeatures = featuresWidth * featuresHeight;
    }		   

    protected void init(PApplet parent, int imgWidth, int imgHeight, int nFeatures,
		                FeatureTrackerParameters params)
    {
        this.parent = parent;
    	calculateNFeatures(nFeatures);
    	width = imgWidth; 
    	height = imgHeight;
    	
    	pgl = null;
        try
        {
            pgl = (PGraphicsOpenGL)parent.g;
        } 
        catch (ClassCastException cce)
        {
            System.err.println("Error: GPUKLT can only work with OpenGL sketches.");
            return;
        }
        
        parameters = params;
        util = new Util(parent, pgl.gl);

        features = new KLTTrackedFeature[this.nFeatures];
        tracks = new FeatureTrack[this.nFeatures];
        for (int i = 0; i < this.nFeatures; i++)
        {
        	features[i] = new KLTTrackedFeature();
        	tracks[i] = new FeatureTrack();
        }
        
        CgFragmentProgram.initializeCg();
        tracker = new KLTSequenceTracker(new KLTSequenceTrackerConfig(parameters));
        tracker.allocate(width, height, parameters.nLevels, 
        		         featuresWidth, featuresHeight, 
        		         parameters.kernelSize, parameters.explicitLod);
        
        nDetectedFeatures = 0;
    	nNewFeatures = 0;
    	nPresentFeatures = 0;
        
        frameCount = 0;
    }

    protected void beginTracking()
    {
		pgl.beginGL();
	    
	    Util.saveGLState();
	    Util.disableBlend();		
    }
    
    protected void runTracking(int texID)
    {
	    if (frameCount == 0)
	    {
	    	nDetectedFeatures = tracker.detect(texID, features);
	    }
	    else if (0 < parameters.nTrackedFrames && frameCount % parameters.nTrackedFrames == 0)
	    {
	    	nNewFeatures = tracker.redetect(texID, features);
	    }
	    else
	    {
	    	nPresentFeatures = tracker.track(texID, features);
	    }    	
    }
    
    protected void runTracking(int[] pixels)
    {
	    if (frameCount == 0)
	    {
	    	nDetectedFeatures = tracker.detect(pixels, features);
	    }
	    else if (0 < parameters.nTrackedFrames && frameCount % parameters.nTrackedFrames == 0)
	    {
	    	nNewFeatures = tracker.redetect(pixels, features);
	    }
	    else
	    {
	    	nPresentFeatures = tracker.track(pixels, features);
	    }    	
    }
    
    protected void endTracking()
    {
	    updateTracks();

        Util.gl.glFinish();

        FrameBufferObject.disableFBORendering();
        
        tracker.advanceFrame();

	    frameCount++;
	    
	    Util.restoreGLState();
	    
	    pgl.endGL();
    }        
    
    protected PApplet parent;
    protected PGraphicsOpenGL pgl; 
    
	protected FeatureTrackerParameters parameters;
    
	protected KLTSequenceTracker tracker;
	protected KLTTrackedFeature[] features;
	protected FeatureTrack[] tracks;
	protected Util util;
	
	protected int width;
	protected int height;
	
	protected int featuresWidth;
	protected int featuresHeight;
	protected int nFeatures;
	
	protected int frameCount;
	
	protected int nDetectedFeatures;
	protected int nNewFeatures;
	protected int nPresentFeatures;
}
