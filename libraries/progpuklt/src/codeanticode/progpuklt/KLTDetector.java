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
import javax.media.opengl.*;
import com.sun.opengl.util.*;
import java.nio.*;

class KLTDetector
{
	KLTDetector()
	{
	    init(5);
	}
	
    KLTDetector(int minDist)
    {
    	init(minDist);
    }
    
    void init(int minDist)
    {
        this.minDist = minDist;
        gl = Util.gl;
        margin = 10.0f;
        convRowsBuffer = new RTTBuffer("rgb=32f", "KLTDetector.convRowsBuffer");
        cornernessBuffer = new RTTBuffer("rgba=8", "KLTDetector.cornernessBuffer");
        nonmaxRowsBuffer = new RTTBuffer("rgba=8", "KLTDetector.nonmaxRowsBuffer");
        pointListBuffer = new RTTBuffer("rgb=32f", "KLTDetector.pointListBuffer");
        
        histpyrTexID = new int[1];
        histpyrFbIDs = new int[16];
        vbo = new int[1];        
        
        cornernessShader1 = null;
        cornernessShader2 = null;
        nonmaxShader = null;
        traverseShader = null;
        
        discriminatorShader = null;
        buildHistpyrShader = null;
        presentFeaturesShader = null;   
    }

    void setBorderMargin(float margin) { this.margin = margin; }

    void allocate(int width, int height, int pointListWidth, int pointListHeight)
    {
        if (cornernessShader1 != null)
        {
        	Util.raiseGLError("cornernessShader1 already created", "KLTDetector.allocate()");
    	    return;
    	}
        
        Util.checkGLErrors("KLTDetector.allocate()");

    	this.width = width;
    	this.height = height;
    	this.pointListWidth = pointListWidth;
    	this.pointListHeight = pointListHeight;

    	//_featuresBuffer.allocate(featureWidth, featureHeight);
    	convRowsBuffer.allocate(width, height);
    	cornernessBuffer.allocate(width, height);
    	nonmaxRowsBuffer.allocate(width, height);
    	pointListBuffer.allocate(pointListWidth, pointListHeight);
    	Util.checkGLErrors("KLTDetector.allocate()");

    	// The histogram pyramid is a POT, find the next POT value >= max(width, height).
    	histpyrWidth = 1;
    	nHistpyrLevels = 0;
    	while (histpyrWidth < PApplet.max(width, height))
    	{
    	    histpyrWidth *= 2;
    	    ++nHistpyrLevels;
        }
    	histpyrWidth = (1 << (nHistpyrLevels-1));

    	gl.glGenTextures(1, histpyrTexID, 0);
    	gl.glGenFramebuffersEXT(nHistpyrLevels, histpyrFbIDs, 0);

    	gl.glBindTexture(GL.GL_TEXTURE_2D, histpyrTexID[0]);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
  	    // This is the simplest way to create the full mipmap pyramid - temporary enable auto mipmap generation.
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
    	gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA32F_ARB, histpyrWidth, histpyrWidth, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_FALSE); 

