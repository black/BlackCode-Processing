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
import java.util.*;

public class AUBaseCurve {
	
	public static final int CRCURVE = 0;
	public static final int BEZCURVE = 1;
  	
  float[][] knots;        // first index is knot index, second is variable
  float[][] matrix;       // for finding points
  int numGeomVals;		   // how many of the first dimensions contribute to length
  int curveType;           // CRCURVE or BEZCURVE
  boolean clampT;          // true if you want to clamp t to [0,1] 
  float[][] ArcLengths;   // one list per curve segment. Avoid Java's icky list-of-lists stuff.
  float density;          // adjust how many arclength samples are saved
  boolean rebuildArcLengths;    // true if we need to rebuild arclengths
  
  // these are useful internal state variables
  float[] leftSide;       // convenience for left side of matrix multiply
  float[] leftTan;        // convenience for left side of matrix multiply for tangents
  float lastLeftSideT;	   // the last t value used; so we know when to remake leftSide
  float lastLeftSideTanT; // the last t value for the tangent left side
  float mappedT;          // the t value in the mapped segment
  int mappedKnotStart;     // start of the 4-knot group that uses mappedT
  
  int skipLength;          // how much to bump knot counter per segment
  int numCurves;           // number of curves represented by knot array
  int firstLengthKnot;     // where to start gathering lengths
  int numSegments;         // how many pairs to use when gathering lengths
  
  /************************************************************************* 
   * Constructor
   *************************************************************************/

  // I don't do validity checking in these constructors because the two constructors take care of that
  AUBaseCurve(float[][] _knots, float[][] _matrix, int _numGeomVals, int _curveType) { 
	density = 1.0f;
	setKnots(_knots);
  	matrix = new float[4][4];
  	for (int i=0; i<4; i++) {
  		for (int j=0; j<4; j++) {
  			matrix[i][j] = _matrix[i][j];
  		}
  	}
  	numGeomVals = _numGeomVals;
  	curveType = _curveType;
  	rebuildArcLengths = true;
  	leftSide = new float[4];
  	leftTan = new float[4];
  	lastLeftSideT = -1;
  	lastLeftSideTanT = -1;
  	clampT = false;
  }
  
  /*
   *  We need this because subclasses have to call the superclass constructor on their first
   *  line, but they have to build the knots array from their arguments. So they call the
   *  constructor with everything it needs but the knots, and then fill in the knots later.
   */
  void setKnots(float[][] _knots) { 
	if (_knots == null) {
		knots = null;
		return;
	}
  	int numKnots = _knots.length;
  	int numVars = _knots[0].length;
  	knots = new float[numKnots][numVars];
  	for (int k=0; k<numKnots; k++) {
  		for (int v=0; v<numVars; v++) {
  			knots[k][v] = _knots[k][v];
  		}
  	}  
  	rebuildArcLengths = true;
  }
  
  void setNumGeomVals(int _numGeomVals) {
	  numGeomVals = _numGeomVals;
  }
  
  /************************************************************************* 
   * Getting values 
   *************************************************************************/

  public float getX(float _t) {
	  return getIndexValue(_t, 0);
  }
  
  public float getY(float _t) {
	  return getIndexValue(_t, 1);
  }

  public float getZ(float _t) {
	  return getIndexValue(_t, 2);
  }
  
  public float getIndexValue(float _t, int _index) {
	  if (knots[0].length < _index+1) {
		  AULib.reportError("AUBaseCurve", "getIndexValue", "knots array does not have a value at index "+Float.toString(_index), "");
		  return 0;
	  }
	  mapTtoCurve(_t);
	  makeLeftSide(mappedT);
	  float v = evalCurve(knots[mappedKnotStart][_index], knots[mappedKnotStart+1][_index], knots[mappedKnotStart+2][_index], knots[mappedKnotStart+3][_index]);
	  return v;
  }
  
  public float getTanX(float _t) {
	  return getIndexTan(_t, 0);
  }
  
  public float getTanY(float _t) {
	  return getIndexTan(_t, 1);
  }
  
  public float getTanZ(float _t) {
	  return getIndexTan(_t, 2);
  }
  
  public float getIndexTan(float _t, int _index) {
	  if (knots[0].length < _index+1) {
		  AULib.reportError("AUBaseCurve", "getIndexTan", "knots array does not have a value at index "+Float.toString(_index), "");
		  return 0;
	  }
	  mapTtoCurve(_t);
	  makeLeftSideTan(mappedT);
	  float v = evalTan(knots[mappedKnotStart][_index], knots[mappedKnotStart+1][_index], knots[mappedKnotStart+2][_index], knots[mappedKnotStart+3][_index]);
	  return v;
  }
  
  public int getCurveType() {
	  return curveType;
  }
  
  /************************************************************************* 
   * Setting values 
   *************************************************************************/

