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
 * This is a template class and can be used to start a new processing library or tool.
 * Make sure you rename this class as well as the name of the example package 'template' 
 * to your own library or tool naming convention.
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

/*************************************************
 * PROCESSING REPLACEMENTS
 * Some of Processing's handy math and color functions.
 * These are mostly for debugging and while writing code.
 * In particular, the color functions should always be
 * replaced with in-line code for efficiency reasons.
 *************************************************/

public class AUMisc implements PConstants {
	
	public AUMisc() {
	}
	
	public static float jmap(float t, float s0, float s1, float e0, float e1) {
		float v = 0;
		if (s0 != s1) v = (t-s0)/(s1-s0);
		v = e0 + (v*(e1-e0));
		return v;
	}
	
	public static float jrandom(float a) {
		return (float)(a * Math.random());
	}
	
	public static float jlerp(float a, float b, float t) {
		return a+(t*(b-a));
	}
	
	public static float jdist(float x0, float y0, float x1, float y1) {
		return (float)Math.hypot(x1-x0, y1-y0);
	}
	
	public static float jsq(float a) {
		return a*a;
	}
	
	public static float jconstrain(float a, float low, float high) {
		if (a < low) return low;
		if (a > high) return high;
		return a;
	}
	
	public static float jnorm(float value, float start, float stop) {
		if (start == stop) return 0;
		return (value-start)/(stop-start);
	}
	
	public static float jease(float t) {
		if (t<0) return 0;
		if (t>1) return 1;
		float t2 = t*t;
		return (-2*t*t2) + (3*t2);
	}
	
	public static float jmag(float a, float b) {
		return (float)Math.sqrt((a*a)+(b*b));
	}
	
	/*
	 * Take apart and assemble colors. Note when assembling that
	 * the alpha value is 0xFF (format in the int is ARGB).
	 */
	public static int jalpha(int c) {
		return (c >> 24) & 0xFF;
	}
	
	public static int jred(int c) {
		return (c >> 16) & 0xFF;
	}
	
	public static int jgreen(int c) {
		return (c >> 8) & 0xFF;
	}
	
	public static int jblue(int c) {
		return c & 0xFF;
	}
	
	public static int jcolor(int gray) {
		return (0xFF<<24) | ((gray&0xFF)<<16) | ((gray&0xFF)<<8) | (gray&0xFF);
	}
	
	public static int jcolor(int r, int g, int b) {
		return (0xFF<<24) | ((r&0xFF)<<16) | ((g&0xFF)<<8) | (b&0xFF);
	}
	
	public static int jcolor(int r, int g, int b, int a) {
		return ((a&0xFF)<<24) | ((r&0xFF)<<16) | ((g&0xFF)<<8) | (b&0xFF);
	}
}
