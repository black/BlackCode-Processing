/*
 *
 * Blobscanner library by Antonio Molianro.
 * Date 06/01/2011
 *
 * The library assign to each blob a label or if you
 * prefer an ID to identify it. This ID is necessary 
 * for many computer vision task, 
 * included the blob detection itself. Blobscanner
 * allows to retrive the labe of a blob in two ways,
 * and those are by passing the x y screen coordinates 
 * of any of the blob's pixel or 
 * by passing the blob number. 
 *
 */

import Blobscanner.*;

Detector bs;
PImage img ;
 

void setup(){
  size(320, 240);
  bs = new Detector(this, 0, 0, 320, 240, 255);
  img = loadImage("data/blobs.jpg");
 
}

void draw(){
  image(img, 0, 0);
  img.filter(THRESHOLD);
  bs.imageFindBlobs(img);
 
  for(int i = 0; i < bs.getBlobsNumber(); i++)
  println("blob #" + (i+1) + " is labelled " + bs.getLabel(i));
  noLoop();
}
  
