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
 * @example AUCurve_demo 
 * @example AUCurve_Space_demo
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class AUCurve extends AUBaseCurve {
	
	static float[][] Matrix_Cardinal;  // c=.5 is Catmull-Rom
	public static final float[][] Matrix_CatmullRom = {{-.5f, 1.5f, -1.5f, .5f}, {1f, -2.5f, 2f, -.5f}, {-.5f, 0f, .5f, 0f}, {0f, 1f, 0f, 0f}};
	
	public AUCurve(float _x0, float _y0, float _x1, float _y1, 
				float _x2, float _y2, float _x3, float _y3) {
		super(null, Matrix_CatmullRom, 2, CRCURVE); 
		float[][] knots = new float[4][2];
		knots[0][0] = _x0; knots[0][1] = _y0; 
		knots[1][0] = _x1; knots[1][1] = _y1; 
		knots[2][0] = _x2; knots[2][1] = _y2; 
		knots[3][0] = _x3; knots[3][1] = _y3; 
		super.setKnots(knots);
	}
	
	public AUCurve(float _x0, float _y0, float _z0, float _x1, float _y1, float _z1, 
				  float _x2, float _y2, float _z2, float _x3, float _y3, float _z3) {
		super(null, Matrix_CatmullRom, 2, CRCURVE);  
		float[][] knots = new float[4][3];
		knots[0][0] = _x0; knots[0][1] = _y0; knots[0][2] = _z0;
		knots[1][0] = _x1; knots[1][1] = _y1; knots[1][2] = _z0;
		knots[2][0] = _x2; knots[2][1] = _y2; knots[2][2] = _z0;
		knots[3][0] = _x3; knots[3][1] = _y3; knots[3][2] = _z0;
		super.setKnots(knots);
	}
	
	public AUCurve(float[][] _knots, int _numGeomVals, boolean _makeClosed) {
		super(null, Matrix_CatmullRom, _numGeomVals, CRCURVE);  
		if ((_knots == null) || (_knots.length < 4)) {
			AULib.reportError("AUCurve", "AUCurve", "knots is null or less than 4-by-2, using array of four { 1, 1 }", "");
			_knots = new float[4][2];
			for (int i=0; i<4; i++) {
				_knots[i][0] = _knots[i][1] = 1.0f;
			}
		}	
		if ((_numGeomVals < 1) || (_numGeomVals > _knots[0].length)) {
			AULib.reportError("AUCurve", "AUCurve", "_numGeomVals is too small or big, using 1", "knots[0].length="+Float.toString(knots[0].length));
			_numGeomVals = 1;
		}
		_numGeomVals = Math.min(_numGeomVals,  _knots[0].length);
		super.setNumGeomVals(_numGeomVals);
		if (_makeClosed) {
			int klen = _knots.length;
			float[][] closedKnots = new float[klen+3][_knots[0].length];
			for (int i=0; i<klen+3; i++) {
				for (int j=0; j<_knots[0].length; j++) {
					closedKnots[i][j] = _knots[i%klen][j];
				}
			}
			super.setKnots(closedKnots);
		} else {
			super.setKnots(_knots);
		}
	}
  
	//Cardinal spine: c=1 is overshooty, c=.5 is Catmull-Rom, c=0 is polygon
	void makeMatrixCardinal(float c) {
		Matrix_Cardinal = new float[4][4];
		Matrix_Cardinal[0][0] = -c;  Matrix_Cardinal[0][1] = 2-c; Matrix_Cardinal[0][2] = c-2;     Matrix_Cardinal[0][3] = c;
		Matrix_Cardinal[1][0] = 2*c; Matrix_Cardinal[1][1] = c-3; Matrix_Cardinal[1][2] = 3-(2*c); Matrix_Cardinal[1][3] = -c;
		Matrix_Cardinal[2][0] = -c;  Matrix_Cardinal[2][1] = 0;   Matrix_Cardinal[2][2] = c;       Matrix_Cardinal[2][3] = 0;
		Matrix_Cardinal[3][0] = 0;   Matrix_Cardinal[3][1] = 1;   Matrix_Cardinal[3][2] = 0;       Matrix_Cardinal[3][3] = 0;
	}
}