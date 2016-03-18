/**
 * Fisrt Person Camera.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to set up mouse bindings to control the camera
 * as in first-person mode.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;

Scene scene;
boolean firstPerson;
InteractiveFrame iFrame;

void setup() {
  size(640, 360, P3D);		
  scene = new Scene(this);	
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));
  scene.setMouseAsFirstPerson();
  firstPerson = true;
}

void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();

  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(Scene.toPMatrix(iFrame.matrix())); //is possible but inefficient
  iFrame.applyTransformation();//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxes(20);

  // Draw a second torus
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

  popMatrix();
}

public void keyPressed() {
  if ( key == 'i')
    scene.motionAgent().setDefaultGrabber(scene.motionAgent().defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
  if ( key == ' ') {
    firstPerson = !firstPerson;
    if ( firstPerson ) {
      scene.setMouseAsFirstPerson();
    }
    else {
      scene.setMouseAsArcball();
    }
  }
}
