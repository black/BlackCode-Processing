/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 026/01/2011.
 * The screen pixel is set after  
 * cheking if it is a blobs pixel or not.
 * (image based)
 */
import Blobscanner.*;
 

Detector bd;
PImage img;
 
 

void setup(){
  size(200, 200);
  img = loadImage("blobs.jpg");
  bd = new Detector( this, 0, 0, img.width, img.height, 255 );
 
 
}

void draw(){
 
 
  img.filter(BLUR); 
  img.filter(THRESHOLD);
   
  bd.imageFindBlobs(img);
  bd.loadBlobsFeatures();
  
 
  for(int x = 0; x < width; x++){
    for(int y = 0; y < height; y++){  
      float c = map(y,0,height, 0, 255); 
      if(bd.isBlob(x, y)){
        set(x, y, color(c));
      } 
      else {
         
        set(x, y,color(255-c));
      }
    }
  }
} 
