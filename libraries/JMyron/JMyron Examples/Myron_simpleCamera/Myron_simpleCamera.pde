/*

  a very simple example that draws the camera pixels
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
}

void draw(){
  m.update();//update the camera view
  int[] img = m.image(); //get the normal image of the camera
  
  loadPixels();
  for(int i=0;i<width*height;i++){ //loop through all the pixels
      pixels[i] = img[i]; //draw each pixel to the screen
  }
  updatePixels();
}

void mousePressed(){
  m.settings();//click the window to get the settings
}

public void stop(){
  m.stop();//stop the object
  super.stop();
}
