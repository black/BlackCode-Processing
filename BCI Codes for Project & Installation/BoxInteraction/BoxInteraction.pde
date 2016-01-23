ArrayList<DrawRect> test = new  ArrayList();
ArrayList<Ball> balltest = new  ArrayList();
void setup() {
  size(500, 500);
  for (int i=0; i<20; i++) {
    Ball B = new Ball((int)random(width), (int)random(height), (int)random(4, 20));
    balltest.add(B);
  }
}

void draw() {
  background(-1);
  for (Ball B : balltest) {
    B.show();
    B.move();
  }
  for (DrawRect Box : test) {
    Box.show();
  }
}
int x, y, w, h; 
void mousePressed() {
  x = mouseX; 
  y = mouseY;
}
void mouseDragged() {
  w = mouseX; 
  h = mouseY;
}
void mouseReleased() {
  test.add(new DrawRect(x, y, abs(x-w), abs(y-h)));
}

