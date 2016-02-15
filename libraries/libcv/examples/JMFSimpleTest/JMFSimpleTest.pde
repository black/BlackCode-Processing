/*
 * JMFSimpleTest: drawing with video
 *
 * Make sure you edit the CAPTURE_DEVICE string and frame sizes to match your camera/input
 * LibCV currently requires the Sun Java Media Framework installed
 *
 * If your device description is correct but you're receiving an error message, please
 * re-run the JMFRegistry application and choose "Detect Capture devices" to revalidate
 *
 * If you're unsure where to get the device descriptor from, uncomment the line containing
 * listDevices() in the setup() method to print out information about all currently
 * recognized capture devices. 
 *
 * Please consult the javadoc of this library for more information
 * http://toxi.co.uk/p5/libcv/
 *
 * @author: Karsten Schmidt < i n f o -[ a t ]- t o x i . c o . u k >
 */ 
 
import toxi.video.capture.*;

String CAPTURE_DEVICE = "vfw:Microsoft WDM Image Capture (Win32):1";
int CAPTURE_WIDTH = 320;
int CAPTURE_HEIGHT = 240;

SimpleCapture capture;

void setup() {
  size(640,480);
  capture =new JMFSimpleCapture();

  //JMFSimpleCapture.listDevices(); // short version

  // this will dump all available details for each device and format
  //JMFSimpleCapture.listDevices(System.out,true);

  if (!capture.initVideo(CAPTURE_DEVICE, CAPTURE_WIDTH, CAPTURE_HEIGHT, 0)) {
    println(capture.getError());
    // you might have to (re-)run the JMFRegistry application
    // devices sometimes are unrecognized
    System.exit(0);
  }
}

void draw() {
  PImage work=capture.getFrame();
  image(work,mouseX,mouseY);
}

public void stop() {
  capture.shutdown();
  super.stop();
}
