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

class TextureSpecification
{
    TextureSpecification()
    {
        nChannels = 3; 
        nBitsPerChannel = 8;
        isFloatTexture = false; 
        isDepthTexture = false; 
        isPacked = false;
    }

    TextureSpecification(String specString)
    {
        nChannels = 3; 
        nBitsPerChannel = 8;
        isFloatTexture = false; 
        isDepthTexture = false; 
        isPacked = false;
        
        String[] tokens = specString.split("\\s");
        String[] token;
        String key, value;
        for (int i = 0; i < tokens.length; i++)
        {
            token = tokens[i].split("=");
            if (token.length == 2)
            {
                key = token[0];
                value = token[1];            	
            	
                if (key.equals("r"))
                {
                    nChannels = 1;
                    if (-1 < value.indexOf('f'))
                    {
                        isFloatTexture = true;
                        value = value.replace('f', ' ').trim(); 
                    }
                    if (value.equals("")) nBitsPerChannel = isFloatTexture ? 32 : 8;
                    else nBitsPerChannel = Integer.valueOf(value).intValue(); 
                }
                else if (key.equals("rg"))
                {
                    nChannels = 2;
                    if (-1 < value.indexOf('f'))
                    {
                        isFloatTexture = true;
                        value = value.replace('f', ' ').trim(); 
                    }
                    if (value.equals("")) nBitsPerChannel = isFloatTexture ? 32 : 8;
                    else nBitsPerChannel = Integer.valueOf(value).intValue(); 
                }
                else if (key.equals("rgb"))
                {
                    nChannels = 3;
                    if (-1 < value.indexOf('f'))
                    {
                        isFloatTexture = true;
                        value = value.replace('f', ' ').trim(); 
                    }
                    if (value.equals("")) nBitsPerChannel = isFloatTexture ? 32 : 8;
                    else nBitsPerChannel = Integer.valueOf(value).intValue(); 
                }
                else if (key.equals("rgba"))
                {
                    nChannels = 4;
                    if (-1 < value.indexOf('f'))
                    {
                        isFloatTexture = true;
                        value = value.replace('f', ' ').trim(); 
                    }
                    if (value.equals("")) nBitsPerChannel = isFloatTexture ? 32 : 8;
                    else nBitsPerChannel = Integer.valueOf(value).intValue(); 
                }     
                else if (key.equals("depth"))
                {
                    isDepthTexture = true;
                    if (value.equals("")) nBitsPerChannel = 24;
                    else nBitsPerChannel = Integer.valueOf(value).intValue();                   
                }
                else if (key.equals("packed"))
                {
                    isPacked = true;
                }
            }
        }
        
        if (nChannels < 3 && !isPacked)
        {
        	Util.raiseGLWarning("Unpacked single or two channel may not work as render target", "TextureSpecification"); 	
        }
    }

    int getGLInternalFormat()
    {
        if (!isDepthTexture)
        {
            if (!isFloatTexture)
            {
                if (nBitsPerChannel == 8)
                {
                    int[] formats = { 0, GL.GL_LUMINANCE8, 
                	                     GL.GL_LUMINANCE8_ALPHA8,
                  	  	                 GL.GL_RGB8,
                                         GL.GL_RGBA8 };
                    return formats[nChannels];
                }
                else if (nBitsPerChannel == 16)
                {
                    int[] formats = { 0,
                		              GL.GL_LUMINANCE16,
                                      GL.GL_LUMINANCE16_ALPHA16,
                                      GL.GL_RGB16,
                                      GL.GL_RGBA16 };
                  
                    return formats[nChannels];
                }
                else
                {
            	    Util.raiseGLError("Unsupported number of bits for int texture (8 or 16 bits)", "TextureSpecification");
                }
            }
            else
            {
                if (nBitsPerChannel == 32)
                {
                    int[] formats = { 0,
                                      GL.GL_LUMINANCE32F_ARB,
                                      GL.GL_LUMINANCE_ALPHA32F_ARB,
                                      GL.GL_RGB32F_ARB,
                                      GL.GL_RGBA32F_ARB };
                    return formats[nChannels];
                }
                else if (nBitsPerChannel == 16)
                {
                    int[] formats = { 0,
                                      GL.GL_LUMINANCE16F_ARB,
                                      GL.GL_LUMINANCE_ALPHA16F_ARB,
                                      GL.GL_RGB16F_ARB,
                                      GL.GL_RGBA16F_ARB };
                    return formats[nChannels];
                }
                else
                {
                	Util.raiseGLError("Unsupported number of bits for int texture (8 or 16 bits)", "TextureSpecification");	
                }
            }
        }
        else
        {
            if (nBitsPerChannel == 16)
                return GL.GL_DEPTH_COMPONENT16_ARB;
            else if (nBitsPerChannel == 24)
                 return GL.GL_DEPTH_COMPONENT24_ARB;
            else if (nBitsPerChannel == 32)
                 return GL.GL_DEPTH_COMPONENT32_ARB;
            else
            	Util.raiseGLError("Unsupported number of bits for depth texture (16, 24 or 32 bits)", "TextureSpecification");
        }
        return 0;
    }

    int nChannels, nBitsPerChannel;
    boolean isFloatTexture, isDepthTexture;
    boolean isPacked;
 }
