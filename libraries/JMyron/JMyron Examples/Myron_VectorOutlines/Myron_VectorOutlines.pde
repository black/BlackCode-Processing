/* 

point the camera at a white background and
make a dark sillouette with your hand.
you should see your hand converted to a
vector-based shape and rerendered in green outline!

 last tested to work in Processing 0090
 
 JTNIMOY

*/ 

import JMyron.*;

JMyron m;//a camera object

void setup(){
  size(320,240);
  m = new JMyron();//make a new instance of the object
  m.start(width,height);//start a capture at 320x240
 
  m.minDensity(100);  //minimum density - you'll need to tweak this, probably.
  m.trackColor(0,0,0,150); //the color to track, and the tolerance. you'll need to tweak this as well.
  
  println("Myron " + m.version());
  noFill();
}

void draw(){
  m.update();//update the camera view
  int[] img = m.image(); //get the normal image of the camera
   
  //draw what the camera sees
  loadPixels();
  for(int i=0;i<width*height;i++){ //loop through all the pixels
    pixels[i] = img[i]; //draw each pixel to the screen
  }
  updatePixels();

  stroke(0,150,0);
  int[][][] list = m.globEdgePoints(30);//get the outlines
  for(int i=0;i<list.length;i++){
    beginShape(LINE_LOOP);
    if(list[i]!=null){
      for(int ii=0;ii<list[i].length;ii++){
        vertex(list[i][ii][0],list[i][ii][1]);
      }
    }
    endShape();
  }

}

void mousePressed(){
  m.settings();//click the window to get the settings
}

public void stop(){
  m.stop();//stop the object
  super.stop();
}

