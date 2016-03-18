PVector p1, p2;
int r = 50;
float dist;
void setup() {
  size(400, 400);
  p1 = new PVector(50, 50);
  p2 = new PVector(300, 350);
  noCursor();
}

void draw() {
  background(-1);
  PVector m = new PVector(mouseX, mouseY);
  stroke(0);
  line(p1.x, p1.y, p2.x, p2.y);


  dist = PVector.dist(m, p1);
  float ang = angle(p1, p2, m);
  dist = dist*cos((ang<PI/2)?ang:PI+ang);
  PVector d = calculate(p2, p1);
  line(m.x, m.y, d.x, d.y);
  boolean touch = collison(d.x, d.y, m.x, m.y);
  if (!touch)fill(0, 150);
  else fill(0, 50);
  ellipse(d.x, d.y, 4, 4);
  ellipse(m.x, m.y, 2*r, 2*r);
}

float angle(PVector pp1, PVector pp2, PVector mm) { // calculate an angle between two lines p1-->m & p1-->p2
  float m2 = (pp2.y-pp1.y)/(pp2.x-pp1.x);
  float m1 = (mm.y-pp1.y)/(mm.x-pp1.x); 
  float ang  = abs((m2-m1)/(1+m2*m1));
  return ang;
}

PVector calculate(PVector p, PVector q) { // calculate a point on a line at a distance "dist"
  PVector v = PVector.sub(p, q);
  float vnorm = 1/v.mag();
  PVector u = PVector.mult(v, vnorm);
  u.mult(dist);
  PVector t = PVector.add(q, u);
  return t;
}

boolean collison(float x, float y, float h, float k) { // check if the point lying on the circle or not
  int dr = (int)sqrt(sq(x-h)+sq(y-k));
  if (dr<52)return true;
  else return false;
}

