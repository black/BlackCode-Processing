class Interpolation {
  InteractiveFrame keyFrame[];
  KeyFrameInterpolator kfi;
  int nbKeyFrames;  
  boolean enmarche;

  Interpolation( Vec but, Quat qbut) {
    kfi = new KeyFrameInterpolator(scene);
    nbKeyFrames = 3;
    keyFrame=new  InteractiveFrame[nbKeyFrames];
    enmarche=false;

    for (int i=0; i<nbKeyFrames; i++) {
      keyFrame[i] = new InteractiveFrame(scene);
      kfi.addKeyFrame(keyFrame[i]);
    }
    keyFrame[0].setPosition(new Vec(random(-600, -400), random(-500, -200), -500));
    keyFrame[0].setOrientation(new Quat(new Vec(random(-3, 3), random(-3, 3), random(-3, 3)), random(-2, 3.2))); 
    keyFrame[1].setPosition(new Vec(random(-300, -150), 30+random(100, 150), random(180, 240))); 
    keyFrame[1].setOrientation(new Quat(new Vec(random(-3, 3), random(-3, 3), random(-3, 3)), random(2, 6.2))); 
    keyFrame[2].setPosition(but);
    keyFrame[2].setOrientation(qbut);
    kfi.setLoopInterpolation(false);
    kfi.setInterpolationSpeed(0.4);
  }
  
  void dessin(float lng) {
    if (!kfi.interpolationStarted()&& !enmarche) { 
      kfi.startInterpolation();
      enmarche=true;
    }
    pushMatrix();
    kfi.frame().applyTransformation(scene);
    noStroke();
    scene.drawCone(4,5,5,lng);
    popMatrix();
  }
}
