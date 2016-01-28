int N=20, r =100, k=0;

void setup() {
  size(300, 300);
  strokeCap(SQUARE);
} 
void draw() {
  background(-1);
  for (int i=0; i<N; i++) { 
    strokeWeight(10);
    float angplus = 2*TWO_PI/N;
    if (k==i) stroke(255, 0, 0);
    else stroke(0);
    arc(width/2, height/2, r, r, i*angplus, i*angplus+TWO_PI/N);
  } 
  if (k>20)k=0;
  else k++;
  frameRate(10);
}

