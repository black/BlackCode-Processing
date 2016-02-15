import net.silentlycrashing.gestures.*;

/**
 * A simple test for the <a href="http://www.silentlycrashing.net/ezgestures/">ezGestures</a> library.
 * <p>Draw a figure-8 (starting at the top and going left). <br>
 * If your gesture is recognized, the sketch will draw an "8" on screen</p>
 * <p>by Elie Zananiri<br>
 * <a href="http://www.silentlycrashing.net/">silentlyCrashing::net</a></p>
 */
 
int IDLE = 0;
int LOOP_NE = 1;
int LOOP_S = 2;
int LOOP_NW = 3;

int RADIUS = 75;
int SPEED = 2;
int SIZE = 20;

GestureAnalyzer brain;
PostGestureListener figure8Ear;

float ang;
float state;

void setup() {
  size(400, 400);
  smooth();

  fill(247, 247, 0);
  noStroke();
  background(0);

  state = IDLE;

  // initialize the gesture listeners
  brain = new MouseGestureAnalyzer(this);
  brain.setVerbose(true);
  figure8Ear = new PostGestureListener(this, brain, "^(LDRDLURUL)$");
  figure8Ear.registerOnAction("startNWLoop", this);
}

void draw() {
  if (state == LOOP_NW) loopNW();
  else if (state == LOOP_S) loopS();
  else if (state == LOOP_NE) loopNE();
}

public void startNWLoop() {
  background(0);
  ang = -90;
  state = LOOP_NW;
}

public void loopNW() {
  ang -= SPEED;
  ellipse((width/2)+RADIUS*cos(radians(ang)), (height/2-RADIUS)+RADIUS*sin(radians(ang)), SIZE, SIZE);
  if (ang == -270) startSLoop();
}

public void startSLoop() {
  ang = -90;
  state = LOOP_S;
}

public void loopS() {
  ang += SPEED;
  ellipse((width/2)+RADIUS*cos(radians(ang)), (height/2+RADIUS)+RADIUS*sin(radians(ang)), SIZE, SIZE);
  if (ang == 270) startNELoop();
}

public void startNELoop() {
  ang = 90;
  state = LOOP_NE;
}

public void loopNE() {
  ang -= SPEED;
  ellipse((width/2)+RADIUS*cos(radians(ang)), (height/2-RADIUS)+RADIUS*sin(radians(ang)), SIZE, SIZE);
  if (ang == -90) state = IDLE;
}

