/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of the AUBezier object.
* Version 1 - Andrew - Sept 6, 2014
*/

/* We create a big, multi-segment Bezier curve, and also store
* thickness and color information at each knot. To draw the curve
* we take a large number of equally-spaced points along the curve's
* length, and draw each dot using the interpolated size and color
* information. Use the keyboard to control the program:
* 
*/

import AULib.*;

int NumCurves = 3;
AUBezier MyCurve;
boolean Redraw = true;

void setup() {
  size(500, 500);
  colorMode(HSB);  // all color stuff is in HSB
  newBezier();
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

void newBezier() {
  // each knot row: 0:x 1:y 2:weight 3:hue 4:saturation 5:brightness
  float[][] knots = new float[1+(3*NumCurves)][6];  
  fillKnotWithRandom(knots, 0);    // handle knot 0 as a special case
  for (int i=0; i<NumCurves; i++) {
    int k = 1 + (3*i);
    if (k == 1) {
      // knot 1 is special because there's no knot -1 before 0
      fillKnotWithRandom(knots, 1); 
    } else {
      // knot k is symmetrical with knot k-2 around k-2
      for (int j=0; j<knots[0].length; j++) {
        knots[k][j] = knots[k-1][0] + (knots[k-1][j] - knots[k-2][j]);
      }
    }
    fillKnotWithRandom(knots, k+1);
    fillKnotWithRandom(knots, k+2);
  }
  /* Make the new curve. Give it the knots, tell it we have 2 
   * geometry values at the start of each row (the X and Y values),
   * and close it. Remember the geometry values must always come 
   * at the start of each row in the knots.
   */
  MyCurve = new AUBezier(knots, 2, true);   
  Redraw = true;    // we have a new curve, so draw it
}

void fillKnotWithRandom(float[][] knots, int whichKnot) {
  knots[whichKnot][0] = width * random(.2, .8);  // a value for X   
  knots[whichKnot][1] = height * random(.2, .8); // a value for Y not
  knots[whichKnot][2] = random(5, 50);           // line weight
  knots[whichKnot][3] = random(0, 255);          // hue 
  knots[whichKnot][4] = random(150, 255);        // saturation
  knots[whichKnot][5] = random(150, 255);        // brightness
}

void keyPressed() {
  switch (key) {
    case ' ':
      newBezier();  // make a new curve
      break;
    case '-':
      NumCurves = max(2, NumCurves--);  // use fewer curves
      newBezier();
      break;
    case '+':
      NumCurves++;   // use more curves
      newBezier();
      break;
    case 's':
      saveFrame();  // save the results to this directory
      break;
    case 'q':
      exit();       // quit!
      break;
  }
}
