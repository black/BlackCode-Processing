/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AUField with masking
* Note that toPixel() ignores transformation commands. To draw our field
* with transforms, first put it into a PGraphics object, then use image()
* to draw the PGraphics object.
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

AUField Picture, Mask;
PGraphics OutputPG;
int TransferType = 0;

void setup() {
  size(500, 500);
  makePicture();
  makeMask();
  OutputPG = createGraphics(width, height);
  println("press 0 through 5 on the keyboard to see different drawing styles");
  println("Note that I've deliberately let the edges of the mask show when we're");
  println("rotating, to demonstrate that the mask itself is moving with respect");
  println("to the picture in the field.");
  printStatus();
}

// vertical stripes with a couple of circles
void makePicture() {
  PGraphics pg = createGraphics(width, height);
  pg.beginDraw();
  pg.background(255);
  pg.fill(0);
  pg.noStroke();
  for (int x=0; x<width; x+=30) {
    pg.rect(x, 0, 15, height);
  }
  pg.ellipse(200, 200, 100, 100);
  pg.fill(255);
  pg.ellipse(200, 200, 70, 70);
  pg.endDraw();
  Picture = new AUField(this, width, height);
  Picture.fromPixels(AUField.FIELD_LUM, pg);
}

// the mask is three overlapping circles
void makeMask() {
  PGraphics pg = createGraphics(width, height);
  pg.beginDraw();
  pg.background(0);                // mask starts all black
  pg.fill(255);                    // draw white circles
  pg.noStroke();                   // important! don't use black strokes
  pg.ellipse(250, 250, 300, 300);
  pg.ellipse(250, 150, 200, 200);
  pg.ellipse(360, 360, 100, 100);
  pg.fill(0);                      // punch a hole in the mask
  pg.rect(200, 300, 130, 70);
  pg.endDraw(); 
  Mask = new AUField(this, width, height);
  Mask.fromPixels(AUField.FIELD_LUM, pg);
}

void draw() {
 // in case we use OutputPG, set it to transparent black
  OutputPG.beginDraw();
  OutputPG.background(0, 0, 0, 0);
  OutputPG.endDraw();
  
  // mask offsets in case we want them
  int mx = (int)map(sin(frameCount*.02), -1, 1, -10, 10);
  int my = (int)map(cos(frameCount*.02), -1, 1, -10, 10);
  
  background(255, 255, 0);
  pushMatrix();
    translate(width/2., height/2.);
    rotate(frameCount * .01);
    translate(-width/2., -height/2.);
    switch (TransferType) {
      case 0:
        Picture.toPixels(0, 0);
        break;
      case 1:
        Picture.toPixels(0, 0, Mask);
        break;
      case 2:
        Picture.toPixels(0, 0, Mask, mx, my);
        break;
      case 3:
        Picture.toPixels(0, 0, OutputPG);
        image(OutputPG, 0, 0);
        break;
      case 4:
        Picture.toPixels(0, 0, Mask, 0, 0, OutputPG);
        image(OutputPG, 0, 0);
        break;
      case 5:
        Picture.toPixels(0, 0, Mask, mx, my, OutputPG);
        image(OutputPG, 0, 0);
        break;
    }
  popMatrix();
}

// m: Picture overwrites OutputPG data, Mask data goes into OutputPG alpha.
// a: Picture is blended with existing OutputPG data using Mask. OutputPG alpha unchanged.
void keyPressed() {
  TransferType = key - '0';
  TransferType = constrain(TransferType, 0, 5);
  printStatus();
}

void printStatus() {
  switch (TransferType) {   
    case 0: println("TransferType = 0: copy to screen"); break;
    case 1: println("TransferType = 1: copy to screen through mask"); break;
    case 2: println("TransferType = 2: copy to screen through mask with a moving offset"); break;
    case 3: println("TransferType = 3: copy to a PGraphics"); break;
    case 4: println("TransferType = 4: copy to a PGraphics through mask"); break;
    case 5: println("TransferType = 5: copy to a PGraphics through mask with a moving offset"); break;
  }
}
