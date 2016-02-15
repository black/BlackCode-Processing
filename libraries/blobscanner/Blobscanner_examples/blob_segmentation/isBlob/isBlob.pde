/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 01/01/2011.
 * The screen pixel is set after  
 * cheking if it is a blobs pixel or not.
 *
 */
import Blobscanner.*;
import processing.video.*;

Detector bd;
Capture frame;
 
 

void setup(){
  size(520, 360);
  frame = new Capture(this, width, height);
  bd = new Detector( this, 0, 0, frame.width, frame.height, 255 );
 
 
}

void draw(){
  if(frame.available()){
    frame.read();
 }
 
  frame.filter(GRAY);
  frame.filter(THRESHOLD);
  
  bd.imageFindBlobs(frame);
  bd.loadBlobsFeatures();
  
 
  for(int x = 0; x < width; x++){
    for(int y = 0; y < height; y++){  
      float c = map(y,0,height, 0, 254); 
      if(bd.isBlob(x, y)){
        set(x, y, color(c));
      } 
      else {
         
        set(x, y,color(255-c));
      }
    } 
  }
} 
