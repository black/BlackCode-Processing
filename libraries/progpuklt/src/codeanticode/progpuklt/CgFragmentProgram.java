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

import com.sun.opengl.cg.*;
import java.nio.*;

class CgFragmentProgram extends ProgramBase
{
    CgFragmentProgram(String shaderName) 
    { 
        super(shaderName);
        source = "";         
        program = null;
    }

    String getCompiledString()
    {
        if (!CgGL.cgIsProgram(program)) return null;
        return CgGL.cgGetProgramString(program, CgGL.CG_COMPILED_PROGRAM);
    }

    static void initializeCg()
    {
        if (context == null)
        {
           context = CgGL.cgCreateContext();

           fragmentProfile = CgGL.CG_PROFILE_UNKNOWN;
           vertexProfile   = CgGL.CG_PROFILE_UNKNOWN;

           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_ARBFP1)) fragmentProfile = CgGL.CG_PROFILE_ARBFP1;
           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_FP20))   fragmentProfile = CgGL.CG_PROFILE_FP20;
           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_FP30))   fragmentProfile = CgGL.CG_PROFILE_FP30;
           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_FP40))   fragmentProfile = CgGL.CG_PROFILE_FP40;
           //if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_GPU_FP)) fragmentProfile = CgGL.CG_PROFILE_GPU_FP;

           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_ARBVP1)) vertexProfile = CgGL.CG_PROFILE_ARBVP1;
           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_VP20))   vertexProfile = CgGL.CG_PROFILE_VP20;
           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_VP30))   vertexProfile = CgGL.CG_PROFILE_VP30;
           if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_VP40))   vertexProfile = CgGL.CG_PROFILE_VP40;
           //if (CgGL.cgGLIsProfileSupported(CgGL.CG_PROFILE_GPU_VP)) vertexProfile = CgGL.CG_PROFILE_GPU_VP;

           if (fragmentProfile == CgGL.CG_PROFILE_UNKNOWN)
               Util.raiseGLWarning("No useful fragment shader profile found", "CgFragmentProgram.initializeCg()");
           if (vertexProfile == CgGL.CG_PROFILE_UNKNOWN)
               Util.raiseGLWarning("No useful vertex shader profile found.", "CgFragmentProgram.initializeCg()");
        }
    }

    void setProgram(String src)
    {
        source = src;
        if (program != null) CgGL.cgDestroyProgram(program);
        program = null;
    	
    }

    void compile()
    {
        compile(null);
    }
    
    void compile(String[] compilerArgs)
    {
        if (program == null)
        {
        	// Note: compilerArgs array must end in null.
            program = CgGL.cgCreateProgram(context, CgGL.CG_SOURCE, source, 
        	   	                           fragmentProfile, null, compilerArgs);
           
            if (program == null)
            {
        	    int error = CgGL.cgGetError();
                System.err.println("Program: " + shaderName); 
                System.err.println("Cg compiler report:");
                System.err.println(CgGL.cgGetErrorString(error));
                System.err.println(CgGL.cgGetLastListing(context));
            }
        }
        if (!CgGL.cgIsProgramCompiled(program)) CgGL.cgCompileProgram(program);
    }
    
    void enable()
    {
        CgGL.cgGLEnableProfile(fragmentProfile);
        CgGL.cgGLLoadProgram(program);
        CgGL.cgGLBindProgram(program);    	
    }
    
    void disable()
    {
    	CgGL.cgGLDisableProfile(fragmentProfile);    	
    }

    void parameter(String param, float x)
    {
        CgGL.cgSetParameter1f(CgGL.cgGetNamedParameter(program, param), x);    	
    }
    
    void parameter(String param, float x, float y)
    {
    	CgGL.cgSetParameter2f(CgGL.cgGetNamedParameter(program, param), x, y);    	
    }
    
    void parameter(String param, float x, float y, float z)
    {
    	CgGL.cgSetParameter3f(CgGL.cgGetNamedParameter(program, param), x, y, z);    	
    }
    
    void parameter(String param, float x, float y, float z, float w)
    {
    	CgGL.cgSetParameter4f(CgGL.cgGetNamedParameter(program, param), x, y, z, w);    	
    }
    
    void parameter(String param, int len, float[] array)
    {
    	CgGL.cgGLSetParameterArray1f(CgGL.cgGetNamedParameter(program, param), 0, len, FloatBuffer.wrap(array));    	
    }
    
    void matrixParameterR(String param, int rows, int cols, double[] values)
    {
        if (rows > 4 || cols > 4)
        {
        	Util.raiseGLError("Matrix parameter should be <= 4x4", shaderName);
            return;
        }
        
        CgGL.cgGLSetMatrixParameterdr(CgGL.cgGetNamedParameter(program, param), DoubleBuffer.wrap(values));
    }
    
    void matrixParameterC(String param, int rows, int cols, double[] values)
    {
        if (rows > 4 || cols > 4)
        {
        	Util.raiseGLError("Matrix parameter should be <= 4x4", shaderName);
            return;
        }

        CgGL.cgGLSetMatrixParameterdc(CgGL.cgGetNamedParameter(program, param), DoubleBuffer.wrap(values));    	
    }

    int getTexUnit(String param)
    {
        return CgGL.cgGLGetTextureEnum(CgGL.cgGetNamedParameter(program, param));    	
    }
    
    protected String source;
    protected CGprogram program;
    protected static CGcontext context;
    protected static int fragmentProfile;
    protected static int vertexProfile;    
}
