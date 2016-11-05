ArrayList<Circle> poop  = new ArrayList();
ArrayList<PVector> wave  = new ArrayList();

int r =100, k=0;
float startx, starty, startr;

void setup() {
  size(800, 300);
  poop.add(new Circle(1, 100));
}

void draw() {
  background(-1);
  translate(150, height>>1);
  circleFunction();
  if (poop.size()!=0) {
    Circle M = poop.get(poop.size()-1); 
    wave.add(new PVector(M.x, M.y));
    waveFunction();
  }
}

void circleFunction() {
  for (int i=0; i<poop.size (); i++) {
    Circle C = poop.get(i); 
    //  C.move();
    for (int j=0; j<poop.size (); j++) {
      Circle K = poop.get(j); 
      K.show(C.x, C.y);
    }
  }
}

void waveFunction() {
  noFill();
  stroke(0);
  beginShape();
  for (int i=0; i<wave.size (); i++) {
    PVector p = wave.get(i);
    vertex(p.x, p.y);
    p.x++;
    if (p.x>500)wave.remove(i);
  }
  endShape(OPEN);
}

void mousePressed() {
  if (mouseButton==LEFT) {
    Circle M = poop.get(poop.size()-1); 
    poop.add(new Circle(M.k/2, M.r/2));
  } else if (mouseButton==RIGHT && poop.size()!=0) {
    poop.remove(poop.size()-1);
  }
}

