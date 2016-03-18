int r =10, R =50;
void setup() {
  size(250, 250);
} 
void draw() {
  background(#F05050);
  translate(width>>1, height>>1);
  for (int i=0; i<6; i++) {
    float x = R*cos(PI/2+radians(i*360/6));
    float y = R*sin(PI/2+radians(i*360/6));
    pushMatrix();
    translate(x, y);
    cube();
    arm();
    popMatrix();
  }
}

void cube() {
  beginShape();
  for (int i=0; i<6; i++) { 
    r= 10;
    float x = r*cos(PI/2+radians(i*360/6));
    float y = r*sin(PI/2+radians(i*360/6)); 
    vertex(x, y);
  }
  endShape(CLOSE);
}
void arm() {
  noFill();
  beginShape();
  for (int i=0; i<6; i++) { 
    r=20; 
    float x = r*cos(PI/2+radians(i*360/6));
    float y = r*sin(PI/2+radians(i*360/6)); 
    vertex(x, y);
    text(i, x, y);
  }
  endShape(CLOSE);
}

