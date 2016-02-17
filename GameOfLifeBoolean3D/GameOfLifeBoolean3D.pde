import peasy.*;
PeasyCam cam;

int rows=10, cols=10, deps =10;
int w, h, d;
boolean[][][] cell;
boolean[][][] futureCell;
void setup() {
  size(400, 400, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(1000);
  cell = new boolean[rows][cols][deps];
  futureCell = new boolean[rows][cols][deps];
  w = 200/cols;
  h = 200/rows;
  d = 200/deps;
  for (int i=0; i<cols; i++) {
    for (int j=0; j<rows; j++) {
      for (int k=0; k<deps; k++) {
        cell[i][j][k] = (random(100)<40)?true:false;
      }
    }
  }
  frameRate(10);
}
void draw() {
  background(0); 
  lights();
  for (int i=1; i<cols-1; i++) {
    for (int j=1; j<rows-1; j++) {   
      for (int k=1; k<deps-1; k++) {   
        int num = checkNeighbours(i, j, k);
        futureCell[i][j][k] = checkRules(i, j, k, num);
      }
    }
  }

  for (int i=1; i<cols-1; i++) {
    for (int j=1; j<rows-1; j++) { 
      for (int k=1; k<deps-1; k++) { 
        cell[i][j][k] = futureCell[i][j][k];
        if (cell[i][j][k]) {
          noStroke();
          fill(#FF244C);
          pushMatrix();
          translate(i*w, j*h, k*d);
          box(w-2);
          popMatrix();
        }
        futureCell[i][j] = cell[i][j] ;
      }
    }
  }
}

int checkNeighbours(int x, int y, int z) {
  int num =0;
  for (int m=-1; m<=1; m++) {
    for (int n=-1; n<=1; n++) {
      for (int o=-1; o<=1; o++) {
        if (n==0 && m==0 && o==0) continue;
        else if (cell[x+m][y+n][z+o]) num++;
      }
    }
  }
  return num;
}

boolean checkRules(int i, int j, int k, int num) {
  boolean futurelife = false;
  if (!cell[i][j][k] && num == 6) futurelife = true;
  else if ( cell[i][j][k] && (num <4 || num > 6) ) futurelife = false;
  else futurelife = cell[i][j][k];
  return futurelife;
}

