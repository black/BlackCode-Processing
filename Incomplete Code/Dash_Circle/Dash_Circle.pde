void setup() {
  size(400, 400);
  stroke(255);
  strokeWeight(2);
}
 
void draw() {
  background(0);
  translate(width>>1, height>>1);
 
  drawRing(16, 40, 60, -1);
  drawRing(32, 60, 80, 0);
  drawRing(64, 80, 100, 1);
}
 
void drawRing(float n, float ir, float or, int dir) {
  float deltaAngle = TWO_PI / n;
  for (int i = 0; i < n; i++) {
    float a = i * deltaAngle;
    float sina = sin(a), cosa = cos(a);
    line(ir * cosa, ir * sina, or * cosa, or * sina);
  }
}
