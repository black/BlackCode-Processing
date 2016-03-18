boolean detectCollision(PVector c, PVector p1, PVector p2, float rr) {
  float  dist = PVector.dist(c, p1);
  float ang = angle(p1, p2, c);
  dist = dist*cos((ang<PI/2)?ang:PI+ang);
  PVector d = calculate(p2, p1, dist);
  boolean touch = collison(d.x, d.y, c.x, c.y, rr);
  if (!touch)return false;
  else return true;
}

float angle(PVector pp1, PVector pp2, PVector mm) { // calculate an angle between two lines p1-->m & p1-->p2
  float m2 = (pp2.y-pp1.y)/(pp2.x-pp1.x);
  float m1 = (mm.y-pp1.y)/(mm.x-pp1.x); 
  float ang  = abs((m2-m1)/(1+m2*m1));
  return ang;
}

PVector calculate(PVector p, PVector q, float dist) { // calculate a point on a line at a distance "dist"
  PVector v = PVector.sub(p, q);
  float vnorm = 1/v.mag();
  PVector u = PVector.mult(v, vnorm);
  u.mult(dist);
  PVector t = PVector.add(q, u);
  return t;
}

boolean collison(float x, float y, float h, float k, float R) { // check if the point lying on the circle or not
  int dr = (int)sqrt(sq(x-h)+sq(y-k));
  if (dr<R)return true;
  else return false;
}

