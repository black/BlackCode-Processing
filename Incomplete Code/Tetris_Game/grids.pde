int w=10, h=10;
void grid() {
  noFill();
  stroke(0, 10);
  int col = width/w;
  int row = height/h;
  for (int x=0; x<col; x++) {
    for (int y=0; y<row; y++) {
      rect(x*w, y*h, w, h);
    }
  }
}
