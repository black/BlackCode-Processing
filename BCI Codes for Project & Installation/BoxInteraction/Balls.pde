class Ball {
  int x, y, r, dx, dy;
  int i=1, j=1;
  Ball(int x, int y, int r) {
    this.x = x;
    this.y = y;
    this.r = r;
    dx = (random(50)<25)?-1:1;
    dy = (random(50)<25)?-1:1;
  }

  void show() {
    fill(#FA0505);
    noStroke();
    ellipse(x, y, 2*r, 2*r);
  }

  void move() {
    x+=dx*i;
    y+=dy*j;
    if (x<r || x>width-r)i=-1*i;
    if (y<r || y>height-r)j=-1*j;
    for (DrawRect Box : test) {
      if (x>Box.x-r && x<Box.x+Box.w+r) {
        if (y>Box.y-r && y<Box.y+Box.h+r)  i=-1*i;
      } else if (y>Box.y-r && y<Box.y+Box.h+r) {
        if (x>Box.x-r && x<Box.x+Box.w+r)  j=-1*j;
      }
    }
  }
}

