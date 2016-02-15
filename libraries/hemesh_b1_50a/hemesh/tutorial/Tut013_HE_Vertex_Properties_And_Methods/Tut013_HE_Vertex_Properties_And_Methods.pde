import wblut.processing.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;
import wblut.geom.*;


void setup(){
size(100,100,P3D);
HE_Mesh mesh = new HE_Mesh(new HEC_Cube().setEdge(100));
HE_Vertex v = mesh.vItr().next();
HE_Vertex v2 = mesh.vItr().next();
//Accessible through vertex
  
int vkey = v.key();// the unique key

v.setLabel(1);// set info label

int label =v.getLabel();// retrieve label

HE_Vertex vCopy = v.get();// copy of vertex
v.set(vCopy); // move vertex 


WB_Normal vn = v.getVertexNormal(); // vertex normal, average of face normals, cached

double area = v.getVertexArea(); // average area of vertex faces

WB_VertexType3D type = v.getVertexType(); // Vertes type
//HE.VertexType.FLAT: vertex is flat in all faces,
//HE.VertexType.CONVEX: vertex is convex in all faces,
//HE.VertexType.CONCAVE: vertex is concave in all faces,
//HE.VertexType.FLATCONVEX: vertex is convex or flat in all faces,
//HE.VertexType.FLATCONCAVE: vertex is concave or flat in all faces, 
//HE.VertexType.SADDLE: vertex is convex and concave in at least one face each

ArrayList<HE_Vertex> vertices =v.getNeighborVertices(); // get all neighboring vertices

ArrayList<HE_Halfedge> halfedges = v.getHalfedgeStar();// get all halfedges starting in vertex

ArrayList<HE_Edge> edges = v.getEdgeStar();// get all edges in vertex

ArrayList<HE_Face> faces = v.getFaceStar();// get all faces in vertex

int order=v.getVertexOrder(); // vertex order

HE_Halfedge he=v.getHalfedge();// Starting halfedge of vertex
//v.setHalfedge(he); // Change starting halfedge
//v.clearHalfedge(); // Set starting halfedge to null


}
