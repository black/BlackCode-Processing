
import peasy.*;

PeasyCam cam;

PImage img;
void setup() {
  size(300, 300, P3D);
  img  = loadImage("1.jpg");
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(1000);
}
int w=5, h=5;
void draw() {
  background(-1);
  lights();
  img.loadPixels();
  for (int x=0; x<width; x+=w) {
    for (int y=0; y<height; y+=h) {
      color c = img.get(x, y); 
      float d = brightness(c);
      d = d/10;
      if (d<20)d=0;
      fill(c);
      noStroke();
      pushMatrix();
      translate(x, y, d/2);
      box(w, h, d);
      popMatrix();
    }
  }
}

