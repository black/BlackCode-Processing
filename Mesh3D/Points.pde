class Dot {
  PVector loc, vel, acc, temploc;
  float k, m, x, y;

  Dot(float x, float y, float z) {
    loc = new PVector(x, y, z);
    temploc = PVector.mult(loc, w);
    vel = new PVector(0, 0, 0);
    acc = new PVector(0, 0, 0);
    k = 0.05;
    m = 0.05 ;
    x = loc.x ;
    y = loc.y;
  }

  void applyForce(Dot M ) {
    println(M.loc.x, M.loc.y, M.loc.z);
    acc.mult(0);  
    if (M.loc!=loc) {
      PVector dir = PVector.sub(loc, M.loc);
      float d = dir.mag();
      float force = (k*m*M.m)/d*d;
      dir.normalize();
      dir.mult(force);
      acc.add(dir);
    }
  }

  void update() {
    vel.add(acc);
    vel.limit(0.5);
    loc.add(vel);
  }

  PVector[] check(int xx, int yy, int zz) {
    PVector[] P = new PVector[6];
    int t=0;
    for (int i=-1; i<2; i++) {
      for (int j=-1; j<2; j++) {
        for (int k=-1; k<2; k++) {
          if (i== 1 && j == 0 && k == 0  || 
            i==-1 && j == 0 && k == 0  ||
            i== 0 && j == 1 && k == 0  ||
            i== 0 && j ==-1 && k == 0  ||
            i== 0 && j == 0 && k ==-1  || 
            i== 0 && j == 0 && k == 1  ) {
            P[t] = new PVector(i+xx, j+yy, k+zz);
            t++;
          } else {
            continue;
          }
        }
      }
    }
    return P;
  }
}

