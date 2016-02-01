int w=100, h=10, d=100, k=0, N=8;
float t=0;
void setup() {
  size(300, 300, P3D);
} 
void draw() {
  background(0); 
  lights();
  noStroke();
  translate(width>>1, height>>1);
  for (int i=0; i<N; i++) { 
    pushMatrix();
    translate(0, h*i, 0);
    rotateY(radians(i*(N-t)));
    box(w, h, d);
    popMatrix();
  } 
  t = 10*sin(radians(k));
  k++;
}

