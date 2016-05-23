import SimpleOpenNI.*;
import java.util.*;
SimpleOpenNI context;
//----------GRAB GESTURE VARIABLES START------------------------
float        zoomF =0.5f;
float        rotX = radians(180);                                      
float        rotY = radians(0);
//the hand tracking part---------------------->>
float        handThresh = 95;
float        openThresh = 200;
boolean      handsTrackFlag = false;//if kinect is tracking hand or not
ArrayList    handVecList = new ArrayList();//the previous points in a list
String       lastGesture = "";//used to keep track of gestures
PVector      handMin = new PVector();
PVector      handMax = new PVector();
PVector      handVec = new PVector();//the latest/most up to date hand point
int          handVecListSize = 30;//the number of previous points to be remembered 
int          flag;
//-----------GRAB GESTURE VARIABLES END --------------------------- 

void setup()
{
  size(640, 480);  
  smooth();
  context = new SimpleOpenNI(this); //  create kinect object 
  context.enableDepth(); // enable kinect depth camera
  context.enableGesture(); // enable gesture 
  context.enableHands(); //  enable hand recognition 
  // Adding focus gestures ------------------------ 
  context.addGesture("Wave");
  context.addGesture("Click");
  context.addGesture("RaiseHand");
  // End adding----------------------------------------
  context.setMirror(false); // set kinect output mirror false
}
//------------------------MAIN LOOP  ------------------------------------------------ 
void draw()
{
  context.update();
  background(0, 0, 0);
  open_close_palm_gesture();
  color_picker();
  usersboundary();
}
//--------START COLOR PICKING---------------------------------------------------------
void color_picker()
{
  if (((int(last2d.x+20))+int(last2d.y-30)*context.depthWidth())>0) {
    picker= rgbImage.pixels[(int(last2d.x+20))+int(last2d.y-30)*context.depthWidth()];
    if (abs(pointcz-pointpz)<10) {
      countTrack++;
    }
    else {
      countTrack=0;
    }
    output.println("current hand ID- "+handId);
    if (countTrack>30) {
      // if(flag==1)
      handColorMap.put(handId, hex(picker));
    }
  }
}
//--------END COLOR PICKING---------------------------------------------------------

// -------- START HAND TRACKING FOR OPEN AND CLOSE PALM GESTURE---------------------------------------------------------
void open_close_palm_gesture()
{
  PVector realWorldPoint;
  if (handsTrackFlag)  
  {
    handMin = handVec.get();
    handMax = handVec.get();
    int[]   depthMap = context.depthMap();
    int     steps   = 5;  // to speed up the drawing, draw every third point
    //   PVector realWorldPoint;
    for (int y=0;y < context.depthHeight();y+=steps)
    { 
      for (int x=0;x < context.depthWidth();x+=steps)
      {
        int index = x + y * context.depthWidth();
        if (depthMap[index] > 0)
        { 
          realWorldPoint = context.depthMapRealWorld()[index];
          if (realWorldPoint.dist(handVec) < handThresh) {
            point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z); 
            if (realWorldPoint.x < handMin.x) handMin.x = realWorldPoint.x;
            if (realWorldPoint.y < handMin.y) handMin.y = realWorldPoint.y;
            if (realWorldPoint.z < handMin.z) handMin.z = realWorldPoint.z;
            if (realWorldPoint.x > handMax.x) handMax.x = realWorldPoint.x;
            if (realWorldPoint.y > handMax.y) handMax.y = realWorldPoint.y;
            if (realWorldPoint.z > handMax.z) handMax.z = realWorldPoint.z;
          }
        }
      }
    }
    float hDist = handMin.dist(handMax);
    if (hDist > openThresh) {
      println("palm open, dist: " + hDist);
      flag=0;
    }
    else {
      println("palm close, dist: " + hDist);
      flag=1;
    }
  }
}
// --------END HAND TRACKING FOR OPEN AND CLOSE PALM GESTURE-----

//-----------------START DRAWING USER BOUNDARY ----------------------
void usersboundary() {
  int[] userMap = null;
  int userCount = context.getNumberOfUsers();
  if (userCount > 0) {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  } 
  loadPixels();
  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=2; x<context.depthWidth()-2; x++) {
      int index = x + y * context.depthWidth();
      if (userMap != null && userMap[index] > 0) {
        int cindex=userMap[index]%userColors.length;
        usericon=userColors[cindex];
        //if negibouring slots of blob array are 0 and 255 we draw that pixels as edge point
        if (blob_array[index-1]==0&&blob_array[index]==255) {
          layer.fill(usericon);
          layer.ellipse(x, y, 2, 2);
        }
        //if negibouring slots of blob array are 255 and 0  we draw that pixels as edge point
        else    if (blob_array[index]==255&&blob_array[index+1]==0) {

          layer.ellipse(x, y, 2, 2);
        }
      }
    }
  }
}
//-----------------END DRAWING USER BOUNDARY ----------------------

// -------- START HAND EVENTS ---------------------------------------------------------
void onCreateHands(int handId, PVector pos, float time)
{
  println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);
  handsTrackFlag = true;
  handVec = pos;
  handVecList.clear();
  handVecList.add(pos);
}

void onUpdateHands(int handId, PVector pos, float time)
{
  //println("onUpdateHandsCb - handId: " + handId + ", pos: " + pos + ", time:" + time);
  handVec = pos;
  handVecList.add(0, pos);
  if (handVecList.size() >= handVecListSize)
  { // remove the last point 
    handVecList.remove(handVecList.size()-1);
  }
}

void onDestroyHands(int handId, float time)
{
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);
  handsTrackFlag = false;
  context.addGesture(lastGesture);
}
// -------- END HAND EVENTS ---------------------------------------------------------

// -------- START GESTURE EVENTS ---------------------------------------------------------
void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
{
  println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);
  lastGesture = strGesture;
  context.removeGesture(strGesture); 
  context.startTrackingHands(endPosition);
}

void onProgressGesture(String strGesture, PVector position, float progress)
{
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}
// -------- SEND HAND EVENTS ---------------------------------------------------------

