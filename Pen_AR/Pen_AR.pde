/*
Smoke coming out from the tip of the pen. 
 The color of the smoke will have the color of pen.
 */

import processing.video.*;
Capture video;
int n=0;
ArrayList<Dot> smoke = new ArrayList();

void setup() {
  size(640, 480, P3D);
  video = new Capture(this, width, height);
  video.start();
  video.read();
}

void draw() {  
  background(-1);
  video.loadPixels();
  colorTracker(); 
  video.updatePixels();
  image(video, 0, 0);

  smokeGenerator();
}

void smokeGenerator() {
  for (int i=0; i<smoke.size (); i++) {
    Dot d = smoke.get(i);
    d.show();
    d.move(noise(n));
    if (d.V.x<-50) {
      smoke.remove(i);
    }
  } 
  n++;
}

 

