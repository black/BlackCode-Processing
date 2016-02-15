import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// WallAvoid_01.pde
World world;
Domain wd;
Vector2D[] contour;
Building[] bs;
BuildingPic bpic;
Vehicle tourist;
PersonPic touristPic;
StopWatch sw = new StopWatch();

public void setup() {
  size(600, 300);  
  world = new World(600, 300);
  wd = new Domain(0, 0, 600, 300);
  // Create the buildings
  bs = Building.makeFromXML(this, "building.xml");
  bpic = new BuildingPic(this, color(255, 255, 200), color(160, 160, 0), 2);
  for (int i = 0; i < bs.length; i++) {
    bs[i].renderer(bpic);
    world.add(bs[i]);
  }
  // Create the tourist
  tourist = new Vehicle(
      new Vector2D(300, 130),          // position
      10,                              // collision radius
     new Vector2D(15, 15),             // velocity
      40,                              // maximum speed
      new Vector2D(1, 1),              // heading
      1,                               // mass
      0.5,                             // turning rate
      800                              // max force
  ); 
  touristPic = new PersonPic(this, 10, color(160,160,255), color(160,144,70), color(0), 1);
  touristPic.addHints(Hints.HINT_WHISKERS);
  tourist.renderer(touristPic);
  tourist.worldDomain(wd, SBF.WRAP);
  tourist.AP().wanderOn().wallAvoidOn();
  tourist.AP().wallAvoidWeight(100).wallAvoidFactors(4, 16, 2.6, false);
  tourist.AP().wanderFactors(100, 30,10);
  world.add(tourist);
  sw.reset();
}

public void draw() {
  double elapsedTime = sw.getElapsedTime();
  world.update(elapsedTime);
  background(230);
  world.draw(elapsedTime);
}
