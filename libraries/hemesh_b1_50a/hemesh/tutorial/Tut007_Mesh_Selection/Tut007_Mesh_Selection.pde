import wblut.hemesh.modifiers.*;
import wblut.processing.*;
import wblut.hemesh.subdividors.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.core.*;
import processing.opengl.*;

HE_Selection selection;
HE_Mesh box;
WB_Render render;

void setup() {
  size(600, 600, OPENGL);
  HEC_Box boxCreator=new HEC_Box().setWidth(400).setWidthSegments(10)
    .setHeight(200).setHeightSegments(4)
      .setDepth(200).setDepthSegments(4);
  box=new HE_Mesh(boxCreator);

  //define a selection
  selection=new HE_Selection(box);  

  //add faces to selection 
  Iterator<HE_Face> fItr=box.fItr();
  HE_Face f;
  while (fItr.hasNext ()) {
    f=fItr.next();
    if (random(100)<30) selection.add(f);
  }

  HES_CatmullClark cc=new HES_CatmullClark().setKeepEdges(false).setKeepBoundary(false);

  //only modify selection (if applicable)
  box.subdivideSelected(cc, selection, 2);

  //modifier try to preserve selections whenever possible


  box.modifySelected(new HEM_Extrude().setDistance(10).setChamfer(.5), selection);
  render=new WB_Render(this);
}

void draw() {
  background(120);
  lights();
  translate(300, 300, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  fill(255);
  noStroke();
  render.drawFaces(box);
  fill(255, 0, 0);
  render.drawFaces(selection);
  stroke(0);
  render.drawEdges(box);
}




