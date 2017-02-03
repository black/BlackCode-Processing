int x, y, r;
void setup() {
  size(300, 300);
  x = width/2;
  y = width/2;
  r = 100;
  strokeCap(ROUND);
}

void draw() {
  background(-1);
  stroke(0, 80);
  strokeWeight(5);
  int mx = (int)map(mouseX, 0, width, 0, 360+90);
  int my = (int)map(mouseY, 0, height, 0, 360+90);

  arc(x, y, r, r, radians(mx), radians(my));
}

