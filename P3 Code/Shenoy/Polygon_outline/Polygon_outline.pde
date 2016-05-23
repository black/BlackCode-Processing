import diewald_CV_kit.libraryinfo.*;
import diewald_CV_kit.utility.*;
import diewald_CV_kit.blobdetection.*;
BlobDetector blob_detector;

import processing.opengl.*;
import SimpleOpenNI.*;
SimpleOpenNI kinect;
//---------------------------------------------------------
PGraphics topLayer; 
boolean      handsTrackFlag = false;
PVector      handVec = new PVector();
PVector      handVec2D  = new PVector();//just for drawing
String       lastGesture = "";
float        lastZ = 0;
boolean      isPushing, wasPushing;
float        yourClickThreshold = 90;
color colour;
//--------------------------------------------------------
// used by kinect to define users (see onNewUser())
int userID;
// pixel array formed by user (kinect internal processes)
int[] userMap;
// userInt color pixels where user pixel is found
int[] userInt;

// image holder for userInt pixel array, used for blob detection
PImage edgeImg;

// only one 'bond' at a time
Particle bond;
// restrain bond creator to call the bond again;
// this is modified by the way users and blobs are set as a list,
// giving interesting result in regards to the ownership of the bond
int bond_caller_id = -1;
PFont font;

// kinect viewable area, used to defined pixel tracking 
int kwidth = 640;
int kheight = 480;

public void setup() {
  size(640, 480, OPENGL);
  smooth();
  // initialize kinect object
  kinect = new SimpleOpenNI(this);
  // mirror the image
  kinect.setMirror(true);
  // enable depth and user raw data (no skeleton)
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
  //---------------------------------------------
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("Wave");
  kinect.addGesture("Click");
  kinect.addGesture("RaiseHand");
  //-----------------------------------------
  // define greyscale image
  edgeImg = createImage(kwidth, kheight, ALPHA);

  // initialize blob detector dimensions 
  blob_detector = new BlobDetector( kwidth, kheight);
  // level of details
  blob_detector.setResolution(1);
  // use of contour
  blob_detector.computeContours(true);
  blob_detector.computeBlobPixels(false);
  // blbol threshold
  blob_detector.setMinMaxPixels(12*12, 300*kheight);
  // assign blob detection to edgeImg
  blob_detector.setBLOBable(new BLOBable_Colorizer(this, edgeImg));

  // define pixel array according to kinect viewable area
  userInt = new int[kwidth*kheight];

  // define font
  font = loadFont("ArialMT-14.vlw");
  textFont(font);
  textAlign(CENTER, CENTER);

  // low framerate, for aesthetic purposes only
  frameRate(8);
}

