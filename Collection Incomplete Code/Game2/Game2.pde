ArrayList<Particle> poop = new ArrayList();
PVector loc;
void setup() {
  size(350, 600);
  loc = new PVector(width/2, height/2);
  poop.add(new Particle(random(width), 0));
}
int t=0;
void draw() {
  //background(-1);
  myCar();
  screenFlush(); 

  // translate(width-loc.x, width-loc.y);
  pointsGen();
  myCar();
  for (int i=0; i<poop.size (); i++) {
    Particle P  = poop.get(i);
    P.show();
    if (press) {
      if (loc.dist(P.loc)<50) { 
        loc = oRotate(loc, P.loc, PI/180);
      }
    } else {
      P.update();
    }
  }
}

void myCar() {
  noStroke();
  fill(#F2CB02);
  ellipse(loc.x, loc.y, 10, 10);
  //println(loc);
}

boolean press=false;
void mousePressed() {
  press = true;
}
void mouseReleased() {
  press = false;
}

void pointsGen() { 
  int time=second();
  println(time);
  if ( time%4==0 ) {
    poop.add(new Particle(random(width), 0));
    time=0;
    println( frameRate+ " framerate" );
  }
}

PVector oRotate(PVector v, PVector origin, float theta) {
  PVector rVector = PVector.sub(v, origin);
  rVector.rotate(theta); 
  return PVector.add(origin, rVector);
}

