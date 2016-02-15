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
import com.sun.opengl.util.*;
import java.nio.*;

class KLTTrackerWithGain extends KLTTrackerBase
{
	KLTTrackerWithGain()
	{
        super(5, 4, -1, 7);
        init();
	}

    KLTTrackerWithGain(int nIterations, int nLevels, int levelSkip, int windowWidth)
    {
        super(nIterations, nLevels, levelSkip, windowWidth);
        init();
    }

    void init()
    {
        featuresBufferA = new RTTBuffer("rgb=32f", "KLTTrackerWithGain.featuresBufferA");
        featuresBufferB = new RTTBuffer("rgb=32f", "KLTTrackerWithGain.featuresBufferB");
        featuresBufferC = new RTTBuffer("rgb=32f", "KLTTrackerWithGain.featuresBufferC");
        trackingShader = null;
        featuresBuffer0 = featuresBufferA;
        featuresBuffer1 = featuresBufferB;
        featuresBuffer2 = featuresBufferC;   	
    }

    void allocate(int width, int height, int featureWidth, int featureHeight)
    {
        this.width = width;
        this.height = height;
        this.featureWidth = featureWidth;
        this.featureHeight = featureHeight;
        
    	featuresBufferA.allocate(featureWidth, featureHeight);
    	featuresBufferB.allocate(featureWidth, featureHeight);
    	featuresBufferC.allocate(featureWidth, featureHeight);
    	Util.checkGLErrors("KLTTrackerWithGain.allocate()");

    	featuresBuffer0 = featuresBufferA;
    	featuresBuffer1 = featuresBufferB;    	
    }
    
    void deallocate()
    {
        Util.checkGLErrors("begin KLTTrackerWithGain.deallocate()");
       	featuresBufferA.deallocate();
       	featuresBufferB.deallocate();
       	featuresBufferC.deallocate();       	
        Util.checkGLErrors("end KLTTrackerWithGain.deallocate()");    	  	   
    }

    void provideFeaturesAndGain(float[] features)
    {
        featuresBuffer2.bindTexture(GL.GL_TEXTURE0);
        gl.glTexSubImage2D(featuresBuffer2.textureTarget(),
    	                   0, 0, 0, featureWidth, featureHeight,
    	                   GL.GL_RGB, GL.GL_FLOAT, FloatBuffer.wrap(features));
    	featuresBuffer2.unbindTexture();
    	featuresBuffer1.bindTexture(GL.GL_TEXTURE0);
    	gl.glTexSubImage2D(featuresBuffer1.textureTarget(),
    	                   0, 0, 0, featureWidth, featureHeight,
    	                   GL.GL_RGB, GL.GL_FLOAT, FloatBuffer.wrap(features));
    	featuresBuffer1.unbindTexture();
    	//gl.glBindTexture(featuresBuffer1.textureTarget(), 0);
    }
    
    void readFeaturesAndGain(float[] features)
    {
        featuresBuffer2.activate();
    	FloatBuffer readFeaturesBuffer = BufferUtil.newFloatBuffer(features.length);
    	gl.glReadPixels(0, 0, featureWidth, featureHeight, GL.GL_RGB, GL.GL_FLOAT, readFeaturesBuffer);
    	readFeaturesBuffer.get(features);
    }
    
    void trackFeaturesAndGain(int pyrTex0, int pyrTex1)
    {
        if (trackingShader == null)
    	{
    	    trackingShader = new CgFragmentProgram("KLTTrackerWithGain.trackingShader");
    	    trackingShader.setProgramFromFile("klt_tracker_with_gain.cg");
    	    
    	    String[] args = { "-DHALF_WIDTH=" + (int)(windowWidth/2), null };
            trackingShader.compile(args);
             
            Util.checkGLErrors("trackingShader creation");
    	}        

    	Util.setupNormalizedProjection();

    	featuresBuffer0.activate();
    	gl.glColorMask(false, false, true, false);
    	gl.glClearColor(0, 0, 1, 0);
    	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    	gl.glColorMask(true, true, true, true);
    	
    	featuresBuffer2.bindTexture(GL.GL_TEXTURE3);
    	gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);    	

