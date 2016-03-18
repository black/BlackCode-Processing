/**
 * Visual Hints.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to customize proscene visual hints look and feel.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;
InteractiveAvatarFrame iFrame;
boolean displayPaths = true;
Point fCorner = new Point();

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(640, 360, renderer);
  scene = new CustomizedScene(this);
  iFrame = new InteractiveAvatarFrame(scene);
  iFrame.translate(new Vec(30, -30, 0));
  scene.setKeyboardShortcut('r', null);
  scene.setNonSeqTimers();
  scene.setVisualHints(Scene.AXES | Scene.GRID | Scene.PICKING );
  //create a eye path and add some key frames:
  //key frames can be added at runtime with keys [j..n]
  scene.eye().setPosition(new Vec(80,0,0));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  scene.eye().setPosition(new Vec(30,30,-80));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  scene.eye().setPosition(new Vec(-30,-30,-80));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  scene.eye().setPosition(new Vec(-80,0,0));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  //re-position the eye:
  scene.eye().setPosition(new Vec(0,0,1));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.showAll();
}

public void draw() {
  background(40);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid(2);

  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(Scene.toPMatrix(iFrame.matrix())); //is possible but inefficient
  iFrame.applyTransformation();//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxes(20);

  // Draw a second box
  if (scene.motionAgent().defaultGrabber() == iFrame) {
    fill(0, 255, 255);
    scene.drawTorusSolenoid(6, 10);
  }
  else if (iFrame.grabsInput(scene.motionAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid(8, 10);
  }
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid(6, 10);
  }
  popMatrix();
  drawPaths();
}

public void keyPressed() {
  if ( key == 'i')
    scene.motionAgent().setDefaultGrabber(scene.motionAgent().defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
  if(key == 'u')
    displayPaths = !displayPaths;
}

public void drawPaths() {
  if(displayPaths) {
    pushStyle();
    colorMode(RGB, 255);
    strokeWeight(3);
    stroke(220,0,220);
    scene.drawEyePaths();
    popStyle();
  }
  else
    scene.hideEyePaths();
}

void mousePressed() {
  fCorner.set(mouseX, mouseY);
}

public class CustomizedScene extends Scene {
  // We need to call super(p) to instantiate the base class
  public CustomizedScene(PApplet p) {
    super(p);
  }

  @Override
  protected void drawPickingHint() {
    pg().pushStyle();
    pg().colorMode(RGB, 255);
    pg().strokeWeight(1);
    pg().stroke(0,220,0);
    drawPickingTargets();
    pg().popStyle();
  }
  
  @Override
  protected void drawZoomWindowHint() {
    pg().pushStyle();
    float p1x = fCorner.x();
    float p1y = fCorner.y();
    float p2x = mouseX;
    float p2y = mouseY;
    beginScreenDrawing();
    pg().stroke(0, 255, 255);
    pg().strokeWeight(2);
    pg().noFill();
    pg().beginShape();
    vertex(p1x, p1y);
    vertex(p2x, p1y);
    vertex(p2x, p2y);
    vertex(p1x, p2y);
    pg().endShape(CLOSE);
    endScreenDrawing();
    pg().popStyle();
  }
  
  @Override
  protected void drawScreenRotateHint() {
    pg().pushStyle();
    float p1x = mouseX;
    float p1y = mouseY;
    Vec p2 = eye().projectedCoordinatesOf(anchor());
    beginScreenDrawing();
    pg().stroke(255, 255, 0);
    pg().strokeWeight(2);
    pg().noFill();
    line(p2.x(), p2.y(), p1x, p1y);
    endScreenDrawing();
    pg().popStyle();
  }
}
