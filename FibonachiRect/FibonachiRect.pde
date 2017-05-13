int N = 3;
float w = 100;
void setup() {
  size(500, 500);
}
void draw() {
  background(-1);
  float t = map(mouseX, 0, width-10, 0, 360);
  float k = map(mouseY, 0, height-10, 0, 180);
  float[] P = drawRect(width/2, height/2, w, t, k);
  float[] M = drawRect(P[0], P[1], w/2, t, k/2);
  float[] O = drawRect(M[0], M[1], w/4, t, k/4);
}

float[] drawRect(float x, float y, float w, float t, float internal_ang) {
  float ang = radians(t);
  float r = sqrt(sq(w)+sq(w)); 
  float xm = x+r*cos(ang+PI/4);
  float ym = y+r*sin(ang+PI/4);
  float xf = x+w;
  float yf = y+w;
  float[] loc = {
    xm, ym
  };  
  noFill();
  pushMatrix();
  translate(x, y);
  rotate(ang);
  rect(0, 0, w, w);  
  popMatrix();
  return loc;
}

