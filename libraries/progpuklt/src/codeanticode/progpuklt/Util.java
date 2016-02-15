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
import javax.media.opengl.glu.*;

class Util
{
	Util(PApplet parent, GL gl)
	{
		if (Util.parent == null) Util.parent = parent;
		if (Util.gl == null) Util.gl = gl;
		if (Util.glu == null) Util.glu = new GLU();
	}
	
	static void saveGLState()
	{
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
	}
    
	static void restoreGLState()
	{
	    Util.gl.glMatrixMode(GL.GL_PROJECTION);
        Util.gl.glPopMatrix();
        Util.gl.glMatrixMode(GL.GL_MODELVIEW);
        Util.gl.glPopMatrix();
        
        Util.gl.glPopAttrib();
	}

	static void disableBlend()
	{
		if (Util.gl.glIsEnabled(GL.GL_BLEND)) Util.gl.glDisable(GL.GL_BLEND);
	}
	
	static void raiseGLWarning(String msg, String location)
	{
		System.err.println("Warning: " + msg + " at " + location);		
	}
	
	static void raiseGLError(String msg, String location)
	{
		System.err.println("Error: " + msg + " at " + location);	
	}
	
	static void checkGLErrors(String location)
	{
        String errStr;
        int errNum = gl.glGetError();
        while (0 < errNum)
        {
        	errStr = glu.gluErrorString(errNum);	
            System.err.println(errStr + " at " + location);
        }
	}
	
	static boolean checkFrameBufferStatus(String location)
    {
	    int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);

	    String msg;

