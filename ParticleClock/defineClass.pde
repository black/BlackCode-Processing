class Particle {
  int x, y, xi, yi; 
  Particle(int x0, int y0) {
    x = xi = x0;
    y = yi = y0;
  }
  void display() {
    stroke(255, 0, 0);
    point(x, y);
  }
  void update() {
    x += floor(random(-1, 2));
    y += floor(random(-1, 2));
  }
  void reset() {
    println(xi + " "+yi+" "+x+" "+y);
    x = xi;
    y = yi;
    println("reset");
  }
}

