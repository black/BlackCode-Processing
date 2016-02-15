
import wblut.processing.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;

void setup() {
  size(100, 100, P3D);
  HE_Mesh mesh = new HE_Mesh(new HEC_Cube().setEdge(100));

  /*
* A HE_Mesh objects contains 4 kinds of elements, 3 of them self-
   * explanatory:
   *   HE_Vertex
   *   HE_Face
   *   HE_Edge
   *
   * The 4th, the halfedge, requires a bit more explanation, see Tut010_Halfedge
   *
   *  HE_Halfedge
   *
   * Each element has a unique key that is used to access it. 
   */


  // Retrieve a single element; requires the key (normally available from context or a HET_Selector):
  int key=0;
  mesh.getVertexByKey(key);
  mesh.getFaceByKey(key);
  mesh.getEdgeByKey(key);
  mesh.getHalfedgeByKey(key);


  /*
  * Looping through all elements is done with iterators.
  */

  println("# vertices: "+mesh.numberOfVertices());
  Iterator<HE_Vertex> vItr=mesh.vItr();
  HE_Vertex v;
  while (vItr.hasNext ()) {
    v=vItr.next();
    println(v);
    //do thingy
  }

  println("# faces: "+mesh.numberOfFaces());
  Iterator<HE_Face> fItr=mesh.fItr();
  HE_Face f;
  while (fItr.hasNext ()) {
    f=fItr.next();
    println(f);
    //do thingy
  }

  println("# edges: "+mesh.numberOfEdges());
  Iterator<HE_Edge> eItr=mesh.eItr();
  HE_Edge e;
  while (eItr.hasNext ()) {
    e=eItr.next();
    println(e);
    //do thingy
  }

  println("# halfedges: "+mesh.numberOfHalfedges());
  Iterator<HE_Halfedge> heItr=mesh.heItr();
  HE_Halfedge he;
  while (heItr.hasNext ()) {
    he=heItr.next();
    println(he);
    //do thingy
  }
}

