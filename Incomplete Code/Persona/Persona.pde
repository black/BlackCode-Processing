Person P;
void setup() {
  size(500, 200);
  PVector n = new PVector(width/2, height/2);
  P = new Person(n, 0);
}

void draw() {
  background(-1);
  PVector mouse = new PVector(mouseX, mouseY);
  P.show();
  if (press && P.l.dist(mouse)<P.r) {
    P.l.x = mouse.x;
    P.l.y = mouse.y;
  }
}


class Person {
  PVector l;
  int character, i;
  float r=20;
  Person(PVector l, int i) {
    this.l = l;
    this.i = i;
  }

  void show() {
    noStroke();
    if (i==0) fill(#F0E51B);
    else if (i==1) fill(#761BF0);
    else fill(#FC1251);
    ellipse(l.x, l.y, 2*r, 2*r);
  }

  void update() {
  }
}

boolean press = false;
void mousePressed() {
  press = true;
}

void mouseReleased() {
  press = false;
} 

