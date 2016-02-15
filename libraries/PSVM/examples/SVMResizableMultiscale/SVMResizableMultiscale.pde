/*
HandGestureRecognizerInteractive by Greg Borenstein, October 2012
 Distributed as part of PSVM: http://makematics.com/code/psvm
 
 Depends on HoG Processing: http://hogprocessing.altervista.org/
 
 Uses a trained Support Vector Machine to detect hand gestures in live video.
 SVM is trained based on Histogram of Oriented Gradients on the 
 Sebastien Marcel Hand Pose Dataset: http://www.idiap.ch/resource/gestures/
 
 ## Resizable and Multiscale
 
 This variation of SVM recognition uses a RecognitionRegion class to implement two important features:
 
 * The recognition region is moveable and resizable by the user.
 * The recognition region consists of mulitple concentric recognition areas at slightly different scales,
 making recognition more robust to small movements of the object.
 
 */

import hog.*;
import psvm.*;
import processing.video.*;
import java.awt.*;


// Capture object for accessing video feed
Capture video;


SVM model;
PImage testImage;

RecognitionRegion recognitionRegion;
int numObjectsToRecognize = 6;
int recognitionX = 100; // starting position
int recognitionY = 100;
int recognitionSize = 150;
int numAreas = 17;
int spacingBetweenAreas = 5;
float recognitionThreshold = 0.60;


void setup() {
  size(640/2 + 60, 480/2); 
  // capture video at half size for speed
  video = new Capture(this, 640/2, 480/2);
  video.start();   
  // declare our SVM object
  model = new SVM(this);
  // load the trained svm model from the file
  // our data has 324 dimensions because
  // that's what we get from doing Histogram of Oriented
  // Gradients on a 50x50 pixel image
  model.loadModel("hand_gesture_model.txt", 324);
  recognitionRegion = new RecognitionRegion(this, model, numObjectsToRecognize, recognitionX, recognitionY, numAreas, spacingBetweenAreas, recognitionSize);
  recognitionRegion.setThreshold(recognitionThreshold);
}

// video event, necessary for getting live camera
void captureEvent(Capture c) {
  c.read();
}

void draw() {
  background(0);


  // run Histogram of Oriented Gradients on the testImage
  // and pass the results to our model for testing
  //double testResult = model.test(gradientsForImage(testImage)); 
  int[] testResults = recognitionRegion.test(video);

 pushMatrix();
  pushStyle();
  translate(video.width, 10);
  fill(0);
  rect(0, 0, 50, 50);
  for(int i = 0; i < recognitionRegion.getNumAreas(); i++){
    pushMatrix();
    recognitionRegion.drawHog(i);
    popMatrix();
  }
  popStyle();
  popMatrix();

  pushMatrix();
  scale(-1,1);
    translate(-video.width,0);

  // display the video, the test image, and the red box
  image(video,0, 0);

  pushStyle();
  noFill();
  stroke(255, 0, 0);
  strokeWeight(5);
  recognitionRegion.draw();
  popStyle();
  popMatrix();

  for (int i = 0; i < recognitionRegion.getNumAreas(); i++) {
    image(recognitionRegion.getTestImage(i), 0, 50*i);
  }

  // use the result of our SVM test
  // to decide what text to put on the screen
  // based on what gesture is showing
  String result = "Gesture is: ";
  if (recognitionRegion.objectMatched()) {
    switch(recognitionRegion.getBestMatch()) {
    case 1:
      fill(255, 125, 125);
      result = result + "A";
      break;
    case 2:
      fill(125, 255, 125);
      result = result + "B";
      break;
    case 3:
      fill(125, 125, 255);
      result = result + "C";
      break;
    case 4:
      fill(125, 255, 255);
      result = result + "V";
      break;
    case 5:
      fill(255, 255, 125);
      result = result + "Five";
      break;
    case 6:
      fill(255,125,255);
      result = result + "Point";
      break;
    }
  } else {
    fill(255);
    result = result + "UNKNOWN";
  }
  
  result += "\n" + nf((float)recognitionRegion.getTopEstimate(), 2, 2) + "\n          (" +  recognitionRegion.getThreshold() + " is cutoff for recognition)";

  
  text(result, 100, 20);
}

// Helper function that calculates the 
// Histogram of Oriented Gradients for
// a PImage, filled with a lot of HoG magic
float[] gradientsForImage(PImage img) {
  // settings for Histogram of Oriented Gradients
  // (probably don't change these)
  int window_width=64;
  int window_height=128;
  int bins = 9;
  int cell_size = 8;
  int block_size = 2;
  boolean signed = false;
  int overlap = 0;
  int stride=16;
  int number_of_resizes=5;

  // a bunch of unecessarily verbose HOG code
  HOG_Factory hog = HOG.createInstance();
  GradientsComputation gc=hog.createGradientsComputation();
  Voter voter=MagnitudeItselfVoter.createMagnitudeItselfVoter();
  HistogramsComputation hc=hog.createHistogramsComputation( bins, cell_size, cell_size, signed, voter);
  Norm norm=L2_Norm.createL2_Norm(0.1);
  BlocksComputation bc=hog.createBlocksComputation(block_size, block_size, overlap, norm);
  PixelGradientVector[][] pixelGradients = gc.computeGradients(img, this);
  Histogram[][] histograms = hc.computeHistograms(pixelGradients);
  Block[][] blocks = bc.computeBlocks(histograms);
  Block[][] normalizedBlocks = bc.normalizeBlocks(blocks);
  DescriptorComputation dc=hog.createDescriptorComputation();    

  return dc.computeDescriptor(normalizedBlocks);
}

void mouseMoved() {
  if (keyPressed) {
    if (key == 'r') {
      recognitionRegion.updateSize((width/2 - mouseX) - (width/2 - pmouseX));
    } 
    if (key == ' ') {
      recognitionRegion.setPosition(width/2 - mouseX, mouseY);
    }
  }
}

