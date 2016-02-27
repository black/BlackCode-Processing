int t=0, k=0, m=0, n=0, ang=0;
boolean start1, start2;

void setup() {
  size(200, 200);
  start1 = true;
} 

void draw() {
  background(-1);
  translate(width>>1, height>>1);
  Polygon();
  noFill();
  stroke(0);
  strokeWeight(4); 
  if (k<180 && start1) { 
    k+=4;
    arc(0, 0, 100, 100, -PI/2+radians(t), -PI/2+radians(k));
  } else if (t<180) {
    arc(0, 0, 100, 100, -PI/2+radians(t), -PI/2+radians(k));
    t+=4;
    if (t==160)start2 = true;
  } else {
    t=0;
    k =0;
  }
  if (m<180 && start2) { 
    m+=4;
    arc(0, 0, 100, 100, -PI/2-radians(m), -PI/2-radians(n));
  } else if (n<180) {
    arc(0, 0, 100, 100, -PI/2-radians(m), -PI/2-radians(n));
    n+=4;
    if (n==160)start1 = true;
  } else {
    n=0;
    m =0;
  }
}

void Polygon() {
  noStroke();
  fill(0);
  beginShape();
  for (int i=0; i<3; i++) {
    float x = 30*sin(radians(i*360/3));
    float y = -30*cos(radians(i*360/3));
    vertex(x, y);
  }
  endShape(CLOSE);
  float x = 40*sin(radians(ang));
  if (  -15<x && x<15)fill(-1);
  ellipse(x, 0, 10, 10);
  ang+=4;
}

