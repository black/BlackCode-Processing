int l, n;
boolean[][] state;
void setup() {
  size(300, 300);  
  n = 4;
  state = new boolean[n][n];
  l = 300/n;
}

void draw() {
  background(-1);
  grid();
}

void mousePressed() {
  for (int i=0; i<n; i++) {
    for (int j=0; j<n; j++) {
      int index = i+j*n;
      if (i*l<mouseX && mouseX<i*l+l && j*l<mouseX && mouseX<j*l+l) {
        checkTile(i, j);
      }
    }
  }
}

void grid() {
  for (int i=0; i<n; i++) {
    for (int j=0; j<n; j++) {
      int index = i+j*n;
      if (index<n*n)state[i][j] = true;
      else state[i][j] = false; 
      if (state[i][j])fill(0, 50);
      else fill(0, 222, 0); 
      rect(i*l, j*l, l, l);
    }
  }
}

void checkTile(int ix, int jy) {
  for (int i=-1; i<=1; i++) {
    for (int j=-1; j<=1; j++) { 
      int cx = ix+i;
      int cy = jy+j;
      if (i==0 && j==0) continue;
      else if (0<cx && cx< 3 && 0<cy && cy< 3 && state[cx][cy]) println(i + " " + j);
    }
  }
}

