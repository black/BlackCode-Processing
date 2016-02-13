void setup() {
  size(300, 300);
}
int R = 100;
float t=0;
void draw() {
  background(-1);
  noStroke();
  pushMatrix();
  translate(width>>1, height>>1);
  ellipse(0, 0, 5, 5);
  float tt = millis()/1000;
  float x = R*cos(radians(t)); 
  float y = R*sin(radians(t));
  fill(0);
  ellipse(x, y, 5, 5); 
  t+=1;
  popMatrix();
  text(t+ "\n"+second(), 10, 10);
}

