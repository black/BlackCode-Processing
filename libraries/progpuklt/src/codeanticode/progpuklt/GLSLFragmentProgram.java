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
import java.util.*;

class GLSLFragmentProgram extends ProgramBase
{
    GLSLFragmentProgram(String shaderName)        
    {
        super(shaderName);
        gl = Util.gl;
    	source = ""; 
    	program = 0; 
    	inUse = false;
    	texUnitMap = new HashMap<String, Integer>();
    }

    void setProgram(String source)
    {
        this.source = source;
        // To enforce recompilation.
        if (program != 0) gl.glDeleteObjectARB(program);
        program = 0;
    }

    void compile()
    {
        if (program == 0)
        {
            int[] success = { GL.GL_FALSE };
            int logLength;

            program = gl.glCreateProgramObjectARB();

            int fragmentProgram = gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);           
            gl.glShaderSourceARB(fragmentProgram, 1, new String[]{source}, (int[]) null, 0);
            gl.glCompileShaderARB(fragmentProgram);

            gl.glGetObjectParameterivARB(fragmentProgram, GL.GL_OBJECT_COMPILE_STATUS_ARB, success, 0);
           
            if (success[0] == GL.GL_FALSE)
            {
                IntBuffer iVal = BufferUtil.newIntBuffer(1);
                gl.glGetObjectParameterivARB(fragmentProgram, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
                logLength = iVal.get();
              
                ByteBuffer logStr = BufferUtil.newByteBuffer(logLength);
                iVal.flip();
                gl.glGetInfoLogARB(fragmentProgram, logLength, iVal, logStr);
                byte[] infoBytes = new byte[logLength];
                logStr.get(infoBytes);
                System.err.println("GLSL compilation error at " + shaderName + " :");
                System.err.println(new String(infoBytes));
              
                gl.glDeleteObjectARB(program);
                program = 0;
               
                return;
            }

            Util.checkGLErrors(shaderName);

            gl.glAttachObjectARB(program, fragmentProgram);
            gl.glDeleteObjectARB(fragmentProgram); // To delete the shader if the program is deleted.

            gl.glLinkProgramARB(program);

            gl.glGetObjectParameterivARB(fragmentProgram, GL.GL_OBJECT_LINK_STATUS_ARB, success, 0);

            if (success[0] == GL.GL_FALSE)
            {
                IntBuffer iVal = BufferUtil.newIntBuffer(1);
                gl.glGetObjectParameterivARB(fragmentProgram, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
                logLength = iVal.get();
               
                ByteBuffer logStr = BufferUtil.newByteBuffer(logLength);
                iVal.flip();
                gl.glGetInfoLogARB(fragmentProgram, logLength, iVal, logStr);
                byte[] infoBytes = new byte[logLength];
                logStr.get(infoBytes);
                System.err.println("GLSL linking error at " + shaderName + " :");                
                System.err.println(new String(infoBytes));
              
                gl.glDeleteObjectARB(program);
                program = 0;
              
                return;
            }

            Util.checkGLErrors(shaderName);
           
            gl.glUseProgramObjectARB(program);

            // Build a mapping from texture parameters to texture units.
            texUnitMap.clear();
            int count, size;
            int type;
            byte[] paramName = new byte[1024];
            
            int texUnit = 0;

            int[] iVal = { 0 };
            gl.glGetObjectParameterivARB(fragmentProgram, GL.GL_OBJECT_COMPILE_STATUS_ARB, iVal, 0);
            count = iVal[0];
            for (int i = 0; i < count; ++i)
            {
            	IntBuffer iVal1 = BufferUtil.newIntBuffer(1);
            	IntBuffer iVal2 = BufferUtil.newIntBuffer(1);
            	
            	ByteBuffer paramStr = BufferUtil.newByteBuffer(1024);
            	gl.glGetActiveUniformARB(program, i, 1024, null, iVal1, iVal2, paramStr);
                size = iVal1.get();
                type = iVal2.get();
                paramStr.get(paramName);
 
                if ((type == GL.GL_SAMPLER_1D_ARB) ||
                    (type == GL.GL_SAMPLER_2D_ARB) ||
                    (type == GL.GL_SAMPLER_3D_ARB) ||
                    (type == GL.GL_SAMPLER_CUBE_ARB) ||
                    (type == GL.GL_SAMPLER_1D_SHADOW_ARB) ||
                    (type == GL.GL_SAMPLER_2D_SHADOW_ARB) ||
                    (type == GL.GL_SAMPLER_2D_RECT_ARB) ||
                    (type == GL.GL_SAMPLER_2D_RECT_SHADOW_ARB))
                {
                	String strName = new String(paramName, 0, 1024);
                	Integer unit = texUnit;
                    texUnitMap.put(strName, unit);
                    int location = gl.glGetUniformLocationARB(program, strName);
                    gl.glUniform1iARB(location, texUnit);
                    ++texUnit;
                }
            }
           
            gl.glUseProgramObjectARB(0);
        }

        Util.checkGLErrors(shaderName);    	
    }
    
    void compile(String[] compilerArgs)
    {
        if (compilerArgs.length != 0)
        {
        	Util.raiseGLWarning("Arguments to the compiler are not supported (and ignored)", "GLSLFragmentProgram.compile()");
        }
        compile();
    }
    
    void enable()
    {
    	gl.glUseProgramObjectARB(program);
        inUse = true;
    }
    
    void disable()
    {
        inUse = false;
        gl.glUseProgramObjectARB(0);
    }

    void parameter(String param, float x)
    {
    	gl.glUseProgramObjectARB(program);
    	gl.glUniform1fARB(gl.glGetUniformLocationARB(program, param), x);
        if (!inUse) gl.glUseProgramObjectARB(0);
    }
    
    void parameter(String param, float x, float y)
    {
    	gl.glUseProgramObjectARB(program);
    	gl.glUniform2fARB(gl.glGetUniformLocationARB(program, param), x, y);
        if (!inUse) gl.glUseProgramObjectARB(0);    	
    }
    
    void parameter(String param, float x, float y, float z)
    {
    	gl.glUseProgramObjectARB(program);
    	gl.glUniform3fARB(gl.glGetUniformLocationARB(program, param), x, y, z);
        if (!inUse) gl.glUseProgramObjectARB(0);    	
    }
    
    void parameter(String param, float x, float y, float z, float w)
    {
    	gl.glUseProgramObjectARB(program);
    	gl.glUniform4fARB(gl.glGetUniformLocationARB(program, param), x, y, z, w);
        if (!inUse) gl.glUseProgramObjectARB(0);    	
    }
    
    void parameter(String param, int len, float[] array)
    {
    	gl.glUseProgramObjectARB(program);
    	gl.glUniform1fvARB(gl.glGetUniformLocationARB(program, param), len, FloatBuffer.wrap(array));    	
    	if (!inUse) gl.glUseProgramObjectARB(0);    	
    }
    
    void matrixParameterR(String param, int rows, int cols, double[] values)
    {
        float[] fvalues = new float[rows * cols];
        for (int i = 0; i < rows * cols; i++) fvalues[i] = (float)values[i];
        
        gl.glUseProgramObjectARB(program);

        if (rows == 2 && cols == 2)
        {
        	gl.glUniformMatrix2fvARB(gl.glGetUniformLocationARB(program, param),
                                     1, true, FloatBuffer.wrap(fvalues));
        }
        else if (rows == 3 && cols == 3)
        {
        	gl.glUniformMatrix3fvARB(gl.glGetUniformLocationARB(program, param),
                                     1, true, FloatBuffer.wrap(fvalues));
        }
        else if (rows == 4 && cols == 4)
        {
        	gl.glUniformMatrix4fvARB(gl.glGetUniformLocationARB(program, param),
                                     1, true, FloatBuffer.wrap(fvalues));
        }
        else
        	Util.raiseGLError("Matrix parameter should be 2x2, 3x3 or 4x4", shaderName);

        if (!inUse) gl.glUseProgramObjectARB(0);
    }
    
    void matrixParameterC(String param, int rows, int cols, double[] values)
    {
        float[] fvalues = new float[rows * cols];
        for (int i = 0; i < rows * cols; i++) fvalues[i] = (float)values[i];
        
        gl.glUseProgramObjectARB(program);

        if (rows == 2 && cols == 2)
        {
        	gl.glUniformMatrix2fvARB(gl.glGetUniformLocationARB(program, param),
                                     1, false, FloatBuffer.wrap(fvalues));
        }
        else if (rows == 3 && cols == 3)
        {
        	gl.glUniformMatrix3fvARB(gl.glGetUniformLocationARB(program, param),
                                     1, false, FloatBuffer.wrap(fvalues));
        }
        else if (rows == 4 && cols == 4)
        {
        	gl.glUniformMatrix4fvARB(gl.glGetUniformLocationARB(program, param),
                                     1, false, FloatBuffer.wrap(fvalues));
        }
        else
            Util.raiseGLError("Matrix parameter should be 2x2, 3x3 or 4x4", shaderName);

        if (!inUse) gl.glUseProgramObjectARB(0); 	
    }

    int getTexUnit(String param)
    {
    	
        if (texUnitMap.containsKey(param))
        {
            Integer value = (Integer) texUnitMap.get(param);
            return GL.GL_TEXTURE0 + value.intValue();
        }
        else
        	Util.raiseGLError("Parameter name denotes no texture sampler", shaderName);
        return 0;
    }

    protected GL gl;
    protected String source;
    protected int program;
    protected boolean inUse;
    protected HashMap<String, Integer> texUnitMap;
}
