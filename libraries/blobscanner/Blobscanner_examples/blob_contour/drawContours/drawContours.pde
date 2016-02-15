/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 01/01/2011.
 * Draws the blobs contours.
 *
 */
import Blobscanner.*;

Detector bd;

PImage img = loadImage("blobs.jpg");

size(img.width, img.height);

img.filter(THRESHOLD);

bd = new Detector( this, 0, 0, img.width, img.height, 255 );

image(img, 0, 0);

color contoursCol = color(255, 0, 0);
int contoursThickness = 2;

img.loadPixels();

bd.findBlobs(img.pixels, img.width, img.height);
// to call always before to use a method returning or processing a blob feature
bd.loadBlobsFeatures(); 
bd.drawContours(contoursCol, contoursThickness);
 

 
