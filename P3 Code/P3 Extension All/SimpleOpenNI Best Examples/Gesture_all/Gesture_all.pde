import SimpleOpenNI.*;
import java.util.*;
SimpleOpenNI context;

XnVSessionManager sessionManager;

PushDetectorLab pushDetectorLab;
SwipeDetectorLab swipeDetectorLab;

PointDrawer pointDrawer;
void setup() {
  size(640, 480);


  context = new SimpleOpenNI(this);
  //  String ONI_FILE = "/Users/liubo/Movies/oni/MultipleHands.oni";
  String ONI_FILE = "";
  if (context.openFileRecording(ONI_FILE)) println("Playing 3d data from "+ONI_FILE);

  context.setMirror(true);
  context.enableDepth();

  context.enableHands();
  context.enableGesture();

  sessionManager = context.createSessionManager("Click,Wave", "RaiseHand");
 // pushDetectorLab = new PushDetectorLab();
  swipeDetectorLab = new SwipeDetectorLab();
  pointDrawer = new PointDrawer();

 // sessionManager.AddListener(pushDetectorLab);
  sessionManager.AddListener(swipeDetectorLab);
  sessionManager.AddListener(pointDrawer);

  //println("push duration " + pushDetectorLab.GetPushDuration());
}

void draw() {
  context.update();
  context.update(sessionManager);
  image(context.depthImage(), 0, 0, width, height);
  pointDrawer.draw();
}


//////////////////////////////////////////////////
///////////////////////////////////////////////////
// session callbacks

void onStartSession(PVector pos)
{
  println("onStartSession: " + pos);
  context.removeGesture("Wave,Click");
}

void onEndSession()
{
  println("onEndSession: ");
  context.addGesture("Wave,Click");
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

