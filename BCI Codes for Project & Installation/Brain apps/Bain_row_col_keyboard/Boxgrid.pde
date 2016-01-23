class  Boxgrid {
  int bx, by;
  int lx, ly;
  int r=5;
  String[] words = {
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", " ", " ", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", " ", " "
  };
  Boxgrid(int _x, int _y) 
  {
    bx = _x;
    by = _y;
  }
  void display()
  {
    for (int j=0; j<3; j++) {
      for (int i=0; i<10; i++) {
        lx = i*50+bx;
        ly = j*50+by;
        fill(255, 176, 3);
        stroke(0);
        strokeWeight(1);
        if ( i == 3 && j == 1  ) {
          fill(#151515);
          rect(lx, ly, 100, 50, r, r, r, r);
          fill(255);
          textSize(25);
          text("SPACE", lx+7, ly+35);
          i = 4;
        }
        else if ( i == 8 && j == 2  ) {
          fill(#151515);
          rect(lx, ly, 100, 50, r, r, r, r);
          fill(255);
          textSize(25);
          text("DEL", lx+30, ly+35);
          i= 9;
        }
        else
        {
          rect(lx, ly, 50, 50, r, r, r, r);
        }
        fill(255);
        textSize(30);
        if (j==0) {
          text(words[i], lx+15, ly+35);
        }
        if (j==1) {
          if (i==4)
          {
            text(words[i+10], lx-35, ly+35);
          }
          else {
            text(words[i+10], lx+15, ly+35);
          }
        }
        if (j==2) {
          text(words[i+20], lx+15, ly+35);
        }
      }
    }
  }
}

