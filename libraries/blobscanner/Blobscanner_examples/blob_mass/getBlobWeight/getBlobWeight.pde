/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 02/01/2011.
 * Creates a syntetic blob image
 * and finds the blob mass.
 * If you click on the image 
 * the blob disappears and weightBlob(boolean)
 * output a message to the console.
 */
import Blobscanner.*;

Detector bd;

 
boolean white = true;
void setup(){

size(320, 240);

 

bd = new Detector( this, 0, 0,  width,  height, 255 );
}

void draw(){
background(0);
if(white)
fill(255);
ellipse(width/2, height/2, 120, 70);
fill(0);
rectMode(CENTER);
rect( mouseX ,constrain( mouseY/2,height/2, height/2),1, height);
loadPixels();
bd.findBlobs(pixels, width, height);
 

//The parameter is used to print or not a message
//when no blobs are found.
bd.weightBlobs(true);
//if we have blob/s  
if(bd.getGlobalWeight() > 0){
//for each blob in the image..  
for(int i = 0; i < bd.getBlobsNumber(); i++)
//...computes the mass and prints it to the console.
println("   The mass of blob #" + (i+1) + " is " + bd.getBlobWeight(i) + " pixels.");
 }
}
void mousePressed(){
  //hides the blob
  white=!white;
}

 
