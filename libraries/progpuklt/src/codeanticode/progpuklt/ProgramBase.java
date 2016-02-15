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

import java.io.File;
import java.net.URL;

import processing.core.*;

abstract class ProgramBase
{
    ProgramBase(String shaderName) 
    { 
    	this.shaderName = shaderName;
    }

    abstract void setProgram(String source);

    void setProgramFromFile(String fileName)
    {
        File file = new File(Util.parent.dataPath(fileName));
        if (file.exists())
        	// The file was found locally (inside the sketch folder).
        	fileName = file.toString(); 
        else
        {
        	// Using the default file located inside the library jar.
		    URL url = this.getClass().getResource("shaders/" + fileName);
	        if (url != null) fileName = url.toString();
         	else
         	{
         		Util.raiseGLError("Cannot load shader file " + fileName, "ProgramBase.setProgramFromFile()");
         		System.err.println();
         		return;
         	}
        }    	
    	
    	String shaderSource = PApplet.join(Util.parent.loadStrings(fileName), "\n");    	
        setProgram(shaderSource);
    }

    abstract void compile(String[] compilerArgs);
    abstract void enable();
    abstract void disable();

    abstract void parameter(String param, float x);
    abstract void parameter(String param, float x, float y);
    abstract void parameter(String param, float x, float y, float z);
    abstract void parameter(String param, float x, float y, float z, float w);
    abstract void parameter(String param, int len, float[] array);
    abstract void matrixParameterR(String param, int rows, int cols, double[] values);
    abstract void matrixParameterC(String param, int rows, int cols, double[] values);

    abstract int getTexUnit(String param);

    String getShaderName() { return shaderName; }

    protected String shaderName;
}
