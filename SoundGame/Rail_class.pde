class Rail {
  PVector p1, p2;
  Rail(int x, int y, int xm, int ym) {
    p1 = new PVector(x, y);
    p2 = new PVector(xm, ym);
  }
  void show() {
    stroke(0);
    line(p1.x, p1.y, p2.x, p2.y);
  }
}

