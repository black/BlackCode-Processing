import ddf.minim.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;

boolean[][] life;
boolean[][] storelife;
int rows, cols;
float w, h;
void setup() {
  size(600, 600);
  minim = new Minim(this);
  out = minim.getLineOut();




  rows = 100;
  cols = 100;

  life = new boolean[rows][cols];
  storelife = new boolean[rows][cols];

  w = width/cols;
  h = height/rows;
}

void draw() {
  background(-1);

  stroke(0, 10);
  if (start) {
    frameRate(20);
    for (int i=1; i<rows-1; i++) {
      for (int j=1; j<cols-1; j++) {
        int nlife = checkNeighbour(i, j); 
        storelife[i][j] = checkRules(i, j, nlife);
      }
    }
    for (int i=0; i<rows; i++) {
      for (int j=0; j<cols; j++) { 
        life[i][j] = storelife[i][j];
      }
    }

    for (int i=0; i<rows; i++) {
      for (int j=0; j<cols; j++) {
        if (life[i][j]) fill(0);   
        else fill(-1);
        rect(i*w, j*h, w, h);
      }
    }
  } else {
    for (int i=0; i<rows; i++) {
      for (int j=0; j<cols; j++) {
        if (life[i][j]) fill(0);   
        else fill(-1);
        rect(i*w, j*h, w, h);
      }
    }
  }
}

int checkNeighbour(int x, int y) {
  int nlife=0;
  for (int i=-1; i<2; i++) {
    for (int j=-1; j<2; j++) {
      if (i==0 && j==0) continue;
      else if (life[x+i][y+j])nlife++;
    }
  }
  return nlife;
}

boolean checkRules(int i, int j, int num) {
  boolean futurelife = false;
  if (!life[i][j] && num == 3) futurelife = true;
  else if ( life[i][j] && (num < 2 || num > 3) ) futurelife = false;
  else futurelife = life[i][j];
  return futurelife;
}

void mouseDragged() {
  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      if (i*w<mouseX && mouseX<i*w+w && j*h<mouseY && mouseY<j*h+h)life[i][j] = !life[i][j]; 
      storelife[i][j] = false;
    }
  }
}

boolean start;
void keyPressed() {
  start = !start;
}

