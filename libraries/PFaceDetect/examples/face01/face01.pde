import pFaceDetect.*;
import JMyron.*;

PFaceDetect face;
JMyron m;
PImage img;

void setup() {
  size(320,240);
  m = new JMyron();
  m.start(width,height);
  m.findGlobs(0);
  face = new PFaceDetect(this,width,height,
  "haarcascade_frontalface_default.xml");
  frameRate(15);
  img = createImage(width,height,ARGB);
  rectMode(CORNER);
  noFill();
  stroke(255,0,0);
  smooth();
}

void draw() {
  background(0);
  m.update();
  arraycopy(m.cameraImage(),img.pixels);
  img.updatePixels();
  face.findFaces(img);
  image(img,0,0);
  drawFace();
}

void drawFace() {
  int [][] res = face.getFaces();
  if (res.length>0) {
    for (int i=0;i<res.length;i++) {
      int x = res[i][0];
      int y = res[i][1];
      int w = res[i][2];
      int h = res[i][3];
      rect(x,y,w,h);
    }
  }
}

void stop() {
  m.stop();
  super.stop();
}

