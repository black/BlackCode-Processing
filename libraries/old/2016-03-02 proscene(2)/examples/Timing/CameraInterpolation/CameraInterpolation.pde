/**
 * Camera Interpolation.
 * by Jean Pierre Charalambos.
 * 
 * This example (together with Frame Interpolation) illustrates the
 * KeyFrameInterpolator functionality.
 * 
 * KeyFrameInterpolator smoothly interpolate its attached Camera Frames over time
 * on a path defined by Frames. The interpolation can be started/stopped/reset,
 * played in loop, played at a different speed, etc...
 * 
 * In this example, a Camera path is defined by four InteractiveFrames
 * which are attached to the first Camera path (the Camera holds five such paths).
 * The Frames can be moved with the mouse, making the path editable. The Camera
 * interpolating path is played with the shortcut '1'.
 * 
 * Press CONTROL + '1' to add (more) key frames to the Camera path.
 * 
 * Press ALT + '1' to delete the Camera path.
 *
 * The displayed texts are interactive. Click on them to see what happens.
 * 
 * The Camera holds 5 KeyFrameInterpolators, binded to [1..5] keys. Pressing
 * CONTROL + [1..5] adds key frames to the specific path. Pressing ALT + [1..5]
 * deletes the specific path. Press 'r' to display all the key frame camera paths
 * (if any). The displayed paths are editable.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

Scene scene;
ArrayList buttons;
float h;
PFont buttonFont;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);

  //create a camera path and add some key frames:
  //key frames can be added at runtime with keys [j..n]
  scene.camera().setPosition(80,0,0);
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);

  scene.camera().setPosition(30,30,-80);
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);

  scene.camera().setPosition(-30,-30,-80);
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);

  scene.camera().setPosition(-80,0,0);
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);

  //re-position the camera:
  scene.camera().setPosition(0,0,1);
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.showAll();

  //drawing of camera paths are toggled with key 'r'.
  scene.setPathsVisualHint(true);

  buttons = new ArrayList(6);
  for (int i=0; i<5; ++i)
    buttons.add(null);
    
  buttonFont = loadFont("FreeSans-16.vlw");
  
  Button2D button = new ClickButton(scene, new PVector(10,5), buttonFont, 0);
  h = button.myHeight;
  buttons.set(0, button);
}

void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();	

  updateButtons();
  displayButtons();
}

void updateButtons() {
  for (int i = 1; i < buttons.size(); i++) {
    // Check if CameraPathPlayer is still valid
    if ((buttons.get(i) != null) && (scene.camera().keyFrameInterpolator(i) == null ) )
      buttons.set(i, null);
    // Or add it if needed
    if ((scene.camera().keyFrameInterpolator(i) != null) && (buttons.get(i) == null))
      buttons.set(i, new ClickButton(scene, new PVector(10, + ( i ) * ( h + 7 )), buttonFont, i));
  }
}

void displayButtons() {
  for (int i = 0; i < buttons.size(); i++) {
    Button2D button = (Button2D) buttons.get(i);
    if ( button != null )
      button.display();
  }
}