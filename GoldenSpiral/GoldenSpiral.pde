ArrayList<PVector> poop = new ArrayList();
int N = 100;
void setup() {
  size(400, 400);
}

void draw() {
  background(-1);
  int m = (int)map(mouseX, 0, width, 1, 10);
  for (int i=0; i<N; i+=m) { 
    float x = 100*cos(radians(i*360/N));
    float y = 100*sin(radians(i*360/N));
    poop.add(new PVector(x, y));
  } 
  println("FLAG 1", poop.size());
  //control arralist size
  if (<poop.size()) {
  }
  println("FLAG 2", poop.size());
  translate(width/2, height/2);
  noFill();
  stroke(0, 100);
  for (int i=0; i<poop.size (); i++) {
    PVector P = poop.get(i);
    ellipse(P.x, P.y, 5, 5);
  }
}

