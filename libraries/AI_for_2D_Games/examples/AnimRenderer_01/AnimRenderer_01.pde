import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// AnimRenderer_01
World world;
StopWatch sw;
Vehicle tank;
Vector2D target = new Vector2D();
BitmapPic view;

public void setup() {
  size(600, 320);
  world = new World(width, height);
  sw = new StopWatch();
  // Create the mover
  tank = new Vehicle(new Vector2D(width/2, height/2), // position
  40, // collision radius
  new Vector2D(0, 0), // velocity
  40, // maximum speed
  new Vector2D(1, 0), // heading
  15, // mass
  1.5f, // turning rate
  1000 // max force
  ); 
  // What does this mover look like
  view = new BitmapPic(this, "tanks.png", 8, 1);    
  view.showHints(Hints.HINT_COLLISION | Hints.HINT_HEADING | Hints.HINT_VELOCITY);
  tank.renderer(view);
  // Finally we want to add this to our game domain
  world.add(tank);
  sw.reset();
}

public void draw() {
  double elapsedTime = sw.getElapsedTime();
  target.set(mouseX, mouseY);
  tank.AP().arriveOn(target);
  float speed = (float) tank.speed();
  float maxSpeed = (float) tank.maxSpeed();    
  if (speed > 1) {
    float newInterval = map(speed, 0, maxSpeed, 0.6f, 0.04f);
    view.setAnimation(newInterval, 1);
  }
  else {
    view.pauseAnimation();
  }
  world.update(elapsedTime);
  background(218, 140, 54);
  world.draw(elapsedTime);
}
