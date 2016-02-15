/**
 * Matrix Shader
 * by Jean Pierre Charalambos.
 *
 * This examples shows how to bypass Processing matrix handling by using
 * Proscene's own matrix stack. Really useful if you plan to use Proscene
 * directly with jogl.
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene;
InteractiveFrame iFrame;
PShader prosceneShader;
Mat pmv;
PMatrix3D pmatrix = new PMatrix3D( );

void setup() {
  size(640, 360, P3D);
  prosceneShader = loadShader("FrameFrag.glsl", "FrameVert_pmv.glsl");
  scene = new Scene(this);
  scene.setMatrixHelper(new MatrixStackHelper(scene));
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));
}

void draw() {
  background(0);
  //discard Processing matrices
  resetMatrix();
  //set initial model-view and projection proscene matrices
  setUniforms();
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();
  scene.pushModelView();
  scene.applyModelView(iFrame.matrix());
  //iFrame.applyTransformation();//also possible here
  //model-view changed:
  setUniforms();
  if (scene.motionAgent().defaultGrabber() == iFrame) {
    fill(0, 255, 255);
    scene.drawTorusSolenoid();
  }
  else if (iFrame.grabsInput(scene.motionAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid();
  }
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid();
  } 
  scene.popModelView();
}

public void keyPressed() {
  if ( key == 'i')
    scene.motionAgent().setDefaultGrabber(scene.motionAgent().defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
}

//Whenever the model-view (or projection) matrices changes
// we need to update the shader:
void setUniforms() {
  shader(prosceneShader);
  pmv = Mat.multiply(scene.projection(), scene.modelView());
  pmatrix.set(pmv.get(new float[16]));
  prosceneShader.set("proscene_transform", pmatrix);
}
