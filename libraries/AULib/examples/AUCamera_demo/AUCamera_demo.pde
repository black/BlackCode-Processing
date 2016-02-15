/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of the AUCamera object.
* Version 1 - Andrew - Sept 6, 2014
*/

/* The AUCamera takes each image we produce as an exposure. It combines
* as controlled by the selected shutter, and saves the frame when NumExposures 
* images have been received. When NumFrames frames are written, the program
* exits.
*/

import AULib.*;

AUCamera Camera;       // the camera object
int NumFrames = 8;     // the number of frames in this animation
int NumExposures = 50; // each frame is built of this many exposures

void setup() {
  size(500, 500);
  Camera = new AUCamera(this, NumFrames, NumExposures, true);
  Camera.setShutterType(AUCamera.SHUTTER_UNIFORM);
  Camera.setRampTime(0.4);
}

void draw() {
  background(0); 
  fill(255, 0, 0); 
  float time = Camera.getTime();
  translate(width/2., height/2.);  // move to center
  rotate(time * TWO_PI);           // rotate
  ellipse(190, 0, 100, 100);       // draw the ellipse at (190, 0)
  Camera.expose();                 // save this exposure
}
