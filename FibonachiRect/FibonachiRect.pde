int N = 6;
float w = 200;
void setup() {
  size(300, 300);
}
void draw() {
  background(-1);
  translate(width/2, height/2);
  stroke(0);
  fill(0, 10);
  for (int i=0; i<N; i++) {
    pushMatrix();
    if (i%2==0)translate(w/(2*i), -w/(2*i)/2);
    else translate(- w/(2*i)/2, w/(2*i));
    rect(0, 0, w/(2*i), w/(2*i));
    popMatrix();
  } 
}

