package jto.p5graphtheory;


import java.util.Collection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import processing.core.PApplet;
import processing.core.PVector;

/**
 *  ported from p bourke's triangulate.c</br>
 *  <a href="http://astronomy.swin.edu.au/~pbourke/modelling/triangulate/">
 *  http://astronomy.swin.edu.au/~pbourke/modelling/triangulate/</a></br></br>
 *
 *  fjenett, 20th february 2005, offenbach-germany.</br>
 *  contact: <a href="http://www.florianjenett.de/">
 *  http://www.florianjenett.de/</a></br></br>
 *
 *      adapted to take a Vector of Point3f objects and return a Vector of Triangles
 *      (and generally be more Java-like and less C-like in usage -
 *       and probably less efficient but who's benchmarking?)</br></br>
 *      Tom Carden, tom (at) tom-carden.co.uk 17th January 2006 </br></br>
 *
 *      adapted to get rid of those ugly Vector and Point3f objects. it now takes an
 *      ArrayList of PVector objects and return an ArrayList of Triangles objects.
 *      see what Sun thinks about Vector objects here:<br>
 *      <a href="http://java.sun.com/developer/technicalArticles/Collections/Using/index.html">
 *      http://java.sun.com/developer/technicalArticles/Collections/Using/index.html</a></br>
 *      antiplastik, 28 june 2010, paris-france</br></br>
 *
 *      adapted to work in this graph theory library using the Vertex and
 *      Edge classes herein instead of PVector objects and the previous version
 *      of the Edge class.</br></br>
 *      Taylor O'Connor, 14 august 2012, Blacksburg, Virgina.
 *
 *
 *  @version 2012.08.14
 */
public class Triangulate {

  /*
    From P Bourke's C prototype -

    qsort(p,nv,sizeof(XYZ),XYZCompare);

    int XYZCompare(void *v1,void *v2) {
      XYZ *getVertexA(),*getVertexB();
      getVertexA() = v1;
      getVertexB() = v2;
      if (getVertexA()->x < getVertexB()->x)
        return(-1);
      else if (getVertexA()->x > getVertexB()->x)
        return(1);
      else
        return(0);
    }
  */
  private static class XComparator implements Comparator<Vertex> {

    public int compare(Vertex v1, Vertex v2) {
      if (v1.getX() < v2.getX()) {
        return -1;
      }
      else if (v1.getX() > v2.getX()) {
        return 1;
      }
      else {
        return 0;
      }
    }
  }

  /*
    Return TRUE if a point (xp,yp) is inside the circumcircle made up
    of the points (x1,y1), (x2,y2), (x3,y3)
    The circumcircle centre is returned in (xc,yc) and the radius r
    NOTE: A point on the edge is inside the circumcircle
  */
  private static boolean circumCircle(PVector p, Triangle t, PVector circle) {

    float m1,m2,mx1,mx2,my1,my2;
    float dx,dy,rsqr,drsqr;

    /* Check for coincident points */
    if ( Math.abs(t.getVertexA().getY() - t.getVertexB().getY()) < PApplet.EPSILON
        && Math.abs(t.getVertexB().getY()-t.getVertexC().getY()) < PApplet.EPSILON ) {
      System.err.println("CircumCircle: Points are coincident.");
      return false;
    }

    if ( PApplet.abs(t.getVertexB().getY()-t.getVertexA().getY()) < PApplet.EPSILON ) {
      m2 = - (t.getVertexC().getX()-t.getVertexB().getX()) / (t.getVertexC().getY()-t.getVertexB().getY());
      mx2 = (t.getVertexB().getX() + t.getVertexC().getX()) / 2.0f;
      my2 = (t.getVertexB().getY() + t.getVertexC().getY()) / 2.0f;
      circle.x = (t.getVertexB().getX() + t.getVertexA().getX()) / 2.0f;
      circle.y = m2 * (circle.x - mx2) + my2;
    }
    else if ( PApplet.abs(t.getVertexC().getY()-t.getVertexB().getY()) < PApplet.EPSILON ) {
      m1 = - (t.getVertexB().getX()-t.getVertexA().getX()) / (t.getVertexB().getY()-t.getVertexA().getY());
      mx1 = (t.getVertexA().getX() + t.getVertexB().getX()) / 2.0f;
      my1 = (t.getVertexA().getY() + t.getVertexB().getY()) / 2.0f;
      circle.x = (t.getVertexC().getX() + t.getVertexB().getX()) / 2.0f;
      circle.y = m1 * (circle.x - mx1) + my1;
    }
    else {
      m1 = - (t.getVertexB().getX()-t.getVertexA().getX()) / (t.getVertexB().getY()-t.getVertexA().getY());
      m2 = - (t.getVertexC().getX()-t.getVertexB().getX()) / (t.getVertexC().getY()-t.getVertexB().getY());
      mx1 = (t.getVertexA().getX() + t.getVertexB().getX()) / 2.0f;
      mx2 = (t.getVertexB().getX() + t.getVertexC().getX()) / 2.0f;
      my1 = (t.getVertexA().getY() + t.getVertexB().getY()) / 2.0f;
      my2 = (t.getVertexB().getY() + t.getVertexC().getY()) / 2.0f;
      circle.x = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
      circle.y = m1 * (circle.x - mx1) + my1;
    }

    dx = t.getVertexB().getX() - circle.x;
    dy = t.getVertexB().getY() - circle.y;
    rsqr = dx*dx + dy*dy;
    circle.z = PApplet.sqrt(rsqr);

    dx = p.x - circle.x;
    dy = p.y - circle.y;
    drsqr = dx*dx + dy*dy;

    return drsqr <= rsqr;
  }


