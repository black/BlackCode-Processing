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

import java.nio.*;

class PyramidWithDerivativesCreator
{
    PyramidWithDerivativesCreator()
    {
        gl = Util.gl;    	
    	
        width = height = nLevels = 0;
        
        srcFbID = new int[1];
        pyrFbIDs = new int[8];
        tmpFbIDs = new int[8];
        tmp2FbID = new int[1];
        
        srcTexID = new int[1];
        pyrTexID = new int[1];
        tmpTexID = new int[1]; 
        tmpTex2ID = new int[1];
        
        pass1HorizShader = null;
        pass1VertShader = null;
        pass2Shader = null;
    }         

    int numberOfLevels() { return nLevels; }

    void allocate(int w, int h, int n, int k)
    {
        width = w;
        height = h;
        nLevels = n;
        kernelSize = k;

        int textureTarget = GL.GL_TEXTURE_2D;
        
        gl.glGenFramebuffersEXT(1, srcFbID, 0);
        gl.glGenFramebuffersEXT(nLevels, pyrFbIDs, 0);
        gl.glGenFramebuffersEXT(nLevels, tmpFbIDs, 0);
        gl.glGenFramebuffersEXT(1, tmp2FbID, 0);

        gl.glGenTextures(1, srcTexID, 0);
        gl.glGenTextures(1, pyrTexID, 0);
        gl.glGenTextures(1, tmpTexID, 0);
        gl.glGenTextures(1, tmpTex2ID, 0);

        gl.glBindTexture(textureTarget, srcTexID[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); 
        gl.glTexImage2D(textureTarget, 0, GL.GL_LUMINANCE8, w, h, 0, GL.GL_LUMINANCE, GL.GL_UNSIGNED_BYTE, null);

        gl.glBindTexture(textureTarget, pyrTexID[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        //gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        // This is the simplest way to create the full mipmap pyramid.
        gl.glTexParameteri(textureTarget, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
        gl.glTexImage2D(textureTarget, 0, GL.GL_RGB16F_ARB, w, h, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
        gl.glTexParameteri(textureTarget, GL.GL_GENERATE_MIPMAP, GL.GL_FALSE); 

        Util.checkGLErrors("PyramidWithDerivativesCreator.allocate()");

        gl.glBindTexture(textureTarget, tmpTexID[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        //gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(textureTarget, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
        gl.glTexImage2D(textureTarget, 0, GL.GL_RGB16F_ARB, w, h/2, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
        gl.glTexParameteri(textureTarget, GL.GL_GENERATE_MIPMAP, GL.GL_FALSE); 

        gl.glBindTexture(textureTarget, tmpTex2ID[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); 
        gl.glTexImage2D(textureTarget, 0, GL.GL_RGBA16F_ARB, w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);

        gl.glBindTexture(textureTarget, 0);
        Util.checkGLErrors("PyramidWithDerivativesCreator.allocate()");

        boolean status;
        
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, srcFbID[0]);
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, textureTarget, srcTexID[0], 0);
        status = Util.checkFrameBufferStatus("pyramid texture buffer");
        
        for (int level = 0; level < nLevels; ++level)
        {
        	gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, pyrFbIDs[level]);
        	gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, textureTarget, pyrTexID[0], level);
            status = Util.checkFrameBufferStatus("pyramid buffer");
        }

        for (int level = 0; level < nLevels-1; ++level)
        {
        	gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, tmpFbIDs[level]);
        	gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, textureTarget, tmpTexID[0], level);
            status = Util.checkFrameBufferStatus("pyramid tmp buffer");
        }

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, tmp2FbID[0]);
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, textureTarget, tmpTex2ID[0], 0);
        status = Util.checkFrameBufferStatus("pyramid tmp2 buffer");

        Util.checkGLErrors("PyramidWithDerivativesCreator.allocate()");
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);	
    }
    
    void deallocate()
    {
    	gl.glDeleteFramebuffersEXT(1, srcFbID, 0);
    	gl.glDeleteFramebuffersEXT(nLevels, pyrFbIDs, 0);
    	gl.glDeleteFramebuffersEXT(nLevels, tmpFbIDs, 0);
    	gl.glDeleteFramebuffersEXT(1, tmp2FbID, 0);
    	gl.glDeleteTextures(1, pyrTexID, 0);
    	gl.glDeleteTextures(1, srcTexID, 0);
    	gl.glDeleteTextures(1, tmpTexID, 0);
    	gl.glDeleteTextures(1, tmpTex2ID, 0);     	
    }

    void buildPyramidForGrayscaleImage(int texID)
    {
    	int textureTarget = GL.GL_TEXTURE_2D;
    	buildPyramidForGrayscaleImageInit(textureTarget);
    	
    	buildPyramidForGrayscaleImageReadSrcTex(textureTarget, texID);
    	
    	buildPyramidForGrayscaleImageExec(textureTarget);
    }
    
    void buildPyramidForGrayscaleImage(int[] image)
    {
    	int textureTarget = GL.GL_TEXTURE_2D;
    	buildPyramidForGrayscaleImageInit(textureTarget);

    	buildPyramidForGrayscaleImageReadSrcImg(textureTarget, image);
        
    	buildPyramidForGrayscaleImageExec(textureTarget);
    }

