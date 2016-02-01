void setup() {
  size(500, 500);
}
int R=10, n=0, M =40;
float k, t;
void draw() {
  background(-1); 
  translate(width/2, height/2);
  for (int  j=M; j>0; j--) {
    if (j%2==0)fill(230, 230, 220);
    else fill(39, 39, 38);
    pushMatrix();
    if (t<j)rotate( radians(t)*(M-j+1));//+n*TWO_PI/12
    beginShape();
    polygon(j);
    endShape(CLOSE);
    popMatrix();
  }
  if (t>1)t=0;
  else t+=0.05;
  frameRate(10);
}

void polygon(int j) {
  for (int i=0; i<6; i++) {
    float ang = i*TWO_PI/6;
    float x =  (R+j*15)*cos(ang);
    float y =  (R+j*15)*sin(ang);
    vertex(x, y);
  }
}

