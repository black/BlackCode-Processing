/**
 * Mouse Move Came Rotate
 * by Jean Pierre Charalambos.
 *
 * Using a mouse move event (instead of a mouse drag) to perform proscene actions
 * requires a custom mouse agent. Here we show hoew simple is it to create one.
 * 
 * Press the space bar to change the mouse agent. 
 */

import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

Scene scene;
MouseMoveAgent customMouseAgent;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.enableBoundaryEquations();
  scene.setRadius(150);
  scene.showAll();
  customMouseAgent = new MouseMoveAgent(scene, "MyMouseAgent");
  switchAgents();
}

void draw() {	
  background(0);	
  noStroke();
  if ( scene.camera().ballVisibility(new Vec(0, 0, 0), 40) == Camera.Visibility.SEMIVISIBLE )
    fill(255, 0, 0);
  else
    fill(0, 255, 0);
  sphere(40);
}

void switchAgents() {
  if( scene.isMotionAgentEnabled() ) {
    scene.disableMotionAgent();
    scene.inputHandler().registerAgent(customMouseAgent);
    registerMethod("mouseEvent", customMouseAgent);
  }
  else {
    scene.inputHandler().unregisterAgent(customMouseAgent);
    unregisterMethod("mouseEvent", customMouseAgent);
    scene.enableMotionAgent();
  }
}

public void keyPressed() {
  // We switch between the default mouse agent and the one we created:
  if ( key == ' ')
    switchAgents();
}
