PVector find_Center(PVector A, PVector B, PVector C) {
  PVector center;
  float mr = (B.y -A.y)/(B.x-A.x);
  float mt = (C.y -B.y)/(C.x-B.x);
  float h = (mr*mt*(C.y -A.y)+mr*(B.x+C.x)-mt*(A.x+B.x))/(2*(mr-mt));
  float k = -(1/mr)*(h-((A.x+B.x)/2))+(A.y+B.y)/2;
  float r = sqrt(sq(A.x-h)+sq(A.y-k));
  center = new PVector(h, k, r);
  return center;
}

