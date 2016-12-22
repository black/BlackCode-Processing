ArrayList particles = new ArrayList();
boolean reset;
void setup() {
  size(500, 200);
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
  for (int i = 0; i < particles.size (); i++) {
    Particle p = (Particle) particles.get(i);
    p.display();

    if (reset) {
      p.reset();
    } else {
      p.update();
      reset = false;
    }
  }
}

void mousePressed() {
  reset = true;
}

