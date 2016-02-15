/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 01/01/2011.
 * Compute the number of blobs in the image.
 *
 */
import Blobscanner.*;

//This is the instance of the class Detector,
//unique class of Blobscanner.
Detector bd;

PImage img = loadImage("blobs.jpg");

size(img.width, img.height);

img.filter(THRESHOLD);

//This class has only one constructor. 
bd = new Detector( this, 0, 0, img.width, img.height, 255 );

//This method take as argument PImage and Capture objects.
bd.imageFindBlobs(img);

println(bd.getBlobsNumber() + " BLOBS FOUND.");
 
image(img, 0, 0);
