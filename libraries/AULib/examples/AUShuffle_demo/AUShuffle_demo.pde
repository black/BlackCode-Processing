/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of the AUShuffle object.
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

AUShuffle Shuffle;
color[] Clrs = { color(255,0,0), color(0,255,0), color(0,0,255) };

void setup() {
  size(500, 500);
  // create an array of values 0, 1, 2, ... Clrs.length-1
  float[] v = new float[Clrs.length];
  for (int i=0; i<v.length; i++) v[i] = i;
  // create the AUShuffle object to get them randomly
  Shuffle = new AUShuffle(v);
}

void draw() {
  background(255);
  for (int y=10; y<height; y+=20) {
    for (int x=0; x<width; x+=10) {
      int whichClr = round(Shuffle.next()); // get the next entry 
      fill(Clrs[whichClr]);                 // fill with that color
      rect(x, y, 10, 10);                   // and draw the box
    }
  }
  noLoop();
}
