// https://owlcation.com/misc/Butterfly-Curves-in-Polar-Coordinates-on-a-Graphing-Calculator
ArrayList<Butterfly> poop = new ArrayList();
void setup() {
  size(displayWidth, displayHeight);
  for (int i=0; i<50; i++) {
    float x = random(50, width-50);
    float y = random(50, height-50);
    poop.add(new Butterfly(x, y));
  }
}
void draw() {
  background(-1);
  for (int i=0; i<poop.size (); i++) {
    Butterfly B = poop.get(i);
    B.display();
    B.update();
  }
}

class Butterfly {
  int k =0;
  float dr, fr, xm, ym, dm;
  color c;
  Butterfly(float xm, float ym) {
    this.xm = xm;
    this.ym = ym;
    c = (color)random(#000000);
    fr = random(1, 5);
    k = (int)random(0, 180);
    dm = (int)random(1, 6);
  }
  void display() {
    fill(c);
    noStroke(); 
    beginShape();
    for (int i=0; i<360; i+=1) {
      float theta = radians(i);
      float r = 7 - 0.5*sin(theta) + 2.5*sin(3*theta) + 2*sin(5*theta) - 1.7*sin(7*theta) + 3*cos(2*theta) - 2*cos(4*theta) - 0.4*cos(16*theta); // magic :)
      float x = xm+dr*r*cos(theta);
      float y = ym-fr*r*sin(theta);
      vertex(x, y);
    }
    endShape(CLOSE);
  }

  void update() {
    dr = fr*sin(radians(k));
    k+=dm;
  }
}

