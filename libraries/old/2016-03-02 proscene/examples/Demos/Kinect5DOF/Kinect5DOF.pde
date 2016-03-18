/**
 * Kinect 5DOF
 * by Miguel Parra and Pierre Charalambos.
 *
 * This demo shows how to control your scene Eye and iFrames using kinect
 * gestures, emulating 5 degrees-of-freedom; the three translations and two
 * rotations: rotation along y and rotation along z.
 *
 * We implement the (non-conventional) user interaction mechanism as a HIDAgent
 * which provides up to 6DOFs. The Agent gathers kinect input data and reduces
 * it as "bogus" 6DOF event from which the following proscene built-in actions are
 * bound: TRANSLATE_ROTATE and TRANSLATE_XYZ.
 *
 * See the gestures used to control the eye: https://www.youtube.com/watch?v=G8SEzFMmMyI
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import processing.opengl.*;
import SimpleOpenNI.*;
import remixlab.proscene.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;
HIDAgent agent;
Kinect kinect;
PVector kinectPos, kinectRot;
Box [] boxes;

void setup() {
  size(800, 600, P3D);
  scene = new Scene(this);
  kinect=new Kinect(this);

  scene.setRadius(200);
  scene.showAll();

  agent = new HIDAgent(scene, "Kinect") {
    @Override
    public DOF6Event feed() {
      if(!kinect.initialDefined) return null;
      return new DOF6Event(kinectPos.x, kinectPos.y, kinectPos.z, 0, kinectRot.y, kinectRot.z);
    }
  };  
  agent.setSensitivities(0.03, 0.03, 0.03, 0.00005, 0.00005, 0.00005);
  agent.disableTracking();

  boxes = new Box[30];
  for (int i = 0; i < boxes.length; i++) {
    boxes[i] = new Box(scene);
    agent.addInPool(boxes[i].iFrame);
  }
}

void draw() {
  background(0);

  for (int i = 0; i < boxes.length; i++)      
    boxes[i].draw();

  //Update the Kinect data
  kinect.update();

  kinect.draw();

  //Get the translation and rotation vectors from Kinect
  kinectPos=kinect.deltaPositionVector();
  kinectRot=kinect.rotationVector();
}

void onNewUser(SimpleOpenNI curContext, int userId) {
  kinect.onNewUser(curContext, userId);
}