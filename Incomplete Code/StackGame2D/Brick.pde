
class Brick {
  int x, y, w, h;
  int xp, yp, wp, hp;
  Brick() {
    w = 100;
    h = 100;
    x = -w;
    y = height/2;
  }
  void show() {
    stroke(0, 10);
    fill(#F5CB23, 50); 
    rect(x, y-h/2, w, h);
  }
  void check(boolean state, int xtemp) {
    if (state) {
      if (xtemp<x ) {
        xm = x;
        int tempW = xtemp+wm-x; 
        wp =  tempW - wm;
        hp  = hm;
        wm = tempW;
        /*------------*/
        xp = xtemp-wp;
        yp = y;
      } else if (xtemp>x ) {
        xm =xtemp;
        int tempW = x+w-xtemp;
        wp = tempW - wm;
        hp  = hm;
        wm = tempW;
        /*-------------*/
        xp = xtemp-wp;
        yp = y;
      }
    }
  }
  void update() {
    x+=1;
  }
  boolean peice() {
    boolean del;
    fill(#F5233B); 
    rect(xp, yp, wp, hp);
    yp++;
    if (yp>height)del = true;
    else del = false;
    return del;
  }
}

