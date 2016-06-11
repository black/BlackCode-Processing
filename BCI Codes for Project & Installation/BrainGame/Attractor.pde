class Particle {
  PVector loc, vel, acc;
  float k=0.5, m, r;
  int t=0;
  Particle(float x, float y, float m, float r) {
    loc = new PVector(x, y);
    vel = new PVector(random(-0.5, 0.5), random(-0.5, 0.5));
    acc = new PVector(0, 0);
    this.m = m;
    this.r = r;
  }
  void show() {
    pushStyle();
    fill(-1, 50);
    float rr = r*sin(radians(t))+2*r/3;
    ellipse(loc.x, loc.y, 2*rr, 2*rr);
    popStyle();
    noStroke(); 
    ellipse(loc.x, loc.y, 2*r, 2*r); 
    t+=4;
  }

  void update() {
    vel.add(acc);
    vel.limit(2);
    loc.add(vel);
  }
  void applyForce(Particle M ) { 
    acc.mult(0);  
    if (loc!=M.loc) {
      PVector dir = PVector.sub(loc, M.loc);
      float d = dir.mag();
      float force = (k*m*M.m)/d*d;
      dir.normalize();
      dir.mult(force);
      acc.add(dir);
    }
  }
  void bounce() {
    if ((int)loc.x < 0 || (int)loc.x >width ) {
      vel.x = vel.x*-1;
    }
    if ((int)loc.y < 0 || (int)loc.y >height) {
      vel.y = vel.y*-1;
    }
  }
}

