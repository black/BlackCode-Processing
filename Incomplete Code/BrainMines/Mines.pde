class Mine {
  int x, y, r, alpha;
  Mine(int x, int y) {
    this.x = x;
    this.y = y;
    alpha = 255;
    r = 30;
  }
  void show() {
    fill(0, alpha/2);
    stroke(0, alpha);
    ellipse(x, y, 2*r, 2*r);
  }
  void update(float k) {
    alpha = (int)map(k, -1, 1, 0, 255);
  }
}

