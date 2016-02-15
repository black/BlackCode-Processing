import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// Building_02.pde
World world;
Vector2D[] contour;
Building[] bs;
BuildingPic bpic;

public void setup() {
  size(200, 200);  
  world = new World(200, 200);
  bs = Building.makeFromXML(this, "building.xml");
  bpic = new BuildingPic(this, color(255, 255, 200), color(160, 160, 0), 2);
  bs[0].renderer(bpic);
  world.add(bs[0]);
}

public void draw() {
  background(230);
  world.draw(0);
}
