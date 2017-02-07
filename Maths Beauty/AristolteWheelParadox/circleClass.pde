class Ring {
  ArrayList<PVector> points = new ArrayList();
  ArrayList<PVector> lines = new ArrayList();
  float r, dx;
  Ring(float r) { 
    this.r = r; 
    for (int i=0; i<360; i++) {
      float ang = radians(i);
      float xc = r*cos(ang+PI/2);
      float yc = r*sin(ang+PI/2);
      points.add(new PVector(xc, yc));
    }
  }
  void show() {
    noFill();
    stroke(0, 100);
    // rotate(radians(points.size()-1));
    ellipse(dx, 0, 2*r, 2*r);
    stroke(0, 255, 0);
    for (int i=0; i<points.size (); i++) {
      PVector p = points.get(i);
      point(p.x+dx, p.y);
    }
  }

  void updatePoint() {  
    dx = r*radians(points.size()-1);
    lines.add(new PVector(dx, 0));
    points.remove(points.size()-1);
  }

  void drawLine() {
    stroke(0, 255, 0); 
    for (int i=0; i<lines.size (); i++) {
      PVector p = lines.get(i);
      point(p.x, r);
    }
  }


  void reset() {
    for (int i=0; i<360; i++) {
      float ang = radians(i);
      float xc = r*cos(ang+PI/2);
      float yc = r*sin(ang+PI/2);
      points.add(new PVector(xc, yc));
    }
    lines.clear();
  }
}

