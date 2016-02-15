import wblut.processing.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;
import wblut.geom.*;
import wblut.geom2D.*;

//Accessible through face
void setup() {
  size(100, 100, P3D);
  HE_Mesh mesh = new HE_Mesh(new HEC_Cube().setEdge(100));
  HE_Face face=mesh.fItr().next(); 

  int fkey = face.key();// the unique key

  face.setLabel(1);// set info label
  
  int label =face.getLabel();// retrieve label
  
  WB_Point fc = face.getFaceCenter(); // face center

  int order=face.getFaceOrder();//number of vertices

  WB_Normal fn = face.getFaceNormal(); // face normal

  double area = face.getFaceArea(); // face ared

  WB_PolygonType2D type = face.getFaceType(); // face type, WB_PolygonType2D.CONVEX, WB_PolygonType2D.CONCAVE

  ArrayList<HE_Vertex> vertices = face.getFaceVertices(); // get all vertices belonging to face

  ArrayList<HE_Halfedge> halfedges = face.getFaceHalfedges();// get all halfedges belonging to face

  ArrayList<HE_Edge> edges = face.getFaceEdges();// get all edges belonging to face

  ArrayList<HE_Face> faces = face.getNeighborFaces();// get all neighboring faces

  HE_Halfedge he=face.getHalfedge();// Starting halfedge of face
  //face.setHalfedge(he); // Change starting halfedge
  //face.clearHalfedge(); // Set starting halfedge to null

  WB_Plane P = face.toPlane();//Get plane of face with origin in face center
  WB_Polygon poly = face.toPolygon();//Get face as a polygon
  WB_Polygon2D poly2d = face.toPolygon2D();//Get face as a 2D polygon with the face center as origin
  face.sort();// set leftmost halfedge as starting halfedge
  
  //return triangulation as an arraylist of indexed triangles. The inices refer to 
  // vertices as returned by face.getVertices()
  ArrayList<WB_IndexedTriangle2D> tris = face.triangulate(); 
}

