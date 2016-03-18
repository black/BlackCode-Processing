class Rail {
  InteractiveFrame repere;
  int ndepart, narrivee;
  float lambda;
  AxisPlaneConstraint contrainte;
  PVector direc, pos;

  public Rail( int n0, int n1) {
    ndepart=n0;
    narrivee=n1;
    lambda=0.5;
    repere=new InteractiveFrame(scene);
    pos=PVector.mult(PVector.add(Scene.toPVector(balles[ndepart].iFrame.position()), Scene.toPVector(balles[narrivee].iFrame.position())), lambda);
    repere.setPosition(Scene.toVec(pos));
    direc=PVector.sub(Scene.toPVector(balles[ndepart].iFrame.position()), Scene.toPVector(balles[narrivee].iFrame.position()));

    contrainte=new LocalConstraint();
    contrainte.setRotationConstraintType(AxisPlaneConstraint.Type.FORBIDDEN);
    contrainte.setTranslationConstraintType(AxisPlaneConstraint.Type.AXIS);
    contrainte.setTranslationConstraintDirection(Scene.toVec(direc));  
    repere.setConstraint(contrainte);
  }

  void actualiser() {
    PVector adirec=direc.get();
    direc=PVector.sub(Scene.toPVector(balles[ndepart].iFrame.position()), Scene.toPVector(balles[narrivee].iFrame.position()));
    PVector diff=PVector.sub(direc, adirec);
    if (diff.mag()>0) {
      repere.setPosition(Scene.toVec(PVector.add(PVector.mult(Scene.toPVector(balles[narrivee].iFrame.position()), lambda),
                                     PVector.mult(Scene.toPVector(balles[ndepart].iFrame.position()), 1.0-lambda))));
    } 
    pushMatrix();
    repere.applyTransformation();
    contrainte.setTranslationConstraintDirection(Scene.toVec(direc));
    noStroke();
    fill(255, 255, 0);
    sphere(4);
    popMatrix();
    PVector f1f3=PVector.sub(Scene.toPVector(repere.position()), Scene.toPVector(balles[ndepart].iFrame.position()));
    PVector  f1f2=PVector.sub(Scene.toPVector(balles[narrivee].iFrame.position()), Scene.toPVector(balles[ndepart].iFrame.position()));
    lambda=(f1f2.dot(f1f3))/(f1f2.dot(f1f2));
  }
}
