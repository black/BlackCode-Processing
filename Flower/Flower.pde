void setup() {
  size(300, 300);
}
int R = 80, N=15, k=0;
float RR;
void draw() {
  background(-1);
  noStroke();
  fill(#EA4B15, 100);
  for (int i=0; i<N; i++) {
    float ang = i*TWO_PI/N;
    float x = height/2+RR*cos(ang);
    float y = width/2+RR*sin(ang);
    ellipse(x, y, 2*R, 2*R);
  }
  RR = R*sin(radians(k));
  k++;
}

