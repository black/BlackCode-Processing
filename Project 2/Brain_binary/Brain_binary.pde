Bincell[] Cells = new Bincell[20];
void setup() {
  size(500, 350);
  color c = (color)random(#000000);
  for (int x=0;x<5;x++) {
    for (int y=0;y<4;y++) {
      int i = x+y*5;
      Cells[i] = new Bincell(x*100,y*100,c);
    }
  }
}

void draw() {
  background(-1);
  for (int i=0;i<Cells.length;i++) {
    Cells[i].display();
    Cells[i].update(0);
  }
}

class Bincell {
  int x, y, sel;
  color c;
  Bincell(int x, int y, color c) {
    this.x = x;
    this.y = y;
    this.c = c;
  }

  void display() {
    fill(c);
    stroke(-1);
    rect(x, y, 100, 100);
    pushMatrix();
    translate(x+30, y-20);
    for (int i=0;i<4;i++) {
      if (sel==i)fill(0);
      else fill(-1);
      noStroke();
      ellipse(i*15, 100, 10, 10);
    }
    popMatrix();
  }

  void update(int sel) {
    this.sel = sel;
  }
}

