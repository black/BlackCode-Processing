class Dot {
  float r;
  PVector V; 
  Dot(PVector V, int col) {
    this.V =V;
    r = random(2, 10);
  }
  void show() {
    noStroke();
    fill(trackColor);
    ellipse(V.x, V.y, 2*r, 2*r);
  }
  void move(float noise) {
    V.x = V.x-noise*20;
    V.y = V.y+noise*((random(50)<25)?-2.5f:2.5f);
  }
}

