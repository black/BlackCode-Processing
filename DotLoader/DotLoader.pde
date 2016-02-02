Point[] p = new Point[4];
void setup() {
  size(300, 300);
  for (int i=0; i<p.length; i++) {
    p[i] = new Point(2*i*180/p.length);
  }
}

void draw() {
  background(-1);
  for (int i=0; i<p.length; i++) {
    p[i].show();
    p[i].update();
  }
}

class Point {
  int xp =150, yp=150, R=50, t, dx;
  float x, y;
  boolean move;
  Point(int t) {
    this.t=t;
  }
  void show() {
    noStroke();
    fill(#FC0808);
    ellipse(x, y, 10, 10);
  }
  void update() {
    if (t<180 && !move) {
      x = xp + R*cos(radians(t));
      y = yp + R*sin(radians(t));
      t++;
    } else {
      move = true;
    }
    if (move) {
      if (x<xp+R)x+=2;
      else {
        move = false;
        t =0;
      }
    }
  }
}

