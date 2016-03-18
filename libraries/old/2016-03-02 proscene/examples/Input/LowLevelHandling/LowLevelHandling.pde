/**
 * Low
 * by Jean Pierre Charalambos.
 *
 * This examples illustrates how to implement mouse interactions without the
 * default proscene mouse and keyboard agents. The default 'a' and
 * 'g' keyboard shortcuts are intertwined.
 *
 * Press 'c' to change the mouse action (only when the mouse agent is disabled).
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */
 
import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;

boolean enforced = false;	
boolean iFrameGrabsInput;

KeyboardAction keyAction;
DOF2Action mouseAction;
DOF2Event prevEvent, event;
DOF2Event gEvent, prevGenEvent;
KeyboardEvent kEvent;

int count = 4;

InteractiveFrame iFrame;

@Override
public void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);

  // Low-level handling (mouse and keyboard in the case)
  // requires disabling high level handling ;)
  scene.disableKeyboardAgent();
  scene.disableMotionAgent();

  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));

  mouseAction = DOF2Action.ROTATE;
}

@Override
public void draw() {
  background(0);

  fill(204, 102, 0, 155);
  scene.drawTorusSolenoid();

  pushMatrix();
  iFrame.applyTransformation();
  scene.drawAxes(20);

  // Draw a second box		
  if (iFrameGrabsInput) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid();
  } 
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid();
  }

  popMatrix();
}

@Override
public void mouseMoved() {
  // mouseX and mouseY are reduced into a DOF2Event
  event = new DOF2Event(prevEvent, (float) mouseX, (float) mouseY);
  // iFrame may be grabbing the mouse input in two cases:
  // Enforced by the 'y' key
  if (enforced)
    iFrameGrabsInput = true;
  // or if the mouse position is close enough the the iFrame position:
  else
    iFrameGrabsInput = iFrame.checkIfGrabsInput(event);		
  prevEvent = event.get();
}

@Override
public void mouseDragged() {
  // a mouse drag will cause action execution without involving any agent:
  event = new DOF2Event(prevEvent, (float) mouseX, (float) mouseY);
  // the action will be executed by the iFrame or the camera:
  scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, mouseAction, iFrameGrabsInput ? iFrame : scene.eye().frame()));
  prevEvent = event.get();
}

@Override
public void keyPressed() {
  // All keyboard action in proscene are performed by the Scene.
  // Here we define two keyboard actions
  if (key == 'a' || key == 'g') {
    if (key == 'a')
      keyAction = KeyboardAction.TOGGLE_GRID_VISUAL_HINT;
    if (key == 'g')
      keyAction = KeyboardAction.TOGGLE_AXES_VISUAL_HINT;
    kEvent = new KeyboardEvent(key);      
    scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(kEvent, keyAction, scene));
  }
  // Grabbing the iFrame may be done with the keyboard:
  if (key == 'y') {
    enforced = !enforced;
    if (enforced)
      iFrameGrabsInput = true;
    else
      iFrameGrabsInput = false;
  }	
  // The default mouse action (to be performed when dragging it) may be change here:	
  if (key == 'c')
    if (mouseAction == DOF2Action.ROTATE)
      mouseAction = DOF2Action.TRANSLATE;
    else
      mouseAction = DOF2Action.ROTATE;
}
