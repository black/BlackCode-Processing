/**
 * Android 3-DOF
 * by Victor Manuel Forero and Jean Pierre Charalambos.
 *
 * This example requires Android mode to run.
 *
 * Development is taken place at this fork: https://github.com/remixlab/proscene.droid
 * We hope to integrate it back upstream once TouchEvents are directly supported in P5.
 *
 * This example illustrates how to control the Scene using touch events emulating
 * 3-DOFs, including excerpts of the motion and keyboard agents developed at the fork.
 *
 * The customized Android keyboard agent has similar keyboard shortcuts to those found
 * at the Desktop, but with a single eye path. The Android keyboard is displayed by
 * pressing your device settings key. Note that we've defined a single eye path. Use
 * the '1' key to add keyframes to it, '2' to delete the path and '3' to play it.
 */

import java.util.Vector;
import android.view.MotionEvent;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants;
import remixlab.dandelion.core.Constants.DOF3Action;
import remixlab.proscene.Scene;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.agent.*;
import remixlab.proscene.*;

Scene scene;
TouchAgent agent;
Box [] boxes;

public void setup() {
  scene = new Scene(this);  

  DroidKeyboardAgent keyboardAgent = new DroidKeyboardAgent(scene, "KeyboardAgent");  
  scene.disableKeyboardAgent();
  registerMethod("keyEvent", keyboardAgent);

  agent = new TouchAgent(scene, "OtherTouchAgent");

  scene.setNonSeqTimers();
  boxes = new Box[10];
  scene.setDottedGrid(false);

  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(scene);

  frameRate(100);
}

public String sketchRenderer() {
  return P3D;
}

public void draw() {  
  background(0);
  for (int i = 0; i < boxes.length; i++)      
    boxes[i].draw();
  scene.beginScreenDrawing();  
  text(frameRate, 5, 17);
  scene.endScreenDrawing();
}

public boolean dispatchTouchEvent(MotionEvent event) {
  int action = event.getActionMasked(); // get code for action
  switch (action) { // let us know which action code shows up
  case MotionEvent.ACTION_DOWN: 
  case MotionEvent.ACTION_POINTER_1_DOWN:
    agent.addTouCursor(event);
    break;
  case MotionEvent.ACTION_UP: 
  case MotionEvent.ACTION_POINTER_1_UP:
    agent.removeTouCursor(event);
    break;
  case MotionEvent.ACTION_MOVE:
    if (event.getPointerCount() == 1)
      agent.updateTouCursor(event);
    else
      agent.transalateTouCursor(event);
    break;
  }
  return super.dispatchTouchEvent(event); // pass data along when done!
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == MENU) {
      InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(0, 0);
    }
  }
  //press the space bar to switch camera as first person 
  if (keyCode == 62)
    agent.setAsFirstPerson(!agent.isAsFirstPerson());
}

