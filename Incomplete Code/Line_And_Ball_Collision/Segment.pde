class Segment {
  float x, y, xm, ym, dirx, diry;
  Segment(int x, int y, int xm, int ym) {
    this.x =x;
    this.y =y;
    this.xm =xm;
    this.ym =ym;
    if (y>ym && x<xm) dirx=-0.5;
    else if (y>ym && x>xm) dirx=0.5;
    else if (y<ym && x<xm) dirx=0.5;
    else if (y<ym && x>xm) dirx=-0.5;
  }
  void show() {
    stroke(0);
    line(x, y, xm, ym);
  }
  float getY(float xx) {
    float yy = y + (xx-x)*((ym-y)/(xm-x));
    return y;
  }
} 
void mousePressed() {
  xt = mouseX;
  yt = mouseY;
}
void mouseDragged() {
  mouseDown = true;
}
void mouseReleased() {
  Segment S = new Segment(xt, yt, xmt, ymt);
  segmentList.add(S);
  mouseDown = false;
}

