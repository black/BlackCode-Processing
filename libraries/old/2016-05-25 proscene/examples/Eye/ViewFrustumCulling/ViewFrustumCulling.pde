/**
 * View Frustum Culling.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates a basic view frustum culling implementation which is performed
 * by analytically solving the frustum plane equations.
 * 
 * A hierarchical octree structure is clipped against the camera's frustum clipping planes.
 * A second viewer displays an external view of the scene that exhibits the clipping
 * (using Scene.drawCamera() to display the frustum).
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

OctreeNode Root;
Scene scene, auxScene;
PGraphics canvas, auxCanvas;

public void setup() {
  size(640, 720, P3D);
  // declare and build the octree hierarchy
  Vec p = new Vec(100, 70, 130);
  Root = new OctreeNode(p, Vec.multiply(p, -1.0f));
  Root.buildBoxHierarchy(4);

  canvas = createGraphics(640, 360, P3D);
  scene = new Scene(this, canvas);
  scene.enableBoundaryEquations();
  scene.setGridVisualHint(false);

  auxCanvas = createGraphics(640, 360, P3D);
  // Note that we pass the upper left corner coordinates where the scene
  // is to be drawn (see drawing code below) to its constructor.
  auxScene = new Scene(this, auxCanvas, 0, 360);
  //auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
  auxScene.setAxesVisualHint(false);
  auxScene.setGridVisualHint(false);
  auxScene.setRadius(200);
  auxScene.showAll();
}

public void draw() {
  background(0);
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(0);
  Root.drawIfAllChildrenAreVisible(scene.pg(), scene.camera());
  scene.endDraw();
  canvas.endDraw();
  image(canvas, 0, 0);

  auxCanvas.beginDraw();
  auxScene.beginDraw();
  auxCanvas.background(0);
  Root.drawIfAllChildrenAreVisible(auxScene.pg(), scene.camera());
  auxScene.pg().pushStyle();
  auxScene.pg().stroke(255, 255, 0);
  auxScene.pg().fill(255, 255, 0, 160);
  auxScene.drawEye(scene.eye());
  auxScene.pg().popStyle();
  auxScene.endDraw();
  auxCanvas.endDraw();
  // We retrieve the scene upper left coordinates defined above.
  image(auxCanvas, auxScene.originCorner().x(), auxScene.originCorner().y());
}

public void handleMouse() {
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