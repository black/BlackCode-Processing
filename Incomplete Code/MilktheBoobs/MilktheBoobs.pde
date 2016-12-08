import toxi.physics2d.*;
import toxi.physics2d.behaviors.*;
import toxi.geom.*;

VerletPhysics2D physics;

Particle l1, l2, l3, l4;
Particle r1, r2, r3, r4;

void setup() {
  size(360, 580);
  physics=new VerletPhysics2D();
  physics.addBehavior(new GravityBehavior(new Vec2D(0, 0.5)));
  Vec2D center = new Vec2D(width/2, height/2);
  Vec2D extent = new Vec2D(width/2, height/2);
  physics.setWorldBounds(Rect.fromCenterExtent(center, extent));

  setupLeftBoob();
  setupRightBoob();
}

void draw() {
  physics.update();
  background(#ffedbc);
  //if (key=='b')background(#A75265);
  //if (key=='c')background(#ec7263);
  leftBoob();
  rightBoob();
}
void setupLeftBoob() {
  l1 = new Particle(100, 20, 15, 1);
  l2 = new Particle(100, 180, 100, 2);
  l3 = new Particle(20, 90, 10, 3);
  l4 = new Particle(180, 90, 10, 4);
  VerletSpring2D spring1l=new VerletSpring2D(l1, l2, 80, 0.02);
  VerletSpring2D spring2l=new VerletSpring2D(l3, l2, 80, 0.05);
  VerletSpring2D spring3l=new VerletSpring2D(l4, l2, 80, 0.05);
  // Anything we make, we have to add into the physics world
  physics.addParticle(l1);
  physics.addParticle(l2);
  physics.addParticle(l3);
  physics.addParticle(l4);
  physics.addSpring(spring1l);
  physics.addSpring(spring2l);
  physics.addSpring(spring3l);

  l1.lock();
  l3.lock();
  l4.lock();
}
void setupRightBoob() {
  r1 = new Particle(100, 20, 15, 1);
  r2 = new Particle(width-100, 180, 100, 2); // mark
  r3 = new Particle(width-20, 90, 10, 3); // mark 
  r4 = new Particle(180, 90, 10, 4); 
  VerletSpring2D spring1r=new VerletSpring2D(r1, r2, 80, 0.02);
  VerletSpring2D spring2r=new VerletSpring2D(r3, r2, 80, 0.05);
  VerletSpring2D spring3r=new VerletSpring2D(r4, r2, 80, 0.05);
  // Anything we make, we have to add into the physics world
  physics.addParticle(r1);
  physics.addParticle(r2);
  physics.addParticle(r3);
  physics.addParticle(r4);
  physics.addSpring(spring1r);
  physics.addSpring(spring2r);
  physics.addSpring(spring3r);

  r1.lock();
  r3.lock();
  r4.lock();
}

void leftBoob() {
  //------boobs -----
  l1.display();
  l2.display();
  l3.display();
  l4.display();
  noFill();
  stroke(0, 50);
  strokeWeight(3);
  bezier(0, 0, l3.x-30, l3.y+50, l2.x+20, l2.y+l2.r, l4.x, l4.y);
  //----------Nippels -------
  strokeWeight(2);
  fill(#FFBCAF);
  noStroke();
  ellipse( l2.x, l2.y+5, 30, 30);
  fill(#B25E4D, 80);
  noStroke();
  ellipse( l2.x, l2.y+5, 10, 10);
  // Move the second one according to the mouse
  if (mousePressed) {
    l2.lock();
    l2.x = mouseX;
    l2.y = mouseY;
    l2.unlock();
  }
}

void rightBoob() {
  //------boobs -----
  r1.display();
  r2.display();
  r3.display();
  r4.display();
  noFill();
  stroke(0, 50);
  strokeWeight(3);
  // bezier(width, 0, r3.x-30, r3.y+50, r2.x+20, r2.y+r2.r, r4.x, r4.y);
  bezier(r4.x, r4.y, r2.x+20, r2.y+r2.r, r3.x-30, r3.y+50, width, 0);
  //----------Nippels -------
  strokeWeight(2);
  fill(#FFBCAF);
  noStroke();
  ellipse( r2.x, r2.y+5, 30, 30);
  fill(#B25E4D, 80);
  noStroke();
  ellipse( r2.x, r2.y+5, 10, 10);
  // Move the second one according to the mouse
  if (mousePressed) {
    r2.lock();
    r2.x = mouseX;
    r2.y = mouseY;
    r2.unlock();
  }
}

