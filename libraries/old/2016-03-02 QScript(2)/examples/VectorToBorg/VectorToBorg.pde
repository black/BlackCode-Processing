import shapes3d.utils.*;
import shapes3d.animation.*;
import shapes3d.*;

import org.qscript.eventsonfire.*;
import org.qscript.events.*;
import org.qscript.editor.*;
import org.qscript.*;
import org.qscript.operator.*;
import org.qscript.errors.*;

/**
 * Demonstration of QScript using vector math.
 * 
 * The QScript is to calculate the velocity vector for
 * the photon torpedo fired by the space station. 
 * Unfortunately it isn't always accurate and it misses
 * the Borg cube. Why not try improving it :)
 * 
 * The example uses the Shapes3D library.
 * 
 * @author Peter Lager 2014
 *
 */

String[] targetingScript = new String[] {
  "# Calculate the torpedo velocity to intercept the Borg ship", 
  "# The following variables must be initialised first", 
  "# start - vector of torpedo start position", 
  "# target - vector of Borg's position", 
  "# targetVel - vector of Borg's velocity", 
  "# speed - speed of photon torpedo", 
  "# -------------------------------", 
  "# Calculate time to impact if the Borg was stationary", 
  "timeToImpact = mag(target - start) / speed", 
  "# Borg position after time to impact", 
  "expectedPos = target + targetVel * timeToImpact", 
  "# Velocity vector for photon to be at expectedPos after time to impact", 
  "vel = norm(expectedPos - start) * speed", 
  "END(vel)"
};
// Position of space station
PVector pos = new PVector();
// Camera control vectors
PVector lookAt = new PVector();
PVector cam = new PVector();
PVector up = new PVector(0, 1, 0);
// Shapes for 
Toroid station;
Ellipsoid photon;
Ellipsoid stars;
Box borg;
PVector borgVel, photonVel;
boolean hitTarget = false;
int ctime, ltime;
float etime;
int state = 0;

String status = "TARGETING BORG SHIP";
String instruction = "Click mouse to fire torpedo";

public void setup() {
  size(300, 300, P3D);
  textSize(18);
  //hint(DISABLE_OPTIMIZED_STROKE);
  stars = new Ellipsoid(this, 10, 10);
  stars.setTexture("stars01.jpg", 5f, 2.5f);
  stars.drawMode(Shape3D.TEXTURE);
  stars.setRadius(3000);

  borg = new Box(this, 80);
  borg.setTexture("borg1.jpg");
  borg.drawMode(Shape3D.TEXTURE); 
  borg.moveTo(random3D(1000, 1500));
  borgVel = random3D(50, 70);

  station = new Toroid(this, 12, 48);
  station.setRadius(10, 8, 40);
  station.stroke(color(64, 60, 60));
  station.strokeWeight(0.1f);
  station.setTexture("station3.jpg", 6, 4);
  station.drawMode(Shape3D.TEXTURE); 

  photon = new Ellipsoid(this, 6, 18);
  photon.setRadius(4);
  photon.fill(color(200, 200, 0));
  photon.stroke(color(128, 0, 0));
  photon.strokeWeight(0.5f);
  photon.drawMode(Shape3D.SOLID | Shape3D.WIRE); 
  photon.visible(false);
  photonVel = new PVector();

  updateCamPosition(station.getPosVec(), borg.getPosVec());
  perspective(PI/3, (float)width/(float)height, 2, 5000);
  ctime = ltime = millis();
}

// Create a random 3D vector within a certain size range
PVector random3D(float min, float max) {
  PVector temp = PVector.random3D();
  temp.mult(random(min, max));
  return temp;
}

// Reset the game
void reset() {
  status = "TARGETING BORG SHIP";
  hitTarget = false;
  state = 0;
  borg.moveTo(random3D(1000, 1500));
  borgVel = random3D(30, 50);
  photon.visible(false);
  photon.moveTo(station.getPosVec());
  up = PVector.random3D();
  up.normalize();
  updateCamPosition(station.getPosVec(), borg.getPosVec());
}

// Calculate the camera position and direction
void updateCamPosition(PVector eye, PVector at) {
  cam = PVector.sub(eye, at);
  cam.normalize();
  cam.y += 0.2f;
  cam.mult(120);
  cam.add(eye);
  lookAt.set(at);
}

// Fire a photon torpedo if in 'ready' state
public void mouseClicked() {
  if (state == 0) {
    photon.moveTo(station.getPosVec());
    photonVel = calculateMissileVelocity(photon.getPosVec(), borg.getPosVec(), borgVel, 125);
    photon.visible(true);
    state = 1;
    status = "TRACKING PHOTON TORPEDO";
  }
}

// Is the torpedo centre inside the Borg cube
boolean hitBorg(PVector m, PVector b, float bs) {
  return abs(m.x-b.x) < bs && abs(m.y-b.y) < bs && abs(m.z-b.z) < bs;
}

// Allow player to reset after current play is doen
public void keyTyped() {
  if (state == 3 && (key == 'r' || key == 'R'))
    reset();
}

public void draw() {
  ctime = millis();
  etime = (ctime - ltime) * 0.001f;
  ltime = ctime;

  if (state == 0) {
    instruction = "Click mouse to fire torpedo";
  } else if (state == 1) { // Torpedo on its way
    instruction = "";
    // Check for collision
    if (hitBorg(photon.getPosVec(), borg.getPosVec(), 45)) {
      photon.visible(false);
      status = "BORG HIT";
    }
    // Check if missile has passed by Borg
    float d0 = PVector.dist(station.getPosVec(), borg.getPosVec());
    float d1 = PVector.dist(station.getPosVec(), photon.getPosVec());
    if (d1 - d0 > 300) {
      if (photon.visible()) {
        photon.visible(false);
        status = "BORG MISSED";
      }
      photonVel.set(0, 0, 0);
      state = 2;
    }
  } else if (state == 2) { // torpedo has either hit or missed the Borg
    instruction = "Press R to reset";
    state = 3;
  }
  updateCamPosition(photon.getPosVec(), borg.getPosVec());
  // Update borg
  borg.moveBy(PVector.mult(borgVel, etime));
  // Update missile
  photon.moveBy(PVector.mult(photonVel, etime));
  // Keep stars same distance from viewer
  stars.moveTo(photon.getPosVec());

  background(0, 0, 64);

  pushMatrix();
  camera(cam.x, cam.y, cam.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
  borg.draw();
  station.draw();
  photon.draw();
  stars.draw();
  popMatrix();
  fill(255);
  text(status, (width - textWidth(status))/2, 30);
  text(instruction, (width - textWidth(instruction))/2, height - 20);
}

// The main method that evaluates the script when a photon
// torpedo is fired.
public PVector calculateMissileVelocity(PVector start, PVector target, PVector targetVel, float speed) {
  Script script = new Script(targetingScript);
  script.storeVariable("start", toVector(start));
  script.storeVariable("target", toVector(target));
  script.storeVariable("targetVel", toVector(targetVel));
  script.storeVariable("speed", speed);
  Result r = Solver.evaluate(script);
  return toPVector(r.answer.toVector());
}

// Create a PVector from QScript's Vector
PVector toPVector(Vector v) {
  return new PVector((float)v.x, (float)v.y, (float)v.z);
}

// Create a  QScript Vector from a PVector
Vector toVector(PVector pv) {
  return new Vector(pv.x, pv.y, pv.z);
}

