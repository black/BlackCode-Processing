/* --------------------------------------------------------------------------
 * SimpleOpenNI Kinect_Interfaceb
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect library
 * http://code.google.com/p/simple-openni
 * ----------------------------------------------------------------------------
 * ----------------------------------------------------------------------------
 */

//FOR TOMORROW:
//CHECK TO Make thumbChecker work with thumbs detected by boundingBoxDetection. Then party hardy
//the prevThumb problem

import SimpleOpenNI.*;
import oscP5.*;
import netP5.*;
import pbox2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import java.awt.Frame;

OscP5 oscP5;
NetAddress myRemoteLocation;
SimpleOpenNI context;

int currUser;
float        zoomF =0.5f;
float        rotX = radians(180);  // by default rotate the whole scene 180deg around the x-axis, 
// the data from openni comes upside down
float        rotY = radians(0);
//ALL GREEN NOW
color[]      userColors = { 
  color(0, 255, 0), color(0, 255, 0), color(0, 255, 00), color(0, 255, 0), color(0, 255, 0), color(0, 255, 0)
};
color[]      userCoMColors = { 
  color(255, 100, 100), color(100, 255, 100), color(100, 100, 255), color(255, 255, 100), color(255, 100, 255), color(100, 255, 255)
};
//Right Stuff
PVector head, torso, rightHand, imgRHand, rightWrist, rightElbow, rightShoulder, rightHip, rightThumb, rightPinky;
//Left Stuff
PVector leftHand, leftWrist, leftElbow, leftShoulder, leftHip, leftThumb, leftPinky;
PVector[] rightFingers, leftFingers;

//LOOKS LIKE FIVE IS THE NORM MAX, SO LET'S DO SIX
int leftThumbNullCounter=0;
int rightThumbNullCounter=0;
int closedLevel=4;
int openLevel=-4;
BoundingBox bBoxLeft, bBoxRight;


//Gestural stuff
GestureController gc;
int gestureCounter=0;
String[] gestures;

//Finger tracking and joints
float savedZ, savedShoulderWidth, savedLowerArmMag;
PImage lHand, rHand;
PFrame frameLeft, frameRight, f3;

//FingerDetector Objects
FingerDetector left, right;

int rateChange = 16, timer=100;

//TESTING: SMARTERHAND PLACEMENT
float lowerArmOrigMag=-1, lowerArmOrigZ=0;


void setup() {
  size(1024, 768, P3D); 
  frameLeft=new PFrame("Left Hand", 400, 400);
  frameRight=new PFrame("Right Hand", 400, 400);
  //f3=new PFrame("Text Stuff",400,200);
  //f3.s.textFont(font); 


  context = new SimpleOpenNI(this);

  rHand = new PImage(200, 400);
  rightWrist= new PVector();

  savedZ=0.0f;

  // disable mirror
  context.setMirror(false);

  // enable depthMap generation 
  if (context.enableDepth() == false)
  {
    println("Can't open the depthMap, maybe the camera is not connected!");
    exit();
    return;
  }

  context.enableDepth();

  // enable skeleton generation for all joints
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

  // start oscP5, telling it to listen for incoming messages at port 5001 */
  oscP5 = new OscP5(this, 50001);

  // set the remote location to be the localhost on port 5001
  myRemoteLocation = new NetAddress("127.0.0.1", 57131);

  strokeWeight(8);
  stroke(255, 255, 255);

  //Try this if you can't grab images for whatever reason1
  //loadPixels();
  //testImg= new PImage(0,0);

  //WARNING: Putting this back to 90 from 60
  frameRate(90);
}


