import SimpleOpenNI.*;
import Blobscanner.*; 
import java.util.*;
import java.util.Map.*;
import ddf.minim.*;

SimpleOpenNI kinect;
Detector bs;
Minim minim;

AudioPlayer[] player = new AudioPlayer[16]; // for beatbox music 
LinkedHashMap<PVector, Integer> blobValues = new LinkedHashMap< PVector, Integer>();

PImage img, img1, img2, colimg;
PVector myinterest_key;
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
int size=10;
color lastcol = color(255);
color c;
int R, G, B;
float t, l;

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
  //----------------------------------------------------
  minim = new Minim(this); // initialaizing minim object
  for (int i=0; i<player.length;i++) {
    String str = "Incredibox.com_"+i  +".mp3"; // file name & path for beat box music
    //  String str = i+".mp3"; // file name and path for instrumental music
    player[i] = minim.loadFile(str); // load file in audio player array loadFile ( "FILE NAME");
  }  
  //----------------------------------------------------
  topLayer = createGraphics(displayWidth, displayHeight);
  bs = new Detector(this, 0, 0, displayWidth, displayHeight, 255);
  fakeLayer = createGraphics(displayWidth, displayHeight);
  img1 = loadImage("hand.png");
  img2 = loadImage("grab.png");
  //----------------------------------------------------
  perspective(radians(45), float(displayWidth)/float(displayHeight), 10.0f, 150000.0f);
}
void draw()
{
  kinect.update();
  background(255);
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
  handVec.x = (int)map(handVec.x, 0, kinect.depthWidth(), 0, displayWidth);
  handVec.y = (int)map(handVec.y, 0, kinect.depthHeight(), 0, displayHeight);
  //-----------------------
  pushStyle();
  rotateX(radians(180));
  scale(0.65f);
  imageMode(CENTER);
  topLayer.beginDraw();
  topLayer.smooth();
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
  popStyle();
  //----------------------------------------
  //---------------------------------
  img = topLayer.get(0, 0, width, height);
  colimg = img.get();
  img.filter(THRESHOLD, 0.2);
  img.loadPixels();
  bs.imageFindBlobs(img);
  bs.loadBlobsFeatures();
  bs.weightBlobs(false); 
  //*********************************************************************
  for (int i=0;i<bs.getBlobsNumber();i++) {
    PVector[] pixloc = bs.getBlobPixelsLocation(i);
    PVector p = new PVector(pixloc[0].x, pixloc[0].y); // taking out location of the pixle of the blob
    blobValues.put(p, i); // putting the location of the  blob id and its position into the Linked HashMap
  }
  if (flag==0)
    if (blobValues.size()>0) {
      final Set<Entry<PVector, Integer>> mapValues = blobValues.entrySet();
      final int maplength = mapValues.size();
      final Entry<PVector, Integer>[] test = new Entry[maplength];
      mapValues.toArray(test);
      myinterest_key = test[maplength-1].getKey(); // taking out the location of the last color detcted
      //-------------------------------------------------------------------------
      int blobcol = colimg.pixels[(int)myinterest_key.x + width*(int)myinterest_key.y]; // getting blob color from testLayer
      if (blobcol!=lastcol) {
        int r = (blobcol & 0x00ff0000) >> 16; // red channel (0-255)
        int g = (blobcol & 0x0000ff00) >> 8;  // green channel (0-255)
        int b = (blobcol & 0x000000ff);       // blue channel (0-255)
        int maxChannelValue = max(r, g, b);  
        if (maxChannelValue == r) {
          R = (int)random(0, 5);
          println("R:" + R);
          player[R].loop();
        }
        if (maxChannelValue == g) {
          G = (int)random(5, 10);
          println("G:" + G);
          player[G].loop();
        }  
        if (maxChannelValue == b) {
          B = (int)random(10, 16);
          println("B:" + B);
          player[B].loop();
        }
        lastcol =blobcol; // storing current blob color to last color for checking the next coming blob color with current blob color
      }
    }
  //***************************************************
  pushStyle();
  imageMode(CENTER);
  //-----------------------
  fakeLayer.beginDraw();
  fakeLayer.smooth();
  fakeLayer.background(255, 0);
  //-----------------------------------------
  fakeLayer.noStroke();
  fakeLayer.fill(0, 255, 0);
  fakeLayer.rect(0, 0, 50, 50);
  fakeLayer.fill(0, 0, 255);
  fakeLayer.rect(100, 0, 50, 50);
  if (flag==1 && handVec.x < 50 && handVec.y<50   ) {
    Color = fakeLayer.get((int)handVec.x, (int)handVec.y);
  }
  if (flag==1 && handVec.x < 100 && handVec.x > 150 && handVec.y<50   ) {
    Color = fakeLayer.get((int)handVec.x, (int)handVec.y);
  }
  //-----------------------------------------
  fakeLayer.fill(#7500FC);
  fakeLayer.rect(540, 0, 50, 50 );
  fakeLayer.fill(255);
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
  fakeLayer.endDraw();
  image(fakeLayer, 0, 0);
  popStyle();
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

