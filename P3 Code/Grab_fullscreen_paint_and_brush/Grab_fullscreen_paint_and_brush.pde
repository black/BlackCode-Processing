import SimpleOpenNI.*;
SimpleOpenNI kinect;

boolean      handsTrackFlag = false;//if kinect is tracking hand or not
PVector      handVec = new PVector();//the latest/most up to date hand point
ArrayList    handVecList = new ArrayList();//the previous points in a list
int          handVecListSize = 30;//the number of previous points to be remembered 
String       lastGesture = "";//used to keep track of gestures
int          dodge=10;
PVector      handMin = new PVector();
PVector      handMax = new PVector();
float        handThresh = 95;
float        openThresh = 200;
int          flag=0; 
color        Color=color(255, 0, 0);
PGraphics topLayer, fakeLayer;
PImage img1, img2;
int size=10;
void setup()
{
  size(displayWidth, displayHeight, P3D);
  frame.setLocation(0, 0);
  kinect = new SimpleOpenNI(this);
  kinect.setMirror(false);
  kinect.enableDepth();
  kinect.enableRGB();
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("Wave");
  kinect.addGesture("Click");
  kinect.addGesture("RaiseHand");
  kinect.setMirror(true);
  //smooth();
  perspective(radians(45), float(displayWidth)/float(displayHeight), 10.0f, 150000.0f);
  topLayer = createGraphics(displayWidth, displayHeight);
  fakeLayer = createGraphics(displayWidth, displayHeight);
  img1 = loadImage("hand.png");
  img2 = loadImage("grab.png");
}
void mouseDragged()
{
  dodge = dodge + (mouseX-pmouseX);
}

void draw()
{
  kinect.update();
  background(255);
  grids();
  //-----------------------
  translate(displayWidth/2, displayHeight/2, 0);
  pushStyle();
  if (handsTrackFlag)  
  {
    handMin = handVec.get();
    handMax = handVec.get();
    int[]   depthMap = kinect.depthMap();
    int     steps   = 5;  // to speed up the drawing, draw every third point
    int     index;
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
    // line(handMin.x,handMin.y,handMin.z,handMax.x,handMax.y,handMax.z);
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
  popStyle();
  //-----------------------
  pushStyle();
  // translate(-displayWidth/2,0, 0);
  rotateX(radians(180));
  //rotateY(radians(0));
  scale(0.65f);
  imageMode(CENTER);
  topLayer.beginDraw();
  topLayer.smooth();
  topLayer.noStroke();
  topLayer.fill(0, 255, 0);
  topLayer.rect(0, 0, 50, 50);
  handVec.x = (int)map(handVec.x, 0, kinect.depthWidth(), 0, displayWidth);
  handVec.y = (int)map(handVec.y, 0, kinect.depthHeight(), 0, displayHeight);
  if (flag==1 && handVec.x < 50 && handVec.y<50   ) {
    Color = topLayer.get((int)handVec.x, (int)handVec.y);
  }
  topLayer.fill(0, 0, 255);
  topLayer.rect(100, 0, 50, 50);
  if (flag==1 && handVec.x < 100 && handVec.x > 150 && handVec.y<50   ) {
    Color = topLayer.get((int)handVec.x, (int)handVec.y);
  }
  topLayer.strokeWeight(4);
  topLayer.fill(Color);
  if (flag==1) {
    topLayer.stroke(Color);
    topLayer.strokeWeight(size);
    if (handVec.y > 100) {
      topLayer.line(handVec.x, handVec.y, t, l);
    }
  }
  t = handVec.x;
  l = handVec.y;
  image(topLayer, 0, 0);
  topLayer.endDraw();
  //-----------------------
  fakeLayer.beginDraw();
  fakeLayer.smooth();
  fakeLayer.background(255, 0);
  fakeLayer.fill(#7500FC);
  fakeLayer.rect(540, 0, 50, 50 );
  fakeLayer.fill(255);
 // fakeLayer.textMode(CENTER);
  fakeLayer.textSize(40);
  fakeLayer.text("+", 550, 40);
  fakeLayer.fill(#7500FC);
  fakeLayer.rect(600, 0, 50, 50 );
  fakeLayer.fill(255);
  fakeLayer.text("-", 610, 40);
  fakeLayer.imageMode(CENTER);
  if (flag==1) {
    fakeLayer.image(img2, handVec.x, handVec.y);
    if (handVec.x< 590 && 540<handVec.x && handVec.y< 50 && 0<handVec.y)
    { 
      fakeLayer.noStroke();
      fakeLayer.fill(Color);
      size ++;
      fakeLayer.ellipse(handVec.x, handVec.y+50, size, size);
    }
    else if ( handVec.x< 650 && 600<handVec.x && handVec.y< 50 && 0<handVec.y)
    {
      noStroke();
      fakeLayer.fill(Color);
      size--;
      fakeLayer.ellipse(handVec.x, handVec.y+50, size, size);
      if (size<0) {
        size=1;
      }
    }
  }
  else {
    fakeLayer.image(img1, handVec.x, handVec.y);
  }
  image(fakeLayer, 0, 0);
  fakeLayer.endDraw();

  popStyle();
}
float t, l;
void grids() {  
  for (int i=0;i <width;i+=dodge) { 
    for (int j=0;j<height;j+=dodge) {  
      fill(0);    
      textSize(8);      
      text("+", i, j);
    }
  }
}
// -----------------------------------------------------------------
// hand events

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
  {  
    handVecList.remove(handVecList.size()-1);
  }
}

void onDestroyHands(int handId, float time)
{
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);

  handsTrackFlag = false;
  kinect.addGesture(lastGesture);
}

// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
{
  println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);

  lastGesture = strGesture;
  kinect.removeGesture(strGesture); 
  kinect.startTrackingHands(endPosition);
}

void onProgressGesture(String strGesture, PVector position, float progress)
{
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}

