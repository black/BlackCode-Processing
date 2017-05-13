int r = 100;
float k=0;
void setup() {
  size(500, 500);
}

void draw() {
  clear();
  background(-1);

  translate(width>>1, height>>1);
  strokeWeight(4); 
  stroke(0, 200);
  beginShape();
  for (float i=0; i<360; i+=6) {
    float ang = radians(i+k); 
    float dr  = 60*noise(k);
    PVector P = getPoint(r, -dr, ang);
    PVector Q = getPoint(r, 10, -ang);
    PVector R = getPoint(r, dr, ang);
    point(Q.x, Q.y);
    // line(P.x, P.y, R.x, R.y);
    vertex(Q.x, Q.y);
  }
  endShape();
  k+=0.5f;
}

PVector getPoint(float r, float dr, float ang) {
  float x = (r-dr)*cos(ang);
  float y = (r-dr)*sin(ang);
  return new PVector(x, y);
}

