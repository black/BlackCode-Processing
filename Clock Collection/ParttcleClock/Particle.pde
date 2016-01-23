class Particle {
  int x, y;
  Particle(int x0, int y0) {
    x = x0;
    y = y0;
  }
  void display() {
    stroke(-1);
    point(x, y);
  }
  void update() {
    x += (random(-1, 1));
    y += (random(-1, 1));
  }
}

