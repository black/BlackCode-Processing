ArrayList<Bubble> poop = new ArrayList();
Petal[] p = new Petal[5];
boolean state;
void setup() {
  size(600, 300);
  for (int i=0; i<p.length; i++) {
    p[i] = new Petal();
  }
}

void draw() {
  background(-1);

  translate(width>>1, 200); 
  for (int i =0; i<poop.size (); i++) {
    Bubble b = poop.get(i);
    b.show();
    b.move();
    if (b.y<-200)poop.remove(i);
  }
  for (int i=0; i<p.length; i++) {  
    float ang = map(mouseX, 0, width, -PI/6, PI/6);  
    p[i].show(ang, i);
    if (ang>PI/7) { 
      state = true;
    } else state = false;
  }
  if (state) { 
    poop.add(new Bubble());
  }
}


class Petal { 
  color c;
  Petal() {
    c = (color)random(#000000);
  }
  void show(float ang, int i) {  
    pushMatrix();
    rotate(ang);
    pushMatrix();
    scale(1.5); 
    rotate(PI);
    fill(c); 
    noStroke();  
    bezier(0, 0, 25, 25, 25, 75, 0, 100);
    bezier(0, 0, -25, 25, -25, 75, 0, 100);
    fill(0);
    text(i, 0, 50);
    popMatrix();
    popMatrix();
  }
}

class Bubble { 
  float x, y, r, c;
  Bubble() {
    c = random(0, 100);
    x = random(-100, 100);
    y = random(0, -50); 
    r = random(4, 8);
  }
  void show() {
    noStroke();
    fill(0, c);  
    ellipse(x, y, 2*r, 2*r);
  }

  void move() {
    y--;
    x=x+sin(random(100));
  }
}

