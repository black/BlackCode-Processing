import processing.opengl.*;
import wblut.processing.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.core.*;


HE_Mesh[] boxes;
WB_Render render;

void setup() {
  size(600,600,OPENGL);
  smooth();
  render=new WB_Render(this);
  
  //Creators by default refer to absolute world coordinates.
  
  HEC_Box boxCreator=new HEC_Box(); 
  boxCreator.setWidth(50);
  boxCreator.setHeight(100);
  boxCreator.setDepth(50);
  
  //setting the creator to modelview allows to use Processings's
  //pushMatrix() and popMatrix() to position meshes.
  boxCreator.setToModelview(this);
  boxes=new HE_Mesh[17];
  for(int i=0;i<16;i++) {
    pushMatrix();
    float angle=i*TWO_PI/16;
    translate(200*cos(angle),200*sin(angle),400-i*50);
    rotateZ(angle);
    boxes[i]=new HE_Mesh(boxCreator);
   popMatrix();
  }
  
  //setting the creator to worldview restores the default behavior
   boxCreator.setToWorldview();
   pushMatrix();
   translate(100,100,100);// will have no effect
   boxes[16]=new HE_Mesh(boxCreator);
   popMatrix();
}

void draw() {
  background(120);
  lights();
  translate(300,300,-300);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  for(int i=0;i<17;i++) {
    noStroke();
    render.drawFaces(boxes[i]);
    stroke(0);
    render.drawEdges(boxes[i]);
  }
}

