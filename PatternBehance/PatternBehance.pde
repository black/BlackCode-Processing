//PShape bot;
int N=14, w;
float p;
void setup() {
  size(600, 600);
  w = width/N;
  p = w/4;
  //  bot = loadShape("bot1.svg");
}

void draw() {
  background(-1);
  translate(w/2, w/2);
  noStroke();
  for (int i=0; i<N; i++) {
    for (int j=0; j<N; j++) {   
      int index = i+j*N;
      newShape(i*w, j*w, index*90);
    }
  }
}

void newShape(int x, int y, float ang) {
  pushMatrix();
  translate(x, y);
  rotate(radians(ang));
  strokeCap(ROUND);
  stroke(0, 150);
  strokeWeight(5);
  line(p, p, 0, 0);
  line(p, -p, 0, 0);
  for (int l=-1; l<2; l++) {
    for (int m=-1; m<2; m++) {
      noStroke();
      fill(0, 80);
      ellipse(l*p, m*p, 3, 3);
    }
  }
  popMatrix();
}

