void setup() {
  size(300, 300);
}
int k=0, N=0;
void draw() {
  background(#343434);
  translate(width/2, height/2);
  fill(#FF0044);
  ellipse(0, 0, 2*R, 2*R);
  for (int i=0; i<N; i++) {
    pushMatrix();
    rotate(i*PI/N );
    pointLine(radians(k)+i*PI/N);
    popMatrix();
  }

  if (k%400==0) { 
    if (N<10)N++;
    else {
      N=0;
      k=0;
    }
  }
  k++;
}
int R=100;
void pointLine(float k) {
  float x = R*sin(k);
  stroke(-1, 100);
  line(-R, 0, R, 0);
  fill(-1);
  noStroke();
  ellipse(x, 0, 10, 10);
}