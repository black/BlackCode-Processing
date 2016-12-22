class Cactus {
  boolean right, left;
  boolean tempright, templeft;
  int k=0, m=20;
  int x, y, a, b, p, q;
  color c;
  Cactus(int p, int q) {
    this.p = p;
    this.q = q;
    reset();
  }

  void show() {
    if (left==true & right==true) {
      y--;
      b--;
    } else if (left==false && right==true) { 
      a++;
    } else if (left==true && right==false) {
      x--;
    }   
    stroke(c);
    strokeWeight(3);
    point(x, y);
    point(a, b);
  }
  void update() {
    if (k==m) {
      c = (color)random(#000000);
      right = tempright;
      left = templeft;
      right = !right;
      left = !left;
      if (left==true)a=a-m;
      if (right==true)x=x+m;
    }
    if (k==2*m) {
      tempright = right;
      templeft = left;
      right = true;
      left = true; 
      k=0;
    }
    k++;
  }

  void reset() {
    tempright = (random(50)<25?false:true);
    templeft = !tempright;
    right = left = true;
    y = b = q;
    x = p-10;
    a = p+10;
    k=0;
  }
}