void draw() {
  background(0);
  resetCallBond();
  kinect.update();

  if (handsTrackFlag) {
    kinect.convertRealWorldToProjective(handVec, handVec2D);
    float diff = (handVec.z-lastZ);
    isPushing = diff < 0;
    if (diff > yourClickThreshold) {
      if (!wasPushing && isPushing) fill(255, 0, 0);
      if (wasPushing && !isPushing) fill(0, 255, 0);
      colour = color(random(0, 255), random(0, 255), random(0, 255));
    } else fill(255);
    lastZ = handVec.z;
    wasPushing = isPushing;
    ellipse(handVec2D.x, handVec2D.y, 10, 10);
  }


  // if we have detected any users
  if (kinect.getNumberOfUsers() > 0) {
    // find out which pixels have users in them
    userMap = kinect.getUsersPixels(SimpleOpenNI.USERS_ALL);
    // load pixels array
    edgeImg.loadPixels();

    // iterate through usermap
    for (int y = 0; y < kheight; y++) {
      for (int x = 0; x < kwidth; x++) {
        int i = x + y * kwidth;
        // if the current pixel is on a user
        if (userMap[i] != 0) {
          // make userInt pixel white
          userInt[i] = 255;
        } else {
          userInt[i] = 0;
        }
        // update edegeImg pixels
        edgeImg.pixels[i] = color(userInt[i]);
      }
    }

    // update edgeImg pixels to display new image
    edgeImg.updatePixels();

    // Diewalk cv kit: update the blob_detector with the new pixelvalues from edgeImg (see setup() for blob detection initialization)
    blob_detector.update();

    // Diewalk cv kit: get a list of all the blobs
    ArrayList<Blob> blob_list = blob_detector.getBlobs();

    // get number of users to modify contour simplify repeat
    int userCount = kinect.getNumberOfUsers();
    // Diewalk cv kit: iterate through the blob_list
    for (int blob_idx = 0; blob_idx < blob_list.size (); blob_idx++ ) {

      // Diewalk cv kit: get the current blob from the blob-list
      Blob blob = blob_list.get(blob_idx);

      //
      // ASSIGN BLOB TO PARTICIPANT
      //


      // Diewalk cv kit: get the list of all the contours from the current blob
      ArrayList<Contour> contour_list = blob.getContours();

      // Diewalk cv kit: iterate through contour_list
      for (int contour_idx = 0; contour_idx < contour_list.size (); contour_idx++ ) {

        // Diewalk cv kit: get the current contour from  contour_list
        Contour contour = contour_list.get(contour_idx);

        // Diewalk cv kit: get the current boundingbox from the current contour
        BoundingBox bb = contour.getBoundingBox();
        // use it to set min and maqx coordinates of participant

        // Diewalk cv kit: handle the first contour (outer contour = contour_idx == 0) different to the inner contours
        if (contour_idx == 0) {
          // simplifying ratio
          int repeat_simplifying = 4; // CONTROL CONTOUR
          // Diewalk cv kit: can improve speed, if the contour is needed for further work
          ArrayList<Pixel> contour_simple = Polyline.SIMPLIFY(contour, 4, 8);
          // repeat the simplifying process a view more times
          for (int simple_cnt = 0; simple_cnt < repeat_simplifying; simple_cnt++) {
            contour_simple= Polyline.SIMPLIFY(contour_simple, 2, simple_cnt);
          }
          // Diewalk cv kit: draw the simplified contour
          // drawContour(ArrayList<Pixel> pixel_list, int stroke_color, int fill_color, boolean fill, float stroke_weight)

          drawContour(contour_simple, color(255, 255, 255), colour, true, 4);
          // drawPoints(contour_simple, color(0, 0, 200), 4);

          // EACH PARTICIPANT CONTOUR IS TRACKED
          // users[n].track() IS WHERE THE 'MAGIC' HAPPENS...
        } else {
          // Diewalk cv kit: we don't handle inner contours here
        }
      }
    }
  }

  // update bond if exists
  if (bond != null) {
    // update bond position (see Particle.run(x,y)) 
    bond.run(bond.callx, bond.cally);
    // increase timer (used for debounce)
    bond.timer++;
    // minimum y location for bond before being removed
    if (bond.loc.y < -150) {
      bond = null;
      bond_caller_id = -1;
      // if bond has reached destination, remove bond
    } else if (dist(bond.loc.x, bond.loc.y, bond.callx, bond.cally) < 7) {
      bond = null;
      bond_caller_id = -1;
    }
  }
}

// used to call bond from participant tracked body gesture call (arm stretch)
void callBond(float x_, float y_) {
  // increase speed
  bond.speed ++;
  // set destination coordinates
  bond.callx = x_;
  bond.cally = y_;
}

// reset bond speed and destination coordinates
void  resetCallBond() {
  if (bond != null) {
    bond.speed = 4;
    bond.callx = bond.loc.x;
    bond.cally = -150;
  }
}

// get bond timer (from Participant)
int getBondTimer() {
  return bond.timer;
}

// check if bond (from Participant)
Boolean noBond() {
  if (bond == null) {
    return true;
  } else {
    return false;
  }
}

// create new bond using creator id and arm stretch tip position (from Participant)
void createBond(int id_, float x_, float y_) {
  // !!!! just noticed a mistake in the code which makes creator the caller. interesting behaviour because of that!
  bond_caller_id = id_;
  // new particle as bond
  bond = new Particle(new PVector(x_, y_), 20, 5, -10, 1);
  bond.speed = 4;
}

