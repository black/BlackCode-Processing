package jto.p5graphtheory;

import processing.core.PVector;

/**
 *
 *  This is the <code>Edge</code> class.</br></br>
 *
 *  This class models an undirected edge for a graph. The edge has two
 *  vertices and a length. The length is the Euclidean distance between the
 *  two vertices of the Edge. Edges are sorted by length using the comparable
 *  interface.</br>
 *  To associate a different cost to the edge (other than length) you can use
 *  the setCost() method, which will overwrite the length field with the
 *  new cost.<br><br>
 *  NOTE: These are undirected edges, meaning that an edge with vertices {a, b}
 *  is the same as an edge with vertices {b, a}.
 *
 *
 *
 *  @author J. Taylor O'Connor
 *  @version 2012.08.13
 */
public class Edge implements Comparable<Edge>
{
    private  Vertex vertexA;
    private  Vertex vertexB;

    private float length;

    /**
     * This is the constructor for edge objects.
     *
     * @param a <code>Vertex</code> A.
     * @param b <code>Vertex</code> B.
     */
    public Edge(Vertex a, Vertex b) {
        vertexA = a;
        vertexB = b;
        PVector pVectorA = vertexA.getPVector();
        PVector pVectorB = vertexB.getPVector();
        length = Math.abs(pVectorA.dist(pVectorB));
    }

    /**
     * Getter for Vertex A.
     *
     * @return Vertex A.
     */
    public Vertex getVertexA()
    {
        return vertexA;
    }

    /**
     * Getter for Vertex B.
     *
     * @return Vertex B.
     */
    public Vertex getVertexB()
    {
        return vertexB;
    }

    /**
     * Setter for Vertex A.
     *
     * @param vertex the new <code>Vertex</code>.
     *
     */
    public void setVertexA(Vertex vertex) {
        this.vertexA = vertex;
    }

    /**
     * Setter for Vertex B.
     *
     * @param vertex the new <code>Vertex</code>.
     *
     */
    public void setVertexB(Vertex vertex) {
        this.vertexB = vertex;
    }

    /**
     * Returns the length of the edge.
     * If a cost has been set using setCost(),
     * then this returns the cost instead of the length.
     *
     * @return The length or cost of the edge.
     */
    public float getLength()
    {
        return length;
    }

    /**
     * Tests whether this <code>Edge</code> is incident to
     * the given <code>Vertex</code>.
     *
     * @param v the <code>Vertex</code> to be tested.
     *
     * @return True if the <code>Vertex</code> is incident to this
     * <code>Edge</code>.
     */
    public boolean isIncidentTo(Vertex v) {
        return vertexA.equals(v) || vertexB.equals(v);
    }

    /**
     * This allows you to associate a cost with the edge.
     * The default cost is the length of the edge, which is the Euclidean
     * distance between the edge's two vertices.</br></br>
     * Setting a cost will overwrite the length field, and the Edge will use
     * this cost in comparisons with other Edges.
     *
     * @param cost the new cost for the edge.
     */
    public void setCost(float cost) {
        length = cost;
    }

    /**
     * This is the method inhereted from the Comparable interface.
     * This compares the length of this <code>Edge</code>
     * to the length of the other <code>Edge</code>.
     *
     * @param otherEdge the other <code>Edge</code> to compare with this one.
     *
     * @return -1 if this <code>Edge</code> is shorter than the other
     * <code>Edge</code>, 1 if this <code>Edge</code> is longer, and 0 if
     * the two Edges are the same length.
     */
    public int compareTo(Edge otherEdge)
    {
        if(this.getLength() < otherEdge.getLength()) {
            return -1;
        }
        else if(this.getLength() > otherEdge.getLength()) {
            return 1;
        }
        else {
            return 0;
        }
    }

}
