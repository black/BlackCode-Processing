/**
 * Depth.
 * by Jean Pierre Charalambos.
 *
 * This example illustrates how to attach a PShape to an interactive frame.
 * PShapes attached to interactive frames can then be automatically picked
 * and easily drawn.
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;

PShader shader;
Scene scene;
boolean original = true;
color cols[];
float posns[];
InteractiveFrame[] models;

void setup() {
  size(350, 350, P3D);
  //Wierdly enough color.HSB breaks picking
  //colorMode(HSB, 255);
  cols = new color[100];
  posns = new float[300];
  for (int i = 0; i<100; i++) {
    posns[3*i]=random(-1000, 1000);
    posns[3*i+1]=random(-1000, 1000);
    posns[3*i+2]=random(-1000, 1000);
    cols[i]= color(random(255), random(255), random(255));
  }

  scene = new Scene(this);
  models = new InteractiveFrame[100];

  for (int i = 0; i < models.length; i++) {
    models[i] = new InteractiveFrame(scene, drawBox());
    models[i].translate(posns[3*i], posns[3*i+1], posns[3*i+2]);
    pushStyle();
    colorMode(HSB, 255);
    models[i].shape().setFill(cols[i]);
    popStyle();
  }

  scene.setRadius(1000);
  scene.showAll();

  shader = loadShader("depth.glsl");
  shader.set("maxDepth", scene.radius()*2);

  frameRate(1000);
}

void draw() {
  background(0);
  if (original)
    scene.drawFrames();
  else {
    scene.pg().shader(shader);
    scene.drawFrames();
    scene.pg().resetShader(); 
  }
}

PShape drawBox() {
  return createShape(BOX, 60);
}

void keyPressed() {
  original = !original;
}