	    if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT)
	        return true;
	    else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT) ";
	    else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT) ";
	    else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT) ";
	    else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT) ";
	    else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT) ";
	    else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT) ";
	    else if (status == GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT)
	        msg = "Frame buffer is incomplete (GL_FRAMEBUFFER_UNSUPPORTED_EXT) ";
	    else
	        msg = "Frame buffer is incomplete (unknown error code)";
	    
	    System.err.println("Error: " + msg + " at " + location);  
	    return false;
    }
	
	static void interleavePixels(int w, int h, int[] red, int[] green, int[] blue, int[] pixels)
	{
	    for (int p = 0; p < w*h; ++p)
	    {
            pixels[3*p+0] = red[p];
	        pixels[3*p+1] = green[p];
	        pixels[3*p+2] = blue[p];
	    }
	}

	static void interleavePixels(int w, int h, float[] red, float[] green, float[] blue, float[] pixels)
	{
	    for (int p = 0; p < w*h; ++p)
	    {
            pixels[3*p+0] = red[p];
	        pixels[3*p+1] = green[p];
	        pixels[3*p+2] = blue[p];
	    }
	}	

	static void setupNormalizedProjection() { setupNormalizedProjection(false); }
	
	static void setupNormalizedProjection(boolean flipY)
	{
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        if (!flipY)
        	gl.glOrtho(0, 1, 0, 1, -1, 1);
        else
        	gl.glOrtho(0, 1, 1, 0, -1, 1);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();		   
	}
    
    static void renderNormalizedQuad()
	{
        // It is usually recommended to draw a large (clipped) triangle
        // instead of a single quad, therefore avoiding the diagonal edge.
    	gl.glBegin(GL.GL_TRIANGLES);
    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0, 0);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0, 0, 0, 0);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0, 0, 0, 0);
    	gl.glVertex2f(0, 0);
    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 2, 0);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2, 0, 2, 0);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 2, 0, 2, 0);
    	gl.glVertex2f(2, 0);
    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0, 2);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0, 2, 0, 2);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0, 2, 0, 2);
    	gl.glVertex2f(0, 2);
    	gl.glEnd();		
	}

    static void renderNormalizedQuad(int pattern, float ds, float dt)
	{
	    if (pattern == GPU_SAMPLE_NEIGHBORS)
	    {
            gl.glBegin(GL.GL_TRIANGLES);
	        gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0,    0);
	        gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0-ds, 0,    0+ds, 0);
	        gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0,    0-dt, 0,    0+dt);
	        gl.glVertex2f(0, 0);
	        gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 2,    0);
	        gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2-ds, 0,    2+ds, 0);
	        gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 2,    0-dt, 2,    0+dt);
	        gl.glVertex2f(2, 0);
	        gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0,    2);
	        gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0-ds, 2,    0+ds, 2);
	        gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0,    2-dt, 0,    2+dt);
	        gl.glVertex2f(0, 2);
	        gl.glEnd();
	    }
	    else if (pattern == GPU_SAMPLE_DIAG_NEIGBORS)
	    {
	    	gl.glBegin(GL.GL_TRIANGLES);
	    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0,    0);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0-ds, 0-dt, 0+ds, 0+dt);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0+ds, 0-dt, 0-ds, 0+dt);
	    	gl.glVertex2f(0, 0);
	    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 2,    0);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2-ds, 0-dt, 2+ds, 0+dt);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 2+ds, 0-dt, 2-ds, 0+dt);
	    	gl.glVertex2f(2, 0);
	    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0,    2);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0-ds, 2-dt, 0+ds, 2+dt);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0+ds, 2-dt, 0-ds, 2+dt);
	    	gl.glVertex2f(0, 2);
	    	gl.glEnd();
	    }
	    else if (pattern == GPU_SAMPLE_2X2_BLOCK)
	    {
	    	gl.glBegin(GL.GL_TRIANGLES);
	    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0, 0);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0, 0,    0+ds, 0   );
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0, 0+dt, 0+ds, 0+dt);
	    	gl.glVertex2f(0, 0);
	    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 2, 0);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2, 0,    2+ds, 0   );
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 2, 0+dt, 2+ds, 0+dt);
	    	gl.glVertex2f(2, 0);
	    	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0, 2);
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0, 2,    0+ds, 2   );
	    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0, 2+dt, 0+ds, 2+dt);
	    	gl.glVertex2f(0, 2);
	    	gl.glEnd();
	    }
	    else
	    	System.err.println("Unknown sampling pattern.");		
	}
    
    static void renderQuad4Tap(float dS, float dT)
    {
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glMultiTexCoord4f(GL.GL_TEXTURE0, 0-1*dS, 0-1*dT, 0-0*dS, 0-0*dT);
        gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0+1*dS, 0+1*dT, 0+2*dS, 0+2*dT);
        gl.glVertex2f(0, 0);

        gl.glMultiTexCoord4f(GL.GL_TEXTURE0, 2-1*dS, 0-1*dT, 2-0*dS, 0-0*dT);
        gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2+1*dS, 0+1*dT, 2+2*dS, 0+2*dT);
        gl.glVertex2f(2, 0);

        gl.glMultiTexCoord4f(GL.GL_TEXTURE0, 0-1*dS, 2-1*dT, 0-0*dS, 2-0*dT);
        gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0+1*dS, 2+1*dT, 0+2*dS, 2+2*dT);
        gl.glVertex2f(0, 2);
        gl.glEnd();
    }

    static void renderQuad8Tap(float dS, float dT)
    {
    	gl.glBegin(GL.GL_TRIANGLES);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE0, 0-3*dS, 0-3*dT, 0-2*dS, 0-2*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0-1*dS, 0-1*dT, 0-0*dS, 0-0*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0+1*dS, 0+1*dT, 0+2*dS, 0+2*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE3, 0+3*dS, 0+3*dT, 0+4*dS, 0+4*dT);
    	gl.glVertex2f(0, 0);

    	gl.glMultiTexCoord4f(GL.GL_TEXTURE0, 2-3*dS, 0-3*dT, 2-2*dS, 0-2*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2-1*dS, 0-1*dT, 2-0*dS, 0-0*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 2+1*dS, 0+1*dT, 2+2*dS, 0+2*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE3, 2+3*dS, 0+3*dT, 2+4*dS, 0+4*dT);
    	gl.glVertex2f(2, 0);

     	gl.glMultiTexCoord4f(GL.GL_TEXTURE0, 0-3*dS, 2-3*dT, 0-2*dS, 2-2*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0-1*dS, 2-1*dT, 0-0*dS, 2-0*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0+1*dS, 2+1*dT, 0+2*dS, 2+2*dT);
    	gl.glMultiTexCoord4f(GL.GL_TEXTURE3, 0+3*dS, 2+3*dT, 0+4*dS, 2+4*dT);
    	gl.glVertex2f(0, 2);
    	gl.glEnd();
    }       

    static void render2x2Tap(float shiftS, float shiftT, float dS, float dT)
    {
       gl.glBegin(GL.GL_TRIANGLES);
       gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0, 0);
       gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0+shiftS, 0+shiftT,    0+shiftS+dS, 0+shiftT   );
       gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0+shiftS, 0+shiftT+dT, 0+shiftS+dS, 0+shiftT+dT);
       gl.glVertex2f(0, 0);
       gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 2, 0);
       gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 2+shiftS, 0+shiftT,    2+shiftS+dS, 0+shiftT   );
       gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 2+shiftS, 0+shiftT+dT, 2+shiftS+dS, 0+shiftT+dT);
       gl.glVertex2f(2, 0);
       gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0, 2);
       gl.glMultiTexCoord4f(GL.GL_TEXTURE1, 0+shiftS, 2+shiftT,    0+shiftS+dS, 2+shiftT   );
       gl.glMultiTexCoord4f(GL.GL_TEXTURE2, 0+shiftS, 2+shiftT+dT, 0+shiftS+dS, 2+shiftT+dT);
       gl.glVertex2f(0, 2);
       gl.glEnd();
    }    
    
	static void enableTrivialTexture2DShader()
	{
	    if (Util.trivialTexture2DShader == null)
	    {
	        Util.trivialTexture2DShader = new CgFragmentProgram("trivialTexture2DShader");
	        String source =
	            "void main(uniform sampler2D texture, \n" +
	            "                  float2 st : TEXCOORD0, \n" +
	            "              out float4 color : COLOR) \n" +
	            "{ \n" +
	            "   color = tex2D(texture, st); \n" +
	            "} \n";
	        trivialTexture2DShader.setProgram(source);
	        trivialTexture2DShader.compile();
	        Util.checkGLErrors("enableTrivialTexture2DShader()");
	    }
	    Util.trivialTexture2DShader.enable();	
	}
	
	static void disableTrivialTexture2DShader()
	{
	    if (Util.trivialTexture2DShader != null) Util.trivialTexture2DShader.disable();		
	}
	
	static void renderTexture(int id)
	{
		int width = 640;
		int height = 480;
		
		FrameBufferObject.disableFBORendering();
		
        Util.gl.glMatrixMode(GL.GL_PROJECTION);
        Util.gl.glLoadIdentity();
        Util.gl.glOrtho(0.0, width, 0.0, height, -100.0, +100.0);
        Util.gl.glMatrixMode(GL.GL_MODELVIEW);
        Util.gl.glLoadIdentity();        
        Util.gl.glViewport(0, 0, width, height);
        Util.gl.glActiveTexture(GL.GL_TEXTURE0);
        Util.gl.glBindTexture(GL.GL_TEXTURE_2D, id);
        Util.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);         
        Util.gl.glBegin(GL.GL_QUADS);
        Util.gl.glTexCoord2f(0.0f, 1.0f);
        Util.gl.glVertex2f(0.0f, 0.0f);
        Util.gl.glTexCoord2f(1.0f, 1.0f);
        Util.gl.glVertex2f(width, 0.0f);
        Util.gl.glTexCoord2f(1.0f, 0.0f);
        Util.gl.glVertex2f(width, height);
        Util.gl.glTexCoord2f(0.0f, 0.0f);
        Util.gl.glVertex2f(0.0f, height);
        Util.gl.glEnd();
        Util.gl.glBindTexture(GL.GL_TEXTURE_2D, 0);		
	}
	
	// GPU Texture Sampling Pattern
    public static final int GPU_SAMPLE_NONE = 0;  
    public static final int GPU_SAMPLE_NEIGHBORS = 1;     // Sample the 4 direct neighbors (E, W, N, S) 
    public static final int GPU_SAMPLE_DIAG_NEIGBORS = 2; // Sample the diagonal neighbors (NE, SW, NW, SE)
    public static final int GPU_SAMPLE_2X2_BLOCK = 3;     // Sample the 2x2 block (here, E, S, SE)      
		
	static int[] extPixelFormat = { 0, GL.GL_LUMINANCE, GL.GL_LUMINANCE_ALPHA, GL.GL_RGB, GL.GL_RGBA };
	
	static CgFragmentProgram trivialTexture2DShader = null;
	
	static PApplet parent = null;
    static GL gl = null;	
    static GLU glu = null;
}
