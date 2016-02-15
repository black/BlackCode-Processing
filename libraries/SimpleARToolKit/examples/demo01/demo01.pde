import JMyron.*;
import processing.opengl.*;
import pARToolKit.SimpleARToolKit;

JMyron m;
PImage img;
SimpleARToolKit ar;
int capWidth, capHeight;

void setup() {
  size(640, 480, OPENGL);
  capWidth = 320;
  capHeight = 240;
  m = new JMyron();
  m.start(capWidth, capHeight);
  m.findGlobs(0);
  img = createImage(capWidth, capHeight, ARGB);
  ar = new SimpleARToolKit(this, capWidth, capHeight);
  ar.loadPattern("patt.hiro", 80, 0.0f, 0.0f);
  ar.register("showBox");
  stroke(200,200,0);
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
  noFill();
  box(50);
  fill(255);
}