  void setKnotIndex(int _k, int _v, float _value) {
	  if (_k < knots.length) {
		  if (_v < knots[0].length) {
			  knots[_k][_v] = _value;
			  rebuildArcLengths = true;
		  }
	  }
  }
  
  public void setX(int _knotNum, float _x ) { setKnotIndex(_knotNum, 0, _x); }
  public void setY(int _knotNum, float _y ) { setKnotIndex(_knotNum, 1, _y); }
  public void setZ(int _knotNum, float _z ) { setKnotIndex(_knotNum, 2, _z); }
  
  public void setXY(int _knotNum, float _x, float _y) {
	  setKnotIndex(_knotNum, 0, _x); 
	  setKnotIndex(_knotNum, 1, _y); 
  }
  
  public void setXYZ(int _knotNum, float _x, float _y, float _z) {
	  setKnotIndex(_knotNum, 0, _x); 
	  setKnotIndex(_knotNum, 1, _y); 
	  setKnotIndex(_knotNum, 2, _z); 
  }
  
  public void setKnotIndexValue(int _knotNum, int _index, float _value) {
	  setKnotIndex(_knotNum, _index, _value);
  }
  
  public void setKnotValues(int _knotNum, float[] _vals) {
	  for (int i=0; i<_vals.length; i++) {
		  setKnotIndex(_knotNum, i, _vals[i]);
	  }
  }
 
  public void setDensity(float _density) {
  	density = Math.max(0.001f, _density);
  }
  
  public void setClamping(boolean _clamp) {
  	clampT = _clamp;
  }
  
  /************************************************************************* 
   * Matrix stuff 
   *************************************************************************/
  
  // To find [t^3 t^2 t 1] M [v0 v1 v2 v3]T, this finds v=([t^3 t^2 t 1] M) and saves it in global leftSide.
  // We split this off because we need to do this only once to evaluate multiple data values at a given t.
  void makeLeftSide(float t) {
	  if (t == lastLeftSideT) return;
	  
	  // curve evaulation left side
	  float t2 = t*t;
	  float t3 = t*t2;
	  leftSide[0] = (matrix[0][0] * t3) + (matrix[1][0] * t2) + (matrix[2][0] * t) + matrix[3][0];
	  leftSide[1] = (matrix[0][1] * t3) + (matrix[1][1] * t2) + (matrix[2][1] * t) + matrix[3][1];
	  leftSide[2] = (matrix[0][2] * t3) + (matrix[1][2] * t2) + (matrix[2][2] * t) + matrix[3][2];
	  leftSide[3] = (matrix[0][3] * t3) + (matrix[1][3] * t2) + (matrix[2][3] * t) + matrix[3][3];
	  
	  lastLeftSideT =t;
  }
  
  void makeLeftSideTan(float t) {
	  if (t == lastLeftSideTanT) return;
	  // tangent evaluation left side
	  float tan2 = 2*t;
	  float tan3 = 3*t*t;
	  leftTan[0] = (matrix[0][0] * tan3) + (matrix[1][0] * tan2) + matrix[2][0];
	  leftTan[1] = (matrix[0][1] * tan3) + (matrix[1][1] * tan2) + matrix[2][1];
	  leftTan[2] = (matrix[0][2] * tan3) + (matrix[1][2] * tan2) + matrix[2][2];
	  leftTan[3] = (matrix[0][3] * tan3) + (matrix[1][3] * tan2) + matrix[2][3];
	  
	  lastLeftSideTanT = t;
  }
  
  // assumes that makeLeftSide has been made, finds [leftSide] . [v0 v1 v2 v3]T
  float evalCurve(float v0, float v1, float v2, float v3) {
	  return (leftSide[0] * v0) + (leftSide[1] * v1) + (leftSide[2] * v2) + (leftSide[3] * v3);
  }

  float evalTan(float v0, float v1, float v2, float v3) {
	  return (leftTan[0] * v0) + (leftTan[1] * v1) + (leftTan[2] * v2) + (leftTan[3] * v3);
  }
  
  // do both steps at once - this is just for convenience
  float makeLeftAndEval(float t, float v0, float v1, float v2, float v3) {
	  makeLeftSide(t);
	  return evalCurve(v0, v1, v2, v3);
  }
  
  /************************************************************************* 
   * Search arclengths 
   *************************************************************************/

