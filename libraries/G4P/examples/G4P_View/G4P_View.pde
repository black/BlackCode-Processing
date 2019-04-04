/**
 This sketch demonstrates how you can use the GView control without
 attaching a listener.
 
 If you are upgrading an old sketch that uses the GSketchpad control
 then you can use this approach instead.
 
 (c) 2018 Peter Lager
 
 */

import g4p_controls.*;

GView view2D;

// State data for the balls moving inside the viewy
int MAX_BALLS = 50;
PVector[] pos;
PVector[] velocity;
float[] diam;
int[] cols;
boolean moveBalls = true;

public void setup() {
  size(480, 320, P2D);
  surface.setTitle("G4P view control with no listener");
  // Setup 2D view
  view2D = new GView(this, 30, 20, width - 60, height - 60, JAVA2D);
  makeBalls();
  textSize(16);
  textAlign(CENTER);
}

public void draw() {
  background(240, 240, 255);
  if (moveBalls) {
    // move the balls to a new position
    moveBalls();
    // only need to update the view if the balls have moved!
    updateView();
  }
  fill(0);
  text("Use the S key to pause / restart balls", 30, height - 30, width - 60, 30);
}

public void keyReleased() {
  // Toggle ball movement
  if (key == 's') {
    moveBalls = ! moveBalls;
  }
}

// Update the view graphic
public void updateView() {
  PGraphics v = view2D.getGraphics();
  v.beginDraw();
  v.background(200);
  v.noStroke();
  for (int b = 0; b < MAX_BALLS; b++) {
    v.fill(cols[b]);
    v.ellipse(pos[b].x, pos[b].y, diam[b], diam[b]);
  }
  v.endDraw();
}

/**
 * Move the balls in the direction and magnitude of velocity
 */
void moveBalls() {
  for (int b = 0; b < MAX_BALLS; b++) {
    // Calculate the new X position wrapping round the edges if necessary
    float b_width = view2D.width() + 2 * diam[b];
    float nx = ((pos[b].x + diam[b] + velocity[b].x + b_width) % b_width) - diam[b];
    // Calculate the new Y position wrapping round the edges if necessary
    float b_height = view2D.height() + 2 * diam[b];
    float ny = ((pos[b].y + diam[b] + velocity[b].y + b_height) % b_height)  - diam[b];
    // Move the ball to the new position
    pos[b].set(nx, ny);
  }
}

/**
 * Draws the balls at their current location in the view.
 */
void drawBalls() {
  noStroke();
  for (int b = 0; b < MAX_BALLS; b++) {
    fill(cols[b]);
    ellipse(pos[b].x, pos[b].y, diam[b], diam[b]);
  }
}

/**
 * Make a number of balls with random colour, size, position and velocity
 */
void makeBalls() {
  pos = new PVector[MAX_BALLS];
  velocity = new PVector[MAX_BALLS];
  diam = new float[MAX_BALLS];
  cols = new int[MAX_BALLS];
  colorMode(HSB, 360, 100, 100);
  for (int b = 0; b < MAX_BALLS; b++) {
    // Random starting position
    pos[b] = new PVector(random(view2D.width()), random(view2D.height()));
    // Random velocity
    float angle = random(TWO_PI);
    velocity[b] = new PVector(cos(angle), sin(angle));
    // 48 to 120 pixels per second, based on 60fps
    velocity[b].mult(random(0.8f, 2.0f));
    // Random size
    diam[b] = random(20, 100);
    // Random translucent colour
    cols[b] = color((int)random(340), 100, 100, 60);
  }
  colorMode(RGB, 255, 255, 255);
}
