/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 01/01/2011.
 * Finds the number of blobs in the image.
 * In this example the method findBlob(int[], int, int)
 * is used instead of imageFindBlobs(PImage).
 *
 */
import Blobscanner.*;

Detector bd;

PImage img = loadImage("blobs.jpg");

size(img.width, img.height);

img.filter(THRESHOLD);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );

img.loadPixels();

bd.findBlobs(img.pixels, img.width, img.height);

println(bd.getBlobsNumber() + " BLOBS FOUND.");
 
image(img, 0, 0);
