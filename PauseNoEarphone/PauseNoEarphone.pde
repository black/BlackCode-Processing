float k=0;
boolean  move=true;
void setup() {
  size(400, 300);
  noStroke();
  ellipseMode(CENTER);
}

void draw() {
  background(-1);
  fill(250, 0, 100);
  ellipse(width/2+50*cos(PI*noise(k)), height/2+50*sin(PI*noise(k)), 100, 100);
  if (move)k+=0.025f;
}

void mousePressed() {
  move=!move;
}

