ArrayList<xline> poop = new ArrayList();
int t=0, alpha, flag;
int x, y, xm, ym;
void setup() {
  size(600, 400);
  for (int i=0; i<10; i++) {
    if (random(100)<50) {
      x = (int)random(0, width);
      y = 0;
      xm = (int)random(0, width);
      ym = height;
      alpha = 10*i;
      flag = 1;
    } else {
      x = 0;
      y = (int)random(0, height);
      xm = width;
      ym = (int)random(0, height);
      alpha = 10*i;
      flag = -1;
    }
    println(x, y, xm, ym, alpha);
    poop.add(new xline(x, y, xm, ym, alpha, flag));
  }
}

void draw() {
  background(-1);
  for (int i=0; i<poop.size (); i++) {
    xline xl = poop.get(i);
    xl.display();
    xl.move();
  }
}

class xline {
  float x1, y1, x2, y2 ;
  float ax, ay, bx, by;
  int flag, xalpha;
  xline(float x1, float y1, float x2, float y2, int xalpha, int flag) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.xalpha = xalpha;
    this.flag = flag;
    ax = random(0.5);
    ay = random(0.5);
    bx = random(0.5);
    by = random(0.5);
  }
  void display() {
    strokeWeight(4);
    stroke(0, xalpha);
    line(x1, y1, x2, y2);
  }
  void move() {
    if (flag>0) {
      if (x1>width/2) {
        x1 = x1-ax;
      } else {
        x1 = x1+ax;
      }
      if (x2>width/2) x2=x1-bx;
      else x2 = x2+bx;
    } else {
      if (y1>height/2) y1=y1-ay;
      else y1 = y1+ay;
      if (y2>height/2) y2=y1-by;
      else y2 = y2+by;
    }
  }
}

