class Brick {
  int x, y, w, h;
  int xf, yf, wf, hf;
  boolean move=true;
  color brickcolor;
  Brick(int k, int w, int h) {
    brickcolor = (color) random(#000000);
    this.w = w;
    this.h = h;
    if (k<0) {
      x = -w;
      y = height/2;
    } else if (k>0) {
      y = -h;
      x = width/2;
    } else if (k==0) {
      x = width/2;
      y = height/2;
    }
  }
  void show() {
    stroke(0);
    fill(brickcolor);
    rect(x, y, w, h);
  }
  void update() {
    if (move)x++;
  }
  boolean fallingPeice() {
    boolean fall;
    fill(#F5233B); 
    rect(xf, yf, wf, hf); 
    text( x + " " +xf, xf, yf);
    yf++;
    if (yf>height)fall = true;
    else fall = false; 
    return fall;
  }
  void cutX(int xbase, int wid) {
    if (x<xbase) {
      xf = x;
      yf = y;
      x = xbase;
      wf = xbase-xf; 
      w = xf+w-xbase;
      hf = h;
    } else if (x>xbase) { 
      xf = xbase+wid; 
      wf = x+w-(xbase+wid); 
      w = w-wf; 
      hf = h; 
      yf = y; 
      //  println(x + " "+ y + " " + xf+" "+yf + " " +wf+ " "+ w);
    }
  }
}

