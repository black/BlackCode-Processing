import SimpleOpenNI.*;
SimpleOpenNI  kinect;
float        handThresh = 95;
float        openThresh = 200;
int          dodge=10;
void setup()
{
  size(640, 480,P3D);
  smooth();
  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableDepth();
  kinect.enableRGB();
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("Wave");
  kinect.addGesture("Click");
  kinect.addGesture("RaiseHand");
}

void draw()
{
  kinect.update();
  handy();
  image(kinect.depthImage(),0,0);
  grids();
}

void mouseDragged()
{
  dodge = dodge + (mouseX-pmouseX);
}

void grids() {  
  for (int i=0;i <width;i+=dodge) { 
    for (int j=0;j<height;j+=dodge) {  
      fill(255,0,0);    
      textSize(8);      
      text("+", i, j);
    }
  }
}

PVector handMin,handMax;

void handy() {
  if (handsTrackFlag) {
    handMin = handVec.get();
    handMax = handVec.get();
    int[] depthMap = kinect.depthMap();
    int steps =5, index;
    PVector realWorldPoint;
    for (int y=0;y < kinect.depthHeight();y+=steps)
    {  
      for (int x=0;x < kinect.depthWidth();x+=steps)
      {
        index = x + y * kinect.depthWidth();
        if (depthMap[index] > 0)
        { 
          realWorldPoint = kinect.depthMapRealWorld()[index];
          if (realWorldPoint.dist(handVec) < handThresh) {
            fill(255);
            point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z); 
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
  }
  
  
}

//-----------------------------HAND EVENT -------------
PVector handVec = new PVector();
ArrayList<PVector> handPositions;
Boolean handsTrackFlag=false;
int handVecListSize=30;
void onCreateHands(int handId, PVector position, float time)
{
  println("onCreateHands handId: " + handId + ", pos: " + position + ", time:" + time);
  handsTrackFlag = true;
  handVec = position;
  handPositions.clear();
  handPositions.add(position);
}

void onUpdateHands(int handId, PVector position, float time)
{
  handVec = position;
  handPositions.add(0, position); // not understood
  if (handPositions.size() >= handVecListSize)
  {  
    handPositions.remove(handPositions.size()-1);
  }
}
//-----------------------------------------------
void onDestroyHands(int handId, float time)
{
  handsTrackFlag = false;
  kinect.addGesture("Raise Hand");
}

//-------------------GESTURE EVENT ---------------
void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
{
  kinect.startTrackingHands(endPosition);
  kinect.removeGesture("Raise Hand");
}

void onProgressGesture(String strGesture, PVector position, float progress)
{
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}

