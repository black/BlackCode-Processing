ArrayList<PVector> points = new ArrayList();
ArrayList<PVector> boundary = new ArrayList();
PVector start;
float ang;
void setup() {
  size(500, 300);
  for (int i=0; i<1000; i++) {
    points.add(new PVector(random(50, width-50), random(50, height-50)));
  }

  //Get the left most point
  start = getLeftPoint();
}

void draw() {
  background(-1);
  stroke(0);
  for (int i=0; i<points.size (); i++) {
    PVector P = points.get(i);
    point(P.x, P.y);
    /* start a line and rotate it clock wise to 
     see which point hits it first and store that
     point into start */
    start = detectPoint(P, ang);
    boundary.add(start);
  }
  ang++;
}

PVector getLeftPoint() {
}

PVector detectPoint(PVector P, float ang) {
  if (P!=start) {
    //equation of line from start y = y1+m*(x-x1);
    float m = (P.y-start.y)/(P.x-start.x);
    float mm = tan(radians(ang));
    if (abs(m-mm)<0.05) {
      return P;
    }
    line(start.x, start.y, P.x, P.y);
  }
}

