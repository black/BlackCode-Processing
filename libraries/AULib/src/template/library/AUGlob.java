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
 * @example AUGlob_demo 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

/*************************************************
* GLOBS
*************************************************/	

public class AUGlob implements PConstants {
 
  // the inputs
  PApplet theSketch;

  // The input geometry of a Glob
  public PVector C0, C1;      // circle centers
  public float r0, r1;        // circle radii
  public PVector D, Dp;       // target points D and Dprime
  public float a, b, ap, bp;  // control scalars a, b, aprime, bprime
  public float dpar, dperp, dppar, dpperp; // alternative controls for specifying D and Dprime
  
  // the points we derive from the input geometry
  public PVector U, V;
  public PVector E0, E0p, E1, E1p, F0, F0p, F1, F1p;

  // these are for picking which tangent point we want
  final int SIDE_LEFT = -1;
  final int SIDE_RIGHT = 1;
  
  // remember whether we have D points or we need to rebuild them
  boolean buildDFromFloats = false;

  public AUGlob(PApplet _theSketch, PVector _C0, float _r0, PVector _C1, float _r1, PVector _D, float _a, float _b, PVector _Dp, float _ap, float _bp) {
    theSketch = _theSketch;
    C0 = _C0.get();
    C1 = _C1.get();
    r0 = _r0;
    r1 = _r1;
    D = _D.get();
    a = _a;
    b = _b;
    Dp = _Dp.get();
    ap = _ap;
    bp = _bp;
    buildDFromFloats = false;
    buildGeometry();
  }
  
  public AUGlob(PApplet _theSketch, PVector _C0, float _r0, PVector _C1, float _r1, float _dpar, float _dperp, float _a, float _b, float _dppar, float _dpperp, float _ap, float _bp) {
    theSketch = _theSketch;
    C0 = _C0.get();
    C1 = _C1.get();
    r0 = _r0;
    r1 = _r1;
    dpar = _dpar;
    dperp = _dperp;
    a = _a;
    b = _b;
    dppar = _dppar;
    dpperp = _dpperp;
    ap = _ap;
    bp = _bp;
    buildDFromFloats = true;
        
    buildGeometry();
  }
  
  void buildDandDp() {
    PVector tmpU, sclU, sclV;
    float factorU, factorV;
    
    tmpU = new PVector(C1.x-C0.x, C1.y-C0.y);
    float uLen = tmpU.mag();
    float gapLen = uLen - (r0+r1);
    
    factorU = r0 + (dpar * gapLen);
    factorV = -1 * (dperp * gapLen);
    sclU = PVector.mult(U,  factorU);
    sclV = PVector.mult(V, factorV);
    D = C0.get();
    D.add(sclU);
    D.add(sclV);
    
    factorU = -1 * (r1 + (dppar * gapLen));
    factorV = (dpperp * gapLen);
    sclU = PVector.mult(U,  factorU);
    sclV = PVector.mult(V, factorV);
    Dp = C1.get();
    Dp.add(sclU);
    Dp.add(sclV);
  }
	              
  public void render(boolean drawNeck, boolean drawCaps) {
    if (drawCaps) {
      theSketch.ellipse(C0.x, C0.y, 2*r0, 2*r0);
      theSketch.ellipse(C1.x, C1.y, 2*r1, 2*r1);
    }
    if (drawNeck) { // this shape is what the whole program is all about!
      theSketch.beginShape();
    	theSketch.vertex(E0.x, E0.y);
    	theSketch.bezierVertex(F0.x, F0.y, F1.x, F1.y, E1.x, E1.y);
    	theSketch.vertex(E1p.x, E1p.y);
    	theSketch.bezierVertex(F1p.x, F1p.y, F0p.x, F0p.y, E0p.x, E0p.y);
	  theSketch.endShape(CLOSE);
    }
  }
	  
