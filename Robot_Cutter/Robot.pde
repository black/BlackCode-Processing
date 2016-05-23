
class Robot {
  PVector loc, vel;
  int w, h, l;

  Robot(int x, int y) {
    w = 20;
    h = 20;
    l = 6;
    loc = new PVector(x, y);
    vel = new PVector(random(-0.5f, 0.5f), random(-0.5f, 0.5f));
  }
  void show() {
    translate(loc.x, loc.y);
    robotDraw();
  }
  void move() {
    loc.add(vel);
  }
  void robotDraw() {
    noStroke();
    fill(0);
    rect(0, 0, w, h);
    stroke(#4B9805);
    line(w/2, h/2, 3*w/2, h/2);
    fill(#4B9805);
    pushMatrix();
    translate(3*w/2, h/2);
    rotate(PI);
    triangle(0, 0, l/2, l/2, l/2, -l/2);
    popMatrix();
    line(w/2, h/2, w/2, 3*h/2);
    pushMatrix();
    translate(w/2, 3*h/2);
    rotate(-PI/2);
    triangle(0, 0, l/2, l/2, l/2, -l/2);
    popMatrix();
    stroke(#ED0707);
    line(w/2, h/2, -w/2, h/2);
    line(w/2, h/2, w/2, -h/2);
  }
}

