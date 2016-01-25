void setup() {
  size(300, 300);
}
int k=0, N=6;
void draw() {
  background(-1);
  translate(width/2, height/2);
  ellipse(0, 0, 2*R, 2*R);
  for (int i=0; i<N; i++) {
    pushMatrix();
    rotate(i*PI/N+2*PI/N);
    pointLine(k);
    popMatrix();
  }
  k++;
}
int R=100;
void pointLine(int k) {
  float x = R*sin(radians(k));
  line(-R, 0, R, 0);
  ellipse(x, 0, 5, 5);
}