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
 * @example AUShuffle_demo 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

/*************************************************
* AUSHUFFLE
*************************************************/	

public class AUShuffle {
	float[] originals, shuffled;
	int nextIndex, startingIndex;
	
	public AUShuffle(float[] _v) {
		if (_v == null) {
			AULib.reportError("AUShuffle", "AUShuffle", "array is null, using array { 0 }", "");
			_v = new float[1]; 
			_v[0] = 0;
		}
		if (_v.length < 1) {
			AULib.reportError("AUShuffle", "AUShuffle", "array has no entries, using array { 0 }", "");
			_v = new float[1]; 
			_v[0] = 0;
		}
		buildShuffle(_v);
	}
	
	public AUShuffle(int _n) {
		float[] indices = new float[_n];
		for (int i=0; i<indices.length; i++) indices[i] = i;
		buildShuffle(indices);
	}
	
	void buildShuffle(float[] _v) {
		originals = new float[_v.length];
		shuffled = new float[_v.length];
		for (int i=0; i<_v.length; i++) originals[i] = _v[i];
		shuffleAgain((int)(AUMisc.jrandom(originals.length)));
		nextIndex = 0;
	}
	
	void shuffleAgain(int dontStartHere) {
		boolean[] used = new boolean[originals.length];
		for (int i=0; i<used.length; i++) used[i] = false;
		for (int i=0; i<used.length; i++) {
			int n = (int)(AUMisc.jrandom(originals.length));
			if (i==0) {
				n = dontStartHere + (1 + (int)(AUMisc.jrandom(originals.length-1)));
				n = n % originals.length;
			}
			while (used[n]) n = (n+1)%originals.length;
			used[n] = true;
			shuffled[i] = originals[n];
			if (i == used.length-1) startingIndex = n;
		}
	}
	
	public float next() {
		if (nextIndex >= shuffled.length) {
			shuffleAgain(startingIndex);
			nextIndex = 0;
		}
		return shuffled[nextIndex++];
	}
}