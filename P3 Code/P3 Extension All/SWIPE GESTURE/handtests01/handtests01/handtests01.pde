import SimpleOpenNI.*;
import java.util.Iterator;
import java.util.Map;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.AWTException;

SimpleOpenNI kinect;
Robot robot;

XnVSessionManager sessionManager;

PushDetectorLab pushDetectorLab;
SwipeDetectorLab swipeDetectorLab;

PointDrawer pointDrawer;
void setup() {
  size(640, 480);


  kinect = new SimpleOpenNI(this);
  //  String ONI_FILE = "/Users/liubo/Movies/oni/MultipleHands.oni";
  String ONI_FILE = "";
  if (kinect.openFileRecording(ONI_FILE)) println("Playing 3d data from "+ONI_FILE);

  kinect.setMirror(true);
  kinect.enableDepth();

  kinect.enableHands();
  kinect.enableGesture();

  sessionManager = kinect.createSessionManager("Click,Wave", "RaiseHand");
 // pushDetectorLab = new PushDetectorLab();
  swipeDetectorLab = new SwipeDetectorLab();
  pointDrawer = new PointDrawer();

 // sessionManager.AddListener(pushDetectorLab);
  sessionManager.AddListener(swipeDetectorLab);
  sessionManager.AddListener(pointDrawer);

  //println("push duration " + pushDetectorLab.GetPushDuration());
}

void draw() {
  kinect.update();
  kinect.update(sessionManager);
  image(kinect.depthImage(), 0, 0, width, height);
  pointDrawer.draw();
}


//////////////////////////////////////////////////
///////////////////////////////////////////////////
// session callbacks

void onStartSession(PVector pos)
{
  println("onStartSession: " + pos);
  kinect.removeGesture("Wave,Click");
}

void onEndSession()
{
  println("onEndSession: ");
  kinect.addGesture("Wave,Click");
}

void onFocusSession(String strFocus, PVector pos, float progress)
{
  println("onFocusSession: focus=" + strFocus + ",pos=" + pos + ",progress=" + progress);
}

/////////////////
// HANDS

// -----------------------------------------------------------------
// hand events

void onCreateHands(int handId, PVector pos, float time)
{
  println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);
}

void onUpdateHands(int handId, PVector pos, float time)
{
  // println("onUpdateHandsCb - handId: " + handId + ", pos: " + pos + ", time:" + time);
}

void onDestroyHands(int handId, float time)
{
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);
}

// -----------------------------------------------------------------
// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
{
  println("onRecognizeGesture - strGesture: " + strGesture + 
    ", idPosition: " + idPosition + ", endPosition:" + endPosition);
}

void onProgressGesture(String strGesture, PVector position, float progress)
{
  println("onProgressGesture - strGesture: " + strGesture + 
    ", position: " + position + ", progress:" + progress);
}

