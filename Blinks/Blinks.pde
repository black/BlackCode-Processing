int r = 100, N=6;
void setup() {
  size(300, 300);
}

void draw() {
  background(-1);
  pushMatrix();
  translate(width/2, height/2);
  for (int i=0; i<N; i++) {
    float ang = radians(i*360/N);
    float x = r*cos(ang);
    float y = r*sin(ang);
    point(x, y);
    pushMatrix();
    translate(x, y);
    rotate(PI/2+ang);
    _line();
    popMatrix();
  }
  popMatrix();
}

void _line() {
  line(0, 0, 0, 50);
}

