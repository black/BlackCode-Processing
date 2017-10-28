import peasy.*;
import gifAnimation.*; 
GifMaker gifExport;

PeasyCam cam;
ArrayList<PVector> poop = new ArrayList();
float x=1, y=10, z=0;
float a=10, b=28, c = 8.0/3.0;
void setup() {
  size(500, 500, P3D);
  colorMode(HSB);
  cam = new PeasyCam(this, 500);
  gifExport = new GifMaker(this, "lorenz.gif");
}

void draw() { 
  background(0);
  float dt = 0.01;
  float dx = a*(y-x)*dt;
  float dy = (x*(b-z)-y)*dt;
  float dz = (x*y-c*z)*dt;
  x = x + dx;
  y = y + dy;
  z = z + dz;  
  scale(5);
  strokeWeight(0.5);
  poop.add(new PVector(x, y, z));
  noFill();
  beginShape();
  float hu = 0;
  for (PVector v : poop) {
    stroke(hu, 255, 255);
    vertex(v.x, v.y, v.z);
    hu+=0.1;
    if (hu>255) {
      hu=0;
    }
  }
  endShape();
  gifExport.setDelay(1);
  gifExport.addFrame();
}

void keyPressed() {
  gifExport.finish();                 // write file
}

