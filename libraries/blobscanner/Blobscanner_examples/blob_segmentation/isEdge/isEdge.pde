/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 03/02/2011.
 * The boolean methods isEdge(int x,int y) returns
 * true if at the location with coordinates x y
 * there is a blob edge pixel.
 * In this example this methods is used in
 * conjunction with getLabel(int x, int y) and
 * getBlobWeightLabel(int labelnumber) methods to
 * extract the edge of blob/s having a minimum 
 * mass. The edges coordinate are collected and stored    
 * in an array from there taken and drown to
 * a PGraphics buffer( note that this methods was used
 * prevalently to show, as requested on the forum, how to 
 * select and draw a blob's edge to PGraphics buffer). 
 * If you test the sketch in a dark room, use a LED or
 * a torch (or something similar).   
 */

import Blobscanner.*;
import processing.video.*;
PGraphics edges;
Detector bs;
Capture frame;
int minimumWeight = 900;
final int MAXEDGES = (320*240)/4; 
int[] X ;
int[] Y ;


void setup(){
  size(320, 240);
  frame = new Capture(this, 320, 240);
  bs = new Detector(this, 0, 0, 320, 240, 255);
  edges = createGraphics(320, 240, P3D);

}
void draw(){
  
  
  frame.read();
  int k = 0;
  bs.imageFindBlobs(frame);
  bs.loadBlobsFeatures();
  bs.weightBlobs(false);
  
  getEdgeCoordinates();
  
  edges.beginDraw();
  edges.background(0);
  
  for(int i = 0; boolean( X[i]); i++){
 
        edges.stroke(0, 255, 0);
        
        edges.point(X[i], Y[i]);//or do what you want
  } 
  edges.endDraw();
  image(edges, 0, 0);
}
 
 
 
void  getEdgeCoordinates(){ 
   
     X = new int[MAXEDGES];
     Y = new int[MAXEDGES];
      int i = 0;

        for (int y = 0; y < frame.height; y++) {
            for (int x = 0; x < frame.width; x++) {

                 if (bs.isEdge(x, y)  && 
                     bs.getBlobWeightLabel(bs.getLabel(x, y)) >=  minimumWeight) {

                     
                      X[i] = x;
                      Y[i] = y;
  
                     i++;
 
                }
             }
          }
    }