  /*
  *  Binary subdivision through ArcLengths to find the global value of t.
  *   This returns nothing, but side-effects three globals:
  *    mappedT           the t in the found segment
  *    mappedSegment     the found segment
  */
  void mapTtoCurve(float t) {
	// first, handle the edge cases	
	if (clampT) {
		if (t <= 0) {
			mappedT = 0;          // start of segment
			mappedKnotStart = 0;    // return first segment
			return;
		}
		if (t >= 1) {
			mappedT = 1;          // end of segment
			mappedKnotStart = knots.length-4;    // return last segment 
			return;
		}
	}
	if (t>1.) t = (float)(t%1.);
	if (t<0.) t = (float)(Math.ceil(Math.abs(t)) - Math.abs(t));
	if (rebuildArcLengths) {
		buildArcLengths();
		rebuildArcLengths = false;
	}
	// find the segment for this value of t
	mappedKnotStart = 0;
	int mappedSegment = 0;
	float nextEnd = ArcLengths[0][ArcLengths[0].length-1];
	while ((t > nextEnd) && (mappedSegment < ArcLengths.length-1)) {
		mappedSegment++;
		nextEnd = ArcLengths[mappedSegment][ArcLengths[mappedSegment].length-1];
		mappedKnotStart += skipLength;
	}
	
	float[] segmentArcs = ArcLengths[mappedSegment];
	int leftIndex = 0;
	int rightIndex = segmentArcs.length-1;
	int midIndex = (int)Math.round((leftIndex+rightIndex)/2.);
	float midVal = segmentArcs[midIndex];
	int numSteps = 0;
	int maxSteps = 100;  // overkill, but stops runaway subdivision
	while ((numSteps++ < maxSteps) && ((rightIndex-leftIndex) > 1)) {
		midIndex = (int)Math.round((leftIndex+rightIndex)/2.);
		midVal = segmentArcs[midIndex];
		if (t < midVal) rightIndex = midIndex;
		else leftIndex = midIndex;
	}
	float leftVal = segmentArcs[leftIndex];
	float rightVal = segmentArcs[rightIndex];
	float f = (t-leftVal)/(rightVal-leftVal);
	mappedT = AUMisc.jconstrain((leftIndex + f)/(segmentArcs.length-1), 0, 1);
	
	makeLeftSide(mappedT);
 }
  
  /************************************************************************* 
   * Build arclengths 
   *************************************************************************/
  
  void buildArcLengths() {
	// Set these values for CRCURVE, overwrite for BEZCURVE if needed.
	// Recompute each time, in case user changed number of knots or segments  
    skipLength = 1;               // how much to bump knot counter per segment
	numCurves = knots.length-3;   // number of curves represented by knot array
	firstLengthKnot = 1;          // where to start gathering lengths
	numSegments = 1;               // how many pairs to use when gathering lengths
	if (curveType == BEZCURVE) {
		skipLength = 3;
		numCurves = (knots.length-1)/3;
		firstLengthKnot = 0;
		numSegments = 3;
	}
	
	ArcLengths = new float[numCurves][];
	int startingKnot = 0;
	float totalArcLength = 0;
	for (int curveNum=0; curveNum<numCurves; curveNum++) {
		float curveLengthEstimate = getCurveLengthEstimate(startingKnot+firstLengthKnot, numSegments);
		curveLengthEstimate *= density;
		int numSteps = Math.round(curveLengthEstimate);
		if (numSteps < 3) numSteps = 3;
			
		float[] arcs = getArcLengthsForCurve(startingKnot, numSteps, totalArcLength);
		ArcLengths[curveNum] = arcs;
		startingKnot += skipLength;
		totalArcLength = arcs[arcs.length-1];
	};
	
	// normalize the results
	for (int i=0; i<ArcLengths.length; i++) {
		float[] thisSeg = ArcLengths[i];
		for (int j=0; j<thisSeg.length; j++) {
			float val = thisSeg[j];
			thisSeg[j] = val/totalArcLength;
		}
	}
  }
  
  float getCurveLengthEstimate(int firstKnot, int numSegments) {
	  float totalLen = 0;
	  for (int seg=0; seg<numSegments; seg++) {
		  float seglen = 0;
		  for (int g=0; g<numGeomVals; g++) {
			  float d = knots[firstKnot+seg][g] - knots[firstKnot+seg+1][g];
			  seglen += (d*d);
		  }
		  seglen = (float)Math.sqrt(seglen);
		  totalLen += seglen;
	  }
	  return totalLen;
  }
  
  float[] getArcLengthsForCurve(int firstKnot, int numSteps, float totalArcLength) {
	  float[] arcs = new float[numSteps+1];
	  float[] olds = new float[numGeomVals];
	  for (int i=0; i<=numSteps; i++) { // note <= to include last point
		  float t = (i*1.f)/(numSteps);
		  makeLeftSide(t);              // make left side of matrix eq for this t
		  float seglen = 0;
		  for (int g=0; g<numGeomVals; g++) {		  
			  float s = evalCurve(knots[firstKnot][g], knots[firstKnot+1][g], knots[firstKnot+2][g], knots[firstKnot+3][g]);
			  if (i>0) {
				  float ds = s - olds[g];
				  seglen += ds*ds;
			  }
			  olds[g] = s;
		  }
		  seglen = (float)Math.sqrt(seglen);
		  totalArcLength += seglen;
		  arcs[i] = totalArcLength;
	  }
	  return arcs;
  }
}