void draw()
{
  // update the cam
  context.update();
  background(0, 0, 0);

  //GESTURAL STUFF

  if (rightHand!=null) {
    if (gestureCounter<3) {
      gestureCounter++;
    }
    else {
      gestureCounter=0;
      PVector[] skeleton= new PVector[11];
      skeleton[0]=head;
      skeleton[1]=rightHand;
      skeleton[2]=rightWrist;
      skeleton[3]=rightElbow;
      skeleton[4]=rightShoulder;
      skeleton[5]=rightHip;
      skeleton[6]=leftHand;
      skeleton[7]=leftWrist;
      skeleton[8]=leftElbow;
      skeleton[9]=leftShoulder;
      skeleton[10]=leftHip;

      if (gc==null) {

        gc=new GestureController(skeleton);
      }
      else {

        gestures = gc.update(skeleton);

        String forPrinting = "This time I saw: ";

        //f3.s.fill(0);
        //f3.s.rect(0,0,400,200);

        for (int i = 0; i<gestures.length; i++) {
          if (gestures[i]!=null) {
            forPrinting+= gestures[i]+", ";
          }
        }

        //f3.s.fill(255);

        //f3.s.text(forPrinting,5,5,300,150);
      }
    }
  }

  // for all users from 1 to 10
  int i;
  for (i=1; i<=5; i++)
  {
    // check if the skeleton is being tracked
    if (context.isTrackingSkeleton(i))
    {

      sendJointPosition(i);
      //SET ALL THE JOINTS

      //CURRENTLY THIS JUST GRABS THE MOST RECENT USERS, GONNA WANT TO 
      setJoints(i);
    }
  }


  if (rightHand!=null) {
    //If the left fingerDetector is null
    if (left==null) {
      leftWrist = setWrist(leftHand, leftElbow, leftShoulder);
      left = new FingerDetector(leftHand, leftWrist);

      savedShoulderWidth = PVector.sub(leftShoulder, rightShoulder).mag();
      savedZ = leftElbow.z;
      savedLowerArmMag =  PVector.sub(leftHand, leftElbow).mag();
    }
    else {
      PVector lowerArm= PVector.sub(leftHand, leftElbow);

      float thisLowerArmMag = lowerArm.mag();

      float equivalentMagnitude = (thisLowerArmMag/leftElbow.z)*savedZ;

      //Doesn't seem to do anything? Unclear. Keep it in and remove it if testing becomes necessary
      if (equivalentMagnitude>savedLowerArmMag) {
        //println("Did this ever happen? Saved Lower Arm Mag:" +savedLowerArmMag);
        lowerArm.normalize();
        lowerArm.mult(savedLowerArmMag);

        lowerArm.add(leftElbow);

        leftHand = lowerArm;
      }


      leftWrist = setWrist(leftHand, leftElbow, left);
    }

    //If the right fingerDetector is null
    if (right ==null) {
      rightWrist = setWrist(rightHand, rightElbow, rightShoulder);
      //For some reason right wrist is twice as close as left, so we'll double this side

      //Fixes the right hand being too short issue
      PVector newWrist = PVector.sub(rightWrist, rightHand);
      newWrist.normalize();
      newWrist.mult(PVector.sub(leftWrist, leftHand).mag());
      newWrist.add(rightHand);
      rightWrist = newWrist;

      right = new FingerDetector(rightHand, rightWrist);
    }
    else {
      //Try to keep the hand at a more generalized location
      PVector testLowerArm = PVector.sub(rightHand, rightElbow);
      float reference = (lowerArmOrigZ/ ((rightElbow.z+rightHand.z)/2.0f))*(testLowerArm.mag() );



      rightWrist = setWrist(rightHand, rightElbow, right);
    }

    //SETTING UP OF BOUNDING BOXES

    PVector[] leftCorners = establishBoundingBox(leftHand, leftWrist);

    if (leftCorners!=null) {
      if (bBoxLeft==null) {
        bBoxLeft = new BoundingBox(leftCorners[0], leftCorners[1], leftCorners[2], leftCorners[3]);
      }
      else {
        bBoxLeft.reset(leftCorners[0], leftCorners[1], leftCorners[2], leftCorners[3]);
      }
    }

    PVector[] rightCorners = establishBoundingBox(rightHand, rightWrist);

    if (rightCorners!=null) {
      if (bBoxRight==null) {
        bBoxRight = new BoundingBox(rightCorners[0], rightCorners[1], rightCorners[2], rightCorners[3]);
      }
      else {
        bBoxRight.reset(rightCorners[0], rightCorners[1], rightCorners[2], rightCorners[3]);
      }
    }
  }

  // set the scene pos
  //ALL OF THESE ARE NECESSARY
  pushMatrix();
  translate(width/2, height/2, 0);
  rotateX(rotX);
  rotateY(rotY);
  translate(0, 0, -1000);  // set the rotation center of the scene 1000 infront of the camera

  //Draws the Depth Map but ONLY for the hands
  drawDepthHands(2);
  popMatrix();

  if (bBoxRight!=null) {


    float minX = bBoxRight.getMinX();
    float minY = bBoxRight.getMaxY();

    //TESTING DIS 11/25 @ 10:19 PM
    PVector minVec = new PVector(minX, minY, rightElbow.z);

    float maxX = bBoxRight.getMaxX();
    float maxY = bBoxRight.getMinY();

    PVector maxVec = new PVector(maxX, maxY, rightElbow.z);

    PVector minPos_Proj = new PVector(); 
    context.convertRealWorldToProjective(minVec, minPos_Proj);

    PVector maxPos_Proj = new PVector(); 
    context.convertRealWorldToProjective(maxVec, maxPos_Proj);

    PVector rightHand_Proj = new PVector();
    context.convertRealWorldToProjective(rightHand, rightHand_Proj);

    //ellipse(rightHand.x,rightHand.y,15,15);

    PVector rightWrist_Proj = new PVector();
    context.convertRealWorldToProjective(rightWrist, rightWrist_Proj);

    rightHand_Proj.x+=width/8;
    rightHand_Proj.y+=height/8;

    int imgW = (int)maxPos_Proj.x-(int)minPos_Proj.x;
    int imgH = (int) maxPos_Proj.y-(int)minPos_Proj.y;




    //PImage rHand = new PImage();

    //WARNING: This here is a problem, but I don't know how exactly to fix

    //color[] rightPixels = new color[imgW*3*imgH*3];

    int rHandImgX = (int)minPos_Proj.x+(int)(width*(2.0/16.0));
    int rHandImgY = (int)minPos_Proj.y+(int)(height*(2.0/16.0));

    if (imgW>10 && imgH>10 && rHandImgX>0 && rHandImgY>0) {
      rHand = get((int) rHandImgX, (int)rHandImgY, imgW*2, imgH*3);
    }

    int handPosX0=0;
    int handPosY0=0;
    boolean firstRed = false, firstBlue = false;
    PVector wrist = new PVector();
    //Grabs the first red (palm) and first blue (wrist) positions
    for (int y=0; y<rHand.height; y++) {
      for (int x=0; x<rHand.width; x++) {
        int ind = x + y * rHand.width;
        if (ind<rHand.pixels.length) {
          if (red(rHand.pixels[ind])==255 && blue(rHand.pixels[ind])==0) {//if ((rHand.pixels[ind]>> 16 & 0xFF)== 255 && (rHand.pixels[ind] & 0xFF)==0) {
            if (!firstRed) {
              handPosX0=x;
              handPosY0=y;
              firstRed=true;
            }
          }
          //If blue
          if (blue(rHand.pixels[ind])==255 && red(rHand.pixels[ind])==0) {//if ((rHand.pixels[ind] & 0xFF)== 255 && (rHand.pixels[ind]>> 16 & 0xFF)==0) {
            if (!firstBlue) {
              wrist = new PVector(x, y);
              firstBlue=true;
            }
          }
        }
      }
    }
    //println("Wrist before:"+wrist);
    PVector hand = new PVector(handPosX0, handPosY0);

    rHand.loadPixels();

    imgRHand = new PVector(rightHand.x-((int)minPos_Proj.x+(int)(width*(3.0/16.0))), rightHand.y-((int)minPos_Proj.y+(int)(height*(3.0/16.0))));
    PVector imgRWrist = new PVector(rightWrist_Proj.x-(minPos_Proj.x+(int)(width*(3.0/16.0))), rightWrist_Proj.y-(minPos_Proj.y+(int)(height*(3.0/16.0))));


    //Update finger detector
    right.update(rHand, hand, wrist);



    //I think changing the display meant that I couldn't draw the ellipses as that'd have to happen
    //inside of the secondaryApplet draw. (WHICH SOULD STILL HAPPEN IF THIS IS SLOWER
    frameRight.s.display = rHand;

    //DRAW THE THUMBS IF THERE WERE ANY  
    if (right!=null) {
      PVector thumb = right.thumbDetection();
      //println("Right Thumb: "+thumb);
      PVector[] fingerz = right.pickOutFingers();

      frameRight.s.hand = right.hand;  
      frameRight.s.thumb = thumb;
      frameRight.s.fingers = fingerz;

      frameRight.s.a = right.a;
      frameRight.s.b=right.b;
      frameRight.s.c = right.c;
      frameRight.s.d = right.d;

      if (thumb!=null) {
        rightThumb=thumb;
        rightThumbNullCounter=5;
      }
      else {
        if (rightThumbNullCounter<0) {
          rightThumb=null;
        }
        else {
          rightThumbNullCounter++;
        }
      }
    }
  }


  if (bBoxLeft!=null) {
    float minX = bBoxLeft.getMinX();
    float minY = bBoxLeft.getMaxY();

    PVector minVec = new PVector(minX, minY, leftElbow.z);

    float maxX = bBoxLeft.getMaxX();
    float maxY = bBoxLeft.getMinY();

    PVector maxVec = new PVector(maxX, maxY, leftElbow.z);

    PVector minPos_Proj = new PVector(); 
    context.convertRealWorldToProjective(minVec, minPos_Proj);

    PVector maxPos_Proj = new PVector(); 
    context.convertRealWorldToProjective(maxVec, maxPos_Proj);

    PVector leftHand_Proj = new PVector();
    context.convertRealWorldToProjective(leftHand, leftHand_Proj);

    PVector leftWrist_Proj = new PVector();
    context.convertRealWorldToProjective(leftWrist, leftWrist_Proj);

    leftHand_Proj.x+=width/8;
    leftHand_Proj.y+=height/8;

    int imgW = (int)maxPos_Proj.x-(int)minPos_Proj.x;
    int imgH = (int) maxPos_Proj.y-(int)minPos_Proj.y;

    PImage lHand = new PImage();

    //WARNING: This here is a problem, but I don't know how exactly to fix
    if (imgW>10 && imgH>10) {
      lHand = get((int)minPos_Proj.x, (int)minPos_Proj.y, imgW*2, imgH*3);
    }

    lHand.loadPixels();


    int handPosX0=0;
    int handPosY0=0;
    boolean firstRed = false, firstBlue = false;
    PVector wrist = new PVector();
    //Grabs the first red (palm) and first blue (wrist) positions
    for (int y=0; y<lHand.height; y++) {
      for (int x=0; x<lHand.width; x++) {
        int ind = x + y * lHand.width;
        if (ind<lHand.pixels.length) {
          if (red(lHand.pixels[ind])==255 && blue(lHand.pixels[ind])==0) {//if ((lHand.pixels[ind]>> 16 & 0xFF)== 255 && (lHand.pixels[ind] & 0xFF)==0) {
            if (!firstRed) {
              handPosX0=x;
              handPosY0=y;
              firstRed=true;
            }
          }
          //If blue
          if (blue(lHand.pixels[ind])==255 && red(lHand.pixels[ind])==0) {//if ((lHand.pixels[ind] & 0xFF)== 255 && (lHand.pixels[ind]>> 16 & 0xFF)==0) {
            if (!firstBlue) {
              wrist = new PVector(x, y);
              firstBlue=true;
            }
          }
        }
      }
    }
    //println("Wrist before:"+wrist);
    PVector hand = new PVector(handPosX0, handPosY0);

    //Adding this in for when the hand and wrist are in the same place
    if (lHand!=null && hand!= wrist) {
      left.update(lHand, hand, wrist);
    }


    frameLeft.s.display = left.out;

    if (left!=null && left.hand!=null) {
      PVector thumb = left.thumbDetection();
      PVector[] fingerz = left.pickOutFingers();


      // println("Left Thumb: "+thumb);

      frameLeft.s.thumb = thumb;
      frameLeft.s.fingers = fingerz;
      frameLeft.s.hand = left.hand;

      frameLeft.s.a = left.a;
      frameLeft.s.b=left.b;
      frameLeft.s.c = left.c;
      frameLeft.s.d = left.d;
      if (thumb!=null) {
        leftThumb=thumb;
        leftThumbNullCounter=5;
      }
      else {
        if (leftThumbNullCounter<0) {
          leftThumb=null;
        }
        else {
          leftThumbNullCounter++;
        }
      }
    }
  }


  // draw the center of mass
  /**
   //TRYING: Don't really need this
   PVector pos = new PVector();
   pushStyle();
   strokeWeight(20);
   for (int userId=1;userId <= userCount;userId++)
   {
   context.getCoM(userId, pos);
   
   stroke(userCoMColors[userId % userCoMColors.length]);
   point(pos.x, pos.y, pos.z);
   }  
   popStyle();
   popMatrix();
   */

  /**
   fill(255,0,0);
   rect(width/8,height/8,20,20);
   
   fill(0,255,0);
   rect(width/4,height/4,20,20);
   */
}

