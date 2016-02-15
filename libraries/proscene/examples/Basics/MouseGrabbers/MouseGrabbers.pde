/**
 * Mouse Grabbers.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to attach an iFrame to an object to control it.
 *
 * Once you select a torus it will be highlighted and you can manipulate
 * it with the mouse. Drag the different mouse buttons to see what happens.
 *
 * The displayed texts '+' and '-' are interactive and are implemented
 * as clickable buttons.
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

Scene scene;
ArrayList toruses;
Button2D button1, button2;
int myColor;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);

  PFont buttonFont = loadFont("FreeSans-36.vlw");
  button1 = new ClickButton(scene, new PVector(10, 10), buttonFont, "+", true);
  button2 = new ClickButton(scene, new PVector(16, (2 + button1.myHeight)), buttonFont, "-", false);

  scene.setGridVisualHint(true);
  if(scene.is3D()) scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(150);
  scene.showAll();

  myColor = 125;
  toruses = new ArrayList();
  addTorus();
}

public void draw() {
  background(0);
  for (int i = 0; i < toruses.size(); i++) {
    InteractiveTorus box = (InteractiveTorus) toruses.get(i);
    box.draw(true);
  }
  button1.display();
  button2.display();
}

public void addTorus() {
  InteractiveTorus iTorus = new InteractiveTorus(scene);
  toruses.add(iTorus);
}

public void removeTorus() {
  if (toruses.size() > 0) {
    scene.inputHandler().removeFromAllAgentPools(((InteractiveTorus) toruses.get(0)).iFrame);
    toruses.remove(0);
  }
}
