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

class ImageTexture2D
{
    ImageTexture2D()
	{
	    init("<unnamed 2D texture>");
	}
    
    ImageTexture2D(String texName)
    { 
        init(texName); 
    }

    void init(String name)
    {
    	gl = Util.gl;
        texName = name; 
        textureID = new int[1]; textureID[0] = 0; 
        textureTarget = 0; 
        width = 0; 
        height = 0;	    	 
    }

    boolean allocateID()
    {
        textureTarget = GL.GL_TEXTURE_2D;
        gl.glGenTextures(1, textureID, 0);

        gl.glBindTexture(textureTarget, textureID[0]);

        // Default is clamp to edge and nearest filtering.
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); 
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        
        Util.checkGLErrors(texName);
        
        return true;
    }
    
    void deallocateID()
    {
        if (textureID[0] == 0) return;
        clear();
        gl.glDeleteTextures(1, textureID, 0);
        textureID[0] = 0;
        Util.checkGLErrors(texName);   	
    }

    void reserve(int width, int height, TextureSpecification texSpec)
    {
        this.width  = width;
        this.height = height;

        int internalFormat = texSpec.getGLInternalFormat();
        int format = texSpec.isDepthTexture ? GL.GL_DEPTH_COMPONENT : GL.GL_RGB;
        int type = texSpec.isFloatTexture ? GL.GL_FLOAT : GL.GL_UNSIGNED_BYTE;

        gl.glBindTexture(textureTarget, textureID[0]);
        gl.glTexImage2D(textureTarget, 0, internalFormat, width, height, 0, format, type, null);
        
        Util.checkGLErrors(texName);    	 
    }

    void overwriteWith(int[] pixels, int nChannels)
    {
        int format = Util.extPixelFormat[nChannels];
        int type = GL.GL_UNSIGNED_BYTE;

        gl.glBindTexture(textureTarget, textureID[0]);
        gl.glTexSubImage2D(textureTarget, 0, 0, 0, width, height, format, type, IntBuffer.wrap(pixels));
        
        Util.checkGLErrors(texName);
    }
     
    void overwriteWith(int[] redPixels, int[] greenPixels, int[] bluePixels)
    {
        int format = Util.extPixelFormat[3];
        int type = GL.GL_UNSIGNED_BYTE;
        
        int[] pixels = new int[3 * width * height];
        
        Util.interleavePixels(width, height, redPixels, greenPixels, bluePixels, pixels);

        gl.glBindTexture(textureTarget, textureID[0]);
        gl.glTexSubImage2D(textureTarget, 0, 0, 0, width, height, format, type, IntBuffer.wrap(pixels));
    	
        Util.checkGLErrors(texName);    	 
    }
    
    void overwriteWith(float[] pixels, int nChannels)
    {
        int format = Util.extPixelFormat[nChannels];
        int type = GL.GL_FLOAT;

        gl.glBindTexture(textureTarget, textureID[0]);
        gl.glTexSubImage2D(textureTarget, 0, 0, 0, width, height, format, type, FloatBuffer.wrap(pixels));
        
        Util.checkGLErrors(texName);
    }
    
    void overwriteWith(float[] redPixels, float[] greenPixels, float[] bluePixels)
    {
        int format = Util.extPixelFormat[3];
        int type = GL.GL_FLOAT;
        
        float[] pixels = new float[3 * width * height];
        
        Util.interleavePixels(width, height, redPixels, greenPixels, bluePixels, pixels);

        gl.glBindTexture(textureTarget, textureID[0]);
        gl.glTexSubImage2D(textureTarget, 0, 0, 0, width, height, format, type, FloatBuffer.wrap(pixels));
    	
        Util.checkGLErrors(texName);     
    }
    
    void clear()
    {
    	gl.glBindTexture(textureTarget, textureID[0]);
    	gl.glTexImage2D(textureTarget, 0, GL.GL_RGB, 0, 0, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
    	Util.checkGLErrors(texName);   	
    }

    void bind()
    {
    	if (!gl.glIsEnabled(textureTarget)) gl.glEnable(textureTarget);
    	gl.glBindTexture(textureTarget, textureID[0]);	
    }
    
    void bind(int texUnit)
    {
    	gl.glActiveTexture(texUnit);
        bind();
    }

    void unbind()
    {
    	gl.glBindTexture(textureTarget, 0);
    	//gl.glDisable(textureTarget);
    }
    
    void unbind(int texUnit)
    {
    	gl.glActiveTexture(texUnit);
        unbind();
    }

    /*
    void enable()
    {
    	if (!gl.glIsEnabled(textureTarget)) gl.glEnable(textureTarget);
    	
    	gl.glBindTexture(textureTarget, textureID[0]);
    	gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
    	//gl.glEnable(textureTarget);
    }
    
    void enable(int texUnit)
    {
    	gl.glActiveTexture(texUnit);
        enable();
    }
    
    void disable()
    {
    	gl.glBindTexture(textureTarget, 0);
    	//gl.glDisable(textureTarget);	
    }
         
    void disable(int texUnit)
    {
    	gl.glActiveTexture(texUnit);
        disable();    	
    }
 */
  
    protected GL gl;
    int getWidth() { return width; }
    int getHeight() { return height; }
    int textureID() { return textureID[0]; }
    int textureTarget() { return textureTarget; }

    protected String texName;
    protected int[] textureID;
    protected int textureTarget;
    protected int width, height;
 }