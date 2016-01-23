import toxi.physics2d.*;
import toxi.physics2d.behaviors.*;
import toxi.geom.*;

VerletPhysics2D physics;

Particle p1;
Particle p2;
Particle p3;
Particle p4;

void setup() {
  size(360, 580);
  physics=new VerletPhysics2D();
  physics.addBehavior(new GravityBehavior(new Vec2D(0, 0.5)));
  Vec2D center = new Vec2D(width/2, height/2);
  Vec2D extent = new Vec2D(width/2, height/2);
  physics.setWorldBounds(Rect.fromCenterExtent(center, extent));
  p1 = new Particle(100, 20, 15);
  p2 = new Particle(100, 180, 100);
  p3 = new Particle(20, 90, 10);
  p4 = new Particle(180, 90, 10);
  VerletSpring2D spring1=new VerletSpring2D(p1, p2, 80, 0.02);
  VerletSpring2D spring2=new VerletSpring2D(p3, p2, 80, 0.05);
  VerletSpring2D spring3=new VerletSpring2D(p4, p2, 80, 0.05);
  // Anything we make, we have to add into the physics world
  physics.addParticle(p1);
  physics.addParticle(p2);
  physics.addParticle(p3);
  physics.addParticle(p4);
  physics.addSpring(spring1);
  physics.addSpring(spring2);
  physics.addSpring(spring3);

  p1.lock();
  p3.lock();
  p4.lock();
}

void draw() {
  physics.update();
  background(#ffedbc);
  //if (key=='b')background(#A75265);
  //if (key=='c')background(#ec7263);
  //------boobs -----
  p1.display();
  p2.display();
  p3.display();
  p4.display();
  noFill();
  stroke(0, 50);
  strokeWeight(3);
  bezier(0, 0, p3.x-30, p3.y+50, p2.x+20, p2.y+p2.r, p4.x, p4.y);
  //----------Nippels -------
  strokeWeight(2);
  noFill();
  stroke(0, 50);
  ellipse( p2.x, p2.y+5, 25, 25);
  fill(0);
  noStroke();
  ellipse( p2.x, p2.y+5, 10, 10);
  // Move the second one according to the mouse
  if (mousePressed) {
    p2.lock();
    p2.x = mouseX;
    p2.y = mouseY;
    p2.unlock();
  }
}

