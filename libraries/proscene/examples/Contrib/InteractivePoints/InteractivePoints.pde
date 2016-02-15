/**
 * Interactive Points
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

float r;
int nb=4;
int nbrail=6;
Scene scene;
Balle[] balles;

PVector[] posfr= {
  new PVector(-100, -100, 0), new PVector(-60, 80, 0), new PVector(30, 10, 80), new PVector(100, -40, -70)
  };
color[] couleurs= {
  color(255, 0, 0), color(0, 0, 255), color(255, 0, 255), color(100, 0, 255)
};
Rail[] guides;

void setup() {
  size(640, 640, P3D);
  r=3;
  scene = new Scene(this);
  scene.setRadius(130);
  scene.showAll();
  scene.setCameraType(Camera.Type.PERSPECTIVE);
  scene.setAxesVisualHint(false);
  scene.drawAxes(30f);
  balles=new Balle[nb];
  for (int i=0;i<nb;i++) {
    balles[i]=new Balle(posfr[i], couleurs[i]);
  }

  guides=new Rail[6];
  guides[0]=new Rail(0, 1);
  guides[1]=new Rail(1, 2);
  guides[2]=new Rail(2, 0);
  guides[3]=new Rail(0, 3);
  guides[4]=new Rail(1, 3);
  guides[5]=new Rail(2, 3);
}

void draw() {
  background(255, 200, 50);
  directionalLight(255, 255, 255, -1, -1, 1);
  directionalLight(255, 255, 255, 1, -1, -1);
  directionalLight(255, 255, 0, 1, 0, 0);
  
  //les points bleus
  for (int i=0;i<nb;i++) {
    pushMatrix();
    balles[i].iFrame.applyTransformation();
    scene.drawAxes(r*15f);
    noStroke();
    fill(0, 0, 255);
    sphere(r);
    popMatrix();
  }

  //les points guidés
  for (int i=0;i<nbrail;i++)
  {
    guides[i].actualiser();
    ligne(Scene.toPVector(balles[guides[i].ndepart].iFrame.position()), Scene.toPVector(balles[guides[i].narrivee].iFrame.position()));
  }
  
  //les faces des tretraedres
  forTriangles(0, 1, 2, 0, 2, 1, color(255, 0, 0, 100), color(255, 0, 255));
  forTriangles(0, 2, 3, 2, 3, 5, color(255, 255, 0, 100), color(255, 0, 255));
  forTriangles(0, 1, 3, 0, 3, 4, color(0, 255, 255, 100), color(255, 0, 0));
  forTriangles(1, 2, 3, 1, 4, 5, color(0, 255, 0, 100), color(0, 0, 255));
}

void ligne(PVector u, PVector v) {
  stroke(255, 255, 0);
  strokeWeight(2);
  line(2.0*u.x-v.x, 2.0*u.y-v.y, 2.0*u.z-v.z, 2.0*v.x-u.x, 2.0*v.y-u.y, 2.0*v.z-u.z);
}

void dessinT(int e, int i, int k, color c) {
  fill(c);
  beginShape();
  vertex(balles[e].iFrame.position().x(), balles[e].iFrame.position().y(), balles[e].iFrame.position().z());
  vertex(guides[i].repere.position().x(), guides[i].repere.position().y(), guides[i].repere.position().z()); 
  vertex(guides[k].repere.position().x(), guides[k].repere.position().y(), guides[k].repere.position().z());
  endShape();
}

void dessinT0(int e, int i, int k, color c) {
  fill(c);
  beginShape();
  vertex(guides[e].repere.position().x(), guides[e].repere.position().y(), guides[e].repere.position().z()); 
  vertex(guides[i].repere.position().x(), guides[i].repere.position().y(), guides[i].repere.position().z()); 
  vertex(guides[k].repere.position().x(), guides[k].repere.position().y(), guides[k].repere.position().z());
  endShape();
}

void forTriangles(int n, int m, int p, int a, int b, int c, color c1, color c2) {
  dessinT(n, a, b, c1);
  dessinT(m, a, c, c1);
  dessinT(p, b, c, c1);
  dessinT0(a, b, c, c2);
}
