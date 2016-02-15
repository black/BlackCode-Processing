/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * For each blob in the image computes 
 * and prints the center of mass  
 * coordinates x y to the console
 * and to the screeen. 
 *   
 * 
 */
import Blobscanner.*;

Detector bd;

PImage img;
PFont f = createFont("", 10);

void setup(){
size(320, 240);
img = loadImage("blobs.jpg");
img.filter(THRESHOLD);
textFont(f, 10);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );
 
}

void draw(){
image(img, 0, 0);

bd.imageFindBlobs(img);

//This call is indispensable for nearly all the other methods
//(please check javadoc).
bd.loadBlobsFeatures();

//This methods needs to be called before to call 
//findCentroids(booleaan, boolean) methods.
bd.weightBlobs(false);

//Computes the blob center of mass. If the first argument is true, 
//prints the center of mass coordinates x y to the console. If the 
//second argument is true, draws a point at the center of mass coordinates.
bd.findCentroids(false, false);
 

stroke(255, 100, 0); 

//For each blob in the image..
for(int i = 0; i < bd.getBlobsNumber(); i++) {
  
stroke(0, 255, 0);
strokeWeight(5);
  
//...computes and prints the centroid coordinates x y to the console...
println("BLOB " + (i+1) + " CENTROID X COORDINATE IS " + bd.getCentroidX(i));
println("BLOB " + (i+1) + " CENTROID Y COORDINATE IS " + bd.getCentroidY(i));
println("\n");

//...and draws a point to their location. 
point(bd.getCentroidX(i), bd.getCentroidY(i));

//Write coordinate to the screen.
fill(255, 0, 0);
text("x-> " + bd.getCentroidX(i) + "\n" + "y-> " + bd.getCentroidY(i), bd.getCentroidX(i), bd.getCentroidY(i)-7);
}
 
}
  
