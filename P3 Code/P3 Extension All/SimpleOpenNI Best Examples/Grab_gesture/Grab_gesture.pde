import SimpleOpenNI.*;
SimpleOpenNI context;
float        zoomF =0.5f;
float        rotX = radians(180);                                      
float        rotY = radians(0);
//the hand tracking part
boolean      handsTrackFlag = false;//if kinect is tracking hand or not
PVector      handVec = new PVector();//the latest/most up to date hand point
ArrayList    handVecList = new ArrayList();//the previous points in a list
int          handVecListSize = 30;//the number of previous points to be remembered 
String       lastGesture = "";//used to keep track of gestures
 
PVector      handMin = new PVector();
PVector      handMax = new PVector();
float        handThresh = 95;
float        openThresh = 200;
 
void setup()
{
  size(640,480,P3D);  
  context = new SimpleOpenNI(this);
  context.setMirror(false);
  if(context.enableDepth() == false)
  {
     println("Can't open the depthMap, maybe the camera is not connected!"); 
     exit();
     return;
  }
 
  // enable hands + gesture generation
  context.enableGesture();
  context.enableHands();
  // add focus gestures  / here i do have some problems on the mac, i only recognize raiseHand ? Maybe cpu performance ?
  context.addGesture("Wave");
  context.addGesture("Click");
  context.addGesture("RaiseHand");
 
  stroke(255,255,255);
  smooth();
 
  perspective(radians(45),
              float(width)/float(height),
              10.0f,150000.0f);
 }
 
void draw()
{
  // update the cam
  context.update();
 
  background(0,0,0);
 
  // set the scene pos
  translate(width/2, height/2, 0);
  rotateX(rotX);
  rotateY(rotY);
  scale(zoomF);
 
  // draw the tracked hand
  if(handsTrackFlag)  
  {
    //update hand from point cloud
    handMin = handVec.get();
    handMax = handVec.get();
    // draw the 3d point depth map
    int[]   depthMap = context.depthMap();
    int     steps   = 3;  // to speed up the drawing, draw every third point
    int     index;
    PVector realWorldPoint;
    for(int y=0;y < context.depthHeight();y+=steps)
    {
      for(int x=0;x < context.depthWidth();x+=steps)
      {
        index = x + y * context.depthWidth();
        if(depthMap[index] > 0)
        { 
          // draw the projected point
          realWorldPoint = context.depthMapRealWorld()[index];
          if (realWorldPoint.dist(handVec) < handThresh) {
            point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z); 
            if(realWorldPoint.x < handMin.x) handMin.x = realWorldPoint.x;
            if(realWorldPoint.y < handMin.y) handMin.y = realWorldPoint.y;
            if(realWorldPoint.z < handMin.z) handMin.z = realWorldPoint.z;
            if(realWorldPoint.x > handMax.x) handMax.x = realWorldPoint.x;
            if(realWorldPoint.y > handMax.y) handMax.y = realWorldPoint.y;
            if(realWorldPoint.z > handMax.z) handMax.z = realWorldPoint.z;
          }
        }
      } 
    }
    line(handMin.x,handMin.y,handMin.z,handMax.x,handMax.y,handMax.z);
    float hDist = handMin.dist(handMax);
    if(hDist > openThresh) println("palm open, dist: " + hDist);
    else                   println("palm close, dist: " + hDist);
 
    pushStyle();
      stroke(255,0,0,200);
      noFill();
      beginShape();
        for(int i = 0 ; i < handVecList.size(); i++){
          PVector p = (PVector) handVecList.get(i);
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

