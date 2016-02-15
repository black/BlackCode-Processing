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
 * @example AUChoose_demo
 * @example AUDist_demo
 * @example AUEase_demo
 * @example AUWave_demo
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class AULib implements PConstants {
	
	public final static String VERSION = "2.2.1";
	public final static int ERROR_LEVEL_SILENT = 0;
	public final static int ERROR_LEVEL_NORMAL = 1;
	public final static int ERROR_LEVEL_EXPERT = 2;
	public final static int ERROR_LEVEL_QUIT = 3;
	public static int ErrorLevel = ERROR_LEVEL_NORMAL;
	
	// this constructor is not needed
	public AULib() {  
		//welcome();
	}
	
	public static String version() {
		return VERSION;
	}
	
	private void welcome() {
		System.out.println("Andrew's Utilities (AULib) 2.2.1 by Andrew Glassner http://glassner.com");
	}
	
	public static void setErrorLevel(int _errorLevel) {
		ErrorLevel = _errorLevel;
	}
	
	public static void reportError(String _file, String _func, String _msg, String _data) {
		switch (ErrorLevel) {
			case ERROR_LEVEL_SILENT:
				break;
			case ERROR_LEVEL_NORMAL:
			default:
				System.out.println(_file+" ("+_func+"): "+_msg);
				break;
			case ERROR_LEVEL_EXPERT:
				System.out.println(_file+" ("+_func+"): "+_msg+" data="+_data);
				break;
			case ERROR_LEVEL_QUIT:
				System.out.println(_file+" ("+_func+"): "+_msg);
				System.exit(0);
				break;
		}
	}
	 
	/*************************************************
	 * COIN FLIP
	 *************************************************/
	
	public static boolean flip() {
	  float rnd = (float)Math.random();
	  if (rnd > .5) return true;
	  return false;
	}

	 
	/*************************************************
	 * WRAP
	 *************************************************/
	
	// When value is positive, return value % modulus. When value is negative, return
	// what I wish modulo did: treat each interval (n*modulus, (n+1)*modulus) the
	// same for every integer n, even when n is negative. When modulus is negative,
	// flip things around so input values in the interval [0, modulus) return unchanged,
	// just as when modulus is positive.
	// see http://www.imaginary-institute.com/blog/2015/07/11/a-practical-replacement-for-modulo
	
	float wrap(float v, float m) {   
		if (m > 0) {
			if (v < 0) v = m-((-v) % m);          // get negative v into region [0, m)
		} else {
			m = -m;                               // the positive value is easier to work with
			if (v < 0) v += m * (1 + Math.floor(-v/m));  // add m enough times so v > 0
			v =  m - (v % m);                     // get v % m, then flip the curve negative
		}
	return v % m;                           // return v % m, now that both are positive
}

	
	/*************************************************
	 * ANIMATION CONVENIENCE
	 *************************************************/
	
	// given time in [0,1] return [0,1] showing where we are in the lifetime of this object.
	// 0 <= startTime < 1, startTime < endTime < 2
	// We actually want to return 2 pieces of information: the new time and whether you're
	// inside the range. But we can only return one data item, sigh. Since the new time will 
	// always be [0,1] we return -1 to indicate out of range. Ick.
	public static float lifetime(float startTime, float endTime, float time) {    
	  float dtime = endTime-startTime;
	  if (dtime == 0) return -1;
	  float phase = (time-startTime)/dtime;
	  if (time < startTime) {
		  phase = ((time+1)-startTime)/dtime;
	  }
	  if ((phase<0) || (phase>1)) return -1;
	  return phase;
	}
	
	/*************************************************
	 * QUICK BLENDS
	 *************************************************/
	
	public static float cosEase(float _t) {
	  if (_t<0) return 0;
	  if (_t>1) return 1;
	  float cost = (float)Math.cos(Math.PI*_t);
	  return AUMisc.jmap(cost, 1, -1, 0, 1);
	}

	// A cubic blend. Derivative is 0 at both ends
	public static float S(float _t) { // f(t) = -2x^3 + 3x^2
	  if (_t<0) return 0;
	  if (_t>1) return 1;
	  return _t * _t * (3.0f - 2.0f * _t);	
	}
	
	// Identical to S(), just another name for the same thing
	public static float cubicEase(float _t) { // f(t) = -2x^3 + 3x^2
	  if (_t<0) return 0;
	  if (_t>1) return 1;
	  return _t * _t * (3.0f - 2.0f * _t);	
	}
	
	// fifth-order blend. Slightly flatter at ends than the cubic. 
	public static float S5(float _t) {
	  if (_t<0) return 0;
	  if (_t>1) return 1;
	  return _t * _t * _t * (_t * (_t * 6.0f - 15.0f) + 10.0f);
	}
	
	
	/*************************************************
	 * CHOOSING
	 *************************************************/
	
	public static float chooseOne(float[] _v) {
		if ((_v == null) || (_v.length < 1)) {
			reportError("AULib", "chooseOne", "the input array is null or has no entries. returning 0", "");
			return 0;
		}
		int index = (int)(AUMisc.jrandom(_v.length));
		return _v[index];
	}
	
	public static int chooseOne(int[] _v) {
		if ((_v == null) || (_v.length < 1)) {
			reportError("AULib", "chooseOne", "the input array is null or has no entries. returning 0", "");
			return 0;
		}
		int index = (int)(AUMisc.jrandom(_v.length));
		return _v[index];
	}
	
	public static int chooseOne(int _n) {
		int index = (int)(AUMisc.jrandom(_n));
		return index;
	}

	// This is slow because we allocate a new array each time,
	// but doing it without the array is even slower!
	public static float chooseOneWeighted(float[] _v, float[] _w) {
		if ((_v == null) || (_v.length < 1) || (_w == null) || (_w.length < 1) || (_v.length != _w.length)) {
			reportError("AULib", "chooseOneWeighted", "the two arrays are not both present with same size. returning 0", "");
			return 0;
		}
		float[] wsums = new float[_w.length];
		float totalW = 0;
		for (int i=0; i<_w.length; i++) totalW += _w[i];
		wsums[0] = _w[0]/totalW;           // do case 0 by itself for speed
		for (int i=1; i<_w.length; i++) {
			wsums[i] = wsums[i-1] + (_w[i]/totalW);
		}
		float wr = (float)Math.random();
		for (int i=0; i<wsums.length; i++) {
			if (wr < wsums[i]) return _v[i];
		}
	return(_v[_v.length-1]);
	}
	
	public static int chooseOneWeighted(int[] _v, float[] _w) {
		if ((_v == null) || (_v.length < 1) || (_w == null) || (_w.length < 1) || (_v.length != _w.length)) {
			reportError("AULib", "chooseOneWeighted", "the two arrays are not both present with same size. returning 0", "");
			return 0;
		}
		float[] wsums = new float[_w.length];
		float totalW = 0;
		for (int i=0; i<_w.length; i++) totalW += _w[i];
		wsums[0] = _w[0]/totalW;           // do case 0 by itself for speed
		for (int i=1; i<_w.length; i++) {
			wsums[i] = wsums[i-1] + (_w[i]/totalW);
		}
		float wr = AUMisc.jrandom(1);
		for (int i=0; i<wsums.length; i++) {
			if (wr < wsums[i]) return _v[i];
		}
		return(_v[_v.length-1]);
	}
	
	
	
	/*************************************************
	 * DISTANCES
	 *************************************************/
	
	// these are for use with dist
	public static final int DIST_RADIAL = 0;
	public static final int DIST_LINEAR = 1;
	public static final int DIST_BOX = 2;
	public static final int DIST_PLUS = 3;
	public static final int DIST_ANGLE = 4;

	// these are for use with distN
	public static final int DIST_NGON = 5;
	public static final int DIST_STAR = 6;

	static float AB_tolerance = .00001f;  // if |AB|<AB_tolerance, consider them identical

	public static float dist(int _distanceType, float _ax, float _ay, float _bx, float _by, float _px, float _py) {
		float d = 0;
		switch (_distanceType) {
			default:
			case DIST_RADIAL:
				d = radialStyleDistanceToP(_ax, _ay, _bx, _by, _px, _py);
				break;
			case DIST_LINEAR:
				d = linearStyleDistanceToP(_ax, _ay, _bx, _by, _px, _py);
				break;
			case DIST_BOX:
				d = boxStyleDistanceToP(_ax, _ay, _bx, _by, _px, _py);
				break;
			case DIST_PLUS:
				d = plusStyleDistanceToP(_ax, _ay, _bx, _by, _px, _py);
				break;
			case DIST_ANGLE:
				d = angleStyleDistanceToP(_ax, _ay, _bx, _by, _px, _py);
				break;
		}
		return d;
	}

	public static float distN(int _distanceType, int _n, float _ax, float _ay, float _bx, float _by, float _px, float _py) {
		float d = 0;
		switch (_distanceType) {
			default:
			case DIST_NGON:
				d = ngonStyleDistanceToP(_n, _ax, _ay, _bx, _by, _px, _py);
				break;
			case DIST_STAR:
				d = starStyleDistanceToP(_n, _ax, _ay, _bx, _by, _px, _py);
				break;
		}
		return d;
	}
	   
	// signed distance of p from line ab. Positive is on right from a to b.
	static float linearStyleDistanceToP(float ax, float ay, float bx, float by, float px, float py) {   
		float t = getTOnLineNearestP(ax, ay, bx, by, px, py);
		float rx = AUMisc.jlerp(ax, bx, t);
		float ry = AUMisc.jlerp(ay, by, t);
		float distar = AUMisc.jdist(ax, ay, rx, ry);
		float distab = AUMisc.jdist(ax, ay, bx, by);
		float d = 0;
		if (distab > AB_tolerance) d = distar/distab;  
		if (t < 0) d *= -1;
		return d;
	}

	static float radialStyleDistanceToP(float ax, float ay, float bx, float by, float px, float py) { 
		float distap = AUMisc.jdist(ax, ay, px, py);
		float distab = AUMisc.jdist(ax, ay, bx, by);
		float d = 0;
		if (distab > AB_tolerance) d = distap/distab;
		return d;
	}

	static float plusStyleDistanceToP(float ax, float ay, float bx, float by, float px, float py) { 
		float cx = ax + (by-ay);  // create a point c so that line ac is perpendicular to ab
		float cy = ay + (ax-bx);
		float d1 = linearStyleDistanceToP(ax, ay, bx, by, px, py);
		float d2 = linearStyleDistanceToP(ax, ay, cx, cy, px, py);
		float d = Math.min(Math.abs(d1), Math.abs(d2));  // beacuase linearStyleDistanceToP is signed
		return d;
	}

	static float boxStyleDistanceToP(float ax, float ay, float bx, float by, float px, float py) { 
		// Just a bunch of vector algebra. Rather than add dependencies on a vector
		// math package, it's easy enough to just type it all out.
		float cx = ax + (by-ay);  // create a point c so that line ac is perpendicular to ab
		float cy = ay + (ax-bx);
		float dx = ax + (ay-by);  // point d is c reflected through a
		float dy = ay + (bx-ax);
		float ux = (bx+cx)/2.f;  // u is on diagonal between b and c
		float uy = (by+cy)/2.f;
		float jx = (bx+dx)/2.f;  // j is on diagonal between b and d
		float jy = (by+dy)/2.f;
		float t1 = getTOnLineNearestP(ax, ay, ux, uy, px, py);
		float t2 = getTOnLineNearestP(ax, ay, jx, jy, px, py);
		float vx = AUMisc.jlerp(ax, ux, t1);  // v is point on au nearest to p
		float vy = AUMisc.jlerp(ay, uy, t1);
		float kx = AUMisc.jlerp(ax, jx, t2);  // k is point on aj nearest to p
		float ky = AUMisc.jlerp(ay, jy, t2);
		float distpv = AUMisc.jdist(px, py, vx, vy);
		float distpk = AUMisc.jdist(px, py, kx, ky);
		float refDist = AUMisc.jdist(ax, ay, bx, by);
		refDist /= 1.41421356237f; // divide by sqrt(2) to compare to box side, not corner
		if (refDist < AB_tolerance) refDist = 1;    
		if (distpv < distpk) return AUMisc.jdist(ax, ay, vx, vy)/refDist;
		return AUMisc.jdist(ax, ay, kx, ky)/refDist;
	}

	static float angleStyleDistanceToP(float ax, float ay, float bx, float by, float px, float py) { 
		float thetaB = (float)(Math.PI + Math.atan2(by-ay, bx-ax));
		float thetaP = (float)(Math.PI + Math.atan2(py-ay, px-ax));
		while (thetaP > thetaB) thetaP -= (float)(2*Math.PI);
		if (Math.abs(thetaB-thetaP) > Math.PI) thetaP += (float)(2*Math.PI);
		float d = (float)((thetaP-thetaB)/Math.PI);
		return d;
	}

	static float getTOnLineNearestP(float ax, float ay, float bx, float by, float px, float py) {
		// quick and easy, find intersection of line (a,b) and its perpendicular through p
		float qx = px+(by-ay);  // q is point on line through p perpendicular to (a,b)
		float qy = py-(bx-ax);
		float t = intersectLines(ax, ay, bx, by, px, py, qx, qy);
		return t;
	}

	// find t on line P1,P2 that gives point of intersection with line P3,P4
	static float intersectLines(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		float x43 = x4-x3;
		float y43 = y4-y3;
		float x31 = x3-x1;
		float y31 = y3-y1;
		float x21 = x2-x1;
		float y21 = y2-y1;
		float tnum = (x43 * y31) - (x31 * y43);
		float tden = (x43 * y21) - (x21 * y43);
		float t = 0;
		if (tden != 0) t = tnum/tden;  // tden should never be 0, but always be safe
		return t;
	}

	static float ngonStyleDistanceToP(int n, float ax, float ay, float bx, float by, float px, float py) { 
		// Same vector algebra comment as for the plus version. So much code for simple vector algebra.
		if (n<3) return linearStyleDistanceToP(ax, ay, bx, by, px, py);
		boolean foundAny = false;
		float edgeDist = (float)(AUMisc.jdist(ax, ay, bx, by) * Math.cos((2*Math.PI)/(2*n)));
		if (edgeDist == 0) return(0);  // A=B, so nothing interesting here
		float bax = bx-ax;  // vector B-A
		float bay = by-ay;
		float minAM = 0;
		float minPM = 0;
		for (int lineNum=0; lineNum<n; lineNum++) {
			float incAngle = (float)(lineNum * ((2*Math.PI)/n));
			float thetaC = (float)(lineNum * ((2*Math.PI)/n));
			float thetaD = (float)((lineNum+1) * ((2*Math.PI)/n));
			float cosThetaC = (float)Math.cos(thetaC);
			float sinThetaC = (float)Math.sin(thetaC);
			float cosThetaD = (float)Math.cos(thetaD);
			float sinThetaD = (float)Math.sin(thetaD);
			float cx = ax + ((bax * cosThetaC) - (bay * sinThetaC)); // b rotated by thetaC
			float cy = ay + ((bax * sinThetaC) + (bay * cosThetaC));
			float dx = ax + ((bax * cosThetaD) - (bay * sinThetaD)); // b rotated by thetaD
			float dy = ay + ((bax * sinThetaD) + (bay * cosThetaD));
			float ex = (cx+dx)/2.f;  // e is midpoint of cd, so it's midpoint of edge
			float ey = (cy+dy)/2.f;
			float t = getTOnLineNearestP(ax, ay, ex, ey, px, py);
			// now find the point on ae closest to p, call it m
			float mx = AUMisc.jlerp(ax, ex, t);
			float my = AUMisc.jlerp(ay, ey, t);
			float dPM = AUMisc.jdist(px, py, mx, my);  
			if ((t >= 0) && ((!foundAny) || (dPM < minPM))) {
				minPM = dPM;
				minAM = AUMisc.jdist(ax, ay, mx, my);
				foundAny = true;
			}
		}
		return(minAM/edgeDist);
	}
	  
	static float starStyleDistanceToP(int n, float ax, float ay, float bx, float by, float px, float py) { 
		if (n<2) return linearStyleDistanceToP(ax, ay, bx, by, px, py);
		float minMP = 0;
		float bax = bx-ax;  // vector B-A
		float bay = by-ay;
		boolean foundAny = false;
		for (int lineNum=0; lineNum<n; lineNum++) {
			float incAngle = (float)(lineNum * ((2*Math.PI)/n));
			float cosTheta = (float)Math.cos(incAngle);
			float sinTheta = (float)Math.sin(incAngle);
			float cx = ax + ((bax * cosTheta) - (bay * sinTheta)); // rotate c by incAngle
			float cy = ay + ((bax * sinTheta) + (bay * cosTheta));
			float t = getTOnLineNearestP(ax, ay, cx, cy, px, py);
			if (t > 0) {
				float mx = AUMisc.jlerp(ax, cx, t);
				float my = AUMisc.jlerp(ay, cy, t);
				float distMP = AUMisc.jdist(mx, my, px, py);
				if ((!foundAny) || (distMP < minMP)) {
					minMP = distMP;
					foundAny = true;
				}
			}
		}
		float distab = AUMisc.jdist(ax, ay, bx, by);  
		if (distab > AB_tolerance) minMP /= distab;
		return minMP;
	}

	
	
	/*************************************************
	 * EASING
	 *************************************************/
	
	public static final int EASE_LINEAR = 0;       // the simplest case, for completeness
	public static final int EASE_IN_CUBIC = 1;      // start slow, finish abruptly D:[0, !0]  V:[0,1]
	public static final int EASE_OUT_CUBIC = 2;     // start abruptly, finish slow D:[!0, 0]  V:[0,1]
	public static final int EASE_IN_OUT_CUBIC = 3;  // start and end slow D:[0, 0]  V:[0,1]
	public static final int EASE_IN_BACK = 4;       // start slow, back up, finish abruptly D:[0, !0] V:[-1.1, 1]
	public static final int EASE_OUT_BACK = 5;      // start abruptly, overshoot, then end slow D:[!0, 0]  V:[0, 1.1]
	public static final int EASE_IN_OUT_BACK = 6;   // start slow, undershoot, overshoot, end slow D:[0, 0]  V:[-0.05, 1.05]
	public static final int EASE_IN_ELASTIC = 7;    // sit there, wiggle a bit, then zoom to end D:[0, !0]  V:[-.336, 1]
	public static final int EASE_OUT_ELASTIC = 8;   // zoom to overshoot, then slowly wiggle into place D:[!0, 0] V:[0, 1.33]
	public static final int EASE_IN_OUT_ELASTIC = 9; // sit there, wiggle, overshoot end, wiggle and settle D:[0, 0]  V:[-.17, 1.17]
	// These are my hybrids
	public static final int EASE_CUBIC_ELASTIC = 10;       // start slow, then bounce at the end D=[0,0] V:[0,1.17]
	public static final int EASE_ANTICIPATE_CUBIC = 11;   // small negative motion, then ease
	public static final int EASE_ANTICIPATE_ELASTIC = 12; // small backwards motion, then bounce

	public static float ease(int _easeType, float _t) {
		// This would be so much nicer with function pointers or functions as parameters. But we
		// can't do that in Processing (or in Java), so I just repeat the same in/out/inout format 
		// over and over with different functions.
		float t = _t;
		float v=0;
		float ti = 1-t;   // value for out versions (rather than in)
		float h0 = 2*t;   // t for first half of in/out combos
		float h1 = 2*ti;  // t for second half of in/out combos
		if (t<0) return 0;
		if (t>1) return 1;
		switch (_easeType) {
			default:
			case EASE_LINEAR:
				v = t;
				break;
			case EASE_IN_CUBIC:  // start slow, finish abruptly [0, !0]
				v = cubic(t);
				break;
			case EASE_OUT_CUBIC:  // start abruptly, finish slow [!0, 0]
				v = 1 - cubic(ti);
				break;
			case EASE_IN_OUT_CUBIC:  // start and end slow [0, 0]
				if (t < .5) {
					v = .5f * cubic(h0);
				} else {
					v = .5f + (.5f * (1f - cubic(h1)));
				}
			break;
				case EASE_IN_BACK:  // start slow, back up, finish abruptly [0, !0]
				v = back(t);
				break;
			case EASE_OUT_BACK:   // start abruptly, overshoot, then end slow [!0, 0]
				v = 1 - back(ti);
				break;
				case EASE_IN_OUT_BACK:  // start slow, undershoot, overshoot, end slow [0, 0]
				if (t < .5) {
					v = .5f * back(h0);
				} else {
					v = .5f + (.5f * (1f - back(h1)));
				}
				break;
			case EASE_IN_ELASTIC:  // sit there, wiggle a bit, then zoom to end [0, !0]
				v = elastic(t);
				break;
			case EASE_OUT_ELASTIC:  // zoom to overshoot, then slowly wiggle into place [!0, 0]
				v = 1 - elastic(ti);
				break;
			case EASE_IN_OUT_ELASTIC:  // sit there, wiggle, overshoot end, wiggle and settle [0, 0]
				if (t < .5) {
					v = .5f * elastic(h0);
				} else {
					v = .5f + (.5f * (1 - elastic(h1)));
				}
				break;
			case EASE_CUBIC_ELASTIC:
				// My hybrid. Start with a slow cubic ease, then bounces at the end.
				float switchCenter = .6f;
				float blendRadius = .05f;
				float sinHeight = .45f;     // biggest value is 1.17 matching elastic
				float blendStart = switchCenter-blendRadius;
				float firstPart = cubic(t/switchCenter);
				if (t < blendStart) {
					v = firstPart;
				} else {
					float t2 = (t-blendStart)/(1-blendStart);
					float theta = (float)(2 * (2*Math.PI) * t2);
					float scl = (float)(Math.pow(2, -7*t2) - 0.0078125); // subtract 2^(-7) so we really go to 0
					float secondPart = (float)(1 + sinHeight * scl * Math.sin(theta));
					float blendEnd = switchCenter+blendRadius;
					if (t > blendEnd) {
						v = secondPart;
					} else {
						float blend = (t-blendStart)/(2*blendRadius);
						blend = cubicEase(blend);
						v = AUMisc.jlerp(firstPart, secondPart, blend);
					}
				}
				break;
			case EASE_ANTICIPATE_CUBIC:
			// My hybrid. Start with a small backward jump, then eases at the end.
				float aeJumpDuration = .05f;
				float aeJumpSize = .05f;
				if (t < aeJumpDuration) {
					v = -aeJumpSize * t/aeJumpDuration;
				} else {
					float t2 = (t-aeJumpDuration)/(1-aeJumpDuration);
					v = ease(EASE_OUT_CUBIC, t2);
					v = AUMisc.jlerp(-aeJumpSize, 1, v);
				}
				break;
			case EASE_ANTICIPATE_ELASTIC:
				// My hybrid. Start with a small backward jump, then bounces at the end.
				float abJumpDuration = .05f;
				float abJumpSize = .05f;
				if (t < abJumpDuration) {
					v = -abJumpSize * t/abJumpDuration;
				} else {
					float t2 = (t-abJumpDuration)/(1-abJumpDuration);
					v = ease(EASE_OUT_ELASTIC, t2);
					v = AUMisc.jlerp(-abJumpSize, 1, v);
				}
				break;
		}
		return v;
	}

	static float cubic(float t) { 
		return (t*t*t); 
	}   

	// This is a little cubic: f(t) = (1+g)t^3 - (g)t^2
	// This is the closed form of a cubic Bezier with values (0, 0, -g/3, 1)
	// The industry standard for g is 1.70158. I don't know where that comes from, or
	// why it's so precise. Anything near 1.7 produces a visually indistinguishable result. 
	static float back(float t) { 
		float g = 1.70158f; // The industry standard, so stick with it. 
		return (t*t)*(((1+g) * t) - g);
	}
	
	// We want this to run from [0,1], but it really runs from [0.000976564, 1] 
	static float elastic(float t) { 
		return (float)(Math.pow(2, 10*(t-1)) * Math.cos(6*Math.PI*t));
	}
	
	// This version of elastic really runs does from [0,1]. Rarely worth the extra effort.
	static float elastic01(float t) {
		double mins = .000976563;  // 2^(-10)
		double scls = .999023;  // 1-mins
		double s = Math.pow(2, 10*(t-1));
		s = (s-mins)/scls;
		return (float)(s * Math.cos(6*Math.PI*t));
	}


	
	/*************************************************
	 * WAVES
	 *************************************************/
	
	public static final int WAVE_TRIANGLE = 0;  // 0 to 1 to 0, symmetrical
	public static final int WAVE_BOX = 1;       // t < a ? 1 : 0
	public static final int WAVE_SAWTOOTH = 2;  // t
	public static final int WAVE_SINE = 3;      // sin(a*2pi*t) mapped to (0,1)
	public static final int WAVE_COSINE = 4;    // cos(a*2pi*t) mapped to (0,1)
	public static final int WAVE_BLOB = 5;      // Gaussian bump from 1 to 0
	public static final int WAVE_VAR_BLOB = 6;  // controllable blob
	public static final int WAVE_BIAS = 7;      // bow down for a<.5, ramp@.5, up for a>.5
	public static final int WAVE_GAIN = 8;      // S for a<.5, ramp@.5, inverse-S for a>.5
	public static final int WAVE_SYM_BLOB = 9;       // symmetrical blob
	public static final int WAVE_SYM_VAR_BLOB = 10;  // symmetrical controllable blob
	public static final int WAVE_SYM_BIAS = 11;      // symmetrical bias
	public static final int WAVE_SYM_GAIN = 12;      // symmetrical gain
	
	public static float wave(int _waveType, float _t, float _a) {
		float t = _t;
		if (t>=0) t = (float)(t%1.);
		else t = (float)(1.-(Math.abs(t)%1.));
		
		float symt = (float)(1.-(2.*t));
		if (t > .5) symt = (float)(2.*(t-.5));
		if (t==0) symt = (float).9999999;  // funky edge case because f(0) often not f(1)
		float v = 0;
		float a = AUMisc.jconstrain(_a, 0, 1);
		switch (_waveType) {
			default:
			case WAVE_TRIANGLE:
				v = (float)(1-(2*Math.abs(t-.5)));
				break;
			case WAVE_BOX:
				if (t < a) v = 1;
				break;
			case WAVE_SAWTOOTH:
				v = t;
				break;
			case WAVE_SINE:
				v = AUMisc.jmap((float)Math.sin(a*(2*Math.PI)*t), 1, -1, 1, 0);
				break;
			case WAVE_COSINE:
				v = AUMisc.jmap((float)Math.cos(a*(2*Math.PI)*t), 1, -1, 1, 0);
				break;
			case WAVE_BLOB: // Gaussian bump
				float e2p5 = 0.00193045f;  // e^-(2.5^2), which is < .004 (about 1/255)
				float ev = AUMisc.jmap(t, 0, 1, 0, 2.5f);
				v = AUMisc.jmap((float)Math.exp(-AUMisc.jsq(ev)), 1, e2p5, 1, 0);
				break;
			case WAVE_VAR_BLOB: // Baranoski-Rokne controllable blob
				v = (AUMisc.jsq(t-1) * AUMisc.jsq(t+1))/(1+(a*50*AUMisc.jsq(t))); // expand the range of a from [0,1] to [0,50]
				break;
			case WAVE_BIAS:
				// Schlick bias function
				// 0 to 1. a = 0 = flat w/jump at 1, .25=bowed down, .5=flat, .75=bowed up, 1=jump to 1
				if (a==0) {
					v = (t < 1) ? 0 : 1;
				} else {
					float bias1 = (1.f/a)-2.f;
					v = t/(1.f+(bias1*(1.f-t)));
				}
				break;
			case WAVE_GAIN:
				 // Schlick gain function. derivatives not 0.
				if (a==0) {
				v = (t < .5) ? 0 : 1;
				} else {
					float gain1 = ((1.f/a)-2.f)*(1.f-(2.f*t));
					if (t < .5) v = t/(1+gain1);
					else v = (gain1-t)/(gain1-1);
				}
				break;
			case WAVE_SYM_BLOB:
				v = wave(WAVE_BLOB, symt, a);
				break;
			case WAVE_SYM_VAR_BLOB: 
				v = wave(WAVE_VAR_BLOB, symt, a);
				break;
			case WAVE_SYM_BIAS: 
				v = wave(WAVE_BIAS, symt, a);
				break;
			case WAVE_SYM_GAIN:
				v = wave(WAVE_GAIN, symt, a);
				break;
		}
		v = AUMisc.jconstrain(v, 0, 1);
		return v;
	}
}



