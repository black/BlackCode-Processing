/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of chooseOneWeighted() 
* Version 1 - Andrew - Sept 6, 2014
*/

/*
For each value from 0-9, give it a random weight.
Then draw a large number of samples. Plot the given
weight in beige, and the percentage of returned values
in blue. They ought to be pretty close.

Press any key to run it again with new weights.
*/

import AULib.*;

boolean Redraw = true;

void setup() {
  size(500, 500);
  PFont myFont = loadFont("Serif-24.vlw");
  textFont(myFont);
  textAlign(LEFT);
  Redraw = true;
  println("Press the space bar to generate a new set of data.");
}

void draw() {
  if (!Redraw) return;
  Redraw = false;
  // build the values, and set up weights
  float[] vals = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
  float [] weights = new float[vals.length];
  float maxWeight = -1;
  for (int i=0; i<weights.length; i++) {
    weights[i] = random(1, 5);
    maxWeight = max(weights[i], maxWeight);
  }
  
  // draw the values, and their weights in red
  float stripHeight = height/(2.+vals.length);
  for (int i=0; i<vals.length; i++) {
    float bottom = (i+2) * stripHeight;
    fill(0);
    text(i, 20, bottom-textAscent());
    fill(255);   // beige
    rect(50, bottom, 400, -stripHeight);
    fill(250, 195, 150); // beige
    rect(50, bottom, 400*weights[i]/maxWeight, -stripHeight);
  }
  
  // choose a whole bunch 
  float[] results = new float[vals.length];
  for (int i=0; i<results.length; i++) results[i] = 0;
  int numChooses = 10000;
  float maxResult = -1;
  for (int i=0; i<numChooses; i++) {
    int v = (int)AULib.chooseOneWeighted(vals, weights);
    results[v]++;
    maxResult = max(maxResult, results[v]);
  }
  
  // draw the results half-high in green
  for (int i=0; i<vals.length; i++) {
    float bottom = (i+2) * stripHeight;
    fill(125, 175, 255);  // sky blue
    rect(50, bottom, 400*results[i]/maxResult, -stripHeight/2.);
  }
}

void keyPressed() { // press any key to generate new results
  Redraw = true;
}
