int num = 10;
PVector[] dot = new PVector[num];
void setup() {
  size(600, 400);
  for (int i=0; i<num; i++) {
    dot[i] = new PVector(random(width), random(height));
  }
}

void draw() {
  background(-1);
  noStroke();
  fill(0);
  for (int i=0; i<dot.length; i++) {
    ellipse(dot[i].x, dot[i].y, 5, 5);
  }
}

PVector findNearestPoints(PVector P) {
  float tempDist = 0;
  for (int i=0; i<dot.length; i++) {
    if (P!=dot[i]) {
      float d = P.dist(C);
    }
  }
  return point[];
} 

