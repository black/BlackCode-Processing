/**
 * Basic Use.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates a direct approach to use proscene by Scene proper
 * instantiation.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;
//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  size(640, 360, renderer);
  //Scene instantiation
  scene = new Scene(this);
  // when damping friction = 0 -> spin
  scene.eye().frame().setDampingFriction(0);
}

void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();
}

void keyPressed() {
  if(scene.eye().frame().dampingFriction() == 0)
    scene.eye().frame().setDampingFriction(0.5);
  else
    scene.eye().frame().setDampingFriction(0);
  println("Camera damping friction now is " + scene.eye().frame().dampingFriction());
}
