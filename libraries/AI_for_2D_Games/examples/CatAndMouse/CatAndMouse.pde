import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

final int SAFE_HERE    = 101;
final int AFTER_YOU    = 102;
final int DIE         = 103;
final int NEXT_MOUSE  = 104;

final int NBR_MICE    = 3;

final float CAT_WANDER_SPEED  = 70;  
final float CAT_CHASE_SPEED    = 160;  
final float CAT_SEEK_SPEED    = 110;  

final float MOUSE_WANDER_SPEED  = 50;  
final float MOUSE_EVADE_SPEED  = 90;

// 4 states for the cat
CatWanderState catWanderState = new CatWanderState();
ChaseMouseState chaseMouseState = new ChaseMouseState();
SeekMouseState seekMouseState = new SeekMouseState();
KillMouseState killMouseState = new KillMouseState();
// 3 states for the mouse
MouseGlobalState mouseGlobalState = new MouseGlobalState();
MouseWanderState mouseWanderState = new MouseWanderState();
EvadeCatState evadeCatState = new EvadeCatState();

World world;
StopWatch sw;
Cat cat;
Mouse[] mice = new Mouse[NBR_MICE];

float killDistSq;
String legend;
float legendOffsetX;

// Cat and Mouse Simulator
public void setup() {
  size(600, 600);
  world = new World(width, height);
  textSize(12);
  legend = "HINTS [1]-Heading  [2]-Velocity  [3]-Wander  [4]-View  [5]-Obstacle detection  [0]-All off";
  legendOffsetX = (width - textWidth(legend))/2;
  createBarrels();
  createCat();
  createMice();

  reset();
  killDistSq = (float) (cat.colRadius() + mice[0].colRadius());
  killDistSq *= (1.2f * killDistSq);

  sw = new StopWatch();
}

public void draw() {
  double elapsedTime = sw.getElapsedTime();
  world.update(elapsedTime);
  background(200);
  fill(0);
  text(legend, legendOffsetX, height - 6);
  world.draw(elapsedTime);
}

public void keyTyped() {
  int selectedHint = -1;
  switch(key) {
  case '1':
    selectedHint = Hints.HINT_HEADING;
    break;
  case '2':
    selectedHint = Hints.HINT_VELOCITY;
    break;
  case '3':
    selectedHint = Hints.HINT_WANDER;
    break;
  case '4':
    selectedHint = Hints.HINT_VIEW;
    break;
  case '5':
    selectedHint = Hints.HINT_OBS_AVOID;
    break;
  case '0':
    selectedHint = 0;
    break;
  }
  if (selectedHint == 0) { // remove all hints
    cat.renderer().showHints(selectedHint);
    for (Mouse mouse : mice)
      mouse.renderer().showHints(selectedHint);
  }
  else if (selectedHint > 0) { // toggle hint selected
    boolean hintOn = (cat.renderer().getHints() & selectedHint) == selectedHint;
    if (hintOn) {
      cat.renderer().removeHints(selectedHint);
      for (Mouse mouse : mice)
        mouse.renderer().removeHints(selectedHint);
    }
    else {
      cat.renderer().addHints(selectedHint);
      for (Mouse mouse : mice)
        mouse.renderer().addHints(selectedHint);
    }
  }
}

public void createBarrels() {
  int innerCol = color(233, 160, 92);
  int outerCol = color(127, 55, 7);
  float border = 3.2f;
  ObstaclePic view;
  view = new ObstaclePic(this, innerCol, outerCol, border);

  Obstacle[] barrels = Obstacle.makeFromXML(this, "cm_obstacles.xml");
  for (Obstacle barrel : barrels) {
    barrel.renderer(view);
    world.add(barrel);
  }
}

public void createMice() {
  for (int i = 0; i < mice.length; i++) {
    Mouse mouse = new Mouse(Vector2D.ZERO, // position
    10, // collision radius
    Vector2D.ZERO, // velocity
    MOUSE_WANDER_SPEED, // maximum speed
    Vector2D.random(null), // heading
    1, // mass
    2.5f, // turning rate
    2000 // max force
    ); 
    Domain mouseDomain = new Domain(-10, -10, width+10, height+10);
    mouse.worldDomain(mouseDomain, SBF.WRAP);
    mouse.viewFactors(100, 0.9f*PApplet.TWO_PI);
    mouse.renderer(new MousePic(this, 20));
    mouse.AP().wanderFactors(null, 30, 30);
    mouse.AP().obstacleAvoidDetectBoxLength(30);
    mouse.FSM().setGlobalState(mouseGlobalState);
    mouse.FSM().changeState(mouseWanderState);
    world.add(mouse);
    mice[i] = mouse;
  }
}

public void createCat() {
  cat = new Cat(Vector2D.ZERO, // position
  20, // collision radius
  Vector2D.ZERO, // velocity
  CAT_WANDER_SPEED, // maximum speed
  Vector2D.random(null), // heading
  1.5, // mass
  2.5f, // turning rate
  2500 // max force
  ); 
  Domain catDomain = new Domain(12, 12, width-12, height-12);
  cat.worldDomain(catDomain, SBF.REBOUND);
  cat.viewFactors(260, PApplet.TWO_PI/7);
  cat.renderer(new CatPic(this, 40));
  cat.AP().wanderFactors(100, 40, 20);
  cat.AP().obstacleAvoidDetectBoxLength(40);
  world.add(cat);
}

public void reset() {
  // Remove all pending births and deaths
  //    world.cancelBirthsAndDeaths();
  Vector2D pos;
  // Starting position for the cat
  pos = Vector2D.random(null).mult(50);
  cat.moveTo(pos.x + 150, pos.y + 150);
  cat.velocity(0, 0);
  cat.AP().obstacleAvoidOn();
  cat.FSM().changeState(catWanderState);
  cat.miceKilled = 0;
  world.add(cat);
  // Starting positions for mice
  int[] dx = { 
    150, 450, 450
  };
  int[] dy = { 
    450, 450, 150
  };
  for (int i = 0; i < mice.length; i++) {
    pos = Vector2D.random(null).mult(50);
    mice[i].alive = true;
    mice[i].moveTo(pos.x + dx[i], pos.y + dy[i]);
    mice[i].velocity(0, 0);
    mice[i].AP().obstacleAvoidOn();
    mice[i].FSM().changeState(mouseWanderState);
    world.add(mice[i]);
  }
}





