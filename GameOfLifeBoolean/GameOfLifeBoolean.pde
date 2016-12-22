int rows=100, cols=100;
int w, h;
boolean[][] cell;
boolean[][] futureCell;
void setup() {
  size(700, 700);
  cell = new boolean[rows][cols];
  futureCell = new boolean[rows][cols];
  w = width/cols;
  h = height/rows;

  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) {
      //if (i*w<mouseX && mouseX<i*w+w && j*w<mouseY && mouseY<j*w+w) {
      cell[i][j] = (random(50)<25)?true:false;
      futureCell[i][j] = false;
      // }
    }
  }

  //frameRate(10);
}

void mouseDragged() {
  //  for (int i=0; i<cols; i++) {
  //    for (int j=0; j<rows; j++) {
  //      if (i*w<mouseX && mouseX<i*w+w && j*w<mouseY && mouseY<j*w+w) {
  //        cell[i][j] = true;
  //        //  futureCell[i][j] = false;
  //      }
  //    }
  //  }
}
void draw() { 
  noStroke();
  fill(#FF121A, 2);
  rect(0, 0, width, height);


  for (int i=1; i<cols-1; i++) {
    for (int j=1; j<rows-1; j++) {   
      int num = checkNeighbours(i, j);
      futureCell[i][j] = checkRules(i, j, num);
    }
  }
  //  stroke(-1, 10);
  noStroke();
  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) { 
      cell[i][j] = futureCell[i][j];
      if (cell[i][j]) { 
        fill(-1);
        ellipse(i*w, j*h, w, h);
      }
    }
  }
}

int checkNeighbours(int x, int y) {
  int num =0;
  for (int m=-1; m<=1; m++) {
    for (int n=-1; n<=1; n++) {
      if (n==0 && m==0) continue;
      else if (cell[x+m][y+n]) num++;
    }
  }
  return num;
}

boolean checkRules(int i, int j, int num) {
  boolean futurelife = false;
  if (!cell[i][j] && num == 3) futurelife = true;
  else if ( cell[i][j] && (num < 2 || num > 3) ) futurelife = false;
  else futurelife = cell[i][j];
  return futurelife;
}

