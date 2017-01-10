ArrayList particles = new ArrayList();
void setup() {
  size(200, 200);
  background(0);
  fill(255);
  textSize(90);
  text("ABHINAV", 30, 130);
  for (int x = 0; x < width; x++) {
    for (int y = 0; y < height; y++) {
      if (get(x, y) == color(255)) {
        particles.add(new Particle(x, y));
      }
    }
  }
}
void draw() {
  background(0);
  for (int i = 0; i < particles.size(); i++) {
    Particle p = (Particle) particles.get(i);
    p.display();
    p.update();
  }
}
class Particle {
  int x, y;
  Particle(int x0, int y0) {
    x = x0;
    y = y0;
  }
  void display() {
    stroke(255, 0, 0);
    point(x, y);
  }
  void update() {
    x += floor(random(-1, 2));
    y += floor(random(-1, 2));
  }
}
