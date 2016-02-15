/**
 * Cajas Orientadas.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates some basic Frame properties, particularly how to orient them.
 * Select and move the sphere (holding the right mouse button pressed) to see how the
 * boxes will immediately be oriented towards it. You can also pick and move the boxes
 * and still they will be oriented towards the sphere.
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene;
Box [] cajas;
Sphere esfera;

public void setup() {
  //size(640, 360, P3D);
  size(640, 360, OPENGL);
  scene = new Scene(this);  
  scene.setAxesVisualHint(false);    
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(160);
  //scene.camera().setPosition(new PVector(10,0,0));
  //scene.camera().lookAt( scene.center() );
  scene.showAll();    
  //scene.disableBackgroundHanddling();    

  esfera = new Sphere(scene);
  esfera.setPosition(new Vec(0.0f, 1.4f, 0.0f));
  esfera.setColor(color(0, 0, 255));

  cajas = new Box[30];
  for (int i = 0; i < cajas.length; i++)
    cajas[i] = new Box(scene);

  frameRate(500);
}

public void draw() {
  background(0);  

  esfera.draw();
  for (int i = 0; i < cajas.length; i++) {
    cajas[i].setOrientation(esfera.getPosition());
    cajas[i].draw(true);
  }
}

public void keyPressed() {
  if ((key == 'y') || (key == 'Y')) {
    scene.setDottedGrid(!scene.gridIsDotted());
  }
  if ((key == 'u') || (key == 'U')) {
    println("papplet's frame count: " + frameCount);
    println("scene's frame count: " + scene.timingHandler().frameCount());
  }
  if ((key == 'v') || (key == 'V')) {
    println("papplet's frame rate: " + frameRate);
    println("scene's frame rate: " + scene.timingHandler().frameRate());
  }
}
