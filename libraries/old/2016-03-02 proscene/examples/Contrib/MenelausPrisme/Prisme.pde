class Prisme {
  int nb=4;
  InteractiveFrame[] reperes ;
  AxisPlaneConstraint contrainteX, contrainteZ, contrainte4;
  PVector inter12, inter23, inter13;
  float tanang0, haut12, haut13, haut23;
  
  Prisme() {   
    reperes=new InteractiveFrame[nb];
    for (int i=0;i<nb;i++) {
      reperes[i]=new InteractiveFrame(scene);
    }
    
    reperes[0].setTranslation(-100, 100, 0); 
    reperes[1].setTranslation(80, 100, 0); 
    reperes[2].setTranslation(-90, 100, 0); 
    reperes[3].setTranslation(-30, 100, 0);

    reperes[0].setRotation(new Quat(new Vec(-1, 0, 0), 2.4));
    reperes[1].setRotation(new Quat(new Vec(0, 0, 1), -0.6));
    reperes[2].setRotation(new Quat(new Vec(0, 0, 1), 1.1));
    reperes[3].setRotation(new Quat(new Vec(0, 0, 1), 0.3));
    contrainteX=new WorldConstraint();
    contrainteX.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0.0f, 0.0f, 0.0f));
    contrainteX.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0.1f, 0.0f, 0.0f));
    contrainteZ=new WorldConstraint();
    contrainteZ.setTranslationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0.1f, 0.0f, 0.0f));
    contrainteZ.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0.0f, 0.0f, 1.0f));
    reperes[0].setConstraint(contrainteX);
    reperes[1].setConstraint(contrainteZ);
    reperes[2].setConstraint(contrainteZ);
    reperes[3].setConstraint(contrainteZ);
  }          

  void draw() {
    rectan(255, 210, 0, -100, -100, 100, 100);
    //le plan 0
    pushMatrix();
    pushMatrix();
    reperes[0].applyTransformation();
    fill(255, 0, 0);
    noStroke();
    sphere(3);
    Quat q= (Quat)reperes[0].orientation();
    float c0=sq(q.w())-sq(q.x());
    float s0=2.0*q.w()*q.x();
    tanang0=s0/c0;
    rectan(255, 255, 55, 0, 0, 200, 200);
    popMatrix();

    //le plan 1
    pushMatrix();
    reperes[1].applyTransformation();
    fill(0, 255, 0);
    noStroke();
    sphere(3);
    popMatrix();

    //le plan 2
    pushMatrix();
    reperes[2].applyTransformation();
    fill(0, 255, 0);
    noStroke();
    sphere(3);
    popMatrix(); 

    //le plan 3
    pushMatrix();
    reperes[3].applyTransformation();
    fill(0, 255, 0);
    noStroke();
    sphere(3);
    popMatrix();

    inter12=intersection(1, 2);
    inter23=intersection(2, 3);
    inter13=intersection(1, 3);

    haut12=(-100+ inter12.y)*tanang0;  
    haut23=(-100+ inter23.y)*tanang0;    
    haut13=(-100+ inter13.y)*tanang0;

    dessinerPrisme();
    dessineMobiles(); 
    popMatrix();
  }

  void rectan( int rr, int gr, int br, float dx, float dy, float ax, float ay) {
    stroke(0); 
    noFill();
    beginShape();

    vertex( dx, dy, 0);       
    vertex(dx, ay, 0);
    vertex(ax, ay, 0);
    vertex(ax, dy, 0);
    endShape(CLOSE);
  }

  PVector intersection(int i, int j) {
    float lambda ;  
    Quat q0= (Quat)reperes[i].orientation();
    Quat q1= (Quat)reperes[j].orientation();
    float s0=sq(q0.w())-sq(q0.z());
    float c0=-2.0*q0.w()*q0.z();
    float s1=sq(q1.w())-sq(q1.z());
    float c1=-2.0*q1.w()*q1.z();
    float d=reperes[j].position().x()-reperes[i].position().x();
    lambda=d*s1/(c0*s1-c1*s0);
    PVector res= PVector.add(Scene.toPVector(reperes[i].position()), new PVector(lambda*c0, lambda*s0, 0));      
    balle(res, color(255, 255, 0));
    return res;
  }

  void balle(PVector res, color c) {
    pushStyle();  
    pushMatrix();
    translate(res.x, res.y, res.z);
    fill(0, 0, 255);
    noStroke();
    sphere(2);
    stroke(c);
    strokeWeight(4);
    line(0, 0, 0, 0, 0, (-100+res.y)*tanang0);
    translate(0, 0, (-100+res.y)*tanang0);
    fill(0);
    noStroke();
    sphere(2);
    popMatrix();  
    popStyle();
  }
  
  void dessinerPrisme() {
    stroke(150);
    triangle(inter12, 1, haut12);
    triangle(inter12, 2, haut12);
    triangle(inter13, 1, haut13);
    triangle(inter13, 3, haut13);          
    triangle(inter23, 2, haut23);
    triangle(inter23, 3, haut23);
    unText1(" "+1, inter12);
    unText1(" "+2, inter13);
    unText1(" "+3, inter23);
  }

  void dessineMobiles() {
    PVector bas=comb(1-tempo, inter12, tempo, inter13);
    balle(bas, color(0, 0, 255));       
    PVector bas1=comb(1-tempo, inter13, tempo, inter23);
    balle(bas1, color(255, 0, 0));
    PVector  bas2=comb(1-tempo, inter23, tempo, inter12);   
    balle(bas2, color(0, 255, 0));
    unText1("homothetie 1->2", bas);
    unText1("homothetie 2->3", bas1);
    unText1("homothetie 3->1", bas2);
  }

  void triangle(PVector inter, int n, float haut) {
    stroke(255, 255, 0);
    beginShape();
    fill(255, 70*n, 150);
    vertex( inter.x, inter.y, inter.z);
    fill(70*n, 50*n, 200);   
    vertex(inter.x, inter.y, inter.z+haut);

    vertex(reperes[n].position().x(), reperes[n].position().y(), reperes[n].position().z());
    endShape(CLOSE);
  }
}
