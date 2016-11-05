class Circle {
  float x, y, xc, yc;
  float k, r, frequency, amplitude;
  Circle(float frequency, float amplitude) {
    this.frequency = frequency;
    this.amplitude = amplitude;
    k = 0;
    r = amplitude;
  }
  void show(float xm, float ym) {
    xc = xm;
    yc = ym;
    stroke(0);
    noFill();
    ellipse(xc, yc, 2*r, 2*r);
    float ang = radians(k);
    x = xc+r*cos(ang);
    y = yc+r*sin(ang); 
    noStroke();
    fill(0);
    ellipse(x, y, 4, 4);
  }

  void move() {
    if (k<360)k+=frequency;
    else k=0;
  }
}

