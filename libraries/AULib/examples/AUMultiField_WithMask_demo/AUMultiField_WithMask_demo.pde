/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULib/AULib.php
* Show use of AUMultiField  with masking 
* Note that toPixel() ignores transformation commands. To draw our field
* with transforms, first put it into a PGraphics object, then use image()
* to draw the PGraphics object.
* Version 1 - Andrew - Oct 9, 2014
*/

import AULib.*;

AUMultiField Picture;   // the colorful picture we'll draw
AUField Mask;           // the mask external to the picture
PGraphics OutputPG;     // the PGraphics object we might draw into
int TransferType = 0;   // how to draw the frame 

void setup() {
  size(500, 500);
  makePicture();
  makeMask();
  OutputPG = createGraphics(width, height);
  println("press 0 through 7 on the keyboard to see different styles");
  printStatus();
}

// vertical blue with a couple of circles. We'll set everything to transparent
// before drawing, so the picture becomes its own mask.
void makePicture() {
  PGraphics pg = createGraphics(width, height);
  pg.beginDraw();
  pg.background(0, 0, 0, 0);  // completely transparent 
  pg.fill(75, 95, 150);       // draw dark blue stripes
  pg.fill(255);
  pg.noStroke();
  for (int x=0; x<width; x+=30) {
    pg.rect(x, 0, 15, height);
  }
  pg.fill(155, 65, 105);     // dark magenta circle
  pg.ellipse(200, 200, 100, 100);
  pg.fill(245, 115, 205);    // light magenta circle inside it
  pg.ellipse(200, 200, 70, 70);
  pg.endDraw();
  Picture = new AUMultiField(this, 4, width, height);
  Picture.RGBAfromPixels(pg);
  image(pg, 0, 0);
}

// the mask is three overlapping circles
void makeMask() {
  PGraphics pg = createGraphics(width, height);
  pg.beginDraw();
  pg.background(0);                // mask starts all black
  pg.fill(255);                    // draw white circles
  pg.noStroke();                   // don't use black strokes
  pg.ellipse(250, 250, 300, 300);  // three overlapping circles
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
  
  background(255, 235, 100);  // yellow background with beige dots
  fill(250, 200, 135);
  for (int y=0; y<=height; y+=100) {
    for (int x=0; x<=width; x+=100) {
      ellipse(x, y, 75, 75);
    }
  }
  pushMatrix();
    translate(width/2., height/2.);
    rotate(frameCount * .01);
    translate(-width/2., -height/2.);
    switch (TransferType) {
      case 0: 
        Picture.RGBtoPixels(0, 0);
        break;
      case 1:
        Picture.RGBAtoPixels(0, 0);
        break;
      case 2:
        Picture.RGBtoPixels(0, 0, OutputPG);
        image(OutputPG, 0, 0);
        break;
      case 3:
        Picture.RGBAtoPixels(0, 0, OutputPG);
        image(OutputPG, 0, 0);
        break;
      case 4:
        Picture.RGBtoPixels(0, 0, Mask, 0, 0);
        break;
      case 5:
        Picture.RGBtoPixels(0, 0, Mask, mx, my);
        break;
      case 6:
        Picture.RGBtoPixels(0, 0, Mask, 0, 0, OutputPG);
        image(OutputPG, 0, 0);
        break;
      case 7:
        Picture.RGBtoPixels(0, 0, Mask, mx, my, OutputPG);
        image(OutputPG, 0, 0);
        break;
    }
  popMatrix();
}

void keyPressed() {
  TransferType = key - '0';
  TransferType = constrain(TransferType, 0, 8);
  printStatus();
}

void printStatus() {
  switch (TransferType) {   
    case 0: println("TransferType = 0: copy RGB to screen"); break;
    case 1: println("TransferType = 1: copy RGBA to screen"); break;
    case 2: println("TransferType = 2: copy RGB to a PGraphics and draw that"); break;
    case 3: println("TransferType = 3: copy RGBA to a PGraphics and draw that"); break;
    case 4: println("TransferType = 4: copy RGB to screen using Mask"); break;
    case 5: println("TransferType = 5: copy RGB to screen using Mask with offset"); break;
    case 6: println("TransferType = 6: copy RGB to a PGraphics using Mask, and draw that"); break;
    case 7: println("TransferType = 7: copy RGB to a PGraphics using Mask with offset, and draw that"); break;
  }
}
