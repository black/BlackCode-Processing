PVector[] points;
float  xoff =0 ;
int R = 60;

void setup() {
  size(300, 300);
  points = new PVector[720*2];
  for (float i=0; i<720; i+=0.5) {
    float x = width/2+R*cos(radians(i));
    float y = height/2+R*sin(radians(i));
    points[int(i*2)] = new PVector(x, y);
  }
  background(-1);
}

void draw() {
  fill(-1, 2);
  rect(0, 0, width, height);
  fill(0);
  ellipse(width/2, height/2, 100, 100);
  for (int i=0; i<points.length; i++) {
    float
    stroke(0);
    point(points[i].x, points[i].y);
  }
}

