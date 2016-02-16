int w = 10;
Cell[][] cell = new Cell[300/w][300/w];

void setup() {
  size(300, 300);
  for (int i=0; i<width/w; i++) {
    for (int j=0; j<height/w; j++) {
      cell[i][j] = new Cell(i*w, j*w, w, (random(100)<25)?true:false);
    }
  }
  // frameRate( 2);
}

void draw() {
  background(-1);
  for (int i=1; i<width/w-1; i++) {
    for (int j=1; j<height/w-1; j++) {
      int life=0;
      for (int m=-1; m<=1; m++) {
        for (int n=-1; n<=1; n++) {
          int x = i+m;
          int y = j+n;
          if (x!=i && y!=j && cell[x][y].myLife ) life++;
        }
      }  
      println(life + " " + millis());
      cell[i][j].setLife(life);
      cell[i][j].myLife = cell[i][j].tempLife;
    }
  }
  for (int i=1; i<width/w-1; i++) {
    for (int j=1; j<height/w-1; j++) {
      cell[i][j].show();
    }
  }
}

class Cell {
  int x, y, w;  
  boolean myLife;
  boolean tempLife;
  Cell(int x, int y, int w, boolean myLife) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.myLife = myLife;
  }
  void show() {
    if (myLife)fill(0, 150);
    else fill(-1);
    rect(x, y, w, w);
  }

  void setLife(int nolife) {  
    if (myLife && nolife<2 || nolife>3) tempLife= false; 
    else if (!myLife && nolife==3) tempLife = true;
    else tempLife = myLife;
  }
}

