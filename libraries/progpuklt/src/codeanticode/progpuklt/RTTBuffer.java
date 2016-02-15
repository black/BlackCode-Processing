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
 
class RTTBuffer
{
	RTTBuffer()
	{
	    init("rgb=8", "<unnamed RTT buffer object>");	
	}

	RTTBuffer(String texSpec)
	{
	    init(texSpec, "<unnamed RTT buffer object>");	
	}
	
	RTTBuffer(String texSpec, String rttName)
    { 
		init(texSpec, rttName);
    }
	
	void init(String texSpec, String rttName)
	{
		this.texSpec = texSpec;	 
		tex = new ImageTexture2D(rttName);
		fbo = new FrameBufferObject(rttName);
	}

    boolean allocate(int w, int h)
    {
        tex.allocateID();
        tex.reserve(w, h, new TextureSpecification(texSpec));
        fbo.allocate();
        fbo.makeCurrent();
        fbo.attachTexture2D(tex);
        fbo.checkValidity();
        return true;
     }

     void deallocate()
     {
         fbo.deallocate();
         tex.deallocateID();
     }

    int getWidth() { return tex.getWidth(); }
    int getHeight() { return tex.getHeight(); }
    int textureID() { return tex.textureID(); }
    int textureTarget() { return tex.textureTarget(); }

    void bindTexture() { tex.bind(); }
    void bindTexture(int texUnit) { tex.bind(texUnit); }
    void unbindTexture() { tex.unbind(); }
    void unbindTexture(int texUnit) { tex.unbind(texUnit); }

    /*
    void enableTexture() { tex.enable(); }
    void enableTexture(int texUnit) { tex.enable(texUnit); }
    void disableTexture() { tex.disable(); }
    void disableTexture(int texUnit) { tex.disable(texUnit); }    
    */
    
    void makeCurrent() { fbo.makeCurrent(); }
    void activate() { fbo.activate(true); }
    void activate(boolean setViewport) { fbo.activate(setViewport); }
    boolean isCurrent() { return fbo.isCurrent(); }

    ImageTexture2D getTexture() { return tex; }
    FrameBufferObject getFBO() { return fbo; }

    protected String texSpec;
    protected ImageTexture2D tex;
    protected FrameBufferObject fbo;
 }