  public void renderFrame(float dotRadius) {
	  theSketch.pushStyle();
      //color unprimed = color(255, 128, 0);
      //color primed = color(35, 215, 185);
      // the circles
      theSketch.fill(255,0,0); 
      theSketch.stroke(0);
      theSketch.line(C0.x, C0.y, C1.x, C1.y);
      theSketch.ellipse(C0.x, C0.y, 2*dotRadius, 2*dotRadius);
      theSketch.ellipse(C1.x, C1.y, 2*dotRadius, 2*dotRadius);
      
      // the unprimed frame
      theSketch.fill(255, 128, 0);
      theSketch.stroke(255, 128, 0);
      theSketch.ellipse(E0.x, E0.y, 2*dotRadius, 2*dotRadius);
      theSketch.ellipse(F0.x, F0.y, 2*dotRadius, 2*dotRadius);
      theSketch.line(E0.x, E0.y, F0.x, F0.y);
      theSketch.ellipse(E1.x, E1.y, 2*dotRadius, 2*dotRadius);
      theSketch.ellipse(F1.x, F1.y, 2*dotRadius, 2*dotRadius);
      theSketch.line(E1.x, E1.y, F1.x, F1.y);
      // show tangent extensions
      theSketch.line(E1.x, E1.y, E1.x+(E1.x-F1.x), E1.y+(E1.y-F1.y));
      theSketch.line(E0.x, E0.y, E0.x+(E0.x-F0.x), E0.y+(E0.y-F0.y));
      // the target
      theSketch.ellipse(D.x, D.y, 2*dotRadius, 2*dotRadius);
      theSketch.fill((float)(255*.5), (float)(128*.5), (float)(0*.5));
      theSketch.ellipse(D.x, D.y, dotRadius, dotRadius);

      // the primed frame
      theSketch.fill(35, 215, 185);
      theSketch.stroke(35, 215, 185);
      theSketch.ellipse(E0p.x, E0p.y, 2*dotRadius, 2*dotRadius);
      theSketch.ellipse(F0p.x, F0p.y, 2*dotRadius, 2*dotRadius);
      theSketch.line(E0p.x, E0p.y, F0p.x, F0p.y);
      theSketch.ellipse(E1p.x, E1p.y, 2*dotRadius, 2*dotRadius);
      theSketch.ellipse(F1p.x, F1p.y, 2*dotRadius, 2*dotRadius);
      theSketch.line(E1p.x, E1p.y, F1p.x, F1p.y);
      // show tangent extensions
      theSketch.line(E1p.x, E1p.y, E1p.x+(E1p.x-F1p.x), E1p.y+(E1p.y-F1p.y));
      theSketch.line(E0p.x, E0p.y, E0p.x+(E0p.x-F0p.x), E0p.y+(E0p.y-F0p.y));
      // the target
      theSketch.ellipse(Dp.x, Dp.y, 2*dotRadius, 2*dotRadius);      
      theSketch.fill((float)(35*.5), (float)(215*.5), (float)(185*.5));
      theSketch.ellipse(Dp.x, Dp.y, dotRadius, dotRadius);
 
      theSketch.popStyle();
  }

  /*
  This is the heart of the algorithm. Find vectors U and V to establish a coordinate system.
  Then find points E on the circles that form the tangent line with D, choosing the proper side.
  Using a and b (and a' (ap) and b' (bp)) find points F on the lines from E to D.
  The Bezier curve is formed with two E points as ends and two F points as controls.
  */
  public void buildGeometry() {
    U = new PVector(C1.x-C0.x, C1.y-C0.y);
    U.normalize();
    V = new PVector(U.y, -U.x);
    
    if (buildDFromFloats) buildDandDp();

    
    E0 = getTangentPoint(C0, D, r0, SIDE_RIGHT);
    E1 = getTangentPoint(C1, D, r1, SIDE_LEFT);
    E0p = getTangentPoint(C0, Dp, r0, SIDE_LEFT);
    E1p = getTangentPoint(C1, Dp, r1, SIDE_RIGHT);
  
    F0 = new PVector(E0.x + a*(D.x-E0.x), E0.y + a*(D.y-E0.y));
    F1 = new PVector(E1.x + b*(D.x-E1.x), E1.y + b*(D.y-E1.y));
    F0p = new PVector(E0p.x + (ap*(Dp.x-E0p.x)), E0p.y + (ap * (Dp.y-E0p.y)));
    F1p = new PVector(E1p.x + (bp*(Dp.x-E1p.x)), E1p.y + (bp * (Dp.y-E1p.y)));
  }
  
  PVector getTangentPoint(PVector A, PVector B, float r, int side) {
    PVector U = new PVector(B.x-A.x, B.y-A.y);
    U.normalize();
    PVector V = new PVector(U.y, -U.x);
    float ab = A.dist(B);
    float pb = (float)Math.sqrt((ab*ab)-(r*r));
    float beta = (float)Math.atan2(pb, r);
    float uscl = r * (float)Math.cos(beta);
    float vscl = r * (float)Math.sin(beta);
    PVector P0 = new PVector(A.x + (uscl*U.x) + (vscl*V.x), A.y + (uscl*U.y) + (vscl*V.y));  
    PVector P1 = new PVector(A.x + (uscl*U.x) - (vscl*V.x), A.y + (uscl*U.y) - (vscl*V.y));
    PVector dP0 = new PVector(P0.x-A.x, P0.y-A.y);
    PVector dP1 = new PVector(P1.x-A.x, P1.y-A.y);
    float p0sgn = U.cross(dP0).z;
    float p1sgn = U.cross(dP1).z;
    if (((p0sgn>0) && (p1sgn>0)) || ((p0sgn<0) && (p1sgn<0))) {  // should never happen
      return P0;
    }
    if (side == SIDE_RIGHT) { // the positive side is on the right
      if (p0sgn > 0) return P0;
      return P1;
    } 
    if (p0sgn < 0) return P0;
    return P1;
  }
}