ArrayList<Branch> branches = new ArrayList();
void setup() {
  size(600, 300);
  background(0);
  noStroke();
  fill(255);
  ellipseMode(CENTER); 
  PVector l  = new PVector(width/2, height);
  PVector vel = new PVector(0, -1);
  float d = 32;
  branches.add( new Branch(l, vel, d));
}
void draw() { 
  for (int i=0; i<branches.size (); i++) {
    Branch b = branches.get(i);
    PVector loc = b.loc;
    float dia = b.dia;
    ellipse(loc.x, loc.x, dia, dia);
  }
}

class Branch {
  PVector loc, vel;
  float dia, tempDia;
  Branch(PVector loc, PVector vel, float dia) {
    this.loc = loc;
    this.vel = vel;
    this.dia = dia;
    tempDia = dia/2;
  }
  void update() {
    if (dia>tempDia) {
      loc.add(vel);
      dia-=0.05f;
      PVector jitter = new PVector(random(-1, 1), random(-1, 1));
      jitter.mult(0.5);
      vel.add(jitter);
      vel.normalize();
    } else {
      branches.add(new Branch());
    }
  }
}

