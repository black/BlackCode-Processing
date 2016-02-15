/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * For each blob in the image computes 
 * and prints the center of mass 
 * coordinates x y to the console
 * Also draws a point at their location. 
 *   
 * 
 */
import Blobscanner.*;

Detector bd;

PImage img;
PFont f = createFont("", 10);

 
size(200, 200);
img = loadImage("blobs.jpg");
img.filter(THRESHOLD);
textFont(f, 10);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );
 
image(img, 0, 0);

bd.imageFindBlobs(img);

//This call is indispensable for nearly all the other methods
//(please check javadoc).
bd.loadBlobsFeatures();

//This methods needs to be called before to call 
//findCentroids(booleaan, boolean) methods.
bd.weightBlobs(false);

strokeWeight(5);
stroke(255, 0, 0);

//Computes the blob center of mass. If the first argument is true, 
//prints the center of mass coordinates x y to the console. If the 
//second argument is true, draws a point at the center of mass x y coordinates.
bd.findCentroids(true, true);
 

 
 
  
