class Plan {
  int nb=5;
  InteractiveFrame[] reperes;
  PVector[][] intersections;

  Plan() {
    intersections=new PVector[nb][nb];
    creerTable();
    reperes=new InteractiveFrame[nb];
    for (int i=0;i<nb;i++)
      reperes[i]=new InteractiveFrame(scene, i>0 ? reperes[0] : null);
    reperes[0].setConstraint(waxial);
    reperes[1].setConstraint(lplanaire);
    reperes[2].setConstraint(lplanaire);
    reperes[3].setConstraint(lplanaire);
    reperes[4].setConstraint(fixe);
    // Initialize frames
    reperes[0].setPosition(-100, -100, 0); 
    reperes[1].setTranslation(80, -80, 0); 
    reperes[2].setTranslation(150, -20, 0); 
    reperes[3].setTranslation(100, -180, 0);
    reperes[4].setTranslation(100, 0, 0);
    reperes[0].setRotation(new Quat(new Vec(1, 0, 0), 5.6)); 
    reperes[1].setRotation(new Quat(new Vec(0, 0, 1), 0.6));
    reperes[2].setRotation(new Quat(new Vec(0, 0, 1), 1.1));
    reperes[3].setRotation(new Quat(new Vec(0, 0, 1), -1.3));
    reperes[4].setRotation(new Quat(new Vec(0, 0, 1), 0.0));
  }
  
  void draw() {
    pushMatrix();

    reperes[0].applyTransformation();
    scene.drawAxes(80);  
    noStroke();
    fill(0, 255, 0);
    sphere(6);
    fill(255, 0, 0, 60);
    beginShape();
    vertex(0, 100, 0);
    fill(155, 100, 200, 60); 
    vertex(0, -200, 0);
    fill(155, 0, 200, 60);
    vertex(200, -200, 0);
    fill(155, 155, 0, 60);
    vertex(200, 100, 0);
    endShape(CLOSE);

    pushMatrix();
    reperes[1].applyTransformation();
    drawLine(1, color(255, 0, 0));
    popMatrix();
    pushMatrix();
    reperes[2].applyTransformation();
    drawLine(2, color(255, 0, 0));
    popMatrix();
    pushMatrix();
    reperes[3].applyTransformation();
    drawLine(3, color(0, 0, 255));
    popMatrix();
    pushMatrix();
    reperes[4].applyTransformation();
    drawLine(4, color(255));
    popMatrix();
    //  ********************** 
    calculerIntersections();


    popMatrix();// retour au monde
  }
  
  void drawLine(int n, color c) {
    if (n!=4) {
      noStroke();
      fill(c);
      sphere(4);
    };
    fill(255, 0, 0, 98);
    stroke(0);
    line(-200, 0, 0, 200, 0, 0);
  }
  
  float det(float a, float b, float ap, float bp) {
    return a*bp-ap*b;
  }
  
  PVector cramer(float a, float b, float c, float ap, float bp, float cp) {
    float d=det(a, ap, b, bp);
    float dx=det(c, cp, b, bp);
    float dy=det(a, ap, c, cp);
    return new PVector(dx/d, dy/d, 0);
  }

  PVector intersection(InteractiveFrame f1, InteractiveFrame f2) {
    Quat q1=(Quat)f1.rotation();
    float c1=q1.w()*q1.w()-q1.z()*q1.z();
    float s1=2.0*q1.w()*q1.z();
    Quat q2=(Quat)f2.rotation();
    float c2=q2.w()*q2.w()-q2.z()*q2.z();
    float s2=2.0*q2.w()*q2.z();
    PVector res=cramer(-s1, c1, -s1*f1.translation().x()+c1*f1.translation().y(), 
    -s2, c2, -s2*f2.translation().x()+c2*f2.translation().y());
    return res;
  }
  
  void creerTable() {
    for (int i=0;i<nb;i++) {
      for (int j=0;j<nb;j++) {
        intersections[i][j]=new PVector();
      }
    }
  }
  
  void  calculerIntersections() {
    for (int i=1;i<nb;i++) {
      for (int j=i+1;j<nb;j++) {
        intersections[i][j]=intersection(reperes[i], reperes[j]);
        pushMatrix();     
        translate(intersections[i][j].x, intersections[i][j].y, intersections[i][j].z);
        fill(255, 255, 0);
        noStroke();
        sphere(2); 
        popMatrix();
      }
    }
  }
  
  void rectangle(color c) {
    fill(c);
    beginShape();
    vertex(0, 0, 0);
    vertex(0, -200, 0);
    vertex(200, -200, 0);
    vertex(200, 0, 0);
    endShape(CLOSE);
  }
}
