/**
 * Standard Camera.
 * by Jean Pierre Charalambos.
 * 
 * A 'standard' Camera with fixed near and far planes.
 * 
 * Note that the precision of the z-Buffer highly depends on how the zNear()
 * and zFar() values are fitted to your scene (as it is done with the default PROSCENE
 * camera). Loose boundaries will result in imprecision along the viewing direction. //<>//
 * 
 * Press 't' in the main viewer (the upper one) to toggle the camera kind.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene, auxScene;
PGraphics canvas, auxCanvas;
StdCamera stdCam;
Camera origCam;

void setup() {
  size(640, 720, P3D);

  canvas = createGraphics(640, 360, P3D);
  scene = new Scene(this, canvas);

  stdCam = new StdCamera(scene);
  scene.setCamera(stdCam);

  scene.setRadius(200);
  scene.showAll();

  // enable computation of the frustum planes equations (disabled by default)
  scene.enableBoundaryEquations();
  scene.setGridVisualHint(false);
  scene.addGraphicsHandler(this, "mainDrawing");

  auxCanvas = createGraphics(640, 360, P3D);
  // Note that we pass the upper left corner coordinates where the scene
  // is to be drawn (see drawing code below) to its constructor.
  auxScene = new Scene(this, auxCanvas, 0, 360);
  auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
  auxScene.setAxesVisualHint(false);
  auxScene.setGridVisualHint(false);
  auxScene.setRadius(400);
  auxScene.showAll();
  auxScene.addGraphicsHandler(this, "auxiliarDrawing");
}

public void mainDrawing(Scene s) {
  PGraphics p = s.pg();
  p.background(0);
  p.noStroke();
  // the main viewer camera is used to cull the sphere object against its frustum
  switch (scene.ballVisibility(new Vec(0, 0, 0), scene.radius()*0.6f)) {
  case VISIBLE:
    p.fill(0, 255, 0);
    p.sphere(scene.radius()*0.6f);
    break;
  case SEMIVISIBLE:
    p.fill(255, 0, 0);
    p.sphere(scene.radius()*0.6f);
    break;
  case INVISIBLE:
    break;
  }
}

void auxiliarDrawing(Scene s) {
  mainDrawing(s);    
  s.pg().pushStyle();
  s.pg().stroke(255, 255, 0);
  s.pg().fill(255, 255, 0, 160);
  s.drawEye(scene.camera());
  s.pg().popStyle();
}

void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  scene.endDraw();
  canvas.endDraw();
  image(canvas, 0, 0);

  auxCanvas.beginDraw();
  auxScene.beginDraw();
  auxScene.endDraw();
  auxCanvas.endDraw();
  // We retrieve the scene upper left coordinates defined above.
  image(auxCanvas, auxScene.originCorner().x(), auxScene.originCorner().y());
}

void handleMouse() {
  if (mouseY < 360) {
    scene.enableMotionAgent();
    scene.enableKeyboardAgent();
    auxScene.disableMotionAgent();
    auxScene.disableKeyboardAgent();
  } 
  else {
    scene.disableMotionAgent();
    scene.disableKeyboardAgent();
    auxScene.enableMotionAgent();
    auxScene.enableKeyboardAgent();
  }
}

void keyPressed() {
  if (key == 't') {
    stdCam.toggleMode();
    this.redraw();
  }
  if ( key == 'u' )
    scene.eyeFrame().setMotionBinding(MouseAgent.WHEEL_ID, "translateZ");
  if ( key == 'v' )
    scene.eyeFrame().setMotionBinding(MouseAgent.WHEEL_ID, "scale");
}

public class StdCamera extends Camera {
  boolean standard;

  public StdCamera(Scene scn) {
    super(scn);
    // camera frame is a gFrame by default, but we want an iFrame
    // to bind 'u' and 'v' actions to it
    setFrame(new EyeFrame(this));
    standard = false;
  }

  public void toggleMode() {
    standard = !standard;
  }

  public boolean isStandard() {
    return standard;
  }

  @Override
  public float zNear() { 
    if (standard) 
      return 0.001f; 
    else 
      return super.zNear();
  }

  @Override
  public float zFar() {
    if (standard) 
      return 1000.0f; 
    else 
      return super.zFar();
  }

  @Override
  public float rescalingOrthoFactor() {
    if (isStandard())
      return 1.0f;
    return super.rescalingOrthoFactor();
  }
}