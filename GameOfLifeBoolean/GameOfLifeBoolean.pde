int rows=80, cols=80;
int w, h;
boolean[][] cell;
boolean[][] futureCell;
void setup() {
  size(500,500);
  cell = new boolean[rows][cols];
  futureCell = new boolean[rows][cols];
  w = width/cols;
  h = height/rows;

 // frameRate(20);
}

void mouseDragged() {
  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) {
      if (i*w<mouseX && mouseX<i*w+w && j*w<mouseY && mouseY<j*w+w) {
        cell[i][j] = true;
      //  futureCell[i][j] = false; 
      }
    }
  }
}
void draw() {
  background(#FF121A); 



  for (int i=1; i<cols-1; i++) {
    for (int j=1; j<rows-1; j++) {   
      int num = checkNeighbours(i, j);
      futureCell[i][j] = checkRules(i, j, num);
    }
  }
  stroke(-1, 50);
  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) { 
      cell[i][j] = futureCell[i][j];
      if (cell[i][j]) fill(-1);
      else fill(#FF121A); 
      ellipse(i*w, j*h, w, h);
     //futureCell[i][j] = cell[i][j] ;
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