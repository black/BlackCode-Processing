Tetris b;
void setup() {
  size(300, 500);
  b = new Tetris(50, 50);
}

void draw() {
  background(-1);
  grid();
  b.show();
  b.update();
}


class Tetris {
  int x, y;   
  boolean[][] block = new boolean[2][3];
  Tetris(int x, int y) {
    this.x = x;
    this.y = y;
    for (int i=0; i<2; i++) {
      for (int j=0; j<3; j++) {
        if (j%2!=0 && random(15)<10) block[i][j]=true;
      }
    }
  }
  void show() {
    noFill();
    for (int i=0; i<2; i++) {
      for (int j=0; j<3; j++) {
        if (block[i][j]==true) fill(0);
        rect(x+i*w, y+j*h, w, h);
      }
    }
  }

  void update() {
    if (down) {
      y+=10;
      down=false;
    }
    if (right) {
      x+=10;
      right=false;
    }
    if (left) {
      x-=10;
      left=false;
    }
  }
}

