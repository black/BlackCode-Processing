import peasy.*;
PeasyCam cam;
//----------------------
PVector e1;
PVector e2;
PVector e3;
//----------------------
PVector mouse;

void setup() {
  size(400, 400, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(2000);

  strokeWeight(4);
}
float a, b, c;
void draw() {
  background(9);
  fill(-1, 50);
  noStroke();
  rect(0, 0, width, height);
  mouse = new PVector(mouseX, mouseY, 400);
  //-----------------------------
  e1 = new PVector(78, 262, 0);
  e2 = new PVector( 269, 139, 0);
  e3 = new PVector(107, 122, 264);
  //-----------------------------
  stroke(11, 187, 237);
  line(0, 0, 0, e1.x, e1.y, e1.z);
  stroke(244, 85, 85);
  line(0, 0, 0, e2.x, e2.y, e2.z);
  stroke(23, 158, 1);
  line(0, 0, 0, e3.x, e3.y, e3.z);
  stroke(#f9b808);
  line(0, 0, 0, mouse.x, mouse.y, mouse.z);

  e1.mult(a);
  e2.mult(b);
  e3.mult(c);

  stroke(#912CFF);
  line(0, 0, 0, e1.x, e1.y, e1.z);
  stroke(#2CD3FF);
  line(0, 0, 0, e2.x, e2.y, e2.z);
  stroke(#FF992C);
  line(0, 0, 0, e3.x, e3.y, e3.z);
  stroke(#FFBC00);
  line(0, 0, 0, mouse.x, mouse.y, mouse.z);
}
 
