/**
 * DOF.
 * by Jean Pierre Charalambos.
 *
 * This example illustrates how to attach a PShape to an interactive frame.
 * PShapes attached to interactive frames can then be automatically picked
 * and easily drawn.
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;

PShader depthShader, dofShader;
PGraphics srcPGraphics, depthPGraphics, dofPGraphics;
Scene scene;
color cols[];
float posns[];
InteractiveFrame[] models;
int mode = 2;

void setup() {
  size(350, 350, P3D);
  colorMode(HSB, 255);
  cols = new color[100];
  posns = new float[300];
  for (int i = 0; i<100; i++) {
    posns[3*i]=random(-1000, 1000);
    posns[3*i+1]=random(-1000, 1000);
    posns[3*i+2]=random(-1000, 1000);
    cols[i]= color(random(255), random(255), random(255));
  }

  srcPGraphics = createGraphics(width, height, P3D);
  scene = new Scene(this, srcPGraphics);
  models = new InteractiveFrame[100];

  for (int i = 0; i < models.length; i++) {
    models[i] = new InteractiveFrame(scene, boxShape());
    models[i].translate(posns[3*i], posns[3*i+1], posns[3*i+2]);
    models[i].shape().setFill(cols[i]);
  }

  scene.setRadius(1000);
  scene.showAll();

  depthShader = loadShader("depth.glsl");
  depthShader.set("maxDepth", scene.radius()*2);
  depthPGraphics = createGraphics(width, height, P3D);
  depthPGraphics.shader(depthShader);

  dofShader = loadShader("dof.glsl");
  dofShader.set("aspect", width / (float) height);
  dofShader.set("maxBlur", 0.015);  
  dofShader.set("aperture", 0.02);
  dofPGraphics = createGraphics(width, height, P3D);
  dofPGraphics.shader(dofShader);

  frameRate(1000);
}

void draw() {
  //same as: PGraphics pg = scene.pg();
  PGraphics pg = srcPGraphics;

  // 1. Draw into main buffer
  for (int i = 0; i < models.length; i++)
     models[i].shape().setFill(models[i].grabsInput() ? color(0,255,255) : cols[i]);
  pg.beginDraw();
  scene.beginDraw();
  pg.background(0);
  scene.drawFrames();
  scene.endDraw();
  pg.endDraw();

  // 2. Draw into depth buffer
  depthPGraphics.beginDraw();
  depthPGraphics.background(0);
  scene.drawFrames(depthPGraphics);
  depthPGraphics.endDraw();

  // 3. Draw destination buffer
  dofPGraphics.beginDraw();
  //dofPGraphics.background(0);
  dofShader.set("focus", map(mouseX, 0, width, -0.5f, 1.5f));
  dofShader.set("tDepth", depthPGraphics);    
  dofPGraphics.image(pg, 0, 0);
  dofPGraphics.endDraw();

  // display one of the 3 buffers
  if (mode==0)
    image(pg, 0, 0);
  else if (mode==1)
    image(depthPGraphics, 0, 0);
  else
    image(dofPGraphics, 0, 0);
}

PShape boxShape() {
  return createShape(BOX, 60);
}

void keyPressed() {
  if ( key=='0') mode = 0;
  if ( key=='1') mode = 1;
  if ( key=='2') mode = 2;
}