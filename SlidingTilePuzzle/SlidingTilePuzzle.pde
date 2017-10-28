int N = 4;
Tile[] tile = new Tile[N*N];  
void setup() {
  size(300, 300);
  int w = width/N;
  for (int j=0; j<4; j++) {
    for (int i=0; i<4; i++) {
      int index = i+j*4;  
      PVector pos = new PVector(i, j);
      tile[index] = new Tile(pos, w, index);
    }
  }
}

void draw() {
  background(-1);
  for (int i=0; i<tile.length; i++) {
    tile[i].show();
  }
}

class Tile {
  PVector pos;
  int w, str;
  Tile(PVector pos, int w, int str) {
    this.pos = pos;
    this.w = w;
    this.str = str;
  }
  void show() {
    if (str==0)fill(0);
    else noFill(); 
    rect(pos.x*w, pos.y*w, w, w );
    fill(0);
    textSize(24);
    text(str, pos.x*w+w/3, pos.y*w+w/2);
  }
  void check() {
  }
  void move() {
  }
}

