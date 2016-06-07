/**
 * Mini Map
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to use proscene off-screen rendering to build
 * a mini-map of the main Scene where all objetcs are interactive. It also
 * shows Frame syncing among views. 
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 * Press 'x' and 'y' to change the mini-map eye representation.
 */

import remixlab.proscene.*;

Scene scene, auxScene;
PGraphics canvas, auxCanvas;  
InteractiveFrame frame1, auxFrame1, frame2, auxFrame2, frame3, auxFrame3;
InteractiveFrame iFrame;

int                w       = 200;
int                h       = 120;
int                oX      = 640 - w;
int                oY      = 360 - h;
boolean            showMiniMap  = true;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P2D;
public void setup() {
  size(640, 360, renderer);
  canvas = createGraphics(640, 360, renderer);
  scene = new Scene(this, canvas);
  scene.setPickingVisualHint(true);
  frame1 = new InteractiveFrame(scene);
  frame1.translate(30, 30);
  frame2 = new InteractiveFrame(scene, frame1);
  frame2.translate(40, 0);
  frame3 = new InteractiveFrame(scene, frame2);
  frame3.translate(40, 0);

  auxCanvas = createGraphics(w, h, renderer);
  auxScene = new Scene(this, auxCanvas, oX, oY);
  auxScene.setRadius(200);
  auxScene.showAll();

  auxFrame1 = new InteractiveFrame(auxScene);
  auxFrame1.translate(30, 30);
  auxFrame2 = new InteractiveFrame(auxScene, auxFrame1);
  auxFrame2.translate(40, 0);
  auxFrame3 = new InteractiveFrame(auxScene, auxFrame2);
  auxFrame3.translate(40, 0);

  iFrame = new InteractiveFrame(auxScene);
  iFrame.fromFrame(scene.eyeFrame());
  handleAgents();
}

public void customDrawing(PGraphics pg) {
  if(auxScene.is3D())
    pg.box(200);
  else {
    pg.pushStyle();
    pg.rectMode(CENTER);
    pg.rect(0, 0, 200, 200);
    pg.popStyle();
  }
}

public void draw() {
  handleAgents();
  InteractiveFrame.sync(scene.eyeFrame(), iFrame);
  InteractiveFrame.sync(frame1, auxFrame1);
  InteractiveFrame.sync(frame2, auxFrame2);
  InteractiveFrame.sync(frame3, auxFrame3);
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(0);
  mainDrawing(scene);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, 0, 0);
  if (showMiniMap) {
    auxCanvas.beginDraw();
    auxScene.beginDraw();
    auxCanvas.background(29,153,243);
    auxScene.pg().pushStyle();
    auxScene.pg().stroke(255, 255, 0);
    auxScene.pg().fill(255, 255, 0, 160);
    iFrame.draw();
    auxScene.pg().popStyle();
    auxDrawing(auxScene);
    auxScene.endDraw();
    auxCanvas.endDraw();
    // We retrieve the scene upper left coordinates defined above.
    image(auxCanvas, auxScene.originCorner().x(), auxScene.originCorner().y());
  }
}

public void mainDrawing(Scene s) {  
  s.pg().pushStyle();
  s.pushModelView();
  //the 'correct way' would be:
  //s.applyTransformation(s == scene ? frame1 : auxFrame1);
  //but we do the dirty way, since we can apply either frame to either scene:
  s.applyTransformation(frame1);
  s.drawAxes(40);
  //Note that each frame is registered at a different scene. So if we want to pick the frame in either scene:
  if ((s.motionAgent().trackedGrabber() == frame1) || (s.motionAgent().trackedGrabber() == auxFrame1))
    s.pg().fill(255, 0, 0);
  else 
  s.pg().fill(0, 0, 255);
  s.pg().rect(0, 0, 40, 10, 5);

  s.pushModelView();
  //we do it the correct way:
  s.applyTransformation(s == scene ? frame2 : auxFrame2);
  //also possible would be:
  //s.applyTransformation(frame2);
  s.drawAxes(40);
  //Note that each frame is registered at a different scene. So if we want to pick the frame in either scene:
  if ((s.motionAgent().trackedGrabber() == frame2) || (s.motionAgent().trackedGrabber() == auxFrame2))
    s.pg().fill(255, 0, 0);
  else
    s.pg().fill(255, 0, 255);
  s.pg().rect(0, 0, 40, 10, 5);

  s.pushModelView();
  //we do it the dirty way:
  s.applyTransformation(frame3);
  s.drawAxes(40);
  //Note that each frame is registered at a different scene. So if we want to pick the frame in either scene:
  if ((s.motionAgent().trackedGrabber() == frame3) || (s.motionAgent().trackedGrabber() == auxFrame3))
    s.pg().fill(255, 0, 0);
  else 
  s.pg().fill(0, 255, 255);
  s.pg().rect(0, 0, 40, 10, 5);

  s.popModelView();
  s.popModelView();
  s.popModelView();
  s.pg().popStyle();
}

public void auxDrawing(Scene s) {
  mainDrawing(s);
  s.pg().pushStyle();
  s.pg().stroke(255, 255, 0);
  s.pg().fill(255, 255, 0, 160);
  s.pg().popStyle();
}

void handleAgents() {
  scene.enableMotionAgent();
  auxScene.disableMotionAgent();
  scene.enableKeyboardAgent();
  auxScene.disableKeyboardAgent();
  if ((oX < mouseX) && (oY < mouseY) && showMiniMap) {
    scene.disableMotionAgent();
    auxScene.enableMotionAgent();
    scene.disableKeyboardAgent();
    auxScene.enableKeyboardAgent();
  }
}

public void keyPressed() {
  if(key == ' ')
    showMiniMap = !showMiniMap;
  if(key == 'x')
    iFrame.addGraphicsHandler(this, "customDrawing");
  if(key == 'y')
    iFrame.addGraphicsHandler(scene.eyeFrame());
}