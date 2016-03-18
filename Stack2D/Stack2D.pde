ArrayList<Brick> poop = new ArrayList();
void setup() {
  size(400, 400);
  poop.add(new Brick(0, 100, 100));
  poop.add(new Brick(-1, 100, 100));
}

void draw() {
  background(-1);
  grid();
  Brick F = poop.get(0);
  F.show(); 
  for (int i=1; i<poop.size (); i++) {
    Brick B = poop.get(i);
    B.show(); 
    if (cut) {
      B.cutX(F.x, F.w);
      boolean fall = B.fallingPeice(); 
      if (fall) {
        cut = false;
        B.move = false;
        poop.add(new Brick(-1, B.w, B.h));
      }
    } else B.update();
  }
}
boolean cut;
void keyPressed() {
  if (key==' ' )cut = true;
}

