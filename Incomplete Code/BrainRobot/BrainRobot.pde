ArrayList<Hurdle> poop = new ArrayList();
ArrayList<Blast> blastpoop = new ArrayList();
Car car;
void setup() {
  size(300, 500);
  car = new Car(new PVector(width>>1, height>>1));
  for (int i=0; i<10; i++) {
    poop.add(new Hurdle(new PVector(random(width), random(height))));
  }
}

void draw() { 
  background(-1);
  float k = map(mouseX, 0, width, -180, 0);
  if (mousePressed) {
    car.update(k);
  }
  for (int i=0; i<poop.size (); i++) {
    Hurdle h = poop.get(i);
    h.show();
    h.update();
    if (h.l.y>height) {
      poop.remove(i);
      poop.add(new Hurdle(new PVector(random(width), 0)));
    }
    if (dist(h.l.x, h.l.y, car.l.x, car.l.y)<30) {
      poop.remove(i);
      poop.add(new Hurdle(new PVector(random(width), 0)));
      blastpoop.add(new Blast( h.l, h.c));
    }
  }

  if (blastpoop.size ()>0) {
    for (int i=0; i<blastpoop.size (); i++) {
      Blast B = blastpoop.get(i);
      B.show();
      if (B.m==0)blastpoop.remove(i);
    }
  }

  dialControl(k);

  car.show();
}

void dialControl(float k) { 
  noStroke();
  fill(-1);
  rect(0, height-50, width, 50);
  pushMatrix();
  translate(width/2, height);
  rotate(radians(k));
  stroke(0);
  line(0, 0, 50, 0);
  popMatrix();
}

void mousePressed() {
}

