void setup() {
  size(300, 300);
}
int R = 40, N=8, m=0;
float k=0.0, t=0;
void draw() {
  background(-1);
  k = 10*sin(radians(t));
  noStroke();
  for (int j=0; j<N; j++) {

    for (int i=0; i<360; i+=20) {
      if (i%2==0)k=k*-1;
      else k= k;
      float x = width/2+(R+j*10)*cos(radians(i+k*j));
      float y = height/2+(R+j*10)*sin(radians(i+k*j));
      fill(0);
      ellipse(x, y, 4, 4);
    }
  }
  t++;
}

