/**
 * Application Control.
 * by Jean Pierre Charalambos.
 * 
 * This demo controls the shape and color of the scene torus and box using two interactive
 * frame instances, one displaying an ellipse and the other a rect.
 *
 * The behavior of an interactive frame may be customized eihter through external registration
 * of some key and mouse handler methods (such as with the ellipse frame) or through inheritance
 * (such as with the rect frame).
 * 
 * Click and drag the ellipse or the rect with the left mouse to control the torus and box
 * color and shape.
 * Press ' ' (the spacebar) to toggle the application canvas aid.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.bias.event.*;
import remixlab.proscene.*;

int                w       = 200;
int                h       = 120;
int                oX      = 640 - w;
int                oY      = 360 - h;
PGraphics          ctrlCanvas;
Scene              ctrlScene;
public PShape      eShape;
InteractiveFrame   e;
InteractiveRect    r;
PGraphics          canvas;
Scene              scene;
boolean            showControl  = true;

float  radiusX  = 40, radiusY = 40;
int    colour  = color(255, 0, 0);

public void setup() {
  size(640, 360, P2D);
  
  rectMode(CENTER);

  canvas = createGraphics(640, 360, P3D);
  scene = new Scene(this, canvas);

  ctrlCanvas = createGraphics(w, h, P2D);
  ctrlScene = new Scene(this, ctrlCanvas, oX, oY);

  e = new InteractiveFrame(ctrlScene);
  updateEllipse(e);
  e.setMotionBinding(this, MouseAgent.WHEEL_ID, "changeShape");
  e.setMotionBinding(this, LEFT, "changeShape");
  e.setClickBinding(this, LEFT, 1, "changeColor");
  e.setKeyBinding(this, 'x', "colorBlue");
  e.setKeyBinding(this, 'y', "colorRed");
  //ctrlScene.keyAgent().setDefaultGrabber(e);
  
  r = new InteractiveRect(ctrlScene);
  r.setMotionBinding(MouseAgent.WHEEL_ID, "changeShape");
  r.setMotionBinding(LEFT, "changeShape");
  r.setClickBinding(LEFT, 1, "changeColor");
  r.setKeyBinding('u', "colorBlue");
  r.setKeyBinding('v', "colorRed");
  ctrlScene.keyAgent().setDefaultGrabber(r);
}

public void changeShape(InteractiveFrame frame, DOF1Event event) {
  radiusX += event.dx()*5;
  updateEllipse(frame);
}

public void changeShape(InteractiveFrame frame, DOF2Event event) {
  radiusX += event.dx();
  radiusY += event.dy();
  updateEllipse(frame);
}

public void changeColor(InteractiveFrame frame) {
  colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
  updateEllipse(frame);
}

public void colorBlue(InteractiveFrame frame) {
  colour = color(0, 0, 255);
  updateEllipse(frame);
}

public void colorRed(InteractiveFrame frame) {
  colour = color(255, 0, 0);
  updateEllipse(frame);
}

public void updateEllipse(InteractiveFrame frame) {
  frame.setShape(createShape(ELLIPSE, -60, 0, 2 * radiusX, 2 * radiusY));
  frame.shape().setFill(color(colour));
}

public void draw() {
  handleAgents();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(255);
  canvas.fill(colour);
  canvas.pushMatrix();
  canvas.translate(-80,0,0);
  scene.drawTorusSolenoid((int) map(PI * radiusX * radiusY, 20, w * h, 2, 50), 100, radiusY/2, radiusX/2);
  canvas.popMatrix();
  canvas.pushMatrix();
  canvas.translate(80,0,0);
  canvas.fill(r.colour);
  canvas.box(r.halfWidth, r.halfHeight, (r.halfWidth + r.halfHeight) / 2);
  canvas.popMatrix();
  scene.endDraw();
  canvas.endDraw();
  image(canvas, scene.originCorner().x(), scene.originCorner().y());
  if (showControl) {
    ctrlCanvas.beginDraw();
    ctrlScene.beginDraw();
    ctrlCanvas.background(125, 125, 125, 125);
    ctrlScene.drawFrames();
    ctrlScene.endDraw();
    ctrlCanvas.endDraw();
    image(ctrlCanvas, ctrlScene.originCorner().x(), ctrlScene.originCorner().y());
  }
}

void handleAgents() {
  scene.enableMotionAgent();
  ctrlScene.disableMotionAgent();
  scene.enableKeyboardAgent();
  ctrlScene.disableKeyboardAgent();
  if ((oX < mouseX) && (oY < mouseY) && showControl) {
    scene.disableMotionAgent();
    ctrlScene.enableMotionAgent();
    scene.disableKeyboardAgent();
    ctrlScene.enableKeyboardAgent();
  }
}

public void keyPressed() {
  if (key == ' ')
    showControl = !showControl;
}