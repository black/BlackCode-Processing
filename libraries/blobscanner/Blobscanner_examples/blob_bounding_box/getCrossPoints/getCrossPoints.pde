/*
 * Blobscanner library by Antonio Molinaro. 
 * Date 10/01/2011 
 *
 * If the mouse is onto a blob,
 * draws the middle point of the blob's
 * bounding box four sides. 
 */

import Blobscanner.*;

PImage img;
Detector bs;

void setup() {
  size(320, 240);
  bs = new Detector(this, 0, 0, width, height, 255);
  img = loadImage("data/blobs.jpg");
 
}  

void draw(){
   
  image(img, 0, 0);
  img.filter(THRESHOLD);
  
  bs.imageFindBlobs(img);
  bs.loadBlobsFeatures();
  bs.drawBox(color(0, 255, 0), 1);
  
  strokeWeight(7);
  stroke(255, 0, 0);
 
  //For each blob in the image..
  for(int i = 0; i < bs.getBlobsNumber(); i++){
    //...if the label at mouse position is equal to the current blob's label...
    if(bs.getLabel(i)==bs.getLabel(mouseX, mouseY)){
      //....draws blob's cross points.
      point(bs.getCrossPoints(i,0, false).x, bs.getCrossPoints(i,0, false).y);//top 
            point(bs.getCrossPoints(i,1, false).x, bs.getCrossPoints(i,1, false).y);//down
                 point(bs.getCrossPoints(i,2, false).x, bs.getCrossPoints(i,2, false).y);//left
                     point(bs.getCrossPoints(i,3, false).x, bs.getCrossPoints(i,3, false).y);//right
    }
  }
 
}
