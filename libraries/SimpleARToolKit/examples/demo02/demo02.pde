import JMyron.*;
import processing.video.*;
import processing.opengl.*;
import pARToolKit.SimpleARToolKit;

SimpleARToolKit ar;
int capWidth, capHeight;
JMyron m;
Movie mov;
PImage img;

void setup() {
  size(400, 300, OPENGL);
  capWidth = 320;
  capHeight = 240;
  m = new JMyron();
  m.start(capWidth,capHeight);
  m.findGlobs(0);
  img = createImage(capWidth,capHeight,ARGB);
  mov = new Movie(this,"mv02.mov");
  mov.loop();
  mov.read();
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
  translate(0,0,-20);
  beginShape(QUADS);
  texture(mov);
  vertex(-60,-45,0,240);
  vertex(60,-45,320,240);
  vertex(60,45,320,0);
  vertex(-60,45,0,0);
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