  /*
    Triangulation subroutine
    Takes as input vertices (PVectors) in ArrayList pxyz
    Returned is a list of triangular faces in the ArrayList triangles
    These triangles are arranged in a consistent clockwise order.
  */
  /**
   * This takes a collection of <code>Vertex</code> objects and
   * returns a Delaunay triangulation of them in the form of a list of
   * triangles.
   *
   * @param vertexCollection a collection of vertices.
   * @return an ArrayList of Triangle objects representing the triangulation.
   */
  public static ArrayList<Triangle> triangulate(Collection<Vertex> vertexCollection ) {

    // sort vertex array in increasing x values
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    for(Vertex vertex : vertexCollection) {
        vertices.add(vertex);
    }
    Collections.sort(vertices, new XComparator());

    /*
      Find the maximum and minimum vertex bounds.
      This is to allow calculation of the bounding triangle
    */
    float xmin = vertices.get(0).getX();
    float ymin = vertices.get(0).getY();
    float xmax = xmin;
    float ymax = ymin;

    Iterator<Vertex> pIter = vertices.iterator();
    while (pIter.hasNext()) {
      Vertex v = pIter.next();
      if (v.getX() < xmin) xmin = v.getX();
      if (v.getX() > xmax) xmax = v.getX();
      if (v.getY() < ymin) ymin = v.getY();
      if (v.getY() > ymax) ymax = v.getY();
    }

    float dx = xmax - xmin;
    float dy = ymax - ymin;
    float dmax = (dx > dy) ? dx : dy;
    float xmid = (xmax + xmin) / 2.0f;
    float ymid = (ymax + ymin) / 2.0f;

    ArrayList<Triangle> triangles = new ArrayList<Triangle>(); // for the Triangles
    HashSet<Triangle> complete = new HashSet<Triangle>(); // for complete Triangles

    /*
      Set up the supertriangle
      This is a triangle which encompasses all the sample points.
      The supertriangle coordinates are added to the end of the
      vertex list. The supertriangle is the first triangle in
      the triangle list.
    */
    Triangle superTriangle = new Triangle();
    superTriangle.setVertexA(new Vertex( xmid - 2.0f * dmax, ymid - dmax, 0.0f ));
    superTriangle.setVertexB(new Vertex( xmid, ymid + 2.0f * dmax, 0.0f ));
    superTriangle.setVertexC(new Vertex( xmid + 2.0f * dmax, ymid - dmax, 0.0f ));
    triangles.add(superTriangle);

    /*
      Include each point one at a time into the existing mesh
    */
    ArrayList<Edge> edges = new ArrayList<Edge>();
    pIter = vertices.iterator();
    while (pIter.hasNext()) {

      Vertex v = pIter.next();

      edges.clear();

      /*
        Set up the edge buffer.
        If the point (xp,yp) lies inside the circumcircle then the
        three edges of that triangle are added to the edge buffer
        and that triangle is removed.
      */
      PVector circle = new PVector();

      for (int j = triangles.size()-1; j >= 0; j--) {

        Triangle t = triangles.get(j);
        if (complete.contains(t)) {
          continue;
        }

        boolean inside = circumCircle( v.getPVector(), t, circle );

        if (circle.x + circle.z < v.getX()) {
          complete.add(t);
        }
        if (inside) {
          edges.add(new Edge(t.getVertexA(), t.getVertexB()));
          edges.add(new Edge(t.getVertexB(), t.getVertexC()));
          edges.add(new Edge(t.getVertexC(), t.getVertexA()));
          triangles.remove(j);
        }

      }

      /*
        Tag multiple edges
        Note: if all triangles are specified anticlockwise then all
        interior edges are opposite pointing in direction.
      */
      for (int j=0; j<edges.size()-1; j++) {
        Edge e1 = edges.get(j);
        for (int k=j+1; k<edges.size(); k++) {
          Edge e2 = edges.get(k);
          if (e1.getVertexA() == e2.getVertexB() && e1.getVertexB() == e2.getVertexA()) {
            e1.setVertexA(null);
            e1.setVertexB(null);
            e2.setVertexA(null);
            e2.setVertexB(null);
          }
          /* Shouldn't need the following, see note above */
          if (e1.getVertexA() == e2.getVertexA() && e1.getVertexB() == e2.getVertexB()) {
            e1.setVertexA(null);
            e1.setVertexB(null);
            e2.setVertexA(null);
            e2.setVertexB(null);
          }
        }
      }

      /*
        Form new triangles for the current point
        Skipping over any tagged edges.
        All edges are arranged in clockwise order.
      */
      for (int j=0; j < edges.size(); j++) {
        Edge e = edges.get(j);
        if (e.getVertexA() == null || e.getVertexB() == null) {
          continue;
        }
        triangles.add(new Triangle(e.getVertexA(), e.getVertexB(), v));
      }

    }

    /*
      Remove triangles with supertriangle vertices
    */
    for (int i = triangles.size()-1; i >= 0; i--) {
      Triangle t = triangles.get(i);
      if (t.sharesVertex(superTriangle)) {
        triangles.remove(i);
      }
    }

    return triangles;
  }

}
