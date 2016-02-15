/**
 * Returns the blob number at the x y coordinates. 
 * If at these coordinate there isn't a blob
 * returns -1; 
 * <code>findBlobs()</code> or <code>imageFindBlobs(PImage)</code>
 * must be called first to call this method.
 * @param x The X coordinate to check for blob presence.
 * @param y The Y coordinate to check for blob presence.
 * @return  Returns the blob number at the x y coordinates. 
 * If at these coordinate there isn't a blob
 * returns -1; 
 */
import Blobscanner.*;
PImage img ;
Detector bs;

void setup(){
  size(200, 200);
  img = loadImage("blobs.jpg");
  bs = new Detector(this,0,0,200,200,255);
  img.filter(THRESHOLD);
  frameRate(6);
}

void draw(){
  image(img, 0, 0);
  bs.imageFindBlobs(img);
  bs.loadBlobsFeatures();
  bs.weightBlobs(false);
  
  if(frameCount==4)frameCount=0;
  if(bs.isMatch(mouseX, mouseY, frameCount)){
  PVector[] pix = bs.getBlobPixelsLocation(frameCount);
  
     for(int j = 0; j < pix.length; j++){
        point(pix[j].x, pix[j].y);
     }
     println("Mouse over blob number " + bs.getBlobNumberAt(mouseX, mouseY));
  } 
  
}  
