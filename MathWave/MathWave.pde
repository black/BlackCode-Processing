ArrayList<PVector> poop = new ArrayList();
int R = 50;
int k=0;
float N = 4;
float m=0;
void setup() {
  size(800, 400);
}

void draw() {
  background(-1);
  N = map(mouseX, 0, width, 3, 10);
  m = map(mouseY, 0, height, -1, 1);
  fill(0);
  text(N + " " + m, mouseX, mouseY);
  noFill();
  strokeWeight(2);
  stroke(0, 50); 
  beginShape(); 
  for (int i=0; i<360; i++) { 
    float ang = radians(i);
    float x = 100+cos(ang+radians(k*m))*polygonFunction(N, ang);
    float y = height/2+sin(ang+radians(k*m))*polygonFunction(N, ang);
    vertex(x, y); 
    if (k==i) {
      stroke(#FF4040);
      line(100, height/2, x, y);
      stroke(#40B4FF);
      line(x, y, 200, y);
      poop.add(new PVector(200, y));
    }
  }
  endShape(CLOSE);
  line(200, 0, 200, height);
  beginShape();
  if (poop.size()>0)
    for (int i=0; i<poop.size (); i++) {
      PVector P = poop.get(i);
      stroke(#FFB108);
      vertex(P.x, P.y);      
      if (P.x<width)P.x++;
      else poop.remove(i);
    }
  endShape();
  if (k<360)k++;
  else k=0;
}

float polygonFunction(float n, float i) {
  float k = R/cos((2/n)*asin(sin((n/2)*i)));
  // println(k);
  return k;
}

