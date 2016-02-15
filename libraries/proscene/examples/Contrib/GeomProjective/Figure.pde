class Figure {
  PVector pt1, pt2, pt3, pt4;
  Plan plan12, plan23, plan34, plan41;
  color c1=color(255, 255, 255, 254);
  color c2=color(255, 255, 0, 254);
  color c3=color(250, 250, 255, 254);
  color c4=color(55, 255, 50, 254);
  color c5=color(155, 100, 250, 254);
  Figure(PVector p1, PVector p2, PVector p3, PVector p4) {
    pt1=p1;
    pt2=p2;
    pt3=p3;
    pt4=p4;
    plan12=new Plan(pt1, pt2);
    plan23=new Plan(pt2, pt3);
    plan34=new Plan(pt3, pt4);
    plan41=new Plan(pt4, pt1);
  }

  void draw() {
    fill(100, 100, 255, 150);
    plan12.draw(c1, c5);
    plan23.draw(c3, c2);
    plan34.draw(c3, c4);
    plan41.draw(c4, c5);
    stroke(0, 0, 255);
    noFill();
    strokeWeight(3);

    ligne(mul(pt1, 3), mul(pt1, -3));
    ligne(mul(pt2, 3), mul(pt2, -3));
    ligne(mul(pt3, 3), mul(pt3, -3));
    ligne(mul(pt4, 3), mul(pt4, -3));
    strokeWeight(1);
  }
}
