class Particle extends VerletParticle2D {
  int r, i;
  Particle(float x, float y, int r, int i) {
    super(x, y);
    this.r=r;
    this.i=i;
  } 
  void display() {
    fill(-1);
    noStroke();
    //ellipse(x, y, r, r);
    text(i, x, y);
  }
}

