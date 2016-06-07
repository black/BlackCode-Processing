class Splash {
  int x, y;
  float rad, angle, i;
  color c;
  Splash(int _x, int _y, float _i, color splc) {
    x = _x;
    y = _y;
    i = _i;
    rad = random(2, 50);
    angle = random(0, TWO_PI);
    c = splc;
  }

  void display() {
    //layer.beginDraw();
    layer.fill(c);
    layer.noStroke();
    float spotX = x + cos(angle)*2*i;
    float spotY = y + sin(angle)*3*i;
    layer.ellipse(spotX, spotY, rad-i, rad-i+1.8);
    //layer.endDraw();
  }
  void update() {
    y = y+1;
  }
}

