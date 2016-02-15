/**
 * decodeImageCam - another example of the use of the zxing4processing.decodeImage()
 * method
 *
 * (c) 2009 Rolf van Gelder :: http://www.cage.nl/ :: http://www.cagewebdev.com/
 * 
 * Opens a webcam and tries to find QRCodes in the cam captured images
 * using the zxing4processing.decodeImage() method
 *
 * When a QRCode is detected it will decode it and read the content using speech synth
 *
 * Run this sketch and hold a printed copy of a QRCode in front of the cam
 *
 * Note: make sure your video image is NOT mirrored! It won't detect QRCodes
 * that way...
 *
 */

/*
 * IMPORT THE zxing4processing LIBRARY + DECLARE A ZXING4P OBJECT
 * http://www.artisopensource.net/productslife/library.php
 */
import com.aos.zxing4processing.*;
ZXING4P zxing;

/*
 * IMPORT OpenCV VIDEO LIBRARY + DECLARE AN openCV CAPTURE OBJECT
 * http://ubaa.net/shared/processing/opencv/
 */
import hypermedia.video.*;
OpenCV videoCap;

/*
 * IMPORT SPEECH LIBRARY + DECLARE A simpleSpeech OBJECT
 * http://www.bryanchung.net/?p=208
 */
import simpleSpeech.*;
Speak speaker;

/****************************************************************************
 * SETUP
 ***************************************************************************/
void setup()
{
  size(640,480);

  // CREATE A CAPTURE INSTANCE
  videoCap = new OpenCV(this);
  videoCap.capture(width,height);  

  // CREATE A NEW EN-/DECODER INSTANCE
  zxing = new ZXING4P(this);

  // CREATE A NEW SPEAKER INSTANCE
  speaker = new Speak(this);
}

/****************************************************************************
 * DRAW
 ***************************************************************************/
void draw()
{ 
  background(0);

  // GET VIDEO CAPTURE
  videoCap.read();

  // DISPLAY VIDEO CAP
  image(videoCap.image(), 0, 0);

  // TRY TO DETECT AND DECODE A QRCODE IN THE VIDEO CAP
  // decodeImage(boolean tryHarder, PImage img)
  // tryHarder: false => fast detection (less accurate)
  //            true  => best detection (little slower)  
  String decodedText = zxing.decodeImage(true,videoCap.image());

  if(!decodedText.equals(""))
  {  // FOUND A QRCODE! USE SPEECH SYNTH TO READ IT OUT LOUD
    speaker.speak(decodedText);
  }  
}




