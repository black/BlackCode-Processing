import wblut.processing.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;
import wblut.geom.*;



void setup() {
  size(100, 100, P3D);

  HE_Mesh mesh = new HE_Mesh(new HEC_Cube().setEdge(100));
  HE_Edge edge=mesh.eItr().next();

  //Accessible through edge

  int ekey = edge.key();// the unique key

  edge.setLabel(1);// set info label

  int label =edge.getLabel();// retrieve label

  WB_Point ec = edge.getEdgeCenter(); // edge center

  WB_Normal en = edge.getEdgeNormal(); // edge normal, average of face normals

  WB_Vector et = edge.getEdgeTangent(); // normalized vector along edge

  double area = edge.getEdgeArea(); // average area of edge faces

  double dihedralAngle = edge.getDihedralAngle(); // angle between edge faces in radians

  HE_Halfedge he=edge.getHalfedge();// One halfedge of edge
  HE_Halfedge he2=edge.getHalfedge().getPair();// Other halfedge of edge
  //edge.setHalfedge(he); // Change starting halfedge
  //edge.clearHalfedge(); // Set starting halfedge to null

  HE_Vertex v1= edge.getStartVertex();// first vertex
  HE_Vertex v2= edge.getEndVertex();// second vertex
  HE_Face f1= edge.getFirstFace();// first face
  HE_Face f2= edge.getSecondFace();// second face

  WB_Segment S = edge.toSegment();//Get edge as WB_Segment
}

