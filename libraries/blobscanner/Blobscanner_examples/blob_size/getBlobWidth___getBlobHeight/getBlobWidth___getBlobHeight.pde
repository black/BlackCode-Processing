/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * Computes width and height 
 * for each blob in the image.
 * 
 */
import Blobscanner.*;

Detector bd;

PImage img = loadImage("blobs.jpg");

size(img.width, img.height);

img.filter(THRESHOLD);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );

image(img, 0, 0);

bd.imageFindBlobs(img);

//This call is indispensable for nearly all the other methods
//(please check javadoc).
bd.loadBlobsFeatures();
 

//For each blob in the image..
for(int i = 0; i < bd.getBlobsNumber(); i++) {
  
//...compute and print the width and the height to the console.
println("BLOB " + (i+1) + " WIDTH IS " + bd.getBlobWidth(i)); 
println("BLOB " + (i+1) + " HEIGHT IS " + bd.getBlobHeight(i));
println("\t");
}
  
