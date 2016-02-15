/**
 MarketPlace Patrol.
 This is the pre-cursor to the City Patrol sketch. <br>
 
 You can define the patrol route by clicking on the green
 dots. These are the nodes in the graph used for path finding.
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

import java.awt.Point;

World world;
Domain wd;

Building[] buildings;
Obstacle[] stalls;
Vehicle[] tourists;
Vehicle[] patrol;
Graph routes;
GraphNode[] nodes;
GraphNode dest = null;
int[] stallCol1, stallCol2;
StopWatch watch;

int partSize = 200, partOverlap = 40;

public void setup() {
  size(700, 600);
  cursor(CROSS);
  stallCol1 = new int[] { 
    color(174, 44, 79), color(255, 227, 40)
  };
  stallCol2 = new int[] { 
    color(1, 8, 99), color(251, 190, 37), color(228, 4, 4), color(14, 143, 86)
  };

  world = new World(width, height, partSize, partOverlap);
  world.noOverlap(true);

  wd = new Domain(0, 0, width, height);
  // Get the navigation map
  routes = Graph.makeFromXML(this, "d001_graph.xml");
  nodes = routes.getNodeArray();
  // Create the buildings
  buildings = Building.makeFromXML(this, "d001_buildings.xml");
  for (Building b : buildings) {
    BuildingPic bpic = new BuildingPic(this, color(200, 96, 96), color(0), 2.0f);
    b.renderer(bpic);
    world.add(b);
  }
  // Create the market stalls (round bits)
  stalls = Obstacle.makeFromXML(this, "d001_obstacles.xml");
  for (Obstacle stall : stalls) {
    Umbrella opic = new Umbrella(this, stall.colRadius(), stallCol2);
    stall.renderer(opic);
    world.add(stall);
  }
  // Create the patrol
  patrol = Vehicle.makeFromXML(this, "d001_patrol.xml");
  for (int i = 0;  i < patrol.length; i++) {
    PersonPic apic = new PersonPic(this, 10, color(160, 160, 255), color(160, 144, 70), color(0), 1);
    patrol[i].AP().wallAvoidOn().obstacleAvoidOn().wallAvoidFactors(3, 20, 2.8, true);
    if (i == 0) {
      apic.bodyFill(color(100, 100, 255));
      patrol[i].AP().pathOn().arriveWeight(900);
    }
    else {
      patrol[i].AP().offsetPursuitOn(patrol[0], Vector2D.sub(patrol[i].pos(), patrol[0].pos()));
      patrol[i].AP().offsetPursuitWeight(30);
    }
    patrol[i].forceRecorderOn();
    patrol[i].renderer(apic);
    patrol[i].worldDomain(wd);
    world.add(patrol[i]);
  }
  // Now create the tourists
  tourists = Vehicle.makeFromXML(this, "d001_people.xml");
  for (Vehicle tourist : tourists) {
    PersonPic apic = new PersonPic(this, 10, color(255, 160, 255), color(145, 64, 47), color(0), 1);
    tourist.renderer(apic);
    tourist.AP().wanderOn().wanderFactors(70, 30, 10);
    tourist.AP().wallAvoidOn().wallAvoidFactors(3, 16, 2.8, false);
    tourist.AP().obstacleAvoidOn();
    tourist.worldDomain(wd);
    world.add(tourist);
  }
  watch = new StopWatch(); // last thing to be done in setup
}

public void draw() {
  float deltaTime = (float) watch.getElapsedTime();
  background(220);
  showNodes();
  world.update(deltaTime);
  if (!patrol[0].AP().isPathOn())
    patrol[0].velocity(0, 0);
  world.draw(deltaTime);
}

public void mouseMoved() {
  dest = routes.getNodeNear(mouseX, mouseY, 0, 10);
}

public void mouseClicked() {
  if (dest != null)
    patrol[0].AP().pathAddToRoute(routes, dest);
}

public void showNodes() {
  pushStyle();
  fill(200, 128);
  noStroke();
  int r;
  ellipseMode(CENTER);
  for (GraphNode node : nodes) {
    if (dest == null || dest != node) {
      r = 8;
      fill(0, 200, 0);
    }
    else {
      r = 24;
      fill(0, 128, 0);
    }
    ellipse(node.xf(), node.yf(), r, r);
  }
  popStyle();
}