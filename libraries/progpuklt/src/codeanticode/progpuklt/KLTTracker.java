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

class KLTTracker extends KLTTrackerBase
{
	KLTTracker()
	{
        super(5, 4, -1, 7);
        init();		
	}
	
    KLTTracker(int nIterations, int nLevels, int levelSkip, int windowWidth)
    {
        super(nIterations, nLevels, levelSkip, windowWidth);
        init();
    }

    void init()
    {
        featuresBufferA = new RTTBuffer("rgb=32f", "KLTTracker.featuresBufferA");
        featuresBufferB = new RTTBuffer("rgb=32f", "KLTTracker.featuresBufferB");
        trackingShader = null;
 
        featuresBuffer0 = featuresBufferA;
        featuresBuffer1 = featuresBufferB;    	
    }
    
    void allocate(int width, int height, int featureWidth, int featureHeight)
    {
        this.width = width;
        this.height = height;
        this.featureWidth = featureWidth;
        this.featureHeight = featureHeight;
        
    	featuresBufferA.allocate(featureWidth, featureHeight);
    	featuresBufferB.allocate(featureWidth, featureHeight);
        Util.checkGLErrors("KLTTracker.allocate()");

    	featuresBuffer0 = featuresBufferA;
        featuresBuffer1 = featuresBufferB;    	
    }
    
    void deallocate()
    {
        Util.checkGLErrors("begin KLTTracker.deallocate()");
    	featuresBufferA.deallocate();
    	featuresBufferB.deallocate();
        Util.checkGLErrors("end KLTTracker.deallocate()");    	
    }

    void provideFeatures(float[] features)
    {
    	featuresBuffer1.bindTexture(GL.GL_TEXTURE0);
    	gl.glTexSubImage2D(featuresBuffer1.textureTarget(),
    	                   0, 0, 0, featureWidth, featureHeight,
    	                   GL.GL_RGB, GL.GL_FLOAT, FloatBuffer.wrap(features));
    	featuresBuffer1.unbindTexture(GL.GL_TEXTURE0);
    	//gl.glBindTexture(featuresBuffer1.textureTarget(), 0);	
    }
    
    void readFeatures(float[] features)
    {
        featuresBuffer1.activate();
        FloatBuffer readFeaturesBuffer = BufferUtil.newFloatBuffer(features.length);
        gl.glReadPixels(0, 0, featureWidth, featureHeight, GL.GL_RGB, GL.GL_FLOAT, readFeaturesBuffer);
        readFeaturesBuffer.get(features);
    }
    
    void trackFeatures(int pyrTex0, int pyrTex1)
    {
        if (trackingShader == null)
    	{
    	    trackingShader = new CgFragmentProgram("KLTTracker.trackingShader");
    	    trackingShader.setProgramFromFile("klt_tracker.cg");

    	    String[] args = { "-DNITERATIONS=" + nIterations, 
    	    		          "-DN_LEVELS=" + nLevels, 
    	    		          "-DLEVEL_SKIP=" + levelSkip,
    	    		          "-DHALF_WIDTH=" + (int)(windowWidth/2),
    	    		          null }; // Arguments array must end in null.
            trackingShader.compile(args);

            Util.checkGLErrors("trackingShader creation");
    	}

    	Util.setupNormalizedProjection();
    	featuresBuffer1.activate();

    	float ds = 1.0f / width;
    	float dt = 1.0f / height;
    	
    	featuresBuffer0.bindTexture(GL.GL_TEXTURE0);
    	gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

    	gl.glActiveTexture(GL.GL_TEXTURE1);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, pyrTex0);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST);
    	//gl.glEnable(GL.GL_TEXTURE_2D);

    	gl.glActiveTexture(GL.GL_TEXTURE2);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, pyrTex1);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST);
    	//gl.glEnable(GL.GL_TEXTURE_2D);

    	trackingShader.parameter("ds", ds, dt);
    	trackingShader.parameter("wh", width, height);
    	trackingShader.parameter("sqrConvergenceThreshold", convergenceThreshold * convergenceThreshold);
    	trackingShader.parameter("SSD_Threshold", SSDThreshold);
    	trackingShader.parameter("validRegion", margin / width, margin / height,
    	                         1.0f - margin / width, 1.0f - margin / height);
    	trackingShader.enable();
    	Util.renderNormalizedQuad();
    	trackingShader.disable();

    	featuresBuffer0.unbindTexture(GL.GL_TEXTURE0);
    	gl.glActiveTexture(GL.GL_TEXTURE1);
    	//gl.glDisable(GL.GL_TEXTURE_2D);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    	gl.glActiveTexture(GL.GL_TEXTURE2);
    	//gl.glDisable(GL.GL_TEXTURE_2D);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }

    static protected CgFragmentProgram trackingShader;
    protected RTTBuffer featuresBufferA, featuresBufferB;
}
