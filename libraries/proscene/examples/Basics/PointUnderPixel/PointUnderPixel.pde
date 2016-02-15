/**
 * Point Under Pixel.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates 3D point picking.
 * 
 * Left click to zoom on pixel
 * Ctrl + left click to set anchor
 * Right click to draw a ray from the pixel position onto the scene
 * Center click to display the whole scene
 * Press 'z' to reset anchor
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;
Point screenPoint = new Point();
Vec orig = new Vec();
Vec dir = new Vec();
Vec end = new Vec();

Vec pup;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setKeyboardShortcut('z', Scene.KeyboardAction.RESET_ANCHOR);
  scene.setMouseClickBinding(Target.EYE, LEFT, 1, ClickAction.ZOOM_ON_PIXEL);
  scene.setMouseClickBinding(Target.EYE, CENTER, 1, ClickAction.SHOW_ALL);
  scene.setMouseClickBinding(Target.EYE, Event.CTRL, LEFT, 1, ClickAction.ANCHOR_FROM_PIXEL);
  //Point under pixel requires noSmooth, see here: 
  //https://github.com/processing/processing/issues/2771
  noSmooth();
}

void draw() {  
  background(0);
  drawRay();
  fill(255, 0, 0);
  box(40);
  // proscene visual hints are drawn after draw() so we push the modelview
  pushMatrix();
  translate(50, 50);
  fill(0, 255, 0);
  box(40);
  // get back to the world coordinate system
  popMatrix();
}

void drawRay() {		
  if (pup != null) {
    pushStyle();
    strokeWeight(5);
    stroke(255, 255, 0);
    point(pup.x(), pup.y(), pup.z());
    stroke(0, 0, 255);		
    line(orig.x(), orig.y(), orig.z(), end.x(), end.y(), end.z());
    popStyle();
  }
}

void mouseClicked() {
  if (mouseButton == RIGHT) {
    pup = scene.pointUnderPixel(new Point(mouseX, mouseY));
    if ( pup != null ) {
      screenPoint.set(mouseX, mouseY);
      scene.camera().convertClickToLine(screenPoint, orig, dir);				
      end = Vec.add(orig, Vec.multiply(dir, 1000.0f));
    }
  }
}