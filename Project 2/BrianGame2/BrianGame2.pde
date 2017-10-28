ArrayList<PVector> poop = new ArrayList();
Polygon poly;
void setup() {  
  size(600, 200); 
  poly = new Polygon(3, 30);
  for (int i=0; i<600; i+=4) {
    poop.add(new PVector(i, ((random(200)<3)?height-noise(i)*10:height)));
  }
}
float n=3, x, dy;
void draw() {  
  background(-1);
  noStroke();  
  translate(0, - height/3);
  if (right) {
    n++;
  } else if (left) {
    if (n>3)n--;
    else n= 3;
  }
  x+=0.01f;
  if (jump) {
    if (dy<60)dy+=3;
    else jump = false;
  } else {
    if (dy>0)dy-=2;
    else dy=0;
  }
  pushMatrix();
  translate(0, -dy);
  fill(#FFC503);
  poly.storeVertex(int(n)); 
  poly.update(x);  
  poly.display();
  popMatrix();
  stroke(0, 150);
  noFill();
  beginShape();
  for (int i=0; i<poop.size (); i++) {
    PVector P = poop.get(i);
    vertex(P.x, P.y);
    if (P.x>0)P.x-=0.5f;
    else {
      poop.remove(i);
      poop.add(new PVector(width, ((random(200)<3)?height-noise(i)*10:height)));
    }
  }
  endShape(OPEN);
} 

