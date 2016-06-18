boolean[][] mines;
boolean[][] clickedStatus;
int[][] minesCount;
int rows, cols, w, h;
void setup() {
  size(300, 300);
  w = 30;
  h = 30;
  cols = width/w;
  rows = height/h;
  mines = new boolean[rows][cols];
  clickedStatus = new boolean[rows][cols];
  minesCount = new int[rows][cols];

  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      mines[i][j] = (random(50)<15)?true:false;
      clickedStatus[i][j] = false;
    }
  }

  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      if (!mines[i][j]) minesCount[i][j] = getNumberofBombs(i, j);
    }
  }
}

void draw() {
  background(-1);
  /*----show no. of bombs -----*/
  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      fill(-1);
      rect(i*w, j*h, w, h);
      if (clickedStatus[i][j]) {
        if (mines[i][j]) {
          println("YOU LOOSE");
          if (mines[i][j]) {
            fill(0);
            rect(i*w, j*h, w, h);
          }
        } else continue;
      }
    }
  }
  /*----show no. of bombs -----*/
  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      if (!mines[i][j]) {
        if (clickedStatus[i][j]) {
          fill(0, 50);
          rect(i*w, j*h, w, h);
          fill(0);
          textSize(14);
          text(minesCount[i][j], i*w+w/3, j*h+h/1.5);
        } else {
          fill(-1);
          rect(i*w, j*h, w, h);
        }
      }
    }
  }
}

void mousePressed() { 
  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      if (mousePressed && i*w<mouseX && mouseX<i*w+w && j*h<mouseY && mouseY<j*h+h) {
        clickedStatus[i][j] = true;
      }
    }
  }
}

int getNumberofBombs(int i, int j) {
  int bomb=0;
  for (int x=-1; x<2; x++) {
    for (int y=-1; y<2; y++) {
      if (x==0 && y==0)continue;
      else if (0<=x+i && x+i<cols && 0<=y+j && y+j<rows) {
        if (mines[x+i][y+j])bomb++;
      }
    }
  }
  return bomb;
}

