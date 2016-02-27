PGraphics pg;
void setup() {
  size(300, 300);
}
float x;
boolean moved= false;
void draw() {
  background(-1); 
  if (moved)x+=dx;
  fill(0);
  text(x, width>>1, height>>1);
}
float dx=0;
void mouseDragged() {
  dx = mouseX-pmouseX;
  moved = true;
}
void mouseReleased() {
  moved = false;
}

