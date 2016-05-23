PVector[]  P = new PVector[3] ;
void setup() {
  size(300, 300);
  for (int i=0; i<3; i++) { 
    P[i]   = new PVector(random(50, width-50), random(50, height-50));
  }
}

void draw() {
  background(-1);
  strokeWeight(2);

  stroke(0, 255, 0);
  PVector M = find_Center(P[0], P[1], P[2]); 
  ellipse(M.x, M.y, 2*M.z, 2*M.z);
  point(M.x, M.y);

  stroke(0);
  for (int i=0; i<3; i++) { 
    point(P[i].x, P[i].y);
  }
}

PVector find_Center(PVector A, PVector B, PVector C) {
  PVector center;
  float ma = (B.y -A.y)/(B.x-A.x);
  float mb = (C.y -B.y)/(C.x-B.x);
  float h = (ma*mb*(A.y -C.y)+mb*(A.x+B.x)-ma*(B.x+C.x))/(2*(mb-ma));
  float k = -(1/ma)*(h-((A.x+B.x)/2))+(A.y+B.y)/2;
  float r1 = sqrt(sq(A.x-h)+sq(A.y-k));
  float r2 = sqrt(sq(B.x-h)+sq(B.y-k));
  float r3 = sqrt(sq(C.x-h)+sq(C.y-k));
  println(r1+" "+r2+" "+r3);
  center = new PVector(h, k, r1);
  return center;
}

