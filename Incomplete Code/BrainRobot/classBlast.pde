class Blast {
  PVector l;
  int m;
  color c;
  Blast(PVector l, color c) {
    this.l = l;
    this.c = c;
  }
  void show() {
    float r = 40*sin(radians(m));
    int alpha = (int)map(m, 0, 180, 0, 255);
    noStroke();
    fill(c, alpha);
    ellipse(l.x, l.y, 2*r, 2*r);
    if (m<180)m++;
  }
}

