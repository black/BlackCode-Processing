int rows=25, cols=25;
int w, h;
boolean[][] cell;
void setup() {
  size(300, 300);
  cell = new boolean[rows][cols];
  w = width/cols;
  h = height/rows;
  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) {
      cell[i][j] = (random(100)<50?true:false);
    }
  }
}

void draw() {
  background(-1);
  for (int i=1; i<cols-1; i++) {
    for (int j=1; j<rows-1; j++) {  
      int life = 0;
      for (int m=-1; m<=1; m++) {
        for (int n=-1; n<=1; n++) {
          int x = i+m;
          int y = j+n;
          if (cell[y][y] && i!=m && j!=m) life++;
          println(life);
        }
      }
      cell[i][j] = checkRules(i, j, life);
    }
  }
  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) { 
      if (cell[i][j]) fill(0);
      else fill(255); 
      rect(i*w, j*h, w, h);
    }
  }
}

boolean checkRules(int i, int j, int life) {
  boolean futurelife = false;
  if (cell[i][j] && life<2 ) futurelife = false; // underpopulation
  else if (cell[i][j] && life>3 )futurelife = false;  //overopulation
  else if (!cell[i][j] && life==3)futurelife = true; // birth
  return futurelife;
}

