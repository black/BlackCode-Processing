int N=150;
Arm[] A = new Arm[N];
void setup() {
  size(500, 500);
  A[0] = new Arm(width/2, height/2, 0);
  for (int i=1; i<A.length; i++) {
    A[i] = new Arm(A[i-1].xx, A[i-1].yy, i);
  }
}

void draw() {
  background(-1);
  float k = map(mouseX, 0, width, 0, 360);
  noFill();
  beginShape();
  for (int i=1; i<A.length; i++) {
    A[i].show();
    A[i].update(A[i].xx, A[i].yy, i*k);
    //vertex(A[i].x, A[i].y);
  }
  endShape();
  k++;
}

class Arm {
  float x, y, xx, yy, ang;
  int len;
  Arm(float x, float y, int i) {
    this.x = x;
    this.y = y;
    len = 10; 
    ang = i*10;
    xx = x+len*cos(radians(ang));
    yy = y+len*sin(radians(ang));
  }
  void show() {
    stroke(0);
    point(x, y);
    line(x, y, xx, yy);
  }
  void update( float x, float y, float i) {
    ang = i;
    //    this.x =x;
    //    this.y =y;
    xx = x+len*cos(radians(ang));
    yy = y+len*sin(radians(ang));
  }
}

