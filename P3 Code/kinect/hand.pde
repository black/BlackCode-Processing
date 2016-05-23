
String lastGesture;
boolean track = false;

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition){
  println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);
  lastGesture = strGesture;
  context.removeGesture(strGesture);
  context.startTrackingHands(endPosition);
}

void onCreateHands(int handId, PVector pos, float time){
  println("onCreateHands - handId: " + handId + ", pos: " + pos + ", time:" + time);
  if(drawSculpture && (splinePoints.getPointList().size() == 0)){
    track = true;
  }
}

void onUpdateHands(int handId, PVector pos, float time){
  if(track){
    splinePoints.add(new Vec3D(pos.x,pos.y,pos.z));
  }
  if(drawBalls){
    float velMag = random(50,80);
    float ang1 = random(0,TWO_PI);
    float ang2 = random(0,HALF_PI/3);
    PVector vel = new PVector(velMag*cos(ang1)*sin(ang2),velMag*cos(ang2),velMag*sin(ang1)*sin(ang2));
    balls.add(new Ball(PVector.add(pos,new PVector(0,50,0)),vel,30));
  }
}

void onDestroyHands(int handId, float time){
  println("onDestroyHandsCb - handId: " + handId + ", time:" + time);
  context.addGesture(lastGesture);
  track = false;
}

