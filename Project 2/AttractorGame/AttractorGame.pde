ArrayList<Particle> poop = new ArrayList();
ArrayList<Particle> addPoop = new ArrayList();

void setup() {
  size(800, 400, P3D);
}
void mouseDragged() {
  float r =random(1, 4);
  Particle P = new Particle((int)random(r, width-r), (int)random(r, height-r), 1, r, -1);
  poop.add(P);
}
void draw() {
  background(0);
  //---------------------------------
  for (int i=0; i<poop.size (); i++) {
    Particle P  =poop.get(i);
    P.show();
  }
  for (int i=0; i<addPoop.size (); i++) {
    Particle P  = addPoop.get(i);
    P.show();
    P.collisionDetection();
  }
  //----------------------------------
  for (int i=0; i<poop.size (); i++) {
    Particle P = poop.get(i);
    P.collisionDetection();
    for (int j=i; j<poop.size (); j++) {
      Particle K = poop.get(j);
      K.applyForce(P);
    }
  }
}


class Particle {
  PVector loc, vel, acc;
  int mass;
  float r, K=1;
  color c;
  Particle(int x, int y, int mass, float r, color c) {
    loc = new PVector(x, y);
    vel = new PVector(random(-0.05, 0.05), random(-0.05, 0.05));
    acc = new PVector(0, 0);
    this.c=c;
    this.r = r;
    this.mass = mass;
    K=K*((random(50)<25)?-1:1);
  }
  void show() {
    noStroke();
    if (K<0)fill(c);
    else fill(255, 0, 0);
    ellipse(loc.x, loc.y, 2*r, 2*r);
  }
  void applyForce(Particle P) {
    if (P!=this) {
      acc.mult(0);
      PVector dir = PVector.sub(P.loc, loc);
      PVector dirtemp = dir;
      float dists = P.loc.dist(loc);
      float force = (K*P.mass*mass)/sq(dir.mag());
      dir.normalize();
      dir.mult(force);
      acc.add(dir);
      vel.add(acc);
      vel.limit(0.05);
      loc.add(vel);
      println("dtemp " + abs(dir.mag()));
      println("di "+ dists);
      if (dists<50) {
        stroke(-1, 20);
        line(P.loc.x, P.loc.y, loc.x, loc.y);
      }
    }
  }

  void collisionDetection() {
    if (loc.x>width-r) {
      loc.x=width-r ;
      vel.x*=-1.0;
    } else if (loc.x<r) {
      loc.x=r ;
      vel.x*=-1.0;
    } else if (loc.y>height-r) { 
      loc.y = height-r;
      vel.y*=-1.0;
    } else if (loc.y<r) {
      loc.y=r;
      vel.y*=-1.0;
    }
  }
}

