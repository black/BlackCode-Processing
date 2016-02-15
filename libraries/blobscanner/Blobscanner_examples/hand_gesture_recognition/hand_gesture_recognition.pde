/*
 * Hand detection and gesture recognition v 0.0.1.
 * (c) Antonio Molinaro 2011 http://code.google.com/p/blobscanner/.
 * Background subtraction (c) Andrew Senior 2006  http://www.andrewsenior.com.
 * This program detects and counts the hand's fingers
 * using a web cam as detecting device and Blobscanner as main
 * analysis tool. It can be easily used as very basic starting point to built
 * a touch less computer interface by using the finger's tip centroid as point of interest
 * to control a virtual device.
 * This software is part of Blobscanner's examples.
 * 
 *
 * USAGE:
 * start the sketch;
 * press any key to grab a background frame;
 * place the hand in front of the camera (best performance during test at 40cm of distance from it);
 * 
 * HINTS:
 * 1)If the background becomes too instable(e.g. too many blobs), grab a new one pressing a key.
 * 2)The only foreground object must be the hand/s.
 * 3)The fingers detection algorithm is scale/rotation invariant ,
 * but to obtain the best performance the hand axis should be perpendicular to the floor 
 * and parallel to the camera. My personal advice is to place the camera on the PC's screen
 * and to adjust it until only the ceiling of the room is in the frame. In this way you can 
 * place the hand in the most natural position. Watch this video to better understand how to
 * place the camera http://www.youtube.com/watch?v=At2x0BvxK8Q.
 *
 * This software's performances can be improved by different means. One of them is by eliminating 
 * all the calls to red,green and blue Processing's functions , and 
 * using instead the switch operator(e.g. int red = (pixelValue & 0xff0000) >> 16;). Also the many color
 * function calls can be substituted by using the switch operator to gaining speed.
 * Reducing the images displayed is another way of increasing this software performances. 
 * 
 * This program is free software and is released under General Public License v3 (GPL v3).
 */
 
 
import Blobscanner.*;
import processing.video.*;

PImageOperations  PImOps;
Capture frame;
PBGS  pBackground;
FingerDetector fd  ;
Detector bs, bstips;

final int TIPS_MASS = 45;
final int HAND_MASS = 600;

int iThreshold = 190;
boolean iSetNext = true;

PFont f;
PImage tips = createImage(320,240, RGB); 
PImage imgDiff = createImage(320,240, RGB); 
PImage  FG = createImage(320,240,RGB); 
PImage  imgDiffColor = createImage(320,240,RGB);


void setup(){
   
  size(700,540);
  f = createFont("", 20);
  textFont(f, 20);
 
   
 
  frame = new Capture(this, 320, 240 );
  PImOps = new PImageOperations();
  pBackground = new PBGS(FG);
  fd = new FingerDetector(320, 240 );
  bs = new Detector(this, 0, 0, 320, 240, 255);
  bstips = new Detector(this, 0, 0, 320, 240, 255);
}
 
void draw(){
  if(frame.available()) {
  frame.read();
  
  background(102);
  
  //   BACKGROUND SUBTRACTION  //
  
  frame.loadPixels();
  FG.loadPixels();
  
  arrayCopy(frame.pixels,FG.pixels);       //put this into a PImage
 
  FG.updatePixels();
 
  
  if (iSetNext)
  {
    pBackground.Set(FG);                    // Set the background image
    iSetNext=false;
  }
  pBackground.Update(FG);                   // Update the background model
  imgDiff.loadPixels();
  pBackground.PutDifference();              // Put the difference in imgDiff
  imgDiff.updatePixels();
  image(imgDiff,360,280);                   // Display BGS image (low right)
  
  
  //  FINGER DETECTION   //
 
  imgDiff.loadPixels();
  fd.setImage(imgDiff);                     // Set hand detection image with BGS image
  imgDiff.updatePixels();
  
  bs.imageFindBlobs(imgDiff);               // Compute blobs in BGS image
  bs.loadBlobsFeatures();
  bs.weightBlobs(false);
 
 
 
 
   drawFingersTips();                       
   drawFingersTipsBoundingBox();
    
   displayDiffColorImage();
   drawDiffColorImageBoundingBox();
 
  
  }
 
}
 

// force update of the background model when a key is clicked. 
void keyPressed(){
  iSetNext=true;
}

/*
  This function analyzes the BGS image, then
  based on its data and on the original camera's  
  image data, creates a BGS color image.
  If you need more speed you can eliminate this
  function, and display the original frame instead.
 */
void displayDiffColorImage(){
  int []fg_pix = FG.pixels;               //set a reference to the foreground image
 
  imgDiff.loadPixels();
  imgDiffColor.loadPixels();
  for(int y = 0; y < imgDiff.height; y++){
    for(int x = 0; x < imgDiff.width; x++){
      if(brightness(imgDiff.get(x, y))==0) imgDiffColor.pixels[x+y*imgDiff.width]= 0xff000000;
      else  imgDiffColor.pixels[x+y*imgDiff.width]=fg_pix[x+y*FG.width];
    }
  }
  imgDiffColor.updatePixels();
 
  image(imgDiffColor,20,20);             //Display BGS color image(top left)
}

/*
  This function analyzes the blobs in the
  tips image. If their mass if >= to TIPS_MASS
  a finger is added to the count.
 */
 
void drawText(){
   int fingersCount = 0;
   fill(255);
 
   for(int i = 0; i < bstips.getBlobsNumber(); i++){
     if(bstips.getBlobWeight(i) >= TIPS_MASS) 
     fingersCount++;
    }
     text(fingersCount + " finger tips detected" ,  30 ,height-50);                                  
 }
 
 
 /*
   Here many important things happen.  
   The tips image is first initialized to black.
   Then, the hand's blob is searched in the BGS image.
   Once it has been found, the blob pixels are scanned for
   possibly finger's tips regions. 
   After that a finger tips image is created based upon
   the search's result data. The image is then scanned for blobs.  
 */
 void drawFingersTips(){
   
  tips.loadPixels();
  
  for(int i = 0; i < 320*240; i++)tips.pixels[i] = 0xff000000 ;// Set to black the tips image pixels
  
  //For each pixels in the BGS image (imgDiff)....
  for(int y =  0; y < 240; y++){
    for(int x =  0; x < 320; x++){
     if(bs.isBlob(x, y) && bs.getBlobWeightLabel(bs.getLabel(x, y))>= HAND_MASS){      // if is hand  
 
     if(fd.goodPixel(x, y))                              //if it's a finger's tip pixel set to white
     tips.pixels[x+y*tips.width] = 0xff << 16 & 0xff0000 | 0xff << 8 & 0xff00 | 0xff & 0xff;
     
         } 
      }
   }
   
   tips.updatePixels();
   
   //now the tips image is created (top right)
   //let's compute the blobs in it   
   bstips.imageFindBlobs(tips);
   bstips.loadBlobsFeatures();
   bstips.weightBlobs(false); 
   drawText();
 
   image(tips,360,20);                         //Display tips image (top right) 
 }

 void drawFingersTipsBoundingBox(){
   pushMatrix();
   translate(360,20);
   bstips.drawBox(color(255,0,0),1);
   popMatrix();
 }
 
 void drawDiffColorImageBoundingBox(){
   pushMatrix();
   translate(20,20);
   bs.drawSelectBox(600,255,1);
   popMatrix(); 
   
 }
