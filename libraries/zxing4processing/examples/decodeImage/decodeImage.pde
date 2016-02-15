/**
 * decodeImage
 * A simple example of the use of the
 * zxing4processing.decodeImage() method
 *
 * (c) 2009 Rolf van Gelder :: http://www.cage.nl/ :: http://www.cagewebdev.com/
 * 
 * Opens a photo and uses the zxing4processing.decodeImage() method
 * to find and decode QRCode images in that photo.
 *
 */

/*
 * IMPORT THE zxing4processing LIBRARY + DECLARE A ZXING4P OBJECT
 * http://www.artisopensource.net/productslife/library.php
 */
import com.aos.zxing4processing.*;
ZXING4P zxing;

PFont font;
PImage photo;

String decodedText = "";

void setup()
{ 
  // CREATE A NEW EN-/DECODER INSTANCE
  zxing = new ZXING4P(this);
  
  font = loadFont("ArialMT-14.vlw");
  textFont(font,14);
  textAlign(CENTER);
  
  // LOAD THE PICTURE TO EXAMINE
  photo = loadImage("photo.jpg");
  size(photo.width,photo.height);

  println("Press a key to detect and decode the QRCode");
  
}

void draw()
{ 
  background(255);

  if(decodedText.equals(""))
  { 
    // DISPLAY PHOTO AND WAIT FOR KEY PRESS
    image(photo,0,0);
    fill(255);
    text("Press a key to detect and decode the QRCode",width/2,height-20);
    
  } else
  { 
    // IMAGE HAS BEEN DECODED
    fill(0);
    text("QRCode DETECTED and DECODED. It reads:",width/2,height/2-20);
    fill(#0000FF);
    text(decodedText,width/2,height/2);
    
  }
}

void keyPressed()
{  // TRY TO DETECT AND DECODE A QRCode IN PHOTO
   // decodeImage(boolean tryHarder, PImage img)
   // tryHarder: false => fast detection (less accurate)
   //            true  => best detection (little slower)
   decodedText = zxing.decodeImage(true,photo);
}
