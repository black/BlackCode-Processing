class Plan {
  PVector normale, point1, point2;

  Plan(PVector v1, PVector v2) {
    point1=v1;
    point2=v2;
    normale=point1.cross(point2);
    normale.normalize();
  }
  
  void draw(color r1, color r2 ) {
    PVector d1=mul(point1, 8);
    PVector d2=mul(point2, 8);
    beginShape();
    fill(r1);
    vertex(0, 0, 0);
    fill(r2);
    vertex(d1.x, d1.y, d1.z);
    vertex(d2.x, d2.y, d2.z);
    endShape();
    beginShape();
    fill(r2);
    vertex(0, 0, 0);
    fill(r1);
    vertex(-d1.x, -d1.y, -d1.z);
    vertex(-d2.x, -d2.y, -d2.z);
    endShape();

    stroke(255, 0, 0);
    droite(d1, d2);
  }
}