// used by kinect
void onNewUser(int uID) {
  userID = uID;
  println("tracking");
}

//------------------------ OBJECT --------------------------------------

//////////////// particle
//// !!!!!! this particle class was used in my previous PFA project.
// it has been slightly altered to accomodate new aspects of this project (text display, timer, callx, cally),
// but it is omstly used for the particles physical properties

class Particle {

  // PVector is a class in Processing that makes it easier to store
  //values, and make calculations based on these values. It can make
  //applying forces to objects much easier and more efficient!

  PVector loc; //location vector
  PVector vel; //velocity vector
  PVector acc; //acceleration vector
  float sz;  //size of particle
  float gravity;  //gravity variable
  float mass;  //mass variable
  float velocityLimit = 5;  //the maximum velocity a particle can travel at
  float d;  //distance variable between particle and the target co-ordinates
  float speed;
  color c;
  int bounding_box;

  int timer;
  String content;

  float shape;
  float callx, cally;

  //CONSTRUCTOR
  public Particle(PVector _loc, float _sz, float _gravity, float _mass, int _b) {
    loc = _loc.get();  //when calling loc, return current location of the selected particle
    vel = new PVector(0, 0);  //set vel and acc vectors to 0 as default
    acc = new PVector(0, 0);
    sz = _sz;
    gravity = _gravity;
    mass = _mass;
    c = color(255, 255, 255, 255);
    bounding_box = _b;
    timer = 0;
  }


  //method to render the particle. control how it looks here!
  void display() {
    noStroke();
    fill(c);
    text(content, loc.x, loc.y- textDescent()*2);
    //ellipseMode(CENTER);
    //ellipse(, loc.y, sz, sz);
  }

  //math for attraction and repulsion forces
  //tx and ty are the co-ordinates attraction/repulsion will be applied to
  void forces(float tx, float ty) {
    PVector targetLoc = new PVector(tx, ty);  //creating new vector for attractive/repulsive x and y values
    PVector dir = PVector.sub(loc, targetLoc);  //calculate the direction between a particle and targetLoc
    d = dir.mag();  //calculate how far away the particle is from targetLoc
    dir.normalize();  //convert the measurement to a unit vector

    //calculate the strength of the force by factoring in a gravitational constant and the mass of a particle
    //multiply by distance^2
    float force = (gravity*mass) / (d*d);
    dir.mult(speed);
    //apply directional vector
    applyForce(dir);
  }

  //method to apply a force vector to the particle
  void applyForce(PVector force) {
    force.div(mass);
    acc.add(force);
  }

  //method to update the location of the particle, and keep its velocity within a set limit
  void update() {
    // spiral effect disabled for better control of all forces
    // rotate2D(acc,45);
    vel.add(acc);
    vel.limit(velocityLimit);
    loc.add(vel);
    acc.mult(0);
  }

  //main method that combines all previous methods, and takes two arguments
  //tx and ty are inherited from forces(), and set the attractive/repulsive co-ords
  void run(float tx, float ty) {
    forces(tx, ty);
    display();
    //bounds();
    update();
  }
}
//-------------------------------------------------
void onCreateHands(int handId, PVector pos, float time) {
  println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);

  handsTrackFlag = true;
  handVec = pos;
}

void onUpdateHands(int handId, PVector pos, float time) {
  //println("onUpdateHandsCb - handId: " + handId + ", pos: " + pos + ", time:" + time);
  handVec = pos;
}

void onDestroyHands(int handId, float time) {
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);
  handsTrackFlag = false;
  kinect.addGesture(lastGesture);
}

// -----------------------------------------------------------------
// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  if (strGesture == "Click") println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);

  lastGesture = strGesture;
  kinect.removeGesture(strGesture); 
  kinect.startTrackingHands(endPosition);
}

void onProgressGesture(String strGesture, PVector position, float progress) {
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}

