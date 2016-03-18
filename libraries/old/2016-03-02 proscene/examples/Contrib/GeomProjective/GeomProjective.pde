/**
 * Geometrie Projective
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;

Scene scene;
Figure figure;
PVector or;
PFont font;
InteractiveFrame repere, dragueur, rotateur;
WorldConstraint pivot, drag;
LocalConstraint planaire;
float zplan;

void setup() {
  size(640, 640, P3D);
  scene = new Scene(this);
  scene.setRadius(1600);

  scene.setCameraType(Camera.Type.PERSPECTIVE);
  font = loadFont("FreeSans-24.vlw");
  textFont(font);

  float a=7.0;
  or=new PVector(0, 0, 0);
  zplan=30;
  pivot=new WorldConstraint();
  pivot.setRotationConstraint(AxisPlaneConstraint.Type.FREE, new Vec(0, 0, 0));
  pivot.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  planaire=new LocalConstraint();
  planaire.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new Vec(0, 0, 1));
  planaire.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  drag=new WorldConstraint();
  drag.setTranslationConstraint(AxisPlaneConstraint.Type.FREE, new Vec(0, 0, 0));
  drag.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));

  dragueur = new InteractiveFrame(scene);
  rotateur=new InteractiveFrame(scene);
  repere = new InteractiveFrame(scene, dragueur);
  repere.setConstraint(pivot);  
  dragueur.setConstraint(drag);    
  rotateur.setConstraint(planaire);

  dragueur.setPosition(new Vec(0, 0, 150));
  rotateur.setTranslation(new Vec(12, 0, 0));

  figure=new Figure(new PVector(a, a, 2*a), new PVector(a, -a, 2*a), new PVector(-a, -a, 2*a), new PVector(-a, a, 2*a));
  scene.camera().setPosition(new Vec(0, 0, 520));
  scene.setGridVisualHint(false);
  scene.setAxesVisualHint(false);
  rectMode(CENTER);
}

void draw() {
  background(255);  
  scene.drawGrid(100, 4);
  rotateur.setTranslation(Scene.toVec(normaliser(Scene.toPVector(rotateur.translation()), 30)));
  pushMatrix();
  dragueur.applyTransformation();
  pushMatrix();
  rotateur.applyTransformation();
  fill(255, 0, 0); 
  noStroke();  
  sphere(3);
  popMatrix();    
  noStroke();
  fill(255, 255, 0);
  sphere(3);
  popMatrix();
  ligne(or, Scene.toPVector(dragueur.position()));
  repere.setZAxis(dragueur.position());
  PVector vv=comb(1, Scene.toPVector(dragueur.position()), -1, Scene.toPVector(rotateur.position()));
  repere.setXAxis(Scene.toVec(vv));
  repere.setZAxis(dragueur.position());
  pushMatrix();
  repere.applyTransformation();
  scene.drawAxes(100);
  figure.draw();
  popMatrix();
  pushMatrix();
  translate(0, 0, zplan-2);
  fill(250, 50, 150, 40);
  rect(0, 0, 300, 300);
  popMatrix();
  intersection(figure.plan12);
  intersection(figure.plan23);
  intersection(figure.plan34);
  intersection(figure.plan41);
  ligne(Scene.toPVector(dragueur.position()), Scene.toPVector(rotateur.position()));
  ligne(or, Scene.toPVector(dragueur.position()));
  ligneDeFuite();
  unText1(" modifier l'homographie  ", Scene.toPVector(rotateur.position()));
}

void intersection(Plan plan) {
  PVector d1, d2;
  float skl=200;
  PVector n=Scene.toPVector(repere.inverseTransformOf(Scene.toVec(plan.normale)));
  if (n.y!=0) {
    d1=new PVector(skl, (-n.x*skl-n.z*zplan)/n.y, zplan);
    d2=new PVector(-skl, (n.x*skl-n.z*zplan)/n.y, zplan);
  }
  else {
    d1=new PVector(-n.z*zplan/n.x, skl, zplan);
    d2=new PVector(-n.z*zplan/n.x, -skl, zplan);
  }
  strokeWeight(4);
  ligne(d1, d2);
  strokeWeight(1);
}

void ligneDeFuite() {
  PVector n=Scene.toPVector(repere.inverseTransformOf(new Vec(0, 0, 1)));
  PVector d1, d2;
  float skl=320;

  if (n.y!=0) {
    d1=new PVector(skl, (-n.x*skl-n.z*zplan)/n.y, zplan);
    d2=new PVector(-skl, (n.x*skl-n.z*zplan)/n.y, zplan);
  }
  else {
    d1=new PVector(-n.z*zplan/n.x, skl, zplan);
    d2=new PVector(-n.z*zplan/n.x, -skl, zplan);
  }
  stroke(255, 0, 0);
  strokeWeight(5);
  ligne(d1, d2);
  strokeWeight(1);
  fill(255, 0, 0);

  d1=comb(0.5, d1, 0.5, d2);
  unText1("droite à l'infini", d1);
}
