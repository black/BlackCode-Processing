import SimpleOpenNI.*;
SimpleOpenNI kinect;

ArrayList<PVector> handPositions;

PVector currentHand;
PVector previousHand;

int handx;
int handy;

int hits=0;
int barwidth = 70;
int barheight = 10;

ArrayList<Puck> puckcollection = new ArrayList();

void setup() {
  size (640, 480, P3D);
  Puck P = new Puck();
  puckcollection.add(P);
  //--------------------------------
  kinect = new SimpleOpenNI(this);
  kinect.setMirror(true);
  kinect.enableDepth();
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("RaiseHand");
  handPositions = new ArrayList();
}

void draw() {
  background(-1);
  Hockey_Ground();
  kinect.update();
  for (int i=1;i< handPositions.size(); i++) 
  {
    currentHand = handPositions.get(i);
    previousHand = handPositions.get(i-1);
    handx = int(currentHand.x);
    handy = int(currentHand.y);
  }

  for (int i=0; i< puckcollection.size(); i++) {
    Puck P = (Puck) puckcollection.get(i);
    P.display();
    P.update();
  }
  fill(0, 255, 255);
  if (hits > 0 && hits%4==0) { //  add new balls with the score increment of 4. you change it to desired score
    Puck P = new Puck();
    P.speedup();
    puckcollection.add(P);
    hits++;
    textSize(20);
    text("Score: " + (hits-1), width-100, 20);
  }
  else {
    textSize(20);
    text("Score: " + hits, width-100, 20);
  }

  if (puckcollection.size() >3) { // maximun  five puck would get add if you want more change 3 to your desired value
    puckcollection.remove(1);
  }

  pushStyle();
  rectMode(CENTER);
  rect(handx, height-150, barwidth, barheight);
  popStyle();
}

void onCreateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  handPositions.add(position);
}

void onUpdateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  handPositions.add(position);
}

void onDestroyHands(int handId, float time) {
  handPositions.clear();
  kinect.addGesture("RaiseHand");
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  kinect.startTrackingHands(endPosition);
  kinect.removeGesture("RaiseHand");
}

