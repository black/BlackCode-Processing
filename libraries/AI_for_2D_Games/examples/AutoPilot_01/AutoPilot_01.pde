import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// AutoPilot_01.pde
World world;
StopWatch sw;
Vehicle mover0;

public void setup() {
  size(480, 320);
  world = new World(width, height);
  sw = new StopWatch();
  // Create the mover
  mover0 = new Vehicle(
      new Vector2D(width/2, height/2), // position
      15,                              // collision radius
     new Vector2D(15, 15),            // velocity
      40,                              // maximum speed
      new Vector2D(1, 1),              // heading
      1,                               // mass
      0.5,                             // turning rate
      200                              // max force
  ); 
  // Start wandering
  mover0.AP().wanderOn().wanderFactors(60, 30, 20);
  // What does this mover look like
  ArrowPic view = new ArrowPic(this);    
  // Show wander behaviour hints
  view.showHints(Hints.HINT_WANDER);
  mover0.renderer(view);
  // Constrain movement
  Domain d = new Domain(0, 0, width, height);
  mover0.worldDomain(d, SBF.WRAP);
  // Finally we want to add this to our game domain
  world.add(mover0);
  sw.reset();
}

public void draw() {
  double elapsedTime = sw.getElapsedTime();
  world.update(elapsedTime);
  background(200, 255, 200);
  world.draw(elapsedTime);
}