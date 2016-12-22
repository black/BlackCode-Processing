Shooter S;
Shooter E;
void setup() {
  size(300, 300);
  S = new Shooter(100);
  E = new Shooter(80);
}
float k=0, kk=0;
void draw() {
  background(#FFD21A);
  translate(width>>1, height>>1);
  noFill();
  stroke(0);
  ellipse(0, 0, 2*100, 2*100);
  ellipse(0, 0, 2*80, 2*80);
  S.show();
  S.update(k); 
  S.shooting(shoot);
  E.show();
  E.update((random(100)<50 && millis()/500==0)?kk++:kk--); 
  E.shooting((random(100)<50 && millis()/500==0)?true:false);
  if (right)k++;
  if (left)k--;
}

boolean right=true, left, shoot;
void keyPressed() {
  if (key==CODED) {
    if (keyCode==RIGHT) {
      right=true;
      left=false;
    }
    if (keyCode==LEFT) {
      left=true;
      right=false;
    }
  }
  if (key==' ') {
    shoot =true;
  }
} 

void keyReleased() {
  shoot = false;
}
class Shooter {
  ArrayList<Bullet> poop = new ArrayList();
  PVector loc, dir;
  int R;
  Shooter(int R) {
    this.loc = new PVector(100, 0);
    this.R = R;
  }
  void show() {
    noStroke();
    fill(255, 0, 0);
    ellipse(loc.x, loc.y, 10, 10);
  }
  void update(float k) {
    float x = R*cos(radians(k));
    float y = R*sin(radians(k));
    loc = new PVector(x, y);
    stroke(0);
    line(loc.x, loc.y, 0, 0);
    dir = PVector.sub(new PVector(0, 0), loc);
    dir.normalize() ;
    dir.mult(3); // bullet speed
  }
  void shooting(boolean fire) {
    if (fire) {
      poop.add(new Bullet(new PVector(loc.x, loc.y), dir));
      shoot = false;
    }
    for (int i=0; i<poop.size (); i++) {
      Bullet B = poop.get(i);
      B.show();
      B.update();
      if (B.pos.x<-width/2 || B.pos.x>width/2 ||B.pos.y<-height/2 || B.pos.y>height/2)poop.remove(i);
    }
  }
}

class Bullet {
  PVector pos, dir;
  Bullet(PVector pos, PVector dir) {
    this.pos = pos;
    this.dir = dir;
  }
  void show() {
    noStroke();
    fill(-1);
    ellipse(pos.x, pos.y, 4, 4);
  }
  void update() {
    pos.add(dir);
  }
}

