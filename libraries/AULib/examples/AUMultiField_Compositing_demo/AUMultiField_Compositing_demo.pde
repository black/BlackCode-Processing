/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULib/AULib.php
* Show use of AUMultiField compositing with over()
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

PGraphics Output;
AUMultiField Vstripes, Hstripes, Circles;

void setup() {
  size(500, 500);
  Output = createGraphics(width, height);
  
  makeFields();
  makeImage();
  background(255, 255, 0);
  image(Output, 0, 0);
}

/* To make each field, I first draw the picture into a PGraphics object,
and then the mask into another PGraphics object. I do that so I can use
Processing's drawing commands to make my shapes. Then I load the image
into an opaque AUMultiField, and I load the mask into an AUField. I then
create the output AUMultiField and initialize it to transparent black
everywhere. Then I compose the image, through the mask, into that new
AUMultiField. So that gives me colored pixels that are opaque within the
mask, transparent outside the mask, and smoothly anti-aliased on the mask's
edges. I repeat this three times, once for each image/mask pair.
*/
void makeFields() {
  PGraphics image = createGraphics(width, height);
  PGraphics mask = createGraphics(width, height);
  AUMultiField imageField = new AUMultiField(this, 4, width, height);
  AUField maskField = new AUField(this, width, height);
  
  // first field: vertical stripes in a triangular mask
  
  // make vertical stripes: magenta on cyan
  image.beginDraw();
  image.background(0, 255, 255);
  image.fill(255, 0, 255);
  image.noStroke();
  for (int x=0; x<width+100; x+=40) {
    image.rect(x, 0, 20, height);
  }
  image.endDraw();
  imageField.RGBfromPixels(image);
  
  // make their mask: two circles with a triangle cut out
  mask.beginDraw();
  mask.background(0);
  mask.fill(255);
  mask.noStroke();
  mask.ellipse(125, 170, 200, 200);
  mask.ellipse(125, 330, 200, 200);
  mask.fill(0);
  mask.triangle(125, 100,  50, 360, 200, 360);
  mask.endDraw();
  maskField.fromPixels(AUField.FIELD_RED, mask);
  
  Vstripes = new AUMultiField(this, 4, width, height);
  Vstripes.flattenRGBA(0, 0, 0, 0);
  imageField.over(Vstripes, maskField);
  
  // second field: vertical stripes in a triangular mask
    
  // make horizontal stripes: white on black
  image.beginDraw();
  image.background(0);
  image.fill(255);
  image.noStroke();
  for (int y=0; y<height+100; y+=40) {
    image.rect(0, y, width, 20);
  }
  image.endDraw();
  imageField.RGBfromPixels(image);
  
    // make right mask: triangle with two circles cut out
  mask.beginDraw();
  mask.background(0);
  mask.fill(255);
  mask.noStroke();
  mask.triangle(270, 50, 480, 50, 375, 450);
  mask.fill(0);
  mask.ellipse(375, 130, 100, 100);
  mask.ellipse(375, 200, 100, 100);
  mask.endDraw();
  maskField.fromPixels(AUField.FIELD_RED, mask);
  
  Hstripes = new AUMultiField(this, 4, width, height);
  Hstripes.flattenRGBA(0, 0, 0, 0);
  imageField.over(Hstripes, maskField);
  
  // third field: polka dots in a circular mask
  
  // make the circles
  image.background(90, 250, 110); // bright green
  image.fill(225, 130, 65); // bright orange
  image.noStroke();
  for (int y=0; y<height+100; y+= 40) {
    for (int x=0; x<width+100; x+= 40) {
      image.ellipse(x, y, 30, 30);
    }
  }
  image.endDraw();
  imageField.RGBfromPixels(image);

  mask.beginDraw();
  mask.background(0);
  mask.fill(255);
  mask.noStroke();
  mask.ellipse(250, 250, 230, 230);
  mask.endDraw();
  maskField.fromPixels(AUField.FIELD_RED, mask);
  
  Circles = new AUMultiField(this, 4, width, height);
  Circles.flattenRGBA(0, 0, 0, 0);
  imageField.over(Circles, maskField);
}

/* To create the final image, which I save as a PGraphics, I first
make a new AUMultiField with 4 fields (enough to hold RGBA). I 
initialize that field to transparent black everywhere. Then
I merely compose (that is, use over()) to draw each of my other
colored images (each with their own alpha) into that field. I turn
the result into a PGraphics object and we're done.
*/
void makeImage() {
  AUMultiField accum = new AUMultiField(this, 4, width, height);
  
  accum.flattenRGBA(0, 0, 0, 0);
  
  Vstripes.over(accum);
  Hstripes.over(accum);
  Circles.over(accum);
  
  accum.RGBAtoPixels(0, 0, Output);
}    

