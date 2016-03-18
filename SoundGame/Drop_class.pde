class Drop {
  float r;
  PVector loc, vel, gravity;
  Drop(float x, float y) {
    loc = new PVector(x, y);
    r = 5;
    gravity = new PVector(0, 0.05);
    vel = new PVector(random(0.05), random(0.05));
  }
  void show() {
    noStroke();
    fill(0, 100);
    ellipse(loc.x, loc.y, 2*r, 2*r);
  }
  void check() {
  }
  void update(boolean hit) {
    if (hit) vel.mult(-1);
    else vel.mult(1);
    vel.add(gravity);
    loc.add(vel);
  }
}

