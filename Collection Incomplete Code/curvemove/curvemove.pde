ArrayList<Float> xpoop = new ArrayList();
ArrayList<Float> ypoop = new ArrayList();
void setup() {
  size(400, 300);
  curvegenerate();
}
float t=0;
void draw() {
  background(-1);
  float x = bezierPoint(xpoop.get(0), xpoop.get(1), xpoop.get(2), xpoop.get(3), t);
  float y = bezierPoint(ypoop.get(0), ypoop.get(1), ypoop.get(2), ypoop.get(3), t);
  fill(0);
  ellipse(width/2, height/2, 10, 10);
  pushMatrix();
  translate(width/2-x, height/2-y);
  noFill();
  bezier(xpoop.get(0), ypoop.get(0), xpoop.get(1), ypoop.get(1), xpoop.get(2), ypoop.get(2), xpoop.get(3), ypoop.get(3));
  popMatrix();
  t+=1/100.0;
}


void curvegenerate() {
  for (int i=0; i<4; i++) {
    float x = noise(i)*random(-width/2, width/2);
    float y = noise(i)*random(-800, 800);
    if (i==0)xpoop.add(-width/2.0*2);
    else if (i==3)xpoop.add(width/2.0*2);
    else xpoop.add(noise(i)*random(-width/2, width/2));
    ypoop.add(y);
  }
}

void mousePressed() {
  xpoop.clear();
  ypoop.clear();
  curvegenerate();
  t=0;
}

