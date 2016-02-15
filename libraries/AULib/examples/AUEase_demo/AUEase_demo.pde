/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AULib.ease() 
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

void setup() {
  size(700, 1000);
  PFont myFont = loadFont("Serif-24.vlw");
  textFont(myFont);
  textAlign(LEFT);
}

void draw() {

  background(255);
  float spacing = 30;       // space between examples
  float radius = 20;        // radius of ball
  float left = 8*radius;    // left bounding
  float right = width-left; // right boundary
  float ytop = 2*spacing;   // y of top example
  float dy = 2.4*spacing;   // space between examples
  float x = 0;              // curve text (x,y)
  float y = 0;              
  String easeName = "";     // curve name
  int cycleTime = 180;      // number of frames used to cross
  float textLeft = 20;      // left edge of text
  
  line(left, ytop, left, ytop+(12*dy));    // left vertical line
  line(right, ytop, right, ytop+(12*dy));  // right vertical line
  
  // a runs [0,1] during left->right, then [1,2] during right->left
  float a = (frameCount % (2*cycleTime))/(1.*cycleTime);
  float t = a;
  if (t > 1) t -= 1;
  
  for (int i=0; i<13; i++) {
    if (i== 0) { x = AULib.ease(AULib.EASE_LINEAR, t); easeName = "EASE_LINEAR"; }
    if (i== 1) { x = AULib.ease(AULib.EASE_IN_CUBIC, t); easeName = "EASE_IN_CUBIC"; }
    if (i== 2) { x = AULib.ease(AULib.EASE_OUT_CUBIC, t); easeName = "EASE_OUT_CUBIC"; }
    if (i== 3) { x = AULib.ease(AULib.EASE_IN_OUT_CUBIC, t); easeName = "EASE_IN_OUT_CUBIC"; }
    if (i== 4) { x = AULib.ease(AULib.EASE_IN_BACK, t); easeName = "EASE_IN_BACK"; }
    if (i== 5) { x = AULib.ease(AULib.EASE_OUT_BACK, t); easeName = "EASE_OUT_BACK"; }
    if (i== 6) { x = AULib.ease(AULib.EASE_IN_OUT_BACK, t); easeName = "EASE_IN_OUT_BACK"; }
    if (i== 7) { x = AULib.ease(AULib.EASE_IN_ELASTIC, t); easeName = "EASE_IN_ELASTIC"; }
    if (i== 8) { x = AULib.ease(AULib.EASE_OUT_ELASTIC, t); easeName = "EASE_OUT_ELASTIC"; }
    if (i== 9) { x = AULib.ease(AULib.EASE_IN_OUT_ELASTIC, t); easeName = "EASE_IN_OUT_ELASTIC"; }
    if (i==10) { x = AULib.ease(AULib.EASE_CUBIC_ELASTIC, t); easeName = "EASE_CUBIC_ELASTIC"; }
    if (i==11) { x = AULib.ease(AULib.EASE_ANTICIPATE_CUBIC, t); easeName = "EASE_ANTICIPATE_CUBIC"; }
    if (i==12) { x = AULib.ease(AULib.EASE_ANTICIPATE_ELASTIC, t); easeName = "EASE_ANTICIPATE_ELASTIC"; }
    
    // draw the boxes and the text
    if (a > 1) x = 1-x;
    y = ytop + (dy * i);
    line(left, y, right, y);
    fill(255, 0, 0);
    ellipse(lerp(left, right, x), y, 2*radius, 2*radius);
    fill(0);
    text(easeName, textLeft, y);
  }
}

