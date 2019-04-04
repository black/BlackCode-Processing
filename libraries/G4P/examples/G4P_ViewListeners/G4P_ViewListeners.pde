/**
 In this sketch there are 2 GView controls, one 2D and one 3D view views. Both
 views have attached user-defined listeners so they can respond to the mouse.
 
 The user-defined listener classes are in the other 2 tabs.
 
 The website has a full description of the source code in this sketch and can 
 be seen here
 
 http://lagers.org.uk/g4p/guides/g13-views.html
 
 (c) 2018 Peter Lager
 
 */

import g4p_controls.*;

GButton btnLine, btnRotate, btnBalls;
GView view2D, view3D;
DrawLine viewer2D;
ShowBox viewer3D;

// State data for the balls moving on main display
int MAX_BALLS = 50;
PVector[] pos;
PVector[] velocity;
float[] diam;
int[] cols;
boolean moveBalls = true;

void settings() {
  size(600, 380, P2D);
}

void setup() {
  surface.setTitle("GView demonstration");
  //  cursor(CROSS);
  // Setup 2D view and viewer
  view2D = new GView(this, 30, 24, 260, 200, JAVA2D);
  viewer2D = new DrawLine();
  view2D.addListener(viewer2D);
  viewer2D.randomLine(); // Start with a line somewhere
  // Setup 3D view and viewer
  view3D = new GView(this, 340, 24, 200, 200, P3D);
  viewer3D = new ShowBox();
  view3D.addListener(viewer3D);
  // Buttons
  btnLine = new GButton(this, 30, 280, 260, 24, "Random Line");
  btnRotate = new GButton(this, 340, 280, 200, 24, "Reverse Rotation");
  btnBalls = new GButton(this, 280, 320, 180, 24, "Pause / Restart Balls");
  makeBalls();
}

void draw() {
  background(240, 240, 255);
  if (moveBalls) {
    moveBalls();
  }
  drawBalls();
  fill(0);
  textSize(16);
  textAlign(LEFT);
  text("2D View using JAVA2D", 30, 18);
  text("3D View using P3D", 340, 18);
  text("Sketch window using P2D", 50, 338);
  textSize(14);
  textAlign(CENTER);
  text("Press and drag the mouse to make a line", 30, 230, 200, 40);
  text("Click the mouse to stop/start rotation", 340, 230, 200, 40);
}

/**
 * Event handler for the 3 G4P buttons
 */
void handleButtonEvents(GButton button, GEvent event) { 
  if (button == btnLine) {
    viewer2D.randomLine();
  } else if (button == btnRotate) {
    viewer3D.reverseRotation();
  } else if (button == btnBalls) {
    moveBalls = !moveBalls;
  }
}

/**
 * Move the balls in the direction and magnitude of velocity
 */
void moveBalls() {
  for (int b = 0; b < MAX_BALLS; b++) {
    // Calculate the new X position wrapping round the edges if necessary
    float b_width = width + 2 * diam[b];
    float nx = ((pos[b].x + diam[b] + velocity[b].x + b_width) % b_width) - diam[b];
    // Calculate the new Y position wrapping round the edges if necessary
    float b_height = height + 2 * diam[b];
    float ny = ((pos[b].y + diam[b] + velocity[b].y + b_height) % b_height)  - diam[b];
    // Move the ball to the new position
    pos[b].set(nx, ny);
  }
}

/**
 * Draws the balls at their current location on the main display.
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
    pos[b] = new PVector(random(width), random(height));
    // Random velocity
    float angle = random(TWO_PI);
    velocity[b] = new PVector(cos(angle), sin(angle));
    // 48 to 120 pixels per second, based on 60fps
    velocity[b].mult(random(0.8, 2.0));
    // Random size
    diam[b] = random(20, 100);
    // Random translucent colour
    cols[b] = color((int)random(340), 100, 100, 60);
  }
  colorMode(RGB, 255, 255, 255);
}
