ArrayList<Mine> poop = new ArrayList();
int w;
void setup() {
  size(400, 300);
  w = 50;
  for (int i=0; i<width/w; i++) {
    for (int j=0; j<height/w; j++) {
      poop.add(new Mine(i, j, int(random(9))));
    }
  }
}

void draw() {
  background(#2D2D3B);
  for (int i=0; i<poop.size (); i++) {
    Mine m = poop.get(i);
    if (m.i*w<mouseX && mouseX<m.i*w+w && m.i*w<mouseY && mouseY<m.i*w+w && mousePressed) {
      m.clicked = true;
    }
    m.show();
  }
}

