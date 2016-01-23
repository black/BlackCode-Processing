import ddf.minim.*;

Minim minim;
ArrayList<Particle> poop = new ArrayList();
Particle M;
AudioPlayer song, bgmusic, passingby;
void setup() {
  size(800, 400);
  minim = new Minim(this);
  song = minim.loadFile("veil.mp3");
  bgmusic = minim.loadFile("deepSpace.mp3");
  passingby = minim.loadFile("passingby.mp3");
  M = new Particle(width>>1, height>>1, 5, 50, -1);
}
void mouseDragged() {
  for (int i=0; i<20; i++) {
    Particle P = new Particle(mouseX, mouseY, 1, random(1, 3), #F2004D);
    poop.add(P);
  }
}
void draw() {
  background(0);
  fill(#F2DB00);
  text(poop.size(), width>>1, 30);
  for (int i=0; i<poop.size (); i++) {
    Particle P  =poop.get(i);
    P.show();
  }
  for (int i=0; i<poop.size (); i++) {
    Particle P  =poop.get(i);
    P.applyForce(M);
    P.collisionDetection();
    if (P.loc.dist(M.loc)<M.r) {
      M.mass+=P.mass;
      poop.remove(i);
      song.pause();
      song.rewind();
      song.play();
    } else if (P.loc.dist(M.loc)>M.r && P.loc.dist(M.loc)<M.r+50) {
      passingby.pause();
      passingby.rewind();
      passingby.play();
    }
  }
  M.show();
  //bgmusic.play();
}


class Particle {
  PVector loc, vel, acc;
  int mass, K=1;
  float r;
  color c;
  Particle(int x, int y, int mass, float r, color c) {
    loc = new PVector(x, y);
    vel = new PVector((pmouseX-mouseX)*random(-2, 2), (pmouseY-mouseY)*random(-2, 2));
    acc = new PVector(0, 0);
    this.c=c;
    this.r = r;
    this.mass = mass;
  }
  void show() {
    noStroke();
    fill(c);
    ellipse(loc.x, loc.y, 2*r, 2*r);
  }
  void applyForce(Particle M) {
    PVector dir = PVector.sub(M.loc, loc);
    float force = (K*M.mass*mass)/sq(dir.mag());
    dir.normalize();
    dir.mult(force);
    acc.add(dir);
    vel.add(acc);
    vel.limit(8);
    loc.add(vel);
    acc.mult(0);
  }

  void collisionDetection() {
    if (loc.x>width-r) {
      loc.x=width-r ;
      vel.x*=-1;
    } else if (loc.x<r) {
      loc.x=r ;
      vel.x*=-1;
    } else if (loc.y>height-r) { 
      loc.y = height-r;
      vel.y*=-1;
    } else if (loc.y<r) {
      loc.y=r;
      vel.y*=-1;
    }
  }
}

void keyPressed() {
  poop.clear();
  println(poop.size());
  M.mass =2;
}