  	    for (int k = 0; k < nHistpyrLevels; ++k)
  	  	{
  	    	gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, histpyrFbIDs[k]);
  	    	gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, histpyrTexID[0], k);
   	        boolean status = Util.checkFrameBufferStatus("histo pyramid buffer");
   	    }
  	    
  	  gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

   	    // Those are per instance, not static, in order to have individual windows sizes etc.
   	    {
   	        cornernessShader1 = new CgFragmentProgram("KLTDetector.cornernessShader1");
   	        cornernessShader1.setProgramFromFile("klt_detector_pass1.cg");
   	        cornernessShader1.compile();
   	        Util.checkGLErrors("cornernessShader1 creation");
   	    }

        {
            cornernessShader2 = new CgFragmentProgram("KLTDetector.cornernessShader2");
    	    cornernessShader2.setProgramFromFile("klt_detector_pass2.cg");
    	    cornernessShader2.compile();
    	    Util.checkGLErrors("cornernessShader2 creation");
        }

        {
     	    nonmaxShader = new CgFragmentProgram("KLTDetector.nonmaxShader");
     	    nonmaxShader.setProgramFromFile("klt_detector_nonmax.cg");
    	    String[] args = { "-unroll", "all", "-DMIN_DIST=" + minDist, null };
  	        nonmaxShader.compile(args);
  	        Util.checkGLErrors("nonmaxShader creation");
   	    }

        {
    	    traverseShader = new CgFragmentProgram("KLTDetector.traverseShader");
    	    traverseShader.setProgramFromFile("klt_detector_traverse_histpyr.cg");
    	    String[] args = { "-unroll", "all", "-DPYR_LEVELS=" + nHistpyrLevels, null };
    	    traverseShader.compile(args);
  	        Util.checkGLErrors("traverseShader creation");
        }

        gl.glGenBuffersARB(1, vbo, 0);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vbo[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER_ARB, pointListWidth * pointListHeight * 3 * BufferUtil.SIZEOF_FLOAT, null, GL.GL_DYNAMIC_DRAW_ARB);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
        Util.checkGLErrors("KLTDetector.allocate() end");   	
    }
    
    void deallocate()
    {
    	Util.checkGLErrors("KLTDetector.deallocate() start");
    	convRowsBuffer.deallocate();
    	cornernessBuffer.deallocate();
    	nonmaxRowsBuffer.deallocate();
    	pointListBuffer.deallocate();
    	gl.glDeleteFramebuffersEXT(nHistpyrLevels, histpyrFbIDs, 0);
    	gl.glDeleteTextures(1, histpyrTexID, 0);
    	gl.glDeleteBuffersARB(1, vbo, 0);
    	Util.checkGLErrors("KLTDetector.deallocate() end");    	
    }

    int detectCorners(float minCornerness, int texID)
    {
    	return detectCorners(minCornerness, texID, 0, null);
    }    
    
    int detectCorners(float minCornerness, int texID, int nPresentFeatures)
    {
    	return detectCorners(minCornerness, texID, nPresentFeatures, null); 
    }
    
    // Note: the texID should be a texture as generated by GPUPyramidWithDerivativesCreator.
    int detectCorners(float minCornerness, int texID, int nPresentFeatures,
    		          float[] presentFeaturesBuffer)
    {
        if (discriminatorShader == null)
    	{
        	discriminatorShader = new CgFragmentProgram("KLTDetector.detectCorners().discriminatorShader");
        	discriminatorShader.setProgramFromFile("klt_detector_discriminator.cg");
        	discriminatorShader.compile();
    	    Util.checkGLErrors("discriminatorShader creation");
    	}

    	if (buildHistpyrShader == null)
    	{
    	    buildHistpyrShader = new CgFragmentProgram("KLTDetector.detectCorners().buildHistpyrShader");
    	    buildHistpyrShader.setProgramFromFile("klt_detector_build_histpyr.cg");
    	    buildHistpyrShader.compile();
    	    Util.checkGLErrors("buildHistpyrShader creation");
    	}

    	if (presentFeaturesShader == null)
    	{
            presentFeaturesShader = new CgFragmentProgram("KLTDetector.detectCorners().presentFeaturesShader");
    	    String source =
    	    "void main(out float4 color : COLOR) \n" +
    	    "{ \n" +
    	    "   color = unpack_4ubyte(-1e30); \n" +
    	    "} \n";
    	    presentFeaturesShader.setProgram(source);
    	    presentFeaturesShader.compile();
    	    Util.checkGLErrors("presentFeaturesShade creation");
    	}

    	Util.checkGLErrors("KLTDetector.detectCorners()");

    	Util.setupNormalizedProjection();

    	if (!gl.glIsEnabled(GL.GL_TEXTURE_2D)) gl.glEnable(GL.GL_TEXTURE_2D);
    	
  	    // Compute the cornerness
    	convRowsBuffer.activate();
    	gl.glActiveTexture(GL.GL_TEXTURE0);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, texID);
    	//gl.glEnable(GL.GL_TEXTURE_2D);

    	cornernessShader1.enable();
    	Util.renderQuad8Tap(0.0f, 1.0f / height);
    	cornernessShader1.disable();
    	
    	cornernessBuffer.activate();
    	convRowsBuffer.bindTexture();
    	cornernessShader2.parameter("minCornerness", minCornerness);
    	cornernessShader2.parameter("validRegion", margin / width, margin / height,
    	                             1.0f - margin / width, 1.0f - margin / height);
    	cornernessShader2.enable();
    	Util.renderQuad8Tap(1.0f / width, 0.0f);
    	cornernessShader2.disable();
 
        if (nPresentFeatures > 0)
    	{
    	    // Suppress corner detection in the vicinity of still valid tracks.
        	gl.glPointSize(1);
    	    presentFeaturesShader.enable();
     	    
            // Vertex buffer object version
    	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vbo[0]);
    	    // Mapping the GPU vertex array to a buffer in CPU memory...
    	    FloatBuffer buf = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY).asFloatBuffer();
            // ...and putting the updated data in the buffer.
    	    buf.put(presentFeaturesBuffer);
    	    gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
            
    	    gl.glVertexPointer(2, GL.GL_FLOAT, 3 * BufferUtil.SIZEOF_FLOAT, 0);
    	    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
    	    gl.glDrawArrays(GL.GL_POINTS, 0, nPresentFeatures);
    	    gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
    	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
    	    
    	    presentFeaturesShader.disable();
        }
        
    	// Apply non-max suppression
    	nonmaxRowsBuffer.activate();
    	cornernessBuffer.bindTexture();
    	nonmaxShader.parameter("ds", 1.0f / width, 0.0f);
    	nonmaxShader.enable();
    	Util.renderQuad8Tap(0.0f, 1.0f / height);
    	cornernessBuffer.activate();
    	nonmaxRowsBuffer.bindTexture();
    	nonmaxShader.parameter("ds", 0.0f, 1.0f / height);
    	Util.renderQuad8Tap(1.0f / width, 0.0f);
    	nonmaxShader.disable();

    	// Note: cornerness > 0 denotes an active features, cornerss < 0 a suppressed one.

    	gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, histpyrFbIDs[0]);
    	gl.glClearColor(0, 0, 0, 0);
    	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    	gl.glViewport(0, 0, width / 2, height / 2);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, cornernessBuffer.textureID());
    	discriminatorShader.parameter("wh", width, height);
    	discriminatorShader.enable();
    	Util.render2x2Tap(-0.5f / width, -0.5f / height, 1.0f / width, 1.0f / height);
    	discriminatorShader.disable();

    	gl.glBindTexture(GL.GL_TEXTURE_2D, histpyrTexID[0]);
    	buildHistpyrShader.enable();

    	for (int k = 1; k < nHistpyrLevels; ++k)
    	{
    	    int W = (histpyrWidth >> k);
    	    gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, histpyrFbIDs[k]);
    	    gl.glViewport(0, 0, W, W);
    	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BASE_LEVEL, k-1);
    	    Util.render2x2Tap(-0.25f / W, -0.25f / W, 0.5f / W, 0.5f / W);
    	}

    	buildHistpyrShader.disable();
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BASE_LEVEL, 0);
    	//gl.glDisable(GL.GL_TEXTURE_2D);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    	
    	Util.checkGLErrors("KLTDetector.detectCorners()");

    	float[] vals = new float[4];
    	FloatBuffer nFeaturesReadBuffer = BufferUtil.newFloatBuffer(4);
    	gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
    	gl.glReadPixels(0, 0, 1, 1, GL.GL_RGBA, GL.GL_FLOAT, nFeaturesReadBuffer);
    	nFeaturesReadBuffer.get(vals);
    	
        return (int)(vals[0] + vals[1] + vals[2] + vals[3]);
    }
    
    void extractCorners(int nFeatures, float[] dest)
    {
        pointListBuffer.activate();
        gl.glClearColor(-1, -1, -1, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        int nRequiredRows = (nFeatures + pointListWidth - 1) / pointListWidth;
        gl.glViewport(0, 0, pointListWidth, nRequiredRows);

    	if (!gl.glIsEnabled(GL.GL_TEXTURE_2D)) gl.glEnable(GL.GL_TEXTURE_2D);
        
    	gl.glActiveTexture(GL.GL_TEXTURE0);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, histpyrTexID[0]);
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
        //gl.glEnable(GL.GL_TEXTURE_2D);

        cornernessBuffer.bindTexture(GL.GL_TEXTURE1);
        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

        traverseShader.parameter("nFeatures", nFeatures);
        traverseShader.parameter("pointListWidth", pointListWidth);
        traverseShader.parameter("pyrW", histpyrWidth);
        traverseShader.parameter("srcWH", width, height);
        traverseShader.enable();

        gl.glBegin(GL.GL_TRIANGLES);
        gl.glMultiTexCoord2f(GL.GL_TEXTURE0, -0.5f, -0.5f);
        gl.glVertex2f(0, 0);
        gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 2 * pointListWidth-0.5f, -0.5f);
        gl.glVertex2f(2, 0);
        gl.glMultiTexCoord2f(GL.GL_TEXTURE0, -0.5f, 2 * nRequiredRows-0.5f);
        gl.glVertex2f(0, 2);
        gl.glEnd();

        traverseShader.disable();
        cornernessBuffer.unbindTexture(GL.GL_TEXTURE1);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        //gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, histpyrTexID[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

        FloatBuffer destReadBuffer = BufferUtil.newFloatBuffer(dest.length);        
        gl.glReadPixels(0, 0, pointListWidth, nRequiredRows, GL.GL_RGB, GL.GL_FLOAT, destReadBuffer);
        destReadBuffer.get(dest);
    }

    protected GL gl;
    
    protected int minDist;
    protected int width, height;
    protected int pointListWidth, pointListHeight;
    protected int histpyrWidth, nHistpyrLevels;
    protected float margin;

    protected RTTBuffer convRowsBuffer; // temporary buffer for structure matrix accumulation
    protected RTTBuffer cornernessBuffer;
    protected RTTBuffer nonmaxRowsBuffer; // temporary buffer for non-max suppression
    protected RTTBuffer pointListBuffer;

    protected int[] histpyrTexID;
    protected int[] histpyrFbIDs;
    protected int[] vbo;
    
    protected CgFragmentProgram cornernessShader1;
    protected CgFragmentProgram cornernessShader2;
    protected CgFragmentProgram nonmaxShader;
    protected CgFragmentProgram traverseShader;
    
    protected static CgFragmentProgram discriminatorShader;
    protected static CgFragmentProgram buildHistpyrShader;
    protected static CgFragmentProgram presentFeaturesShader;
}
