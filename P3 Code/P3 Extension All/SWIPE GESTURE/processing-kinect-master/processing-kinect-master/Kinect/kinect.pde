/* --------------------------------------------------------------------------
 * SimpleOpenNI Event Server
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect library
 * http://code.google.com/p/simple-openni
 * --------------------------------------------------------------------------
 * Send out events on sockets
 * ----------------------------------------------------------------------------
 */

import SimpleOpenNI.*;
import processing.opengl.*;

SimpleOpenNI context;
float        zoomF =0.5f;
float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis,
                                   // the data from openni comes upside down
float        rotY = radians(0);
boolean      handsTrackFlag = false;
PVector      handVec = new PVector();
ArrayList    handVecList = new ArrayList();
int          handVecListSize = 30;
String       lastGesture = "";

XnVSessionManager sessionManager;
Server server;

void setup()
{
  //size(1024,768,P3D);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem
  size(640,480,OPENGL);

  server = new Server(8888);
  server.start();

  context = new SimpleOpenNI(this);

  // mirror is by default enabled
  context.setMirror(true);

  // enable depthMap generation
  context.enableDepth();

  // enable the hands + gesture
  context.enableGesture();
  context.enableHands();

  // setup NITE 
  sessionManager = context.createSessionManager("Wave", "RaiseHand");
  new Push(sessionManager, server);
  new Swipe(sessionManager, server);

/**/

  // set how smooth the hand capturing should be
  //context.setSmoothingHands(.5);

  stroke(255,255,255);
  smooth();

  perspective(95.0f,
              float(width)/float(height),
              10.0f,150000.0f);
 }

void draw()
{  
  // update the cam
  context.update();

  // update nite
  context.update(sessionManager);
  
  image(context.depthImage(),0,0);

/*
  // set the scene pos
  translate(width/2, height/2, 0);
  rotateX(rotX);
  rotateY(rotY);
  scale(zoomF);

  // draw the 3d point depth map
  int[]   depthMap = context.depthMap();
  int     steps   = 3;  // to speed up the drawing, draw every third point
  int     index;
  PVector realWorldPoint;

  translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera

  stroke(200);
  for(int y=0;y < context.depthHeight();y+=steps)
  {
    for(int x=0;x < context.depthWidth();x+=steps)
    {
      index = x + y * context.depthWidth();
      if(depthMap[index] > 0)
      {
        // draw the projected point
        realWorldPoint = context.depthMapRealWorld()[index];
        point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z);
      }
    }
  }

  // draw the tracked hand
  if(handsTrackFlag)
  {
    pushStyle();
      stroke(255,0,0,200);
      noFill();
      Iterator itr = handVecList.iterator();
      beginShape();
        while( itr.hasNext() )
        {
          PVector p = (PVector) itr.next();
          vertex(p.x,p.y,p.z);
        }
      endShape();

      stroke(255,0,0);
      strokeWeight(4);
      point(handVec.x,handVec.y,handVec.z);
    popStyle();
  }

  // draw the kinect cam
  context.drawCamFrustum();
  */
}


////////////////////////////////////////////////////////////////////////////////////////////
// session callbacks

void onStartSession(PVector pos)
{
  println("onStartSession: " + pos);
}

void onEndSession()
{
  println("onEndSession: ");
}

void onFocusSession(String strFocus,PVector pos,float progress)
{
  println("onFocusSession: focus=" + strFocus + ",pos=" + pos + ",progress=" + progress);
}


// -----------------------------------------------------------------
// hand events

void onCreateHands(int handId,PVector pos,float time)
{
  //println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);

  handsTrackFlag = true;
  handVec = pos;

  handVecList.clear();
  handVecList.add(pos);
}

void onUpdateHands(int handId,PVector pos,float time)
{
  //println("onUpdateHandsCb - handId: " + handId + ", pos: " + pos + ", time:" + time);
  handVec = pos;

  handVecList.add(0,pos);
  if(handVecList.size() >= handVecListSize)
  { // remove the last point
    handVecList.remove(handVecList.size()-1);
  }
}

void onDestroyHands(int handId,float time)
{
  //println("onDestroyHandsCb - handId: " + handId + ", time:" + time);

  handsTrackFlag = false;
  context.addGesture(lastGesture);
}


// -----------------------------------------------------------------
// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
{
  //println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);

  lastGesture = strGesture;
  context.removeGesture(strGesture);
  context.startTrackingHands(endPosition);

}

void onProgressGesture(String strGesture, PVector position,float progress)
{
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}

// -----------------------------------------------------------------
// Keyboard event
void keyPressed()
{
  switch(key)
  {
  case ' ':
    context.setMirror(!context.mirror());
    break;
  }

  switch(keyCode)
  {
    case LEFT:
      server.send("SWIPELEFT");
      rotY += 0.1f;
      break;
    case RIGHT:
      server.send("SWIPERIGHT");
      rotY -= 0.1f;
      break;
    case UP:
      if(keyEvent.isShiftDown())
        zoomF += 0.01f;
      else
        rotX += 0.1f;
      break;
    case DOWN:
      server.send("PUSH");
      if(keyEvent.isShiftDown())
      {
        zoomF -= 0.01f;
        if(zoomF < 0.01)
          zoomF = 0.01;
      }
      else
        rotX -= 0.1f;
      break;
  }
}

