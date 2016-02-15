import wblut.processing.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;
import wblut.geom.*;

HE_Mesh mesh; 
void setup() {
  size(100, 100, P3D);
  mesh= new HE_Mesh(new HEC_Cube().setEdge(100));
  WB_Point center= mesh.getCenter(); // center of mesh, cached
  HE_Mesh mesh2 = mesh.get(); // deep copy of mesh
  mesh.set(mesh2);// Turn mesh into copy of other mesh
  mesh.add(mesh2);// add entire mesh to existing mesh. No modificiations are mase.

  float[][] vertices=mesh.getVerticesAsFloat(); //Get all vertex coordinates as float[n][3]
  double[][] dvertices=mesh.getVerticesAsDouble(); //Get all vertex coordinates as float[n][3]
  WB_Point[] points=mesh.getVerticesAsPoint(); //Get all vertices as points WB_Point[n]

  //Do something with values
  mesh.setVerticesFromFloat(vertices); //Set all vertices back to provided coordinates as float[n][3]
  mesh.setVerticesFromPoint(points);  //Set all vertices back to provided points as WB_Point[n]

  int[][] faces=mesh.getFacesAsInt(); //Get all faces as an array of vertex indices;

  WB_ExplicitPolygon[] polygonSoup = mesh.getPolygons(); //Get mesh as an array of polygons, vertex information is not copied

  mesh.move(100, 100, 100); // Move mesh by provided coordinates
  WB_Vector v=new WB_Vector(100, 100, 100);
  mesh.move(v); // Move mesh by provided coordinates

  WB_Point p=new WB_Point(100, 100, 100);
  mesh.moveTo(100, 100, 100); //Move mesh to provided coordinates
  mesh.moveTo(p); // Move mesh to provided coordinates

  float angle=PI/6.0;
  mesh.rotateAboutAxis(angle, 100, 100, 100, 200, 200, 200); //Rotate mesh about axis by angle. Axis defined by two points given as coordinates.
  WB_Point q=new WB_Point(200, 200, 200);
  mesh.rotateAboutAxis(angle, p, q); //Rotate mesh about axis by angle. Axis defined by two points.
  mesh.rotateAboutAxis(angle, q, v); //Rotate mesh about axis by angle. Axis defined by point and direction.

  mesh.scale(1.5); //Uniform scale about center
  mesh.scale(1.5, p); //Uniform scale about point
  mesh.scale(1.5, 2.0, 2.5); //Non-uniform scale about center
  mesh.scale(1.5, 2.0, 2.5, p); //Non-uniform scale about point


  mesh.cleanUnusedElementsByFace(); //Remove all elements not part of a face or not part of boundary

  mesh.flipAllFaces(); //Flip normals

    HE_Edge e=mesh.eItr().next();
  mesh.collapseEdge(e); //Collapse edge to edge center. Degenerate faces (less than 3 edges) will be removed.
  mesh.collapseDegenerateEdges(); //Remove all zero-length edges

    e=mesh.eItr().next();
  mesh.deleteEdge(e);//Remove edge. Edges faces are fused into one face.

  e=mesh.eItr().next();
  mesh.splitEdge(e);//Insert new vertex at edge midpoint

  float f=0.25;
  mesh.splitEdge(e, f);//Insert new vertex at edge fraction (f=0..1)

  float[] fs= {
    0.2, 0.4, 0.6, 0.8
  };
  mesh.splitEdge(e, fs);//Insert new vertex at several edge fractions (fs[i]=0..1)
  mesh.splitEdge(e, p);//Insert new vertex at point
  mesh.splitEdge(e, 100, 100, 100);//Insert new vertex at point

  mesh.divideEdge(e, 5);//Split edge in 5 equal parts

  HE_Face face=mesh.fItr().next();
  mesh.triSplitFace(face, p);//Split the face in triangles by connecting all vertices with point
  mesh.triSplitFace(face, 100, 100, 100); //Split the face in triangles by connecting all vertices with point at coordinates
  mesh.triSplitFace(face);//Split the face in triangles using the face center

  mesh.triangulateFaces();//Split all faces in triangles (with exactly 3 vertices)
  mesh.triangulateConcaveFaces();//Split all concave faces in triangles

    //Check the JavaDoc for more...
}

