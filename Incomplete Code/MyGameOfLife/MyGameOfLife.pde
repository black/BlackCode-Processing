ArrayList<BitLife> poop = new ArrayList();
int nobox =40;
boolean flag=false;
void setup() {
  size(400, 400);
  float bsize = width/nobox;
  for (int y=0; y< nobox ; y++) {
    for (int x=0; x< nobox ; x++) {
      BitLife B = new BitLife(false, x, y, bsize);
      poop.add(B);
    }
  }
}

void draw() {
  background(-1);
  for (int i=0; i<poop.size(); i++) {
    BitLife B = (BitLife) poop.get(i);
    B.display();
    B.update();
  }
}


class BitLife {
  boolean life=false;
  int  x, y;
  float bsize;
  BitLife(boolean life, int x, int y, float bsize) {
    this.life = life;
    this.x =x;
    this.y = y;
    this.bsize = bsize;
  }

  void display() {
    stroke(0, 50);
    if (life) fill(#FF0062);
    else noFill();
    rect(x*bsize, y*bsize, bsize, bsize);
  }

  void SetLife() {
    if (x*bsize<mouseX && mouseX<(x+1)*bsize && y*bsize<mouseY && mouseY<(y+1)*bsize && flag) {
      
    }
  }

  void update() {
    if (x*bsize<mouseX && mouseX<(x+1)*bsize && y*bsize<mouseY && mouseY<(y+1)*bsize && flag) {
      life = !life;
      flag=!flag;
    }
  }
}

void mouseDragged() {
  flag=true;
}

