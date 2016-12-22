class Hurdle {
  PVector l;
  color c;
  Hurdle(PVector l) {
    this.l = l;
    c = (color)random(#000000);
  }
  void show() {
    noStroke();
    fill(c);
    ellipse(l.x, l.y, 20, 20);
  }
  void update() {
    l.y++;
  }
}

