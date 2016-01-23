class Particle {
  PVector loc;
  color c;
  int r=5;
  Particle(float x, float y) {
    loc = new PVector(x, y);
    c = (color)random(#000000);
  }
  void show() {
    noStroke();
    fill(0, 50);
    ellipse(loc.x, loc.y, 2*r, 2*r);
  }
  void update() {
    loc.y++;
  }
}

