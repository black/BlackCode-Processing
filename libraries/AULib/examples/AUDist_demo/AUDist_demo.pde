/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AULibrary.dist() and distN() 
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

int WhichDist;  // use keyboard to choose the distance type


void setup() {
  size(500, 500);
  PFont myFont = loadFont("Serif-48.vlw");
  textFont(myFont);
  textAlign(CENTER);
  println("Point A is in red. Point B, controlled by your mouse, is purple");
  println("Every point on the screen shows its distance from A using B and the selected");
  println("distance function. Black=distance of 0, Gray=distance of 0-1, White=distance >= 1");
  println("Press numbers 1-7 to choose different distance functions.");
}

void draw() {
  background(255);
  
  float ax = width/2.;
  float ay = height/2.;
  float bx = mouseX;
  float by = mouseY;
  int numPts = 5; // for demo, use 5 for the NGON and STAR versions
  loadPixels();
  for (int y=0; y<height; y++) {
    for (int x=0; x<width; x++) {
      float d = 0;
      // get the distance using A (center of screen), B (mouse), P (this point)
      if (WhichDist < 5) d = AULib.dist(WhichDist, ax, ay, bx, by, x, y);
                    else d = AULib.distN(WhichDist, numPts, ax, ay, bx, by, x, y); 
      float m = 255*d;
      pixels[(y*width)+x] = color(m);
    }
  }
  updatePixels();
  
  // draw a red circle for point A and a purple one for point B
  stroke(0);
  fill(255, 0, 0); 
  ellipse(ax, ay, 30, 30);
  fill(128, 0, 255); 
  ellipse(bx, by, 30, 30);
    
  String distName = "?";
  if (WhichDist == AULib.DIST_LINEAR) distName = "AU_DIST_LINEAR";
  if (WhichDist == AULib.DIST_RADIAL) distName = "AU_DIST_RADIAL";
  if (WhichDist == AULib.DIST_BOX) distName = "AU_DIST_BOX";
  if (WhichDist == AULib.DIST_PLUS) distName = "AU_DIST_PLUS";
  if (WhichDist == AULib.DIST_ANGLE) distName = "AU_DIST_ANGLE";
  if (WhichDist == AULib.DIST_NGON) distName = "AU_DIST_NGON";
  if (WhichDist == AULib.DIST_STAR) distName = "AU_DIST_STAR";
  fill(255,0,0);
  text(distName, width/2., height-(2.*textAscent()));
}

void keyPressed() {
  if ((key >= '1') && (key <= '7')) WhichDist = key-'1';
}