    	gl.glActiveTexture(GL.GL_TEXTURE1);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, pyrTex0);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    	//gl.glEnable(GL.GL_TEXTURE_2D);
    	//gl.glBindTexture(GL.GL_TEXTURE_2D, 0);    	

    	gl.glActiveTexture(GL.GL_TEXTURE2);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, pyrTex1);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    	//gl.glEnable(GL.GL_TEXTURE_2D);
    	//gl.glBindTexture(GL.GL_TEXTURE_2D, 0);    	

    	float delta = 200.0f;
    	float tau = 1.0f;

    	trackingShader.parameter("wh", width, height);
    	trackingShader.parameter("sqrConvergenceThreshold", 1000000.0f);
    	trackingShader.parameter("SSD_Threshold", 1000000.0f);
    	trackingShader.parameter("validRegion", -1.0f, -1.0f, 2.0f, 2.0f);
    	trackingShader.parameter("lambda", 1.0f);
    	trackingShader.parameter("ds0", 1.0f / featureWidth, 1.0f / featureHeight);
    	trackingShader.enable();

    	for (int level = nLevels - 1; level >= 0; level -= levelSkip)
    	{
    	    int w = width >> level;
    	    int h = height >> level;
    	    float ds = 1.0f / w;
    	    float dt = 1.0f / h;

    	    trackingShader.parameter("ds", ds, dt);

    	    gl.glActiveTexture(GL.GL_TEXTURE1);
    	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BASE_LEVEL, level);

    	    gl.glActiveTexture(GL.GL_TEXTURE2);
    	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BASE_LEVEL, level);

    	    for (int iter = 1; iter <= nIterations; ++iter)
    	    {
    	        trackingShader.parameter("delta", delta);
    	        delta *= tau;
    	        if (iter == 1)
    	        {
    	            trackingShader.parameter("sqrConvergenceThreshold", 1000000.0f);
    	            trackingShader.parameter("SSD_Threshold", 1000000.0f);
    	            trackingShader.parameter("validRegion", -1.0f, -1.0f, 2.0f, 2.0f);
    	        }
    	        else if (iter == nIterations)
    	        {
    	            trackingShader.parameter("sqrConvergenceThreshold", convergenceThreshold * convergenceThreshold);
    	            trackingShader.parameter("SSD_Threshold", SSDThreshold);
    	            trackingShader.parameter("validRegion", margin / width,  margin / height, 1.0f -  margin / width, 1.0f - margin / height);
    	        }

    	        featuresBuffer1.activate();
    	        featuresBuffer0.bindTexture(GL.GL_TEXTURE0);
    	        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
    	        Util.renderNormalizedQuad();
    	        featuresBuffer0.unbindTexture(GL.GL_TEXTURE0);
    	        
    	        RTTBuffer tmp = featuresBuffer0;
    	        featuresBuffer0 = featuresBuffer1;
    	        featuresBuffer1 = tmp;
    	    }
    	}

    	trackingShader.disable();

    	gl.glActiveTexture(GL.GL_TEXTURE1);
    	//gl.glDisable(GL.GL_TEXTURE_2D);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BASE_LEVEL, 0);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    	gl.glActiveTexture(GL.GL_TEXTURE2);
    	//gl.glDisable(GL.GL_TEXTURE_2D);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BASE_LEVEL, 0);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

    	featuresBuffer2.unbindTexture(GL.GL_TEXTURE3);

        RTTBuffer tmp = featuresBuffer0;
        featuresBuffer0 = featuresBuffer2;
        featuresBuffer2 = tmp;        
    }

    protected RTTBuffer featuresBufferA, featuresBufferB, featuresBufferC;
    protected RTTBuffer featuresBuffer2;
    protected static CgFragmentProgram trackingShader;    
}
