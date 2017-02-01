ArrayList<Point> poop = new ArrayList(); 
int N = 1080;
int r = 25;
void setup() {
  size(600, 600, P3D);
  background(-1);
  for (float i=0; i<N; i++) {
    float rad = i*TWO_PI/N;
    float x = r*cos(rad);
    float y = r*sin(rad);
    poop.add(new Point(new PVector(x, y)));
  }
}
void draw() { 
  pushMatrix();
  scale(2);
  translate(width>>2, height>>2);
  //  noFill();
  //  strokeWeight(1);
  //  stroke(150, 2);
  //  ellipse(0, 0, 2*150, 2*150);
  for (int i=0; i<poop.size (); i++) { 
    Point P = poop.get(i);
    P.show();
    if (P.P.mag()<150) {
      P.update();
    } else { 
      poop.remove(i);
    }
  }
  noStroke();
  fill(#583F24, 5);
  ellipse(0, 0, 2*r, 2*r);
  popMatrix();
}

