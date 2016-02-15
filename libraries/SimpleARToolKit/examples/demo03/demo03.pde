import JMyron.*;
import processing.video.*;
import processing.opengl.*;
import pARToolKit.SimpleARToolKit;

SimpleARToolKit ar;
int capWidth, capHeight;
JMyron m;
Movie mov1, mov2;
PImage img;

void setup() {
  size(400, 300, OPENGL);
  capWidth = 320;
  capHeight = 240;
  m = new JMyron();
  m.start(capWidth,capHeight);
  m.findGlobs(0);
  img = createImage(capWidth,capHeight,ARGB);
  mov1 = new Movie(this,"mv02.mov");
  mov2 = new Movie(this,"mv04.mov");
  mov1.loop();
  mov1.read();
  mov2.loop();
  mov2.read();
  ar = new SimpleARToolKit(this, capWidth, capHeight);
  ar.loadPattern("patt.hiro", 80, 0.0f, 0.0f);
  ar.register("showBox");
  noStroke();
  fill(200,200,0);
  rectMode(CENTER);
  frameRate(15);
}

void draw() {
  background(0);
  m.update();
  m.imageCopy(img.pixels);
  img.updatePixels();
  hint(DISABLE_DEPTH_TEST);
  image(img,0,0,width,height);
  hint(ENABLE_DEPTH_TEST);
  if (ar.findMatch(img,100)) {
    ar.showObject();
  }
}

void showBox(SimpleARToolKit _a) {
  pushMatrix();
  rotateX(PI/2.0);
  translate(0,0,-40);
  beginShape(QUADS);
  texture(mov2);
  vertex(-40,-30,0,240);
  vertex(40,-30,320,240);
  vertex(40,30,320,0);
  vertex(-40,30,0,0);
  endShape();
  popMatrix();
  pushMatrix();
  rotateX(PI/2.0);
  translate(0,0,40);
  rotateY(PI);
  beginShape(QUADS);
  texture(mov2);
  vertex(-40,-30,0,240);
  vertex(40,-30,320,240);
  vertex(40,30,320,0);
  vertex(-40,30,0,0);
  endShape();
  popMatrix();
  pushMatrix();
  rotateX(PI/2.0);
  translate(40,0,0);
  rotateY(PI/2.0);
  beginShape(QUADS);
  texture(mov1);
  vertex(-40,-30,0,240);
  vertex(40,-30,320,240);
  vertex(40,30,320,0);
  vertex(-40,30,0,0);
  endShape();
  popMatrix();
  pushMatrix();
  rotateX(PI/2.0);
  translate(-40,0,0);
  rotateY(-PI/2.0);
  beginShape(QUADS);
  texture(mov1);
  vertex(-40,-30,0,240);
  vertex(40,-30,320,240);
  vertex(40,30,320,0);
  vertex(-40,30,0,0);
  endShape();
  popMatrix();
}

void movieEvent(Movie _m) {
  _m.read();
}

void stop() {
  m.stop();
  super.stop();
}








