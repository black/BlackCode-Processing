/**
 Wander Explorer <br>
 
 This demonstrates the wander behaviour and allows you
 to experiment in changing the wander factors.
 */

import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;


// Need G4P library
import g4p_controls.*;

import java.util.*;

World w;
Domain wd;
LinkedList<Vector2D> trail = new LinkedList<Vector2D>();
boolean trailVisible = true;
int maxTrailSize = 2000;
Vehicle v;
Picture view;
AutoPilot apWander;
StopWatch sw = new StopWatch();

public void setup() {
  size(600, 600, JAVA2D);
  createGUI(); // Create the GUI controls
  w = new World(width, height);
  wd = new Domain(20, 40, width-20, height-20);

  v = new Vehicle(new Vector2D(200, 200), // position
  10,  // collision radius
  new Vector2D(10, 10), // velocity
  40,  // maximum speed
  new Vector2D(1, 1), // heading
  1,   // mass
  1,   // turning rate
  200  // max force
  ); 
  view = new ArrowPic(this);    
  view.showHints(Hints.HINT_WANDER | Hints.HINT_HEADING | Hints.HINT_VELOCITY);

  v.renderer(view);
  v.worldDomain(wd);
  apWander = new AutoPilot();
  apWander.wanderOn();
  apWander.wanderFactors(sdrDist.getValueF(), sdrRadius.getValueF(), sdrJitter.getValueF());
  v.AP(apWander);
  // Add this mover to the world
  w.add(v);

  // Initialise the route
  maxTrailSize = sdrTrail.getValueI();
  trail.addLast(v.pos().get());

  frameRate(60);

  // Initialise the stop watch
  sw.reset();
}

public void draw() {
  double deltaTime = sw.getElapsedTime();
  background(160, 255, 160);
  noStroke();
  fill(220, 255, 220);
  rect((float)wd.lowX, (float)wd.lowY, (float)wd.width, (float)wd.height);
  drawTrail();
  w.update(deltaTime);
  w.draw(deltaTime);
}

public void drawTrail() {
  Vector2D curr, p0, p1;
  curr = v.pos();
  p0 = trail.getLast();
  if (!Vector2D.areEqual(curr, p0)) {
    trail.addLast(curr.get());
    while (trail.size ()> maxTrailSize)
      trail.removeFirst();
  }
  if (trailVisible) {
    stroke(60, 200, 60);
    strokeWeight(1);
    p0 = trail.getFirst();
    for (int i = 1; i < trail.size(); i++) {
      p1 = trail.get(i);
      if (p0.distanceSq(p1)< 10000)
        line((float)p0.x, (float)p0.y, (float)p1.x, (float)p1.y);
      p0 = p1;
    }
  }
}

public void updateWanderBits() {
  apWander.wanderFactors(sdrDist.getValueF(), sdrRadius.getValueF(), sdrJitter.getValueF());
}