class Dot {
  float r;
  PVector V;
  color c;
  Dot(PVector V, int col) {
    this.V =V;
    r = random(2, 10);
    int tr = (clckd_colr >> 16) & 0xff;
    int tg = (clckd_colr >> 8) & 0xff;
    int tb = clckd_colr & 0xff;
    c = color(tr, tb, tg);
  }
  void show() {
    noStroke();
    fill(c);
    ellipse(V.x, V.y, 2*r, 2*r);
  }
  void move(float noise) {
    V.x = V.x-noise*5;
    V.y = V.y+noise*((random(50)<25)?-1.5f:1.5f);
  }
}

