/**
 * Dizzy
 * by Jean Pierre Charalambos.
 *
 * This example demonstrates how 2D key frames may be used to perform a Prezi-like
 * presentation. 
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

Scene scene;
PImage img;
PFont buttonFont;
ArrayList buttons;
InteractiveFrame message1;
InteractiveFrame message2;
InteractiveFrame image;
float h;
int fSize = 16;

public void setup() {
  size(640, 360, P2D);

  img = loadImage("dizzi.jpg");
  scene = new Scene(this);
  scene.setGridVisualHint(false);
  scene.setAxesVisualHint(false);

  message1 = new InteractiveFrame(scene);
  message2 = new InteractiveFrame(scene);
  image = new InteractiveFrame(scene);

  message1.setPosition(33.699852f, -62.68051f);
  message1.setOrientation(new Rot(-1.5603539f));
  message1.setMagnitude(0.8502696f);

  message2.setPosition(49.460827f, 74.67359f);
  message2.setOrientation(new Rot(-1.533576f));
  message2.setMagnitude(0.3391391f);

  image.setPosition(-314.30075f, -165.1348f);
  image.setOrientation(new Rot(-0.0136114275f));
  image.setMagnitude(0.07877492f);

  // create a camera path and add some key frames:
  // key frames can be added at runtime with keys [j..n]
  scene.eyeFrame().setPosition(89.71913f, -101.32816f);
  scene.eyeFrame().setOrientation(new Rot(-1.542718f));
  scene.eyeFrame().setMagnitude(0.3391391f);
  scene.eye().addKeyFrameToPath(1);

  scene.eyeFrame().setPosition(61.501305f, 71.02506f);
  scene.eyeFrame().setOrientation(new Rot(-1.5142304f));
  scene.eyeFrame().setMagnitude(0.13526922f);
  scene.eye().addKeyFrameToPath(1);

  scene.eyeFrame().setPosition(-99.00719f, -4.614401f);
  scene.eyeFrame().setOrientation(new Rot(-0.010666408f));
  scene.eyeFrame().setMagnitude(0.8055185f);
  scene.eye().addKeyFrameToPath(1);

  // re-position the camera:
  scene.eyeFrame().setPosition(89.71913f, -101.32816f);
  scene.eyeFrame().setOrientation(new Rot(-1.542718f));
  scene.eyeFrame().setMagnitude(0.3391391f);

  // drawing of camera paths are toggled with key 'r'.
  //scene.setViewPointPathsAreDrawn(true);

  buttons = new ArrayList(6);
  for (int i = 0; i < 5; ++i)
    buttons.add(null);

  buttonFont = loadFont("FreeSans-16.vlw");
  
  Button2D button = new ClickButton(scene, new PVector(10, 5), buttonFont, 0);
  h = button.myHeight;
  buttons.set(0, button);
}

public void draw() {
  background(0);
  fill(204, 102, 0);

  pushMatrix();
  image.applyTransformation();// optimum
  image(img, 0, 0);
  popMatrix();

  pushMatrix();
  message1.applyTransformation();// optimum
  text("I'm useless", 10, 50);
  popMatrix();

  fill(0, 255, 0);

  pushMatrix();
  message2.applyTransformation();// optimum
  text("but I feel dizzy", 10, 50);
  popMatrix();

  updateButtons();
  displayButtons();
}

void updateButtons() {
  for (int i = 1; i < buttons.size(); i++) {
    // Check if CameraPathPlayer is still valid
    if ((buttons.get(i) != null) && (scene.eye().keyFrameInterpolator(i) == null))
      buttons.set(i, null);
    // Or add it if needed
    if ((scene.eye().keyFrameInterpolator(i) != null)	&& (buttons.get(i) == null))
      buttons.set(i, new ClickButton(scene, new PVector(10, +(i) * (h + 7)), buttonFont, i));
  }
}

void displayButtons() {
  for (int i = 0; i < buttons.size(); i++) {
    Button2D button = (Button2D) buttons.get(i);
    if (button != null)
      button.display();
  }
}