void setup() {
  size(100, 100);
}

void draw() {
  background(-1);
  fill(0);
  int h = hour();
  int m = minute();
  int s = second();
  text(h, 10, 50);
  text(m, 50, 50);
  text(s, 80, 50);
}

