/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AUGlob for making a 2d glob object
* Version 1 - Andrew - April 25, 2015
*/

import AULib.*;

AUGlob TheGlob;

PVector C0, C1, D, Dp;       // the four points of the glob
float r0, r1, a, b, ap, bp;  // and the six floats of the glob

void setup() {
  size(500, 500);
  
  C0 = new PVector(110, 130);
  r0 = 70;
  C1 = new PVector(400, 340);
  r1 = 50;
  D = new PVector(300, 220);
  a = .5;
  b = .5;
  Dp = new PVector(300, 140);
  ap = .5;
  bp = .5;
   
  TheGlob = new AUGlob(this, C0, r0, C1, r1, D, a, b, Dp, ap, bp);
}

void draw() {
  background(0);
  fill(255);
  noStroke();
  
  // for fun, move the four points around over time
  TheGlob.C0.set(spinPoint(C0, 20, 25, .02));
  TheGlob.C1.set(spinPoint(C1, 35, 15, .03));
  TheGlob.D.set(spinPoint(D, 35, 55, .07));
  TheGlob.Dp.set(spinPoint(Dp, 55, 95, .05));
  
  // rebuild the geometry and draw the glob
  TheGlob.buildGeometry();
  TheGlob.render(true, true);
  
  // if you uncomment the next line, you can view the frame that defines the glob
  //TheGlob.renderFrame(10);
}

// move a point around an ellipse with the given center and X and Y axes
PVector spinPoint(PVector center, float rX, float rY, float speed) {
  float theta = frameCount * speed;
  PVector p = new PVector(center.x + (rX * cos(theta)), center.y + (rY * (sin(theta))));
  return p;
}
