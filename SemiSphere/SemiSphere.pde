import peasy.*;

PeasyCam pCamera;

float R = 50.0; 
float factor = TWO_PI / 100.0;
float x, y, z;
color[] c = {
  #EA2121, #E0A707, #1BC143, #4C50F2, #F50753
};
PVector[] sphereVertexPoints;

void setup() {
  size(400, 400, P3D);

  pCamera = new PeasyCam(this, 150);
}


void draw() {
  background(50);
  lights();
  for (int i=0; i<5; i++) {
    float X = R-i*10; 
    semiSphere(((i%2==0)?-1:50), X);
  }
}


void semiSphere(color c, float R) {
  fill(c);  
  noStroke();
  for (float phi = 0.0; phi < HALF_PI; phi += factor) {
    beginShape(QUAD_STRIP);
    for (float theta = 0.0; theta < TWO_PI + factor; theta += factor) {
      x = R * sin(phi) * cos(theta);
      z = R * sin(phi) * sin(theta);
      y = -R * cos(phi);

      vertex(x, y, z);

      x = R * sin(phi + factor) * cos(theta);
      z = R * sin(phi + factor) * sin(theta);
      y = -R * cos(phi + factor);

      vertex(x, y, z);
    }
    endShape(CLOSE);
  }
}

