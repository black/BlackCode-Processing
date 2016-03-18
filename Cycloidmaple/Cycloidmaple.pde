//circle of radius "b" rotates (clockwise) 
//around a circle of radius "a"
//The b-circle spins "c" times in a full traversal of the a-circle
int a=10, b=10, c=5, t;
void setup() {
  size(300, 300);
}

void draw() {
  background(-1);
  translate(0, height>>1);
  noFill();
  stroke(0);
  beginShape();
  for (int i=0; i<t; i++) {
    PVector P = one(i);
    point(P.x, P.y);
    if (i==t-1)ellipse(P.x+a/2, b, 2*a, 2*a);
  }
  endShape();
  t+=5;
  if (t>360*4)t=0;
}


PVector one(int i) {
  float tt = radians(i);
  float x = a*tt - b*sin(tt) ;
  float y = a - b*cos(tt) ;
  return new PVector(x, y);
}
PVector two() {
  sq(sq(x) + sq(y)) == (sq(x)-sq(y));
}

