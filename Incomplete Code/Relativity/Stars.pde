class Star {
  float x, y;
  Star(float x, float y) {
    this.x = x;
    this.y = y;
  }
  void show() {
    noStroke();
    fill(#278ECB);
    ellipse(x, y, 10, 10);
  }
  void update(float m) {
    x = x+m;
  }
}