void setJoints(int i) {
  PVector jointPos = new PVector();
  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_LEFT_ELBOW, jointPos);
  leftElbow=jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_LEFT_HAND, jointPos);
  leftHand = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_LEFT_SHOULDER, jointPos);
  leftShoulder = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_TORSO, jointPos);
  torso= jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_RIGHT_ELBOW, jointPos);
  rightElbow = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_RIGHT_HAND, jointPos);
  rightHand = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_RIGHT_SHOULDER, jointPos);
  rightShoulder = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_HEAD, jointPos);
  head = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_LEFT_HIP, jointPos);
  leftHip = jointPos.get();

  context.getJointPositionSkeleton(i, SimpleOpenNI.SKEL_RIGHT_HIP, jointPos);
  rightHip = jointPos.get();
}

/**
 * Draws only the depth pixel version of the hands, with added bonus of commented out body pixels
 *
 * @param: steps amount of pixels to skip in between drawing next pixel
 */
void drawDepthHands(int steps) {
  //Index of point to be drawing. 
  int index;
  int[]   depthMap = context.depthMap();
  PVector realWorldPoint;

  //Get the map of all the various users and their pixels:
  int userCount = context.getNumberOfUsers();
  int[] userMap = null;
  if (userCount > 0)
  {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  }

  //Non-necessary to go through all the points, we only want hands
  //ADD THIS IN FUTURE
  for (int y=0;y < context.depthHeight();y+=steps)
  {
    for (int x=0;x < context.depthWidth();x+=steps)
    {
      index = x + y * context.depthWidth();
      if (depthMap[index] > 0)
      { 
        // get the realworld points
        realWorldPoint = context.depthMapRealWorld()[index];

        // check if there is a user
        if (userMap != null && userMap[index] != 0)
        {
          if (bBoxRight!=null && bBoxLeft!=null) {

            if (bBoxRight.contains(0, 0, realWorldPoint.x, realWorldPoint.y)) {
              if (!excludeFromRight(realWorldPoint)) {
                stroke(255);
                point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);

                //DRAW RED CIRCLE ON RIGHT HAND
                if (checkProximity(realWorldPoint, rightHand, 10.0f)) {
                  stroke(255, 0, 0);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }

                //DRAW BLUE CIRCLE ON RIGHT WRIST
                if (checkProximity(realWorldPoint, rightWrist, 10.0f)) {
                  stroke(0, 0, 255);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
              }
            }
            else if (bBoxLeft.contains(0, 0, realWorldPoint.x, realWorldPoint.y)) {
              if (!excludeFromLeft(realWorldPoint)) {
                stroke(255);
                point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                if (checkProximity(realWorldPoint, leftHand, 10.0f)) {
                  //DRAW RED BIT FOR HAND
                  stroke(255, 0, 0);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
                if (checkProximity(realWorldPoint, leftWrist, 10.0f)) {
                  //DRAW BLUE BIT FOR WRIST
                  stroke(0, 0, 255);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
              }
            }
            else {
              // stroke(0, 255, 0);
              // point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
            }
          }

          else if (bBoxRight!=null) {

            if (bBoxRight.contains(0, 0, realWorldPoint.x, realWorldPoint.y)) {
              if (!excludeFromRight(realWorldPoint)) {
                stroke(255);
                point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                if (checkProximity(realWorldPoint, rightHand, 10.0f)) {
                  stroke(255, 0, 0);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }


                if (checkProximity(realWorldPoint, rightWrist, 10.0f)) {
                  stroke(0, 0, 255);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
              }
            }
          }
          else if (bBoxLeft!=null) {

            if (bBoxLeft.contains(0, 0, realWorldPoint.x, realWorldPoint.y)) {
              if (!excludeFromLeft(realWorldPoint)) {
                stroke(255);
                point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                if (checkProximity(realWorldPoint, leftHand, 10.0f)) {
                  stroke(255, 0, 0);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
                if (checkProximity(realWorldPoint, leftWrist, 10.0f)) {
                  stroke(0, 0, 255);
                  point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
              }
            }
          }
          else {
            stroke(0, 255, 0);
            point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
          }
        }
      }
    }
  }
}

//Determines whether pixel should be excluded from right hand
boolean excludeFromRight(PVector pixel) {
  boolean exclude = false;
  //Shoulder and hip
  if (checkProximity(pixel, rightShoulder, 40.0f) || checkProximity(pixel, rightHip, 40.0f)) {
    exclude=true;
  }

  if (rightShoulder.x>pixel.x) {
    exclude=true;
  }

  return exclude;
}

//Determines whether pixel should be excluded from left hand
boolean excludeFromLeft(PVector pixel) {
  boolean exclude = false;
  //Shoulder and hip
  if (checkProximity(pixel, leftShoulder, 40.0f) || checkProximity(pixel, leftHip, 40.0f)) {
    exclude=true;
  }

  if (leftShoulder.x<pixel.x) {
    exclude=true;
  }

  return exclude;
}


//Find everything that needs replacing
boolean checkProximity(PVector pixel, PVector check, float distWithin) {
  float measure = distWithin;
  boolean closeEnough = false;
  PVector h = check;
  if (h!=null) {
    if (h.x+measure> pixel.x && h.x-measure<pixel.x) {
      if (h.y+measure>pixel.y && h.y-measure<pixel.y) {
        closeEnough=true;
      }
    }
  }

  return closeEnough;
}

void drawCircleForWrist(PVector wristPos) {
  //DRAWING THE WRIST
  // convert real world point to projective space
  PVector jointPos_Proj = new PVector(); 
  context.convertRealWorldToProjective(wristPos, jointPos_Proj);
  wristPos.z=0;

  fill(0, 0, 255);
  // draw the circle at the position of the head with the hand size scaled by the distance scalar
  ellipse(jointPos_Proj.x, jointPos_Proj.y, 500, 500);
}


/**
 Establishes the passed in BoundingBox in accordance with the hand and wrist vectors passed in.
 Instantiates BoundingBox if it doesn't already exist, otherwise goes ahead and just resets.
 
 
 */
PVector[] establishBoundingBox(PVector handIn, PVector wristIn) {

  //In the future should 
  boolean closeEnough = false;
  PVector h = new PVector(handIn.x, handIn.y, handIn.z);

  //Move "Wrist" point as being 1/8th further down, such that we get the 
  //Entire wrist inside the boundingBox

  PVector palmToWrist = PVector.sub(wristIn, handIn);

  palmToWrist.mult(10.0/8.0);

  palmToWrist.add(handIn);

  PVector wrist = new PVector(palmToWrist.x, palmToWrist.y, palmToWrist.z);

  if (h!=null) {
    if (wrist!=null) {
      //Try adding this in
      //wristPos.z=0;

      //Calculate edge of hand
      PVector h2 =PVector.sub(h, wrist);

      //println("H2 before mult: "+h2);
      h2.mult(3);
      //println("H2 after mult: "+h2);

      PVector handLength = h2;
      h2.add(wrist);

      //println("WristPos: "+wristPos);
      //println("h2 :" +h2); 

      PVector one = PVector.sub(wrist, h2);
      //0.4 is a bit big, 0.25 is a bit small
      one.mult(0.5f);
      float orig1X = one.x;
      float orig1Y = one.y;


      one.x=-1*orig1Y;
      one.y=orig1X;


      PVector two=new PVector(one.x, one.y, one.z);
      one.add(wrist);
      two.mult(-1);

      two.add(wrist);

      PVector four = PVector.sub(h2, wrist);
      //
      four.mult(0.5f);

      float orig4X = four.x;
      float orig4Y=four.y;
      four.x=orig4Y;
      four.y=-1*orig4X;

      PVector three = new PVector(four.x, four.y, four.z);

      //Might have to change the h2 to hand if the above multiplier 
      //actually works now
      four.add(h2);
      three.mult(-1);
      three.add(h2);

      PVector[] corners = new PVector[4];

      corners[0]=one;
      corners[1]=two;
      corners[2]=three;
      corners[3]=four;

      return corners;
    }
  }
  return null;
}


/**
 This set wrist is used priot to the FingerDetector
 being established, coming up with the wrist in the less precise way
 
 FIRST TIME
 
 */
PVector setWrist(PVector h, PVector e, PVector s) {
  PVector wristPos=null;
  PVector upperArm = PVector.sub(e, s);
  //println("Upper arm length: "+upperArm.mag());

  PVector lowerArm = PVector.sub(e, h);
  //println("Forearm length: "+lowerArm.mag());

  //TESTING: GONNA NEED FOR BOTH HANDS IF IT WORKS
  if (h==rightHand && lowerArmOrigMag==-1) {
    lowerArmOrigMag = lowerArm.mag();
    lowerArmOrigZ = (e.z+h.z)/2.0f;
  }

  //normalizes lower arm
  lowerArm.normalize();

  //Prediction based on the avergae body for where the wrist should be
  lowerArm.mult(-1*(2.0f/2.5f)*upperArm.mag());

  //Adds an approximation of lower arm to elbow, which should therefore equal near where the wrist is
  wristPos= PVector.add(e, lowerArm);

  wristPos.z=e.z;
  return wristPos;
}


/**
 This set wrist makes use of the hand, elbow, and a non-null FingerDetector to 
 determine where the wrist is based on saved variables in the detector
 
 
 */
PVector setWrist(PVector hand, PVector elbow, FingerDetector detector) {
  PVector wristPos = new PVector();

  PVector lowerArm = PVector.sub(elbow, hand);

  lowerArm.normalize();

  //Find what multiplier to multiply the lower arm by
  float currHandLength = (elbow.z)*detector.initialHandLength/2.0;
  currHandLength = currHandLength/detector.initialWristZ;

  //TRYING
  currHandLength *= 1.5f;

  lowerArm.mult(currHandLength);

  wristPos = PVector.add(hand, lowerArm);

  return wristPos;
} 


void sendJointPosition(int userId)
{
  PVector jointPos = new PVector();   // create a PVector to hold joint positions

  //Arms are reversed/mirrored. 

  /////////////////
  //LEFT SIDE STUFF
  ////////////////

  // get the joint position of the left hand
  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, jointPos);

  // create an osc message
  OscMessage lefthandMessage = new OscMessage("/lefthand");

  // send joint position of y axis by OSC
  lefthandMessage.add(jointPos.x);
  lefthandMessage.add(jointPos.y); 
  lefthandMessage.add(jointPos.z);

  int leftOpened = 0;
  if (leftThumb!=null) {
    leftOpened=1;
  }
  lefthandMessage.add(leftOpened);

  OscMessage leftthumbMessage = new OscMessage("/leftthumb");
  if (leftThumb!=null) {
    leftthumbMessage.add(leftThumb.x);
    leftthumbMessage.add(leftThumb.y);
  }

  OscMessage leftfingersMessage = new OscMessage("/leftfingers");
  if (leftFingers!=null) {
    for (int i = 0; i<leftFingers.length; i++) {
      if (leftFingers[i] !=null) {
        leftfingersMessage.add(leftFingers[i].x);
        leftfingersMessage.add(leftFingers[i].y);
      }
    }
  }

  OscMessage leftwristMessage = new OscMessage("/leftwrist");
  if (leftWrist!=null) {  
    leftwristMessage.add(leftWrist.x);
    leftwristMessage.add(leftWrist.y);
    leftwristMessage.add(leftWrist.z);
  }


  OscMessage leftelbowMessage = new OscMessage("/leftelbow");
  if (leftElbow !=null) {  
    leftelbowMessage.add(leftElbow.x);
    leftelbowMessage.add(leftElbow.y);
    leftelbowMessage.add(leftElbow.z);
  }


  OscMessage leftshoulderMessage = new OscMessage("/leftshoulder");
  if (leftShoulder!=null) {  
    leftshoulderMessage.add(leftShoulder.x);
    leftshoulderMessage.add(leftShoulder.y);
    leftshoulderMessage.add(leftShoulder.z);
  }

  //////////////////
  //RIGHT SIDE STUFF
  /////////////////

  // get the joint position of the right hand
  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, jointPos);

  // create an osc message
  OscMessage righthandMessage = new OscMessage("/righthand");

  // send joint position of y axis by OSC
  righthandMessage.add(jointPos.x);
  righthandMessage.add(jointPos.y); 
  righthandMessage.add(jointPos.z);

  int rightOpened = 0;
  if (rightThumb!=null) {
    rightOpened =1;
  }

  righthandMessage.add(rightOpened);


  OscMessage rightthumbMessage = new OscMessage("/rightthumb");
  if (rightThumb!=null) {
    rightthumbMessage.add(rightThumb.x);
    rightthumbMessage.add(rightThumb.y);
  }

  OscMessage rightfingersMessage = new OscMessage("/rightfingers");
  if (rightFingers!=null) {
    for (int i = 0; i<rightFingers.length; i++) {
      if (rightFingers[i] !=null) {
        rightfingersMessage.add(rightFingers[i].x);
        rightfingersMessage.add(rightFingers[i].y);
      }
    }
  }

  OscMessage rightwristMessage = new OscMessage("/rightwrist");
  if (rightWrist!=null) {  
    rightwristMessage.add(rightWrist.x);
    rightwristMessage.add(rightWrist.y);
    rightwristMessage.add(rightWrist.z);
  }


  OscMessage rightelbowMessage = new OscMessage("/rightelbow");
  if (rightElbow!=null) {  
    rightelbowMessage.add(rightElbow.x);
    rightelbowMessage.add(rightElbow.y);
    rightelbowMessage.add(rightElbow.z);
  }


  OscMessage rightshoulderMessage = new OscMessage("/rightshoulder");
  if (rightShoulder!=null) {  
    rightshoulderMessage.add(rightShoulder.x);
    rightshoulderMessage.add(rightShoulder.y);
    rightshoulderMessage.add(rightShoulder.z);
  }

  //////////////////
  //HEAD AND TORSO
  //////////////////  

  // get the joint position of the right hand
  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, jointPos);

  // create an osc message
  OscMessage headMessage = new OscMessage("/head");


  // send joint position of all axises by OSC
  headMessage.add(jointPos.x);
  headMessage.add(jointPos.y); 
  headMessage.add(jointPos.z);

  //Torso
  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_TORSO, jointPos);

  OscMessage torsoMessage = new OscMessage("/torso");

  torsoMessage.add(jointPos.x);
  torsoMessage.add(jointPos.y); 
  torsoMessage.add(jointPos.z);


  //LEFT LOWER BITS

  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP, jointPos);

  OscMessage leftHipMessage = new OscMessage("/lefthip");

  leftHipMessage.add(jointPos.x);
  leftHipMessage.add(jointPos.y);
  leftHipMessage.add(jointPos.z);

  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_KNEE, jointPos);

  OscMessage leftKneeMessage = new OscMessage("/leftknee");

  leftKneeMessage.add(jointPos.x);
  leftKneeMessage.add(jointPos.y);
  leftKneeMessage.add(jointPos.z);

  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_FOOT, jointPos);

  OscMessage leftFootMessage = new OscMessage("/leftfoot");

  leftFootMessage.add(jointPos.x);
  leftFootMessage.add(jointPos.y);
  leftFootMessage.add(jointPos.z);


  //RIGHT LOWER BITS

  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HIP, jointPos);

  OscMessage rightHipMessage = new OscMessage("/righthip");

  rightHipMessage.add(jointPos.x);
  rightHipMessage.add(jointPos.y);
  rightHipMessage.add(jointPos.z);

  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, jointPos);

  OscMessage rightKneeMessage = new OscMessage("/rightknee");

  rightKneeMessage.add(jointPos.x);
  rightKneeMessage.add(jointPos.y);
  rightKneeMessage.add(jointPos.z);

  context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_FOOT, jointPos);

  OscMessage rightFootMessage = new OscMessage("/rightfoot");

  rightFootMessage.add(jointPos.x);
  rightFootMessage.add(jointPos.y);
  rightFootMessage.add(jointPos.z);

  // Gestural Messaging
  OscMessage gesturesMessage = new OscMessage("/gestures");


  // send joint position of all axises by OSC
  if (gestures!=null) {
    for (int i = 0; i<gestures.length; i++) {
      if (gestures[i]!=null) {
        gesturesMessage.add(gestures[i]);
      }
    }
  }
  // send the messages
  //oscP5.send(gesturesMessage, myRemoteLocation);
  // send the messages

  //upper half

  oscP5.send(righthandMessage, myRemoteLocation);  
  //oscP5.send(rightwristMessage, myRemoteLocation); 
  //oscP5.send(rightelbowMessage, myRemoteLocation);
  //oscP5.send(rightshoulderMessage, myRemoteLocation);  

  oscP5.send(lefthandMessage, myRemoteLocation); 
  //oscP5.send(leftwristMessage, myRemoteLocation);
  //oscP5.send(leftelbowMessage, myRemoteLocation);
  //oscP5.send(leftshoulderMessage,myRemoteLocation);



  oscP5.send(headMessage, myRemoteLocation); 
  //oscP5.send(torsoMessage, myRemoteLocation);


  //Bottom half

  //oscP5.send(rightHipMessage, myRemoteLocation);  
  //oscP5.send(rightKneeMessage, myRemoteLocation); 
  //oscP5.send(rightFootMessage, myRemoteLocation); 

  //oscP5.send(leftHipMessage, myRemoteLocation); 
  //oscP5.send(leftKneeMessage, myRemoteLocation);
  // oscP5.send(leftFootMessage, myRemoteLocation);
}


