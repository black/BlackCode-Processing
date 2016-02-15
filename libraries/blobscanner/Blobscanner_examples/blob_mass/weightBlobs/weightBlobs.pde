/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * Compute the mass of each blob in the image.
 * 
 */
import Blobscanner.*;

Detector bd;

PImage img = loadImage("blobs.jpg");

size(img.width, img.height);

img.filter(THRESHOLD);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );

bd.imageFindBlobs(img);

//The parameter is used to print or not a message
//when no blobs are found.
bd.weightBlobs(false);
//for each blob in the image..
for(int i = 0; i < bd.getBlobsNumber(); i++)
//computes and prints the mass.
println("   The mass of blob #" + (i+1) + " is " + bd.getBlobWeight(i) + " pixels.");
 
image(img, 0, 0);
