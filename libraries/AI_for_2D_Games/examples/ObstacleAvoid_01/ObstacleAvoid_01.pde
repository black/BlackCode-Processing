import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// ObstacleAvoid_01.pde
int[] obs = new int[] {
  100, 100, 36, 
  200, 200, 32, 
  270, 70, 16, 
  380, 180, 37, 
  510, 110, 27, 
  520, 210, 23, 
  400, 80, 10, 
  90, 240, 6
};
World world;
Domain wd;
Vehicle tank;  
BitmapPic view;

StopWatch sw = new StopWatch();

public void setup() {
  size(600, 300);
  world = new World(width, height);
  wd = new Domain(0, 0, width, height);

  ObstaclePic obView = new ObstaclePic(this, color(0, 96, 0), color(0), 3);
  for (int i = 0; i < obs.length; i += 3) {
    Obstacle obstacle = new Obstacle(
    new Vector2D(obs[i], obs[i+1]), // position
    obs[i+2] // collision radius
    );
    obstacle.renderer(obView);
    world.add(obstacle);
  }

  // Create the tank
  tank = new Vehicle(
  new Vector2D(width/2, height/2), // position
  16, // collision radius
  new Vector2D(0, 0), // velocity
  60, // maximum speed
  new Vector2D(1, 1), // heading
  7, // mass
  1.5f, // turning rate
  1000 // max force
  ); 
  view = new BitmapPic(this, "tanks32_4.png", 8, 1);  
  view.showHints(Hints.HINT_HEADING | Hints.HINT_VELOCITY | Hints.HINT_OBS_AVOID);
  tank.renderer(view);
  tank.AP().obstacleAvoidOn().wanderOn();
  tank.AP().wanderFactors(60, 40, 10);
  tank.AP().obstacleAvoidDetectBoxLength(45);
  tank.worldDomain(wd, SBF.WRAP);
  world.add(tank);
}

public void draw() {
  double elapsedTime = sw.getElapsedTime();
  // Animate the tank image
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
  background(220, 255, 220);
  world.draw(elapsedTime);
}
