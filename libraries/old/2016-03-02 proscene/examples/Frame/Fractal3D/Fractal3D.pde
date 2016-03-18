/**
 * Fractale Constructive
 * by Jacques Maire (http://www.alcys.com/)
 * Coded on Monday 18/06/12. Modified the next day.
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene;
InteractiveFrame[] mobiles;
InteractiveFrame repere0;
float  reducteur, rayon, angle, co, alph;
int nb;
Quat q0, q1, q2, q0i, q1i, q2i, q100;
PVector oz, or;
ArrayList alist;

void setup() {
  size(640, 640, P3D);
  scene= new Scene(this);
  scene.setRadius(500);
  scene.setGridVisualHint(false);
  scene.setAxesVisualHint(false);

  alist=new ArrayList();

  nb=4;
  repere0=new InteractiveFrame(scene);
  mobiles=new InteractiveFrame[nb];
  for (int i=0;i<nb;i++)
    mobiles[i]=new InteractiveFrame(scene, i>0 ? mobiles[i-1] : null);
  frameRate(60);
  scene.camera().setPosition(new Vec(0, 0, 1000));
  initialise();
  
  dessine(0, 300);
}

void draw() {
  background(30, 0, 60);
  directionalLight(255, 255, 0, 0, -1, -1);
  directionalLight(0, 0, 255, -1, 0, -1);
  directionalLight(0, 0, 255, -1, 0, 1);
  directionalLight(255, 0, 0, 1, 1, -1);
  fill(255);
  envoiDesPierres();
}

void initialise() {
  oz=new PVector(0, 0, 1);
  or=new PVector(0, 0, 0);
  q100=new Quat();
  alph=2*PI/3;
  q0 =Quat.multiply(new Quat( new Vec(0, 0, 1), 0), new Quat(new Vec(1, 0, 0), alph));
  q0i=Quat.multiply(new Quat( new Vec(0, 0, 1), 0), new Quat(new Vec(1, 0, 0), -alph));
  q1 =Quat.multiply(new Quat( new Vec(0, 0, 1), 2*PI/3), new Quat(new Vec(1, 0, 0), alph));
  q1i=Quat.multiply(new Quat( new Vec(0, 0, 1), 2*PI/3), new Quat(new Vec(1, 0, 0), -alph));
  q2 =Quat.multiply(  new Quat(new Vec(0, 0, 1), 4*PI/3), new Quat(new Vec(1, 0, 0), alph));
  q2i=Quat.multiply( new Quat(new Vec(0, 0, 1), 4*PI/3), new Quat(new Vec(1, 0, 0), -alph));
}

void dessine(int n, float bras) {
  float bra=bras*0.25/cos(alph);
  pushMatrix();
  mobiles[n].applyTransformation();
  if (n<nb-1) {
    mobiles[n+1].setTranslation(new Vec(or.x,or.y,or.z));
    mobiles[n+1].setRotation(q0);
    dessine(n+1, bra);
    mobiles[n+1].setTranslation(q0.rotate(Vec.multiply(new Vec(oz.x,oz.y,oz.z), bra)));
    mobiles[n+1].setRotation(q100);
    dessine(n+1, bras/2);
    mobiles[n+1].setTranslation(Vec.add(q0.rotate(Vec.multiply(new Vec(oz.x,oz.y,oz.z), bra)), Vec.multiply(new Vec(oz.x,oz.y,oz.z), bras/2)));
    mobiles[n+1].setRotation(q0i);
    dessine(n+1, bra);
    //
    mobiles[n+1].setTranslation(new Vec(or.x,or.y,or.z));
    mobiles[n+1].setRotation(q1);
    dessine(n+1, bra);
    mobiles[n+1].setTranslation(q1.rotate(Vec.multiply(new Vec(oz.x,oz.y,oz.z), bra)));
    mobiles[n+1].setRotation(q100);
    dessine(n+1, bras/2);
    mobiles[n+1].setTranslation(Vec.add(q1.rotate(Vec.multiply(new Vec(oz.x,oz.y,oz.z), bra)), Vec.multiply(new Vec(oz.x,oz.y,oz.z), bras/2))); 
    mobiles[n+1].setRotation(q1i);
    dessine(n+1, bra);
    //
    mobiles[n+1].setTranslation(new Vec(or.x,or.y,or.z));
    mobiles[n+1].setRotation(q2);
    dessine(n+1, bra);
    mobiles[n+1].setTranslation(q2.rotate(Vec.multiply(new Vec(oz.x,oz.y,oz.z), bra)));
    mobiles[n+1].setRotation(q100);
    dessine(n+1, bras/2);
    mobiles[n+1].setTranslation(Vec.add(q2.rotate(Vec.multiply(new Vec(oz.x,oz.y,oz.z), bra)), Vec.multiply(new Vec(oz.x,oz.y,oz.z), bras/2))); 
    mobiles[n+1].setRotation(q2i);
    dessine(n+1, bra);
  }
  else {
    Vec pv=mobiles[nb-1].position();
    Quat qv=(Quat)mobiles[nb-1].orientation();
    alist.add(new Pierre( pv, qv, bras));
  }
  popMatrix();
}

void envoiDesPierres() {
  float aa=0.0005*millis();
  float bb=aa/3.0;
  repere0.setOrientation(new Quat(new Vec(cos(aa), sin(aa)*cos(bb), sin(aa)*sin(bb)), aa));
  pushMatrix();
  repere0.applyTransformation();
  float temps=min(alist.size(), millis()/400.0);
  for (int i=0;i<temps;i++) {
    Pierre pierr=(Pierre)alist.get(i);
    pierr.actualiser();
  }
  popMatrix();
}