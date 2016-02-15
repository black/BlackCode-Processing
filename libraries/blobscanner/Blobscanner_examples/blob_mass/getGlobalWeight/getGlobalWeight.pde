/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * Compute the total blob mass in the image.
 * 
 */
import Blobscanner.*;

Detector bd;

PImage img = loadImage("blobs.jpg");

size(img.width, img.height);

img.filter(THRESHOLD);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );

bd.imageFindBlobs(img);

//getGlobalWeight() is the only method related to blobs weight that it's
//possible to call without first call the weightBlobs() method.
//See next example for WeightBlobs()method.
println("   The total blob mass is " + bd.getGlobalWeight() + " pixels.");
 

image(img, 0, 0);
