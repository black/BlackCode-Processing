/**
 * Andrew's Utilities (AULib)
 * Motion blur, fields, easing, waves, uniformly-spaced curves, globs, and more!
 * http://imaginary-institute.com/resources/AULibrary/AULib.php
 *
 * Copyright (c) 2014-5 Andrew Glassner Andrew Glassner http://glassner.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Andrew Glassner http://glassner.com
 * @modified    08/01/2015
 * @version     2.2.1 (221)
 */

package AULib;


import processing.core.*;

/**
 * 
 * @example AUStepper_demo 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */


public class AUStepper {
			
	float[] sumLen;
    int[] easeType;
    int stepsTaken;
    float alfa; 
    int stepNum;

    public AUStepper(float _numFrames, float[] _stepLengths) {
    	_numFrames = Math.max(1, _numFrames);
    	if ((_stepLengths == null) || (_stepLengths.length < 1)) {
    		AULib.reportError("AUStepper", "AUStepper", "stepLengths is not present, or has no entries, using { 1 }", "");
    		_stepLengths = new float[1];
    		_stepLengths[0] = 1;
    	}
    	float[] lens = new float[_stepLengths.length];
        for (int i=0; i<_stepLengths.length; i++) lens[i] = _stepLengths[i];
        float totalLen = 0;
        for (int i=0; i<lens.length; i++) totalLen += lens[i];
        for (int i=0; i<lens.length; i++) lens[i] *= _numFrames/totalLen;
        sumLen = new float[1+lens.length];
        easeType = new int[1+lens.length];
        sumLen[0] = 0;
        for (int i=0; i<lens.length; i++) {
            if (i==0) sumLen[i+1] = lens[0];
            else sumLen[i+1] = sumLen[i] + lens[i];
            easeType[i] = AULib.EASE_IN_OUT_CUBIC;
        }
        stepsTaken = 0;
    }
    
    public AUStepper(int[] _framesPerStep) {
    	if ((_framesPerStep == null) || (_framesPerStep.length < 1)) {
    		AULib.reportError("AUStepper", "AUStepper", "_framesPerStep is not present, or has no entries, using { 1 }", "");
    		_framesPerStep = new int[1];
    		_framesPerStep[0] = 1;
    	}
        sumLen = new float[1+_framesPerStep.length];
        easeType = new int[1+_framesPerStep.length];
        sumLen[0] = 0;
        for (int i=0; i<_framesPerStep.length; i++) {
            sumLen[i+1] = sumLen[i] + _framesPerStep[i];
            easeType[i] = AULib.EASE_IN_OUT_CUBIC;
        }
        stepsTaken = 0;
    }
    
    public AUStepper(int _numSteps, int _framesPerStep) {
        sumLen = new float[1+_numSteps];
        easeType = new int[1+_numSteps];
        sumLen[0] = 0;
        for (int i=0; i<_numSteps; i++) {
            sumLen[i+1] = sumLen[i] + _framesPerStep;
            easeType[i] = AULib.EASE_IN_OUT_CUBIC;
        }
        stepsTaken = 0;
    }

    public void setEases(int[] _eases) {
    	if (_eases == null) {
    		AULib.reportError("AUStepper", "setEases", "_eases is not present. not changing ease types.", "");
    		return;
    	}
        for (int i=0; i<sumLen.length-1; i++) {
            easeType[i] = _eases[i%_eases.length];
        }
    }

    public void setAllEases(int _easeType) {
        for (int i=0; i<sumLen.length-1; i++) {
            easeType[i] = _easeType;
        }
    }

    public void step() {
    	float a = (stepsTaken++) % sumLen[sumLen.length-1];
        int phase = 0;
        for (int i=0; i<sumLen.length; i++) {
        	if (a >= sumLen[i]) phase = i;
        }
        float plen = sumLen[phase+1]-sumLen[phase];
        if (plen == 0) plen = 1;
        float beta = (a - sumLen[phase])/plen;
        beta = AULib.ease(easeType[phase], beta);
        stepNum = phase;    // save the step number
        alfa = beta;        // save the interpolation value 
    }
    
    public float getAlfa() {
    	return alfa;
    }
    
    public int getStepNum() {
    	return stepNum;
    }
    
    public float getFullAlfa() {
    	float falfa = ((stepsTaken-1)/(1.f*sumLen[sumLen.length-1])) % 1.f;
    	return falfa;
    }
}
