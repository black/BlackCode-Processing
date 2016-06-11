class Blast {
  float r, x, y;
  color c;
  Blast(float x, float y) {
    this.x = x;
    this.y = y;
    r = random(5);
    this.c = 0;
  }

  void display() {
    noStroke();
    fill(c, 150);
    ellipse(x, y, r, r);
  }

  void move() {
    r-=0.1;
  }
}

