ArrayList<Mine> mines = new ArrayList();
void setup() {
  size(300, 600);
  for (int i=0; i<5; i++) {
    int x = (int)random(10, width-10);
    int y = (int)random(10, height-10);
    mines.add(new Mine(x, y));
  }
}
int k =0 ;
void draw() {
  background(-1);
  for (int i=0; i<mines.size (); i++) {
    Mine M = mines.get(i);
    M.show();
    float val  = sin(radians(k));
    if (val<0 && dist(mouseX, mouseY, M.x, M.y)<M.r)M.update(val);
  }
  k++;
}

