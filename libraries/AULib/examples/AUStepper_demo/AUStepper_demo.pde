/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULib/AULib.php
* Show use of AUStepper 
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

AUStepper Stepper;       // the AUStepper object
float SqSize = 100;      // side of a square
float Expansion = 100;   // how far it pushes outward

void setup() {
  size(500, 500);
  // Here are examples of the two types of constructors. 
  boolean useFrameCounts = true;
  if (useFrameCounts) {
    // create the stepper using phases with this many frames each
    int[] frameCounts = { 100, 175, 150 };
    Stepper = new AUStepper(frameCounts);
  } else {  
    // create the stepper with a total frame count and relative lengths
    float[] stepCounts = { 1, 1.75, 1.5 };   // relative durations
    int numFrames = 400;                     // total length of animation
    Stepper = new AUStepper(numFrames, stepCounts);
  }
}

void draw() {
  Stepper.step();                      // take a step
  int stepNum = Stepper.getStepNum();  // get the current step number
  float alfa = Stepper.getAlfa();      // get the current value of alfa
  background(255, 230, 150);
  fill(255, 100, 85);
  noStroke();
  switch (stepNum) {                   // execute the proper step
    case 0: step0(alfa); break;
    case 1: step1(alfa); break;
    case 2: step2(alfa); break;
    default: println("I don't know step "+stepNum);
  }
}

// step 0, squares start touching and move outwards
void step0(float alfa) {
  translate(width/2., height/2.);
  float d = (SqSize/2.) + (alfa*Expansion);
  for (int i=0; i<4; i++) {
    pushMatrix();
      rotate(i*HALF_PI);
      translate(d, -d);
      rotate(-HALF_PI);
      rect(-SqSize/2., -SqSize/2., SqSize, SqSize);
    popMatrix();
  }
}

// step 2, each square rotates 90 degrees
void step1(float alfa) {
  translate(width/2., height/2.);
  float d = (SqSize/2.) + Expansion;
  for (int i=0; i<4; i++) {
    pushMatrix();
      rotate(i*HALF_PI);
      translate(d, -d);
      rotate(HALF_PI * (alfa-1));
      rect(-SqSize/2., -SqSize/2., SqSize, SqSize);
    popMatrix();
  }
}

// step 3, squares move back together, spinning and scaling
void step2(float alfa) {
  translate(width/2., height/2.);
  float d = (SqSize/2.) + ((1-alfa)*Expansion);
  float scl = 1-(.6 * sin(PI*alfa));
  for (int i=0; i<4; i++) {
    pushMatrix();
      rotate(i*HALF_PI);
      translate(d, -d);
      rotate(HALF_PI * (alfa-1));
      scale(scl);
      rect(-SqSize/2., -SqSize/2., SqSize, SqSize);
    popMatrix();
  }
}
