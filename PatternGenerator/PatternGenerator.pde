void setup() {
  size(300, 300);
  background(-1);
}
int R =100, RR=150;
int k=0, l=0, m=0;
void draw() {
  //background(-1);
  noStroke();
  fill(-1, 0);
  rect(0, 0, width, height);
  frameRate(20);
  stroke(0, 100);
  translate(width>>1, height>>1);
  for (int i=0; i<m; i+=180) {
    float x = R*cos(radians(i+l));
    float y = R*sin(radians(i+l));
    float a = RR*cos(radians(i-k));
    float b = RR*sin(radians(i-k));
    line(x, y, a, b);
  }
  k+=2;
  l+=4;
  if (m>360)m=0;
  else m++;
}