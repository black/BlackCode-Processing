//int r = 100;
ArrayList<PVector> poop = new ArrayList();
void setup() {
  size(400, 400);
  for (int i=0; i<pow (10, 5); i++) {
    float ang = random(-PI, PI);
    float r = random(0, 100);
    float x = width/2+r*cos(ang);
    float y = height/2+r*sin(ang);
    poop.add(new PVector(x, y, r));
  }
}

void draw() {
  background(30);
  strokeWeight(2);
  stroke(-1, 10);
  for (int i=0; i<poop.size (); i++) {
    PVector p = poop.get(i);
    point(p.x, p.y);
  }
  for (int i=0; i<poop.size (); i++) {
    PVector p = poop.get(i);
    point(p.x, p.y);
  }
}

