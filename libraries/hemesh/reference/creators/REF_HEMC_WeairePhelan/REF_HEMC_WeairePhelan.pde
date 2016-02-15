import wblut.hemesh.modifiers.*;
import wblut.hemesh.composite.*;
import wblut.hemesh.tools.*;
import wblut.processing.*;
import wblut.hemesh.subdividors.*;
import wblut.frame.*;
import wblut.hemesh.options.*;
import wblut.random.*;
import wblut.geom2D.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.core.*;
import wblut.geom.*;
import wblut.math.*;
import wblut.nurbs.*;
import wblut.tree.*;
import wblut.grid.*;



HE_Mesh[] meshes;
int nom;
WB_Render render;
void setup() {
  size(800, 800, P3D);
  HEMC_WeairePhelan wp=new HEMC_WeairePhelan();
  wp.setOrigin(new WB_Point(-200, -200, -200));
  wp.setExtents(new WB_Vector(400, 400, 400));
  wp.setNumberOfUnits(3, 3, 3);
  wp.setScale(150, 150, 150);
  wp.setCrop(true,true,false,true,false,false);//Crop to extents?
  meshes=wp.create();
  nom=wp.numberOfMeshes();
  render=new WB_Render(this);
}


void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, -200);

  scale(1, -1, 1);

  rotateY(TWO_PI/width*mouseX-PI);
  rotateX(TWO_PI/height*mouseY-PI);
  noStroke();
  for (int i=0;i<nom;i++) {
    render.drawFaces(meshes[i]);
  }
  stroke(0);
  for (int i=0;i<nom;i++) {
    render.drawEdges(meshes[i]);
  }
}

