/**
 * Frame Interaction.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to deal with interactive frames: how to pick &
 * manipulate them and how to visually represent them.
 * 
 * Interactivity may be fine-tuned either from an InteractiveFrame instance or from
 * some code within the sketch, see frame2 and frame3 resp. (frame1 has default mouse
 * and keyboard interactivity). Note that the scene eye has a frame instance
 * (scene.eyeFrame()) which may be controlled in the same way.
 * 
 * Visual representations (PShapes or arbitrary graphics procedures) may be related to
 * a frame in two different ways: 1. Applying the frame transformation just before the
 * graphics code happens in draw() (frame1); or, 2. Setting a visual representation to
 * the frame, either by calling frame.setShape(myPShape) or
 * frame.addGraphicsHandler(myProcedure) in setup() (frame2 and frame3, resp.), and
 * then calling scene.drawFrames() in draw() (frame2 and frame3). Note that
 * adding a visual representation to the scene.eyeFrame() is meaningless and therefore
 * not allowed.
 * 
 * Frame picking is achieved by tracking the pointer and checking whether or not it
 * lies within frame 'selection area': a square around the frame's projected origin
 * (frame 1) or the projected frame's visual representation itself (frame2 and frame 3)
 * which requires drawing into an scene.pickingBuffer().
 * 
 * Press 'i' (which is a shortcut defined below) to switch the interaction between the
 * camera frame and the interactive frame. You can also manipulate the interactive
 * frame by picking the blue torus passing the mouse next to its axes origin.
 * 
 * Press 'f' to display the interactive frame picking hint.
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.bias.event.*;
import remixlab.proscene.*;

Scene scene;
InteractiveFrame frame1, frame2, frame3;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(640, 360, renderer);    
  scene = new Scene(this);
  scene.eyeFrame().setDamping(0);
  scene.setPickingVisualHint(true);

  //frame 1
  frame1 = new InteractiveFrame(scene);
  frame1.setPickingPrecision(InteractiveFrame.PickingPrecision.ADAPTIVE);
  frame1.setGrabsInputThreshold(scene.radius()/4);
  frame1.translate(50, 50);

  // frame 2
  frame2 = new InteractiveFrame(scene, createShape(SPHERE, 40));
  frame2.setMotionBinding(LEFT, "translate");
  frame2.setMotionBinding(RIGHT, "scale");

  //frame 3
  frame3 = new InteractiveFrame(scene, this, "boxDrawing");
  frame3.translate(-50, -50);
  frame3.setMotionBinding(this, LEFT, "boxCustomMotion");
  frame3.setClickBinding(this, LEFT, 1, "boxCustomClick");
  //also possible:
  //frame3 = new InteractiveFrame(scene);
  //frame3.addGraphicsHandler(this, "boxDrawing");
}

public void boxDrawing(PGraphics pg) {
  pg.box(30);
}

public void boxCustomMotion(InteractiveFrame frame, MotionEvent event) {
  frame.screenRotate(event);
}

public void boxCustomClick(InteractiveFrame frame) {
  frame.center();
}

public void draw() {
  background(0);    
  // 1. Apply the frame transformation before your drawing

  // Save the current model view matrix
  pushMatrix();
  pushStyle();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(Scene.toPMatrix(iFrame.matrix())); //is possible but inefficient
  frame1.applyTransformation();//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxes(20);

  if (frame1.grabsInput())
    fill(255, 0, 0);
  else 
    fill(0, 255, 255);
  scene.drawTorusSolenoid();

  popStyle();
  popMatrix();

  // 2. Draw frames for which visual representations have been set
  //scene.drawFrames();//also possible
  int color1 = color(0, 255, 0);
  int color2 = color(255, 0, 255);

  if (frame2.grabsInput())
    frame2.shape().setFill(color1);
  else
    frame2.shape().setFill(color2);
  frame2.draw();

  if (frame3.grabsInput())
    fill(0, 0, 255);
  else
    fill(255, 255, 0);
  frame3.draw();
}

public void keyPressed() {
  if(key == ' ')
    if( scene.mouseAgent().pickingMode() == MouseAgent.PickingMode.CLICK ) {
      scene.mouseAgent().setPickingMode(MouseAgent.PickingMode.MOVE);
      scene.eyeFrame().setMotionBinding(LEFT, "rotate");
      scene.eyeFrame().removeMotionBinding(MouseAgent.NO_BUTTON);
    }
    else {
      scene.mouseAgent().setPickingMode(MouseAgent.PickingMode.CLICK);
      scene.eyeFrame().setMotionBinding(MouseAgent.NO_BUTTON, "rotate");
      scene.eyeFrame().removeMotionBinding(LEFT);
    }
}