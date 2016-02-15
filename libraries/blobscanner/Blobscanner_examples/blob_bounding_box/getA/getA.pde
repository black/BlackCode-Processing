/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * Computes the top left bounding box 
 * corner for each blob in the image.
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
 
//Draws the blob's bounding box for all the blobs in the image.
//Check drawSelectBox example if you need draw for a specific blob.
bd.drawBox(color(0, 255, 0), 1);

noFill();
stroke(255, 0, 0);

//Create a reference pointing to the
//top left blob's bounding box corners list.
PVector[] cornerA = bd.getA();

//For each blob in the image..
for(int i = 0; i < bd.getBlobsNumber(); i++)
  
//...draws a circle at its top left bounding box corner.
ellipse( cornerA[i].x, cornerA[i].y, 10, 10);

//The bounding box corners are identified as follows:
 
//          A---------B
//          |         |
//          |         |
//          |         |
//          C---------D
//
// The rispective methods to compute them are :
// corners A getA()
// corners B getB()
// corners C getC()
// corners D getD()
//
// Each method returns a PVector array holding
// the coordinates list
// for each corner.
 

 
