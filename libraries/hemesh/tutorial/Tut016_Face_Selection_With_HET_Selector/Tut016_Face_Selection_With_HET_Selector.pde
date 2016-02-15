import wblut.hemesh.tools.*;
import wblut.processing.*;
import wblut.hemesh.subdividors.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.core.*;




/*
*
 *  HET_Selector draws mesh elements in a flat buffer. The value of the buffer
 *  at the mouse position gives the mesh element currently under mouse cursor.
 *  The type of element returned depends on the draw function that calls the
 *  HET_Selector.
 *  selector.getKey() after drawFaces(selector) will return the key of a face
 *  or null if no face is under the mouse cursor.
 *
 */

HET_Selector selector;
HE_Mesh mesh;
WB_Render render;
float ax,ay;

void setup() {
  size(800,800,P3D);
  mesh=new HE_Mesh(new HEC_Cube().setEdge(400));
  mesh.subdivide(new HES_Planar().setRandom(true).setRange(0.4),2);

  //Instance of HET_Selector, refers to the calling applet.
  selector=new HET_Selector(this);
  render=new WB_Render(this);
}


void draw() {
  background(255);
  lights();
  translate(400,400);
  rotateX(ax);
  rotateY(ay);
  noStroke();
  fill(255);

  //select faces 
  render.drawFaces(selector,mesh);
 
  fill(255,0,0);
 
  // retrieve the face key under the mouse cursor when drawFaces(selector)
  // was last called. Has to be checked for null.
  if (selector.lastKey() !=null) {
   render.drawFace(selector.lastKey(),mesh);
  }

  stroke(0);
  render.drawEdges(mesh);
  println(selector.bufferSize());
}

//basic viewport rotation
void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      ax -= 0.01;
    } 
    else if (keyCode == DOWN) {
      ax += 0.01;
    } 
    else if (keyCode == LEFT) {
      ay -= 0.01;
    } 
    else if (keyCode == RIGHT) {
      ay += 0.01;
    }
  }
}

