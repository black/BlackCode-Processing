/**
 * generateQRCode - QRCode generator (v08/06/2009)
 *
 * A simple example of the use of the
 * zxing4processing.generateQRCode() method
 *
 * Enter a message that will be encoded in a QRCode and hit <enter>
 *
 * Press 's' to save the generated image (as a .gif file)
 * Press 'r' to start again
 *
 * (c) 2009 Rolf van Gelder :: http://www.cage.nl/ :: http://www.cagewebdev.com/
 * 
 */

/*
 * IMPORT THE zxing4processing LIBRARY + DECLARE A ZXING4P OBJECT
 * http://www.artisopensource.net/productslife/library.php
 */
import com.aos.zxing4processing.*;
ZXING4P zxing;

PImage QRCode;
PFont font;

boolean generated = false;
boolean firstTime = true;

String textToEncode = "";
boolean showCursor = true;
int lastTime = 0;

void setup()
{
  size(400,400);

  // ZXING4P ENCODE/DECODER INSTANCE
  zxing = new ZXING4P(this);

  font = loadFont("ArialMT-18.vlw");
  textFont(font,18);
}

void draw()
{
  background(102);

  if(generated)
  {
    // DISPLAY GENERATED IMAGE
    image(QRCode,0,0);
  } 
  else
  {
    // WAIT FOR USER INPUT
    fill(255);
    text("Type the text for your QRCode and press <enter> to generate the image:", 10, 15, width - 40, height);
    rect(0,65,width,height);

    // MAKE CURSOR BLINK
    int t = millis();
    if (t - lastTime > 500)
    {
      showCursor = !showCursor;
      lastTime = t;
    }

    // DISPLAY USER INPUT
    fill(0);
    if (showCursor)
      text(textToEncode + "_", 10, 80, width - 40, height);
    else
      text(textToEncode, 10, 80, width - 40, height);
  }
}

void keyPressed() 
{
  if(generated)
  { 
    // IMAGE HAS BEEN GENERATED
    if(key=='s' || key=='S')
    { 
      // SAVE GENERATED IMAGE
      saveFrame("qrcode.gif");
      println("QRCode image saved as data/qrcode.gif");
    } 
    else if(key=='r' || key=='R')
    { 
      // RESTART
      generated = false;
      textToEncode = "";
    }
  } 
  else
  { 
    // WAITING FOR USER INPUT
    if ((key == ENTER) || (key == RETURN)) 
    {
      // ENCODE THE TEXT INTO A QRCODE IMAGE
      // PImage p = ZXING4P.generateQRCode(String txt,int width,int height)
      // width and height is the size of the generated image
      QRCode = zxing.generateQRCode(textToEncode,width,height);
      if(firstTime)
      {
        println("Press 's' to save the image to disk");
        println("Press 'r' to start again");
      }
      generated = true;      
      firstTime = false;      
    } 
    else if ((key > 31) && (key != CODED)) 
    {
      // REGULAR CHARACTER: ADD TO STRING
      textToEncode = textToEncode + key;
    }
    else if ((key == BACKSPACE) && (0 < textToEncode.length()))
    {
      char c = textToEncode.charAt(textToEncode.length() - 1);
      textToEncode = textToEncode.substring(0, textToEncode.length() - 1);
    }
  }
}








