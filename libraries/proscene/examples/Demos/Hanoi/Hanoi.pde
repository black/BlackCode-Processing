/**
 * Hanoi
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;

Scene scene;
int nbdisques=5;
Systeme systeme;
InteractiveFrame[] frames;
WorldConstraint contrainteGuide, contraintePlan, immobile ;
PFont font;
boolean onScreen;
void setup() {
  size(700, 640, P3D);
  font = loadFont("FreeSans-16.vlw");
  textFont(font);
  scene=new Scene(this);
  scene.setAxesVisualHint(false);
  scene.setGridVisualHint(false);  
  scene.setRadius(300);
  onScreen=true;
  frames=new InteractiveFrame[nbdisques];
  //les contraintes
  contraintePlan= new WorldConstraint();
  contraintePlan.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new Vec(0, 1, 0));
  contraintePlan.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  //
  contrainteGuide= new WorldConstraint();
  contrainteGuide.setTranslationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0, 0, 1));
  contrainteGuide.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  //
  immobile= new WorldConstraint();
  immobile.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  immobile.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  //
  for (int i=0;i<nbdisques;i++) {
    frames[i]=new InteractiveFrame(scene);
    frames[i].setConstraint(immobile);
    frames[i].setPosition(-150, 0, 14*(nbdisques-i-1));
    //scene.setInteractiveFrame(frames[i]);
  }
  systeme=new Systeme();
  scene.camera().setOrientation(new Quat(new Vec(1, 0, 0), -1.3));
  scene.camera().setPosition(new Vec(0, 480, 150));  
  scene.showAll();
}

void draw() {
  background(250, 200, 0);
  directionalLight(251, 155, 250, -1, -1, -0.72);
  directionalLight(155, 155, 255, 0, 1, -0.1);
  directionalLight(255, 255, 255, -0.7, -0.7, -0.5);

  pushMatrix();
  fill(205, 50, 50);
  translate(0, 0, -17);
  box(500, 200, 6);
  fill(155, 155, 255);
  scene.drawCone(3.0, 3.0, 130);
  translate(150, 0, 0);
  scene.drawCone(3.0, 3.0, 130);
  translate(-300, 0, 0);
  scene.drawCone(3.0, 3.0, 130);
  popMatrix();
  systeme.draw();

  noLights();
  scene.beginScreenDrawing();  
  if (onScreen)
  {
    text("Press 'r' to hide the rules", 5, 20);
    text("Tip: Drag each disc on a rod to bottom and leave Hanoi to replace that", 5, 40);
    text("The objective of the puzzle is to move the entire stack to another rod,", 5, height-120);
    text( "obeying the following rules:", 5, height-100);
    text( "1/ Only one disk may be moved at a time.", 5, height-80);
    text( "2/Each move consists of taking the upper disk from one of the rods and sliding it onto another rod,", 5, height-60);
    text( "on top of the other disks that may already be present on that rod.", 5, height-40);
    text( "3/No disk may be placed on top of a smaller disk.", 5, height-20);
  }
  else
    text("Press 'r' to see the rules according to Wikipedia", 5, 20);  
  scene.endScreenDrawing();
  lights();
}

void keyPressed() {
  if ((key == 'r') || (key == 'R'))   
    onScreen = !onScreen;
}
