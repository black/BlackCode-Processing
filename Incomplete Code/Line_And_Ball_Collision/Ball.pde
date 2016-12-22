class Ball {
  float x, y, r, dx, dy, g;
  Ball(float x, float y) {
    this.x =  x;
    this.y =  y;
    dx =  0;
    dy =  0.01f;
    g = 0.25f;
    r = 4;
  }
  void show() {
    noStroke();
    fill(0, 200);
    ellipse(x, y, 2*r, 2*r);
  }
  void update(boolean hit, float xx) {
    if (hit) {
      dy*=-1;
      dx+=xx;
    } else {
      dy*=1;
      dx+=0;
    }
    dy+=g;
    y+=dy;
    x+=dx;
  }
}

