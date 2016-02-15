/**
 Flocking explorer <br>
 
 Demonstrates flocking behaviour and allows the user to experiment
 with changing the behaviour weights.
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

import g4p_controls.*;

float wanderDist = 100, wanderRadius = 30, wanderJitter = 6;
float initWander = 1, initFlock = 16;
float initAlign = 8, initCohesion = 2.2f, initSeparation = 1.3f;
float initWanderJitter = 6, initWanderRadius = 30, initWanderDist = 100;
float initMaxSpeed = 70, initNeighbourhood = 50;

final int NBR_BOIDS = 200;

World w;
Domain wd;
StopWatch sw = new StopWatch();

Vehicle boid;
Vehicle[] boids;
AutoPilot ap;
AutoPilot[] apArray;
BoidPic view;
float deltaTime;
int backcol, boidcol;
long count = 0;

  public void setup() {
    size(620, 540);
    createGUI();

    w = new World(width, height);
    wd = new Domain(0, 0, 400, 440);
    w.noOverlap(true);

    view = new BoidPic(this, 8, color(220, 220, 255));  
    setColors(1);


    // Create a suitable AutoPilot for the boids
    ap = new AutoPilot();
    ap.wanderOn().flockOn();
    ap.wanderFactors(initWanderDist, initWanderRadius, initWanderJitter).flockFactors(50);
    ap.wanderWeight(initWander).flockWeight(initFlock);
    ap.alignmentWeight(initAlign).cohesionWeight(initCohesion).separationWeight(initSeparation);

    makeBoids();
    randomiseBoids();
    for(Vehicle boid : boids)
      w.add(boid);
    frameRate(1000);
    sw.reset();
  }

  public void makeBoids(){
    // Create a suitable AutoPilot for the boids
    ap = new AutoPilot();
    ap.wanderOn().flockOn();
    ap.wanderFactors(initWanderDist, initWanderRadius, initWanderJitter).flockFactors(50);
    ap.wanderWeight(initWander).flockWeight(initFlock);
    ap.alignmentWeight(initAlign).cohesionWeight(initCohesion).separationWeight(initSeparation);


    boids = new Vehicle[NBR_BOIDS];
    apArray = new AutoPilot[NBR_BOIDS];
    for (int i = 0; i < NBR_BOIDS; i++) {
      boid = new Vehicle(new Vector2D(), // position
          5, // collision radius
          new Vector2D(), // velocity
          initMaxSpeed, // maximum speed (was 60)
          new Vector2D(), // heading
          1, // mass
          1, // turning rate
          400 // max force
          ); 
      boid.renderer(view);
      boids[i] = boid;
      apArray[i] = (AutoPilot) ap.clone();
      boid.AP(apArray[i]);
      boid.worldDomain(wd);
    }
  }
  
  public void randomiseBoids() {
    for (int i = 0; i < NBR_BOIDS; i++) {
      float x = (width - 220)/2 + random(-150, 150);
      float y = (height - 100)/2 + random(-150, 150);
      float dirX = (rnd(0, 1) < 0.5) ? -1 : 1;
      float dirY = (rnd(0, 1) < 0.5) ? -1 : 1;
      float vx = dirX * rnd(5, 10);
      float vy = dirY * rnd(5, 10);
      boids[i].moveTo(x, y);
      boids[i].heading(vx, vy);
      boids[i].velocity(vx, vy);
    }
  }


public void draw() {
  deltaTime = (float) sw.getElapsedTime();
  background(backcol);

  w.update(deltaTime);
  w.draw(deltaTime);

  fill(201, 206, 255);
  noStroke();
  rect(0, 440, width, height-440);

  if (++count % 100 == 0) {
    lblFPSvalue.setText(""+frameRate);
    lblCalcTimeValue.setText(""+ (float)w.worldUpdateTime + " s");
  }
}

public void changeWander() {
  for (int i = 0; i < NBR_BOIDS; i++) 
    apArray[i].wanderFactors(wanderDist, wanderRadius, wanderJitter);
}

public void changeNeighbourhood(float radius) {
  for (int i = 0; i < NBR_BOIDS; i++) 
    apArray[i].flockFactors(radius);
}

public void changeMaxSpeed(float radius) {
  for (int i = 0; i < NBR_BOIDS; i++) 
    boids[i].maxSpeed(radius);
}

public float rnd(float low, float high) {
  return (float)Math.random()*(high - low) + low;
}

public void setColors(int cols) {
  switch(cols) {
  case 2:
    backcol = color(32, 32, 0);
    boidcol = color(0, 255, 0); 
    break;
  case 3:
    backcol = color(200, 255, 200);
    boidcol = color(0); 
    break;
  case 4:
    backcol = color(255, 255, 200);
    boidcol = color(255, 0, 0); 
    break;
  default:
    backcol = color(0, 0, 64);
    boidcol = color(220, 220, 255);
  };
  view.setFill(boidcol);
}
