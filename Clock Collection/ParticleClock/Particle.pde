class Particle {
  float x, y;
  float xm, ym;
  float k=0, dk,r; 
  Particle(float x0, float y0) {
    x = x0;
    y = y0;
    k = random(180);
    dk = random(-4, 4);
    r = random(10,30)
  }
  void display() {
    stroke(-1);
    point(xm, ym);
  }
  void update() { 
    if (k<180)k+=dk;
    else k=0;
    xm = x +r*sin(radians(k));
    ym = y +r*cos(radians(k));
  }
}

