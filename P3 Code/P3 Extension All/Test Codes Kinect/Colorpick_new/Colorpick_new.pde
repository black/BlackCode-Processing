import SimpleOpenNI.*;
import java.util.*;
import java.awt.*;
import processing.opengl.*;

SimpleOpenNI context;
float        zoomF =0.5f;
float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, the data from openni comes upside down
float        rotY = radians(0);
boolean      handsTrackFlag = false;
PVector      handVec = new PVector();
ArrayList    handVecList = new ArrayList();
int          handVecListSize = 30;
String       lastGesture = "";

void setup()
{
  size(1024,768,OPENGL);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem
  context = new SimpleOpenNI(this);
  context.setMirror(true);
  context.enableDepth();
  context.enableGesture();
  context.enableHands();
  context.addGesture("Wave");
  context.addGesture("Click");
  context.addGesture("RaiseHand");
  //context.setSmoothingHands(.5); // set how smooth the hand capturing should be
  stroke(255,255,255);
  smooth();
  perspective(radians(45), float(width)/float(height),10.0f,150000.0f);
 }

void draw()
{
  // update the cam
  context.update();
  background(0,0,0);
  translate(width/2, height/2, 0);
  rotateX(rotX);
  rotateY(rotY);
  scale(zoomF);
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
        realWorldPoint = context.depthMapRealWorld()[index]; // draw the projected point
        point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z); 
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
  }
}


// -----------------------------------------------------------------
// hand events

void onCreateHands(int handId,PVector pos,float time)
{
  println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);
 
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
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);
  
  handsTrackFlag = false;
  context.addGesture(lastGesture);
}

// -----------------------------------------------------------------
// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
{
  println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);
  
  lastGesture = strGesture;
  context.removeGesture(strGesture); 
  context.startTrackingHands(endPosition);
  
}

void onProgressGesture(String strGesture, PVector position,float progress)
{
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}
