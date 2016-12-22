Cactus[] c = new Cactus[4];
void setup() {
  size(400, 500); 
  background(-1);
  for (int i=0; i<4; i++) {
    c[i]= new Cactus(i*width/4+40, height);
  }
}

void draw() { 
  for (int i=0; i<4; i++) {
    c[i].show();
    c[i].update();
    if (c[i].y<0) {
      background(-1);
      c[i].reset();
    }
  }
}

