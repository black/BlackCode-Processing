int w = 10;
Cell[][] cell = new Cell[600/w][600/w];

void setup() {
  size(600, 600);
  for (int i=0; i<width/w; i++) {
    for (int j=0; j<height/w; j++) {
      cell[i][j] = new Cell(i*w, j*w, w);
    }
  }
  frameRate(1);
}

void draw() {
  background(-1);
  for (int i=0; i<width/w; i++) {
    for (int j=0; j<height/w; j++) {
      cell[i][j].show();
      int life=0;
      int death=0;
      for (int m=-1; m<=1; m++) {
        for (int n=-1; n<=1; n++) {
          int x = i+m;
          int y = j+n;
          if ( i!=x && j!=y && 0<x && x<width/w && 0<y && y<height/w ) {
            if (cell[x][y].myLife)  life++;
            if (!cell[x][y].myLife)  death++;
          }
        }
      }
      println(life + " " +death);
      cell[i][j].myLife = cell[i][j].setLife(((cell[i][j].myLife)?life:death));
    }
  }
}

class Cell {
  int x, y, w;  
  boolean myLife;
  int[] neighbours = new int[8];
  Cell(int x, int y, int w) {
    this.x = x;
    this.y = y;
    this.w = w;
    if (random(100)<1)myLife = true;
  }
  void show() {
    if (myLife)fill(0, 150);
    else fill(-1);
    rect(x, y, w, w);
  }

  boolean setLife(int nolife) { // the rules of game of life
    boolean life = false;
    if (myLife && nolife<2 ) life= false; 
    else if (myLife && nolife>2 ) life= false; 
    else if (myLife && nolife==2 || nolife==3)life = true;  
    else if (!myLife && nolife==3)life = true;
    return life;
  }
}

