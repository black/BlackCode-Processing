class Particle extends VerletParticle2D {
  int r;
  Particle(float x, float y, int r) {
    super(x, y);
    this.r=r;
  } 
  void display() {
    noFill();
    noStroke();
    ellipse(x, y, r, r);
  }
}

