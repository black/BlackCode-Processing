/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 01/01/2011.
 * Computes the blobs in the video frame
 * and draws the blob's contours and bounding box
 * choosing color based upon a weight threshold.
 *
 */
import Blobscanner.*;
import processing.video.*;

Detector bd;
Capture frame;
PImage img;

void setup(){
  size(520, 360);
  frame = new Capture(this, width, height);
  bd = new Detector( this, 0, 0, frame.width, frame.height, 255 );
  img = createImage(width, height, RGB);
}

void draw(){
  if(frame.available()){
    frame.read();
    //save a copy of the frame to send to the output video stream
    img.copy(frame, 0, 0, 520, 360, 0, 0, 520, 360);
  }
 
  frame.filter(BLUR);
  //frame.filter(THRESHOLD);
  
  bd.imageFindBlobs(frame);
  bd.loadBlobsFeatures();
  bd.weightBlobs(true);
   
  image(img, 0, 0);
  
  int minimumWeight = 1000;
  int thickness = 3;
  color contoursColor = color(255, 0, 0);
  color boundingBoxColor = color(0, 255, 0);
  color selectBoxColor = color(255, 255, 0);
  color selectContoursColor = color(0, 255, 255);
  
  bd.drawContours(contoursColor, thickness);
  bd.drawSelectContours(minimumWeight, selectContoursColor, thickness);
  bd.drawBox(boundingBoxColor, thickness);
  bd.drawSelectBox(minimumWeight, selectBoxColor, thickness);
  if(bd.getBlobsNumber()>0)println(bd.getBlobsNumber());
 
}
 
