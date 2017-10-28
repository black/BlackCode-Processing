int w, h, N;
color[] c;
void setup() {
  size(900, 650);
  w = h= 20;
  N = 5;
  c = new color[N];
  for (int i=0; i<N; i++) {
    c[i] = (color) random(#000000);
  } 
  background(-1);
  for (int x=0; x<width; x+=w) {
    for (int y=0; y<height; y+=h) {
      noStroke();
      fill(#E7E8E8);
      ellipse(x, y, 5, 5);
    }
  }

  for (int x=0; x<width; x+=w) {
    for (int y=0; y<height; y+=h) {  
      strokeWeight(5);
      if (random(50)<25) { 
        stroke((random(50)<10?c[(int)random(N)]:#E7E8E8));
        line(x, y, x+(int)random(-2, 2)*w, y);
      }
    }
  }

  for (int x=0; x<width; x+=w) {
    for (int y=0; y<height; y+=h) {   
      if (random(50)<10) {
        stroke(c[(int)random(N)], 200);
        int rx = (int)random(-4, 4);
        int ry = (random(50)<15?1:-1)*rx;
        line(x, y, x+rx*w, y+ry*h);
      }
    }
  }
}

