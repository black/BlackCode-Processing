//http://makio.free.fr/?p=544

ArrayList hist = new ArrayList();
float joinDist = 130;

void setup() {
  size(1056,706);
  background(255); 
}

void draw() {
}

void mouseMoved() {
  stroke(0);
  PVector d = new PVector(mouseX, mouseY, 0);
  hist.add(0,d);
  for (int p = 0; p < hist.size(); p++) {
    PVector v = (PVector) hist.get(p);
    float joinChance = p/hist.size() +
      d.dist(v)/joinDist;
    if (joinChance < random(0.4))
      line(d.x, d.y, v.x, v.y);
  }
}

void keyPressed() {
  if (key == ' ') {
    background(255);
    hist.clear();
  }
}
