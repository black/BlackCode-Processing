/**
 In this sketch there are 2 GViewPeasyCam controls. Each one has its own
 PeasyCam which operates independantly.
 
 Listeners only report mouse entered, exited and clicked evnets the rest 
 are handled by PeasyCam.
 
 The website has a full description of the source code in this sketch and 
 can be seen here.
 
 http://lagers.org.uk/g4p/guides/g19-peasycamviews.html
 
 (c) 2019 Peter Lager
 
 */

import peasy.*;
import g4p_controls.*;

/**
 * Demonstrates use of GViewPeasyCam
 * 
 */

// PeasyCam views
GViewPeasyCam view1, view2;

// State data for the balls moving on main display
int MAX_BALLS = 50;
PVector[] pos;
PVector[] velocity;
float[] diam;
int[] cols;

void setup() {
  size(600, 340, P2D);
  surface.setTitle("G4P PeasyCam View");
  cursor(HAND);
  // Setup first GViewPeasyCam
  view1 = new GViewPeasyCam(this, 10, 30, 250, 250, 200);
  PeasyCam pcam = view1.getPeasyCam();
  pcam.setMinimumDistance(120);
  pcam.setMaximumDistance(400);
  // Setup second GViewPeasyCam
  view2 = new GViewPeasyCam(this, 290, 30, 300, 200, 400);
  pcam = view2.getPeasyCam();
  pcam.setMinimumDistance(220);
  pcam.setMaximumDistance(800);
  // Setup the main backdrop balls animation
  makeBalls();
  textSize(18);
}

void draw() {
  background(240, 240, 255);
  moveBalls();
  drawBalls();
  updateView2();
  updateView1();
}

void updateView1() {
  // ############################################################
  // Get the graphics context and camera
  PGraphics pg = view1.getGraphics();
  PeasyCam pcam = view1.getPeasyCam();
  // ############################################################
  // Initialise the canvas
  pg.beginDraw();
  pg.resetMatrix();
  // ############################################################
  // World view lighting here (optional)
  pg.ambientLight(100, 100, 100);
  pg.directionalLight(220, 220, 0, 0.8f, 1, -1.2f);

  // ############################################################
  // set model view - using camera state
  pcam.feed();

  // ############################################################
  // Model view lighting here (optional)

  // ############################################################
  // Code to draw canvas
  pg.background(120, 120, 0, 120);

  pg.fill(255, 200, 128);
  pg.stroke(255, 0, 0);
  pg.strokeWeight(4);
  pg.box(80);

  // ############################################################
  // Demonstrates use of the PeaseyCam HUD feature
  pcam.beginHUD();
  pg.rectMode(CORNER);
  pg.noStroke();
  pg.fill(0);
  pg.rect(0, 0, view1.width(), 30);
  pg.fill(255, 255, 0);
  pg.textSize(18);
  pg.textAlign(CENTER, CENTER);
  pg.text("Using Worldview lighting", 0, 0, view1.width(), 30);
  pcam.endHUD();

  // ############################################################
  // We are done!!!
  pg.endDraw();
}

void updateView2() {
  // ############################################################
  // Get the graphics context and camera
  PGraphics pg = view2.getGraphics();
  PeasyCam pcam = view2.getPeasyCam();
  // ############################################################
  // Initialise the canvas
  pg.beginDraw();
  pg.resetMatrix();
  // ############################################################
  // World view lighting here (optional)

  // ############################################################
  // set model view - using camera state
  pcam.feed();

  // ############################################################
  // Model view lighting here (optional)
  pg.ambientLight(140, 140, 140);
  pg.directionalLight(80, 80, 80, 0.8f, 1, -5.2f);

  // ############################################################
  // Code to draw canvas
  pg.background(20, 100, 20, 100);

  // BOX scene object
  pg.pushMatrix();
  pg.translate(-100, 0, 0);
  pg.fill(20, 116, 255);
  pg.stroke(0);
  pg.strokeWeight(0.3f);
  pg.box(100);
  pg.popMatrix();
  // SPHERE scene object
  pg.pushMatrix();
  pg.translate(100, 0, 0);
  pg.rotateX(PI/2);
  pg.fill(160, 255, 160);
  pg.noStroke();
  pg.sphere(100);
  pg.popMatrix();

  // ############################################################
  // Demonstrates use of the PeaseyCam HUD feature
  pcam.beginHUD();
  pg.rectMode(CORNER);
  pg.fill(0);
  pg.rect(0, 0, view2.width(), 30);
  pg.fill(0, 255, 0);
  pg.textSize(18);
  pg.textAlign(CENTER, CENTER);
  pg.text("Using Modelview lighting", 0, 0, view2.width(), 30);
  pcam.endHUD();

  // ############################################################
  // We are done!!!
  pg.endDraw();
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
    velocity[b].mult(random(0.8f, 2.0f));
    // Random size
    diam[b] = random(20, 100);
    // Random translucent colour
    cols[b] = color((int)random(340), 100, 100, 60);
  }
  colorMode(RGB, 255, 255, 255);
}
