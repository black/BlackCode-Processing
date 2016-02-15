import processing.video.*;
import pSmile.PSmile;

Capture cap;
PSmile smile;
PImage img2;
float res, factor;
PFont font;
int w, h;

void setup() {
  size(640,480);
  w = width/2;
  h = height/2;
  background(0);
  cap = new Capture(this, width, height, Capture.list()[1], 25);
  img2 = createImage(w,h,ARGB);
  smile = new PSmile(this,w,h);
  res = 0.0;
  factor = 0.0;
  font = loadFont("SansSerif.plain-16.vlw");
  textFont(font,16);
  textAlign(CENTER);
  noStroke();
  fill(255,200,0);
  rectMode(CORNER);
}

void draw() {
  img2.copy(cap,0,0,width,height,0,0,w,h);
  img2.updatePixels();
  image(cap,0,0);
  res = smile.getSmile(img2);
  if (res>0) {
    factor = factor*0.8 + res*0.2;
    float t_h = factor*30;
    rect(width/2-20,height-t_h-30,40,t_h);
  }
  String str = nf(res,1,4);
  text(str,width/2,height-10);
}

void captureEvent(Capture _c) {
  _c.read();
}
