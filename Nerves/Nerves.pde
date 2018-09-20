ArrayList<Neuron> poop = new ArrayList();
void setup() {
  size(400, 300);
  background(0);
  noStroke();
  ellipseMode(CENTER);
  PVector l = new PVector(width/2, height);
  float d = 20;
  poop.add(new Neuron(l, d));
}

void draw() {
  for (int i=0; i<poop.size (); i++) {
    Neuron N = poop.get(i);
    if (random(1)<0.1) {
      poop.add(new Neuron(N.l, N.d));
    }   
    N.update();
    ellipse(N.l.x, N.l.y, N.d, N.d);
  }
}

class Neuron {
  PVector v, l;
  float d, dir;
  Neuron(PVector l, float d) {
    this.v = new PVector(0, -1);
    this.l = l;
    this.d = d;
    dir = random(-PI/6, PI/6);
    v.rotate(dir);
  }  
  void update() { 
    if (d>1) {
      d-=0.5f;
      l.add(v);
      PVector el = new PVector(random(-1, 1), random(-1, 1));
      v.add(el);
      v.normalize();
    }
  }
}

