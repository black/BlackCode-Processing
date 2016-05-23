import SimpleOpenNI.*;
SimpleOpenNI context;
boolean      handsTrackFlag = false;
PVector      handVec = new PVector();
PVector      handVec2D  = new PVector();//just for drawing
String       lastGesture = "";
float        lastZ = 0;
boolean      isPushing,wasPushing;
float        yourClickThreshold = 20;//set this up as you see fit for your interaction

void setup(){
  size(640,480);  
  context = new SimpleOpenNI(this);
  context.enableDepth();
  // enable hands + gesture generation
  context.enableGesture();
  context.enableHands();
  // add focus gestures  / here i do have some problems on the mac, i only recognize raiseHand ? Maybe cpu performance ?
  context.addGesture("Wave");
  context.addGesture("Click");
  context.addGesture("RaiseHand");

}

void draw()
{
  context.update();
  image(context.depthImage(),0,0);
  // draw the tracked hand
  if(handsTrackFlag){
    context.convertRealWorldToProjective(handVec,handVec2D);
    float diff = (handVec.z-lastZ);
    isPushing = diff < 0;
    if(diff > yourClickThreshold){
      if(!wasPushing && isPushing) fill(255,0,0);
      if(wasPushing && !isPushing) fill(0,255,0);
    }else fill(255);
    lastZ = handVec.z;
    wasPushing = isPushing;
    ellipse(handVec2D.x,handVec2D.y,10,10);
  }

}


// -----------------------------------------------------------------
// hand events

void onCreateHands(int handId,PVector pos,float time){
  println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);

  handsTrackFlag = true;
  handVec = pos;
}

void onUpdateHands(int handId,PVector pos,float time){
  //println("onUpdateHandsCb - handId: " + handId + ", pos: " + pos + ", time:" + time);
  handVec = pos;
}

void onDestroyHands(int handId,float time){
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);
  handsTrackFlag = false;
  context.addGesture(lastGesture);
}

// -----------------------------------------------------------------
// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition){
  if(strGesture == "Click") println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);

  lastGesture = strGesture;
  context.removeGesture(strGesture); 
  context.startTrackingHands(endPosition);
}

void onProgressGesture(String strGesture, PVector position,float progress){
  //println("onProgressGesture - strGesture: " + strGesture + ", position: " + position + ", progress:" + progress);
}
