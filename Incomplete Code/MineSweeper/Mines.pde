class Mine {
  int i, j, numberofBombs;
  boolean clicked;
  Mine(int i, int j, int numberofBombs) {
    this.i = j;
    this.j = j;
    this.numberofBombs = numberofBombs;
  }
  void show() {
    if (clicked) {
      fill(0);
      text(numberofBombs, i*w, j*w);
    } else {
      fill(0);
      rect(i*w, j*w, w, w);
    }
  }
  void setBomb() {
    for (int k=0; k<numberofBombs; k++) {
      int indeX = i+(int)random(-1, 2);
      int indeY = j+(int)random(-1, 2);
      if (0<indeX && indeX<width/w && 0<indeY && indeY<height/w) {
        storeBomb(indeX, indeY);
      }
    }
  }
  void storeBomb(int x, int y) {
  }
}

