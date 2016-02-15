import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// Building_01.pde
World world;
Vector2D[] contour;
Building b;
BuildingPic bpic;

public void setup() {
  size(200, 200);  
  world = new World(200, 200);
  contour = new Vector2D[] {
    new Vector2D(65, 140), 
    new Vector2D(100, 160), 
    new Vector2D(160, 125), 
    new Vector2D(125, 85), 
    new Vector2D(115, 40), 
    new Vector2D(55, 40), 
    new Vector2D(70, 90)
    };
    b = new Building(contour);
  bpic = new BuildingPic(this, color(255, 255, 200), color(160, 160, 0), 2);
  b.renderer(bpic);
  world.add(b);
}

public void draw() {
  background(230);
  world.draw(0);
}

