import peasy.*;

PeasyCam cam;
ArrayList<Dot> poop = new ArrayList();
void setup() {
  size(300, 300, P3D);
  cam = new PeasyCam(this, 100);
  poop.add(new Dot(new PVector(0, 0)));
}
int x=0, y=0, z=0;
void draw() {
  background(-1); 
  if (poop.size()>0) {
    for (int i=0; i<poop.size (); i++) {
      Dot D = poop.get(i);
      D.show();
    }
  }

  if (up)z--;
  if (down)z++;
}

void mouseDragged() {
  poop.add(new Dot(new PVector(mouseX, mouseY, z)));
}
boolean up, down, right, left;
void keyPressed() {
  if (key==CODED) {
    if (keyCode==UP) up = true;
    if (keyCode==DOWN) down = true;
    if (keyCode==RIGHT) right = true;
    if (keyCode==LEFT) left = true;
  }
} 
void keyReleased() {
  up = down = right = left = false;
}

class Dot {
  PVector loc; 
  Dot(PVector loc) {
    this.loc = loc;
  }
  void show() {
    strokeWeight(5);
    stroke(0);
    point(loc.x, loc.y, loc.z);
  }
  void update(float ang) { 
    loc.rotate(radians(ang));
  }
}

