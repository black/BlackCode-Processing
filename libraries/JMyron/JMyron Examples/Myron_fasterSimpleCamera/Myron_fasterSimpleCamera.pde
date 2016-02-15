/*

  an example that draws the camera pixels
  to the screen using the pixel[] array.

 last tested to work in Processing 0090
 
 JTNIMOY
 
 */
 
import JMyron.*;

JMyron m;//a camera object
 
void setup(){
  size(320,240);
  m = new JMyron();//make a new instance of the object
  m.start(width,height);//start a capture at 320x240
  
  m.findGlobs(0);//disable the intelligence to speed up frame rate
  println("Myron " + m.version()); 
  println("Forced Dimensions: " + m.getForcedWidth() + " " + m.getForcedHeight()); 

  loadPixels();

}

void draw(){
  m.update();//update the camera view
  m.imageCopy(pixels);//draw image to stage
  updatePixels();
}

void mousePressed(){
  m.settings();//click the window to get the settings (mac users, this will crash you)
}

public void stop(){
  m.stop();//stop the object
  super.stop();
}
