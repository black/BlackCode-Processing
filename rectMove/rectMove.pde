int N=8, M=8, w, h;
void setup() {
  size(400, 400);
  w = width/N;  
  h = width/M;
}
float t=0;
void draw() {
  background(0);
  translate(w/2, h/2);
  noStroke();
  for (int i=0; i<N; i++) {
    for (int j=0; j<M; j++) {
      fill(-1); 
      ellipse(i*w, j*h, 2*(w/2-2), 2*(h/2-2));
      square(i*w, j*h, (j%2==0)?t:-t);
    }
  }
  t+=0.5;
}

void square(float x, float y, float  t) {
  noStroke();
  pushMatrix();
  translate(x, y); 
  rotate(radians(t)); 
  for (int i=-1; i<1; i++) {
    for (int j=-1; j<1; j++) {
      if (i==0 && j==0 && t>0) {
        fill(0);
        rect(i*w/2, j*h/2, w/2, h/2);
      }
      if (i==-1 && j==0 && t<0) {
        fill(0);
        rect(i*w/2, j*h/2, w/2, h/2);
      }
    }
  }
  popMatrix();
}

