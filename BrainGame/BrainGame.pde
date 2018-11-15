Radio R;
void setup() {
  size(300, 300);
  R = new Radio(width/2, height/2);
}

void draw() { 
  noStroke();
  fill(200, 20);
  rect(0, 0, width, height);
  R.show();
  R.update();
}

class Radio {
  float x, y, r, amp;
  int n = 2;
  Radio(float x, float y) {
    this.x = x;
    this.y = y;
    r = 10;
    amp = 50;
  }
  void show() {
    ellipse(x, y, 2*r, 2*r);
  }
  void update() {
    if (x>width)x=0;
    else x++;
    float ang = map(x, 0, width, 0, n*TWO_PI);
    y = height/2+amp*sin(ang);
  }
}

