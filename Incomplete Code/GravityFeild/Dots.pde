class Dot {
  PVector temploc, loc, vel, acc;  
  float m, G;
  Dot(float x, float y, float z, float m) {
    loc = new PVector(x, y, z);
    temploc = new PVector(x, y, z);
    vel = new PVector(0, 0, 0);
    acc = new PVector(0, 0, 0);
    this.m = m;
    G = 5;
  }

  void show() {
    strokeWeight(4);
    stroke(-1);
    point(loc.x, loc.y, loc.z);
  }

  void applyForce(Dot D) {
    acc.mult(0);
    if (this!=D) {
      PVector dir = PVector.sub(loc, D.loc);
      float d = dir.mag();
      float Fg = G*m*D.m/sq(d);
      dir.normalize();
      dir.mult(Fg);
      acc.add(dir);
    }
  }


  void update() {
    vel.add(acc);
    //vel.limit(4);
    loc.add(vel);
  }
}