void drawSkeleton(int userId)
{  
  // draw limbs  
  context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

  context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

  context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

  context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

  context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
}

// -----------------------------------------------------------------
// SimpleOpenNI user events



// draw the skeleton with the selected joints

// Event-based Methods

// when a person ('user') enters the field of view
void onNewUser(int userId)
{
  println("New User Detected - userId: " + userId);

  // start pose detection
  context.startPoseDetection("Psi", userId);
}

// when a person ('user') leaves the field of view 
void onLostUser(int userId)
{
  println("User Lost - userId: " + userId);
}

// when a user begins a pose
void onStartPose(String pose, int userId)
{
  println("Start of Pose Detected  - userId: " + userId + ", pose: " + pose);

  // stop pose detection
  context.stopPoseDetection(userId); 

  // start attempting to calibrate the skeleton
  context.requestCalibrationSkeleton(userId, true);
}

// when calibration begins
void onStartCalibration(int userId)
{
  println("Beginning Calibration - userId: " + userId);
}

// when calibaration ends - successfully or unsucessfully 
void onEndCalibration(int userId, boolean successfull)
{
  println("Calibration of userId: " + userId + ", successfull: " + successfull);

  if (successfull) 
  { 
    println("  User calibrated !!!");

    // begin skeleton tracking
    context.startTrackingSkeleton(userId);
    currUser=userId;
  } 
  else 
  { 
    println("  Failed to calibrate user !!!");

    // Start pose detection
    context.startPoseDetection("Psi", userId);
  }
}

