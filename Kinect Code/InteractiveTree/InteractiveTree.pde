import SimpleOpenNI.*;

SimpleOpenNI context;

float        zoomF =0.7f;
float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
// the data from openni comes upside down
float        rotY = radians(0);
boolean      autoCalib=true;

PVector      bodyCenter = new PVector();
PVector      bodyDir = new PVector();
PVector      com = new PVector();                                   
PVector      com2d = new PVector();                                   
color[]       userClr = new color[] { 
  color(255, 0, 0), 
  color(0, 255, 0), 
  color(0, 0, 255), 
  color(255, 255, 0), 
  color(255, 0, 255), 
  color(0, 255, 255)
};

float proportion=1;


void setup() {
  size(displayWidth, displayHeight, P3D);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem
  context = new SimpleOpenNI(this);
  if (context.isInit() == false)
  {
    println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
    exit();
    return;
  } 
  context.setMirror(false); 
  context.enableDepth(); 
  context.enableUser(); 
  perspective(radians(45), 
  float(width)/float(height), 
  10, 150000);
}
float ang1 = 0;
void draw() { 
  context.update();
  fill(0, 40);
  rect(0, 0, width, height);
  // set the scene pos
  pushMatrix();
  translate(width/2, height/2, 0);
  rotateX(rotX);
  rotateY(rotY);
  scale(zoomF);

  int[]   depthMap = context.depthMap();
  int[]   userMap = context.userMap();
  int     steps   = 3;  // to speed up the drawing, draw every third point
  int     index;
  PVector realWorldPoint;

  translate(0, 0, -1000);  // set the rotation center of the scene 1000 infront of the camera

  // draw the skeleton if it's available
  int[] userList = context.getUsers();
  for (int i=0; i<userList.length; i++) {
    if (context.isTrackingSkeleton(userList[i]))
      ang1 =  drawSkeleton(userList[i]);

    // draw the center of mass
    if (context.getCoM(userList[i], com)) {
      //      stroke(100, 255, 0);
      //      strokeWeight(1);
      //      beginShape(LINES);
      //      vertex(com.x - 15, com.y, com.z);
      //      vertex(com.x + 15, com.y, com.z);
      //
      //      vertex(com.x, com.y - 15, com.z);
      //      vertex(com.x, com.y + 15, com.z);
      //
      //      vertex(com.x, com.y, com.z - 15);
      //      vertex(com.x, com.y, com.z + 15);
      //      endShape();
      //      //
      //      fill(0, 255, 100);
      // text(Integer.toString(userList[i]), com.x, com.y, com.z);
    }
  }

  popMatrix();
  /*-----------------------*/
  translate(width/2, height-200);
  strokeWeight(10);
  stroke(-1);
  F(100);
  float ang =  map(ang1, -PI, PI, -PI/2, PI/6);
  float opening = map(mouseY, 0, height, 0, 90);
  println(ang1 + " " + ang + " " +degrees(ang));
  tree(1, 15, 100, proportion, 45, degrees(ang));
}

// draw the skeleton with the selected joints
float drawSkeleton(int userId) {
  strokeWeight(3);
  // to get the 3d joint data
  float ang = drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

  drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

  drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

  drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

  drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  

  // draw body direction
  getBodyDirection(userId, bodyCenter, bodyDir);

  bodyDir.mult(200);  // 200mm length 
  bodyDir.add(bodyCenter);

  stroke(255, 200, 200);
  // line(bodyCenter.x, bodyCenter.y, bodyCenter.z, 
  // bodyDir.x, bodyDir.y, bodyDir.z);
  return ang;
}

float drawLimb(int userId, int jointType1, int jointType2) {
  PVector jointPos1 = new PVector();
  PVector jointPos2 = new PVector();
  float  confidence;

  // draw the joint position
  confidence = context.getJointPositionSkeleton(userId, jointType1, jointPos1);
  confidence = context.getJointPositionSkeleton(userId, jointType2, jointPos2);

  // stroke(255, 0, 0, confidence * 200 + 55);
  // line(jointPos1.x, jointPos1.y, jointPos1.z, jointPos2.x, jointPos2.y, jointPos2.z);

  drawJointOrientation(userId, jointType1, jointPos1, 50);
  PVector ptemp = PVector.sub(jointPos1, jointPos2);
  float dir = ptemp.heading();
  return dir;
}

void drawJointOrientation(int userId, int jointType, PVector pos, float length) {
  // draw the joint orientation  
  PMatrix3D  orientation = new PMatrix3D();
  float confidence = context.getJointOrientationSkeleton(userId, jointType, orientation);
  if (confidence < 0.001f) 
    // nothing to draw, orientation data is useless
    return;

  pushMatrix();
  translate(pos.x, pos.y, pos.z);

  // set the local coordsys
  applyMatrix(orientation);

  // coordsys lines are 100mm long
  // x - r
  //  stroke(255, 0, 0, confidence * 200 + 55);
  //  line(0, 0, 0, length, 0, 0);
  //  // y - g
  //  stroke(0, 255, 0, confidence * 200 + 55);
  //  line(0, 0, 0, 0, length, 0);
  //  // z - b    
  //  stroke(0, 0, 255, confidence * 200 + 55);
  //  line(0, 0, 0, 0, 0, length);
  popMatrix();
}

// -----------------------------------------------------------------
// SimpleOpenNI user events

void onNewUser(SimpleOpenNI curContext, int userId) {
  println("onNewUser - userId: " + userId);
  println("\tstart tracking skeleton");
  context.startTrackingSkeleton(userId);
}

void onLostUser(SimpleOpenNI curContext, int userId) {
  println("onLostUser - userId: " + userId);
}

void onVisibleUser(SimpleOpenNI curContext, int userId) {
  //println("onVisibleUser - userId: " + userId);
}

boolean sketchFullScreen() {
  return true;
}
// -----------------------------------------------------------------
// Keyboard events
//
//void keyPressed() {
//  switch(key)
//  {
//  case ' ':
//    context.setMirror(!context.mirror());
//    break;
//  }
//
//  switch(keyCode)
//  {
//  case LEFT:
//    rotY += 0.1f;
//    break;
//  case RIGHT:
//    // zoom out
//    rotY -= 0.1f;
//    break;
//  case UP:
//    if (keyEvent.isShiftDown())
//      zoomF += 0.01f;
//    else
//      rotX += 0.1f;
//    break;
//  case DOWN:
//    if (keyEvent.isShiftDown())
//    {
//      zoomF -= 0.01f;
//      if (zoomF < 0.01)
//        zoomF = 0.01;
//    } else
//      rotX -= 0.1f;
//    break;
//  }
//}

void getBodyDirection(int userId, PVector centerPoint, PVector dir) {
  PVector jointL = new PVector();
  PVector jointH = new PVector();
  PVector jointR = new PVector();
  float  confidence;

  // draw the joint position
  confidence = context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, jointL);
  confidence = context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, jointH);
  confidence = context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, jointR);

  // take the neck as the center point
  confidence = context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, centerPoint);

  /*  // manually calc the centerPoint
   PVector shoulderDist = PVector.sub(jointL,jointR);
   centerPoint.set(PVector.mult(shoulderDist,.5));
   centerPoint.add(jointR);
   */

  PVector up = PVector.sub(jointH, centerPoint);
  PVector left = PVector.sub(jointR, centerPoint);

  dir.set(up.cross(left));
  dir.normalize();
}

