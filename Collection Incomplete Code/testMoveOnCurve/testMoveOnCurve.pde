ArrayList<PVector>   points = new ArrayList<PVector>();
float dtheta=0.0;
int numofPoints=4;
void setup() {
  size(600, 600);
  curvegenerate();
}

float t=0;
void draw() {
  background(-1);

  float x = curvePoint(points.get(points.size ()-4).x, points.get(points.size ()-3).x, points.get(points.size ()-2).x, points.get(points.size ()-1).x, t);
  float y = curvePoint(points.get(points.size ()-4).y, points.get(points.size ()-3).y, points.get(points.size ()-2).y, points.get(points.size ()-1).y, t);

  float tx = curveTangent(points.get(points.size ()-4).x, points.get(points.size ()-3).x, points.get(points.size ()-2).x, points.get(points.size ()-1).x, t);
  float ty = curveTangent(points.get(points.size ()-4).y, points.get(points.size ()-3).y, points.get(points.size ()-2).y, points.get(points.size ()-1).y, t);
  float a = atan2(ty, tx);
  a -= radians(dtheta);
  if (right)dtheta+=10;
  if (left)dtheta-=10;
  t+=1/100.0;

  println(t);

  if (t>1) {
    t=0;
    points.add(new PVector(random(x,width), random(-height, height)));
  }
  
  pushMatrix();
  translate(width/2-x, height/2-y);
  noFill();
  stroke(0);
  beginShape();
  for (int i = 0; i < points.size (); i++) { //0-9
    PVector p = points.get(i);
    curveVertex(p.x, p.y);
    p.x-=x;
    if (p.x<-2*width) {
      points.remove(i);
    }
  }
  endShape();
  fill(#00ADFA);
  noStroke();
  ellipse(x, y, 30, 30);
  pushStyle();
  stroke(#FA0334);
  strokeWeight(5);
  line(x, y, cos(a)*40 + x, sin(a)*40 + y);
  popStyle();
  popMatrix();
}


void curvegenerate() {
  for (int i=0; i<numofPoints; i++) {
    if (i==0 )points.add(new PVector(-width/2, random(height)));
    else if (i==(numofPoints-1) )points.add(new PVector(width/2, random(height)));
    else points.add(new PVector(random(-width/2, width/2), random(height)));
  }
}

boolean right =false, left = false;
void keyPressed() {
  if (key==CODED)
  {
    if (keyCode==RIGHT) right = true;
    else if (keyCode==LEFT) left =true;
  }
}

void keyReleased() {
  if (key==CODED)
  {
    if (keyCode==RIGHT) right = false;
    else if (keyCode==LEFT) left =false;
  }
}

