float R =100;

void setup() {
  size(400, 400);
}

void draw() {
  background(-1);
  stroke(0);
  yNode(width/2, height-R, R, 0);
  int k = (int)map(mouseX, 0, width, 0, 360); 
  for (int n=0; n<4; n++) {
    for (int i=1; i<n*2; i+=2) {
      yNode(width/2-(i*(R/(2*n))*sin(TWO_PI/3)), height-(R+n*R*sin(TWO_PI/3)), R/2, k);
      yNode(width/2+(i*(R/(2*n))*sin(TWO_PI/3)), height-(R+n*R*sin(TWO_PI/3)), R/2, -k);
    }
  }
}

void yNode(float xc, float yc, float R, int k) {
  float r;
  pushMatrix();
  translate(xc, yc);
  rotate(radians(0));
  for (int i=0; i<3; i++) {
    if (i==0)r =R;
    else r = R/2;
    float x = r*sin(i*TWO_PI/3);
    float y = r*cos(i*TWO_PI/3);
    line(0, 0, x, y);
  }
  popMatrix();
}

