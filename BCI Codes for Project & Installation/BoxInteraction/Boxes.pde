class DrawRect {
  int x, y, w, h;
  DrawRect(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  void show() {
    fill(#B0FA05);
    noStroke();
    rect(x, y, w, h);
  }
}

