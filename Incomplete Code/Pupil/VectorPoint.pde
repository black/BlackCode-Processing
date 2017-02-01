
class Point {
  PVector P, O, V; 
  color c;
  Point(PVector P) {
    this.P = P;   
    float a = P.heading();
    V = PVector.fromAngle(a); 
    V.normalize();
    V.mult(0.5);
    c = (color) random(#000000);
  }
  void show() {
    float t = map(P.mag(), 0, 150, 4, 0.5);
    strokeWeight(t);
    stroke(#583F24, 10);
    point(P.x, P.y);
  }
  void update() { 
    V.rotate(random(-0.25, 0.25));
    P.add(V);
  }
}

