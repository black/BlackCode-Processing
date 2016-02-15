/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AUCurve 
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

int NumKnots = 8;
AUCurve MyCurve;
boolean Redraw = true;

void setup() {
  size(500, 500);
  colorMode(HSB);  // all color stuff is in HSB
  newCurve();  
  println("space  make a new curve");
  println("-      make a new curve with fewer segments");
  println("+      make a new curve with more segments");
  println("s      save the image to the sketch's directory");
  println("q      quit");
}

// draw the curve as a large number of equally-spaced dots
void draw() {
  if (!Redraw) return;  // don't bother redrawing the same thing
  background(255);      // white background
  noStroke();           // dots don't have an outline
  int numSteps = 500;   // how many dots to draw
  for (int i=0; i<numSteps; i++) {
    float t = norm(i, 0, numSteps-1);         // t runs from [0,1]
    float x = MyCurve.getX(t);                // get X at this t
    float y = MyCurve.getY(t);                // get Y at this t
    float diam = MyCurve.getIndexValue(t, 2); // get the diameter
    float hue = MyCurve.getIndexValue(t, 3);  // get the hue
    float sat = MyCurve.getIndexValue(t, 4);  // get the saturation
    float brt = MyCurve.getIndexValue(t, 5);  // get the brightness
   
    fill(hue, sat, brt);        // fill with this color
    ellipse(x, y, diam, diam);  // draw this dot
  }
  Redraw = false;    // don't draw again until something changes
}

void newCurve() {
  // each knot row: 0:x 1:y 2:weight 3:hue 4:saturation 5:brightness
  float[][] knots = new float[NumKnots][6];   
  for (int i=0; i<knots.length; i++) {
    knots[i][0] = width * random(.2, .8);  // a value for X   
    knots[i][1] = height * random(.2, .8); // a value for Y not
    knots[i][2] = random(5, 50);           // line weight
    knots[i][3] = random(0, 255);          // hue 
    knots[i][4] = random(150, 255);        // saturation
    knots[i][5] = random(150, 255);        // brightness
  }
  /* Make the new curve. Give it the knots, tell it we have 2 
   * geometry values at the start of each row (the X and Y values),
   * and close it. Remember the geometry values must always come 
   * at the start of each row in the knots.
   */
  MyCurve = new AUCurve(knots, 2, true);   
  Redraw = true;    // we have a new curve, so draw it
}

void keyPressed() {
  switch (key) {
    case ' ':
      newCurve();  // make a new curve
      break;
    case '-':
      NumKnots = max(4, NumKnots-1);  // use fewer knots
      newCurve();
      break;
    case '+':
      NumKnots++;   // use more knots
      newCurve();
      break;
    case 's':
      saveFrame();  // save the results to this directory
      break;
    case 'q':
      exit();       // quit!
      break;
  }
}
