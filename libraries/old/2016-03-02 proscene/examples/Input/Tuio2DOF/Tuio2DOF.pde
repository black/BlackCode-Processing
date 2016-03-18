/**
 * TUIO 2 DOF
 * by Eduardo Moriana and Jean Pierre Charalambos.
 *
 * This example illustrates how to control the Scene using touch events using TUIO
 * which requires a customized Agent. It may be the basis for a complete Proscene
 * Android port to appear in the near future.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import java.util.Vector;

import TUIO.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.agent.*;
import remixlab.proscene.*;

Scene scene;
Box [] boxes;
TUIOAgent agent;
TuioProcessing tuioClient;

public void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  agent = new TUIOAgent(scene, "MyTuioAgent");
  scene.setRadius(150);
  scene.showAll();
  //'f' toggles hints
  scene.setPickingVisualHint(true);
  boxes = new Box[30];

  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(scene);

  tuioClient = new TuioProcessing(this, 3333);
  // 'h' also displays it:
  scene.displayInfo();
}

public void draw() {	
  background(0);
  for (int i = 0; i < boxes.length; i++)			
    boxes[i].draw();		
  drawTuio();
}

private void drawTuio() {
  float obj_size = 10;
  float cur_size = 5;		
  scene.beginScreenDrawing();
  Vector tuioObjectList = tuioClient.getTuioObjects();
  for (int i = 0; i < tuioObjectList.size(); i++) {
    TuioObject tobj = (TuioObject) tuioObjectList.elementAt(i);			
    stroke(0);
    fill(0);			
    rect(-obj_size / 2, -obj_size / 2, obj_size, obj_size);			
    fill(255);
    text("" + tobj.getSymbolID(), tobj.getScreenX(width), tobj.getScreenY(height));
  }

  Vector tuioCursorList = tuioClient.getTuioCursors();
  for (int i = 0; i < tuioCursorList.size(); i++) {
    TuioCursor tcur = (TuioCursor) tuioCursorList.elementAt(i);
    Vector pointList = tcur.getPath();

    if (pointList.size() > 0) {
      stroke(0, 0, 255);
      TuioPoint start_point = (TuioPoint) pointList.firstElement();
      for (int j = 0; j < pointList.size(); j++) {
        TuioPoint end_point = (TuioPoint) pointList.elementAt(j);
        stroke(255, 0, 0, j * 20);
        line(start_point.getScreenX(width), 
        start_point.getScreenY(height), 
        end_point.getScreenX(width), 
        end_point.getScreenY(height));
        start_point = end_point;
      }

      stroke(192, 192, 192);
      fill(192, 192, 192);
      ellipse(tcur.getScreenX(width), tcur.getScreenY(height), 
      cur_size, cur_size);
      fill(0);
      text("" + tcur.getCursorID(), tcur.getScreenX(width) - 5, tcur.getScreenY(height) + 5);
    }
  }
  scene.endScreenDrawing();
}

// these callback methods are called whenever a TUIO event occurs

// called when an object is added to the scene
public void addTuioObject(TuioObject tobj) {
  // println("add object " + tobj.getSymbolID() + " (" +
  // tobj.getSessionID()
  // + ") " + tobj.getX() + " " + tobj.getY() + " "
  // + tobj.getAngle());
}

// called when an object is removed from the scene
public void removeTuioObject(TuioObject tobj) {
  // println("remove object " + tobj.getSymbolID() + " ("
  // + tobj.getSessionID() + ")");
}

// called when an object is moved
public void updateTuioObject(TuioObject tobj) {
  println("update object " + tobj.getSymbolID() + " ("
    + tobj.getSessionID() + ") " + tobj.getX() + " " + tobj.getY()
    + " " + tobj.getAngle() + " " + tobj.getMotionSpeed() + " "
    + tobj.getRotationSpeed() + " " + tobj.getMotionAccel() + " "
    + tobj.getRotationAccel());
}

// called when a cursor is added to the scene
public void addTuioCursor(TuioCursor tcur) {
  // println("add cursor " + tcur.getCursorID() + " (" +
  // tcur.getSessionID()
  // + ") " + tcur.getX() + " " + tcur.getY());

  // AbstractElement abstractElement = elementManager.contains(new
  // DLVector(
  // tcur.getScreenX(width), tcur.getScreenY(height)));
  //
  // if (abstractElement != null) {
  // selected = abstractElement;
  // }

  // dev.clickProfile().handle()
  agent.addTuioCursor(tcur);
}

// called when a cursor is moved
public void updateTuioCursor(TuioCursor tcur) {
  // println("update cursor " + tcur.getCursorID() + " ("
  // + tcur.getSessionID() + ") " + tcur.getX() + " " + tcur.getY()
  // + " " + tcur.getMotionSpeed() + " " + tcur.getMotionAccel());

  // float x = tcur.getPath().get(1).getScreenX(width) -
  // tcur.getPath().get(0).getScreenX(width);
  // float y = tcur.getPath().get(1).getScreenY(height)-
  // tcur.getPath().get(0).getScreenY(height);
  agent.updateTuioCursor(tcur);
}

// called when a cursor is removed from the scene
public void removeTuioCursor(TuioCursor tcur) {
  // println("remove cursor " + tcur.getCursorID() + " ("
  // + tcur.getSessionID() + ")");
  // if (selected != null) {
  // selected = null;
  // }
  agent.removeTuioCursor(tcur);
}

// called after each message bundle
// representing the end of an image frame
public void refresh(TuioTime bundleTime) {
  redraw();
}
