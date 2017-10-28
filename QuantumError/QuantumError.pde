import peasy.*;

PeasyCam cam;
int r=150;
float k=0;
void setup() {
  size(500, 500, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(50);
  cam.setMaximumDistance(500);
}

void draw() {
  background(0);
  stroke(255, 100);
  noFill();
  strokeWeight(4);
  for (int j=0; j<5; j++) {
    beginShape();  
    for (int i=0; i<399; i+=20) {
      float ang = radians(i);
      float x = r*cos(ang);
      float y = r*sin(ang);
      float z =10*sin(i+radians(k*(j+1)));
      curveVertex(x, y, z+j*10);
    }
    endShape(CLOSE);
  }
  k+=1;
}

