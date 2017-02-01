import peasy.*; 
PeasyCam cam; 
float N = 360*4;
float t=0;
void setup() {
  size(600, 400, P3D);   
  cam = new PeasyCam(this, 100);
  strokeWeight(2);
  stroke(-1, 100);
  noFill();
}

void draw() {
  background(0);   


  beginShape();
  for (float i=0; i<TWO_PI; i+=0.1) { 
    for (float j=0; j<TWO_PI; j+=0.1) {
      PVector p = getPoint(i, j, t);
      vertex(p.x, p.y, p.z);
    }
  }
  endShape(CLOSE);
  if (t<TWO_PI)t+=0.005;
  else t = 0;
  //  println(t);
}

float getRadius(float theta, float a, float b, float m, float n1, float n2, float n3) {

  float eq1 = pow(abs(cos(m*theta/4.0)/a), n2);  
  float eq2 = pow(abs(sin(m*theta/4.0)/a), n3);
  float r = abs(pow(eq1+eq2, -1/n1));
  return r;
}

PVector getPoint(float ang1, float ang2, float t) {
  float r1 = 20*getRadius(
  ang1, // angle
  5, // a // size of upper half
  1, // b // size of lower half
  4, // m // no. of spikes
  3, // n1
  sin(t), // n2
  cos(t) // n3
  );
  float r2 = 20*getRadius(
  ang2, // angle
  1, // a // size of upper half
  5, // b // size of lower half
  4, // m // no. of spikes
  3, // n1
  cos(t), // n2
  sin(t) // n3
  );
  float x = r1*cos(ang1)*r2*cos(ang2);
  float y = r1*sin(ang1)*r2*cos(ang2);
  float z = r2*sin(ang2);
  return new PVector(x, y, z);
}

