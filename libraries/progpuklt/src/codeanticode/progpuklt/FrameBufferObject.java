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

class FrameBufferObject
{
	FrameBufferObject()
	{
	    init("<unnamed frame buffer object>");
	}

	FrameBufferObject(String fboName)
	{
	    init(fboName);
	}
	
	void init(String name)
	{
		gl = Util.gl;
	    fboName = name;
	    fboID = new int[1]; fboID[0] = 0;
	    attachedDepthTexture = null;
	    attachedColorTextures = new ImageTexture2D[16];
	    for (int i = 0; i < 16; i++) attachedColorTextures[i] = null; 
	}
	
    boolean allocate()
    {
    	gl.glGenFramebuffersEXT(1, fboID, 0);
        Util.checkGLErrors(fboName);
        return true;
    }
    
    void deallocate()
    {
	    for (int i = 0; i < 16; i++) attachedColorTextures[i] = null;
	    gl.glDeleteFramebuffersEXT(1, fboID, 0);
        Util.checkGLErrors(fboName);    
    }

    void makeCurrent()
    {
    	gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboID[0]);
        Util.checkGLErrors(fboName);    	
    }

    void activate()
    {
   	    activate(true);
    }
     
    void activate(boolean setViewport)
    {
        makeCurrent();
        if (checkValidity())
        {
           if (setViewport)
        	   gl.glViewport(0, 0, getWidth(), getHeight());
        }
    }

    boolean isCurrent()
    {
        int[] curFboID = new int[1];
        gl.glGetIntegerv(GL.GL_FRAMEBUFFER_BINDING_EXT, curFboID, 0);
        return (fboID[0] == curFboID[0]); 
    }
     
    void attachTexture2D(ImageTexture2D texture)
    {
        attachTexture2D(texture, GL.GL_COLOR_ATTACHMENT0_EXT, 0);
    }
     
    void attachTexture2D(ImageTexture2D texture, int attachment)
    {
        attachTexture2D(texture, attachment, 0);
    }     
     
    void attachTexture2D(ImageTexture2D texture, int attachment, int mipLevel)
    {
        if (checkBinding("FrameBufferObject.attachTexture2D()"))
        {
           if (attachment >= GL.GL_COLOR_ATTACHMENT0_EXT && attachment <= GL.GL_COLOR_ATTACHMENT15_EXT)
           {
        	   gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, attachment,
                                            texture.textureTarget(), texture.textureID(), mipLevel);
               attachedColorTextures[attachment - GL.GL_COLOR_ATTACHMENT0_EXT] = texture;
           }
           else if (attachment == GL.GL_DEPTH_ATTACHMENT_EXT)
           {
        	   gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, attachment,
                                            texture.textureTarget(), texture.textureID(), mipLevel);
               attachedDepthTexture = texture;
           }
           else
               Util.raiseGLError("Unknown/unsupported attachment specifier", fboName);
        }
        Util.checkGLErrors(fboName);   
    }

    void attachTextures2D(int numTextures, ImageTexture2D[] textures)
    {
    	attachTextures2D(numTextures, textures, null, null);	
    }

    void attachTextures2D(int numTextures, ImageTexture2D[] textures, int[] attachment)
    {
    	attachTextures2D(numTextures, textures, attachment, null);	
    }    
    
    void attachTextures2D(int numTextures, ImageTexture2D[] textures, int[] attachment, int[] mipLevel)
    {
        for (int i = 0; i < numTextures; ++i)
            attachTexture2D(textures[i],
                            (attachment != null) ? attachment[i] : (GL.GL_COLOR_ATTACHMENT0_EXT + i),
                            (mipLevel != null) ? mipLevel[i] : 0);    	
    }

    void detach(int attachment)
    {
        if (checkBinding("FrameBufferObject.detach()"))
        {
           if (attachment >= GL.GL_COLOR_ATTACHMENT0_EXT && attachment <= GL.GL_COLOR_ATTACHMENT15_EXT)
           {
        	   gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, attachment, GL.GL_TEXTURE_2D, 0, 0);
               attachedColorTextures[attachment - GL.GL_COLOR_ATTACHMENT0_EXT] = null;
           }
           else if (attachment == GL.GL_DEPTH_ATTACHMENT_EXT)
           {
        	   gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, attachment, GL.GL_TEXTURE_2D, 0, 0);
               attachedDepthTexture = null;
           }
           else
               Util.raiseGLError("Unknown/unsupported attachment specifier", fboName);
        }
 	   
       Util.checkGLErrors(fboName);    
    }
    
    void detachAll()
    {
        int numAttachments = getMaxColorAttachments();
        for (int i = 0; i < numAttachments; ++i)
           detach(GL.GL_COLOR_ATTACHMENT0_EXT + i);
    }

    boolean checkValidity()
    {
        if (!checkBinding("FrameBufferObject.checkValidity()"))
            return false;

        int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);

        if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT) return true;
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT)
        {
            Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT)", fboName);
            return false;
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)
        {
        	Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)", fboName);
            return false;
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)
        {
        	Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)", fboName);
            return false;
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)
        {
        	Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)", fboName);
            return false;
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)
        {
        	Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)", fboName);
            return false;
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT)
        {
        	Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT)", fboName);
            return false;
        }
        else if (status == GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT)
        {
        	Util.raiseGLError("Frame buffer is incomplete (GL_FRAMEBUFFER_UNSUPPORTED_EXT)", fboName);
            return false;
        }
        else
        {
        	Util.raiseGLError("Frame buffer is incomplete (unknown error code)", fboName);
            return false;
        }    
    }

    int getWidth()
    {
        for (int i = 0; i < 16; ++i)
            if (attachedColorTextures[i] != null)
                  return attachedColorTextures[i].getWidth();
        return 0;
     }

    int getHeight()
    {
        for (int i = 0; i < 16; ++i)
            if (attachedColorTextures[i] != null)
                  return attachedColorTextures[i].getHeight();
        return 0;
    }

    int frameBufferID()
    { 
        return fboID[0]; 
    }

    ImageTexture2D getColorTexture(int i) 
    { 
    	return attachedColorTextures[i]; 
    }

    static int getMaxColorAttachments()
    {
        int[] res = new int[1];
        Util.gl.glGetIntegerv(GL.GL_MAX_COLOR_ATTACHMENTS_EXT, res, 0);
        return res[0]; 
    }

    static void disableFBORendering()
    {
    	Util.gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);    
    }

    protected boolean checkBinding(String what)
    {
        int[] curFboID = new int[1];
        gl.glGetIntegerv(GL.GL_FRAMEBUFFER_BINDING_EXT, curFboID, 0);
        
        if (curFboID[0] != fboID[0])
        {
            Util.raiseGLError("FBO operation (" + what + ") on unbound frame buffer attempted", fboName);
            return false;
        }
        return true;    	
    }

    protected GL gl;
    protected String fboName;
    protected int[] fboID;
    ImageTexture2D[] attachedColorTextures;
    ImageTexture2D attachedDepthTexture;
}
