import SimpleOpenNI.*;

SimpleOpenNI kinect;

float        zoomF =0.5f;
float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
float        rotY = radians(0);

ArrayList<PVector> poop = new ArrayList();
ArrayList<Particle> particleList = new ArrayList();

void setup() {
  size(displayWidth, displayHeight, P3D);
  kinect = new SimpleOpenNI(this);
  if (kinect.isInit() == false) {
    println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
    exit();
    return;
  }
  kinect.setMirror(false);
  kinect.enableDepth();
  kinect.enableDepth();
  kinect.enableIR();
  kinect.enableUser();
}

void draw() { 
  background(0); 
  kinect.update(); 
  poop.clear();
  translate(width/2, height/2, 0);
  rotateX(rotX);
  rotateY(rotY);
  scale(zoomF); 
  /*----------------------------*/
  int[] depthValues = kinect.depthMap();
  int[] userMap =null;
  int userCount = kinect.getNumberOfUsers();
  println(userCount);
  if (userCount > 0) {
    userMap = kinect.userMap();
  }

  /*----------------------------*/
  int[]   depthMap = kinect.depthMap();
  int     steps   = 4;  // to speed up the drawing, draw every third point
  int     index;
  PVector realWorldPoint; 
  translate(0, 0, -1000);  
  stroke(255); 
  PVector[] realWorldMap = kinect.depthMapRealWorld(); 
  beginShape(POINTS);
  for (int y=0; y < kinect.depthHeight (); y+=steps) {
    for (int x=0; x < kinect.depthWidth (); x+=steps) {
      index = x + y * kinect.depthWidth();
      if (depthMap[index] > 0) { 
        // draw the projected point
        // realWorldPoint = kinect.depthMapRealWorld()[index];
        if (userMap != null && userMap[index] > 0) {
          realWorldPoint = realWorldMap[index];
          float xx = map(realWorldPoint.x, 0, 640, 0, width);
          float yy = map(realWorldPoint.y, 0, 480, 0, height);
          vertex(xx, yy, realWorldPoint.z);        
          poop.add(new PVector(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z));
        } else {
          continue;
        }
      }
    }
  } 
  endShape();  
  if (poop.size()>0) {
    for (int i=0; i<poop.size (); i++) {
      PVector P = poop.get(i);
      float xx = map(P.x, 0, 640, 0, width);
      float yy = map(P.y, 0, 480, 0, height);
      particleList.add(new Particle(xx, yy, P.z));
    }
  }
  if (particleList.size()>0) {
    for (int i=0; i<particleList.size (); i+=2) {
      Particle P = particleList.get(i);
      P.show();
      P.update();
      if (P.y<-height/2) {
        particleList.remove(i);
      }
    }
  }
}


void keyPressed() {
  switch(keyCode) {
  case LEFT:
    rotY += 0.1f;
    break;
  case RIGHT:
    // zoom out
    rotY -= 0.1f;
    break;
  case UP:
    if (keyEvent.isShiftDown())
      zoomF += 0.02f;
    else
      rotX += 0.1f;
    break;
  case DOWN:
    if (keyEvent.isShiftDown()) {
      zoomF -= 0.02f;
      if (zoomF < 0.01)
        zoomF = 0.01;
    } else
      rotX -= 0.1f;
    break;
  }
}


boolean sketchFullScreen() {
  return true;
}

