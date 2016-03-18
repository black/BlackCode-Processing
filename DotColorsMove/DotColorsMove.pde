void setup() {
  size(300, 300);
  background(-1);
}
int R = 80, N=10;
float k=0.0, t=0;
color[] c = {
  #FF0303, #FF03B4, #C703FF, #2303FF, #0361FF, #03E8FF, #03FF8F, #38FF03, #BFFF03, #FFE603
};
void draw() {
  // background(-1);
  fill(-1, 30);
  noStroke();
  rect(0, 0, width, height);
  k = 40*sin(radians(t)); 
  float r = abs(15*sin(radians(t)))+8; 
  noStroke(); 
  for (int j=0; j<N; j++) { 
    float x = width/2+R*cos(radians(j*360/N+k));
    float y = height/2+R*sin(radians(j*360/N+k));
    fill(c[j]);
    ellipse(x, y, r, r);
  }
  t+=2;
}