    void activateTarget(int level)
    {
        int W = (width >> level);
        int H = (height >> level);
        int textureTarget = GL.GL_TEXTURE_2D;

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, pyrFbIDs[level]);
        gl.glViewport(0, 0, W, H);    	
    }

    void buildPyramidForGrayscaleImageInit(int textureTarget)
    {
	    if (pass1HorizShader == null)
        {
            pass1HorizShader = new CgFragmentProgram("PyramidWithDerivativesCreator.buildPyramidForGrayscaleImage.pass1HorizShader");
            pass1HorizShader.setProgramFromFile("pyramid_with_derivative_pass1v.cg");
            String[] args = { "-DKERNEL_SIZE=" + kernelSize, null };
            pass1HorizShader.compile(args);
            Util.checkGLErrors("creating pyramid_with_derivative_pass1v.cg");
        }

        if (pass1VertShader == null)
        {
            pass1VertShader = new CgFragmentProgram("PyramidWithDerivativesCreator.buildPyramidForGrayscaleImage.pass1VertShader");
            pass1VertShader.setProgramFromFile("pyramid_with_derivative_pass1h.cg");
            String[] args = { "-DKERNEL_SIZE=" + kernelSize, null };
            pass1VertShader.compile(args);
            Util.checkGLErrors("creating pyramid_with_derivative_pass1h.cg");
        }
 
        if (pass2Shader == null)
        {
            pass2Shader = new CgFragmentProgram("PyramidWithDerivativesCreator.buildPyramidForGrayscaleImage.pass2Shader");
            pass2Shader.setProgramFromFile("pyramid_with_derivative_pass2.cg");
            pass2Shader.compile();
            Util.checkGLErrors("creating pyramid_with_derivative_pass2.cg");
         }

        Util.setupNormalizedProjection();
        gl.glViewport(0, 0, width, height);

	    if (!gl.glIsEnabled(textureTarget)) gl.glEnable(textureTarget);
    }   

    void buildPyramidForGrayscaleImageReadSrcTex(int textureTarget, int texID)
    {
    	gl.glActiveTexture(GL.GL_TEXTURE0);
    	gl.glBindTexture(textureTarget, texID);
    	gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, srcFbID[0]);
    
	    // Copying texture texID to srcTexID[0] using the trivial shader.
	    Util.enableTrivialTexture2DShader();
	    Util.renderNormalizedQuad();	    
	    Util.disableTrivialTexture2DShader();
	    
	    // Binding srcTexID[0] to first texture unit, so the execution of
	    // pyramid building can proceed normally.
	    gl.glBindTexture(textureTarget, srcTexID[0]);    	
    }
    
    void buildPyramidForGrayscaleImageReadSrcImg(int textureTarget, int[] image)
    {
    	gl.glActiveTexture(GL.GL_TEXTURE0);
    	gl.glBindTexture(textureTarget, srcTexID[0]);
        //gl.glTexSubImage2D(textureTarget, 0, 0, 0, width, height, GL.GL_LUMINANCE, GL.GL_UNSIGNED_BYTE, IntBuffer.wrap(image));
    	gl.glTexSubImage2D(textureTarget, 0, 0, 0, width, height, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, IntBuffer.wrap(image));
    }

	void buildPyramidForGrayscaleImageExec(int textureTarget)
	{
        //gl.glEnable(GL.GL_TEXTURE_2D);
    
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, tmp2FbID[0]);

        pass1HorizShader.enable();
        Util.renderQuad8Tap(0.0f, 1.0f / height);
        pass1HorizShader.disable();

        //gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, textureTarget, pyrTexID[0], 0);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, pyrFbIDs[0]);
        gl.glBindTexture(textureTarget, tmpTex2ID[0]);

        pass1VertShader.enable();
        Util.renderQuad8Tap(1.0f / width, 0.0f);
        pass1VertShader.disable();

        pass2Shader.enable();

        for (int level = 1; level < nLevels; ++level)
        {
           // Source texture dimensions.
           int W = (width >> (level-1));
           int H = (height >> (level-1));

           gl.glBindTexture(textureTarget, pyrTexID[0]);
           gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_BASE_LEVEL, level-1);

           gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, tmpFbIDs[level-1]);
           gl.glViewport(0, 0, W, H / 2);
           Util.renderQuad4Tap(0.0f, 1.0f / H);
           //gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_BASE_LEVEL, 0);

           gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, pyrFbIDs[level]);
           gl.glViewport(0, 0, W / 2, H / 2);
           gl.glBindTexture(textureTarget, tmpTexID[0]);
           gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_BASE_LEVEL, level-1);
           Util.renderQuad4Tap(1.0f / W, 0.0f);
           //gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_BASE_LEVEL, 0);
        }

        pass2Shader.disable();
        //gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glBindTexture(textureTarget, pyrTexID[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_BASE_LEVEL, 0);
        gl.glBindTexture(textureTarget, tmpTexID[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_BASE_LEVEL, 0);
        gl.glBindTexture(textureTarget, 0);
    }    
    
    int textureID() { return pyrTexID[0]; }
    int sourceTextureID() { return srcTexID[0]; }

    protected GL gl;
    
    protected int width, height, nLevels, kernelSize;
    protected int[] srcFbID, pyrFbIDs, tmpFbIDs, tmp2FbID;
    protected int[] srcTexID, pyrTexID, tmpTexID, tmpTex2ID;
    
    protected static CgFragmentProgram pass1HorizShader;
    protected static CgFragmentProgram pass1VertShader;
    protected static CgFragmentProgram pass2Shader;     
}
