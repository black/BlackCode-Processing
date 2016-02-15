package jto.p5graphtheory;

import java.util.TreeSet;
import processing.core.PVector;

/**
 *  This is the <code>Vertex</code> class.</br></br>
 *
 *  This represents a vertex in a graph.  Vertices can be either
 *  2D or 3D vertices.  Each <code>Vertex</code> maintains a set of
 *  incident edges and a set of adjacent vertices.  The degree of the <code>
 *  Vertex</code> is also kept in a field. A boolean field
 *  for checking whether a vertex has been visited is included for algorithm
 *  purposes. Vertices are compared based on their Euclidean distance from the
 *  origin using the Comparable interface.<br>
 *
 *  The location of a <code>Vertex</code> is stored using the processing
 *  PVector class.
 *
 *
 *  @author J. Taylor O'Connor
 *  @version 2012.08.13
 */
public class Vertex implements Comparable<Vertex>
{
    private PVector vector;
    private TreeSet<Edge> incidentEdges;
    private TreeSet<Vertex> adjacentVertices;
    private boolean visited;
    private int degree;

    /**
     * This is the constructor for <code>Vertex</code> objects.
     *
     * @param vector the processing PVector whose location is to be copied to
     * this <code>Vertex</code>
     */
    public Vertex(PVector vector) {
        this.vector = new PVector();
        this.vector.set(vector);
        incidentEdges = new TreeSet<Edge>();
        adjacentVertices = new TreeSet<Vertex>();
        visited = false;
        degree = 0;
    }

    /**
     * This is the constructor for 3D <code>Vertex</code> objects.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     */
    public Vertex(int x, int y, int z) {
        PVector localVector = new PVector(x, y, z);
        this.vector = new PVector();
        this.vector.set(localVector);
        incidentEdges = new TreeSet<Edge>();
        adjacentVertices = new TreeSet<Vertex>();
        visited = false;
        degree = 0;
    }

    /**
     * This is the constructor for 2D <code>Vertex</code> objects.</br>
     * In this case the z-coordinate is set to 0.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Vertex(int x, int y) {
        this(x, y, 0);
    }

    /**
     * This is the constructor for <code>Vertex</code> objects.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     */
    public Vertex(float x, float y, float z) {
        PVector localVector = new PVector(x, y, z);
        this.vector = new PVector();
        this.vector.set(localVector);
        incidentEdges = new TreeSet<Edge>();
        adjacentVertices = new TreeSet<Vertex>();
        visited = false;
        degree = 0;
    }

    /**
     * This is the constructor for 2D <code>Vertex</code> objects.</br>
     * In this case the z-coordinate is set to 0.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Vertex(float x, float y) {
        this(x, y, 0);
    }

    /**
     * This add's an edge to the Vertex's list of incident edges. This means
     * that this vertex is one of the vertices in the edge.</br>
     * The degree of the <code>Vertex</code> is incremented as a result.
     *
     * @param edge the <code>Edge</code> to be added.
     */
    public void addIncidentEdge(Edge edge) {
        if(edge != null) {
            incidentEdges.add(edge);

            if(!edge.getVertexA().equals(this)) {
                adjacentVertices.add(edge.getVertexA());
                degree++;
            }
            else {
                adjacentVertices.add(edge.getVertexB());
                degree++;
            }
        }
    }

    /**
     * This removes an edge from the Vertex's list of incident edges.
     * The degree of the <code>Vertex</code> is decremented as a result.
     *
     * @param edge the <code>Edge</code> to be removed.
     */
    public void removeIncidentEdge(Edge edge) {
        if(edge != null) {
            incidentEdges.remove(edge);

            if(this.equals(edge.getVertexA())) {
                adjacentVertices.remove(edge.getVertexB());
                degree--;
            }
            else if(this.equals(edge.getVertexB())) {
                adjacentVertices.remove(edge.getVertexA());
                degree--;
            }
        }

    }

    /**
     * Returns a sorted <code>TreeSet</code> containing the adjacent
     * vertices to this <code>Vertex</code>.
     *
     * @return a TreeSet of Vertex objects containing the adjacent vertices.
     */
    public TreeSet<Vertex> getAdjacentVertices()
    {
        return adjacentVertices;
    }

    /**
     * Returns a sorted <code>TreeSet</code> containing the incident edges
     * to this <code>Vertex</code>.
     *
     * @return a TreeSet of Edge objects containing the incident edges.
     */
    public TreeSet<Edge> getIncidentEdges()
    {
        return incidentEdges;
    }

    /**
     * Returns the PVector that holds the location of this vertex.
     *
     * @return the PVector location of this vertex.
     */
    public PVector getPVector() {
        return vector;
    }

    /**
     * Getter for the x-coordinate.
     *
     * @return the x-coordinate.
     */
    public float getX()
    {
        return vector.x;
    }

    /**
     * Setter for the x-coordinate.
     *
     * @param x the new x-coordinate.
     */
    public void setX(float x) {
        vector.set(x, vector.y, vector.z);
    }

    /**
     * Getter for the y-coordinate.
     *
     * @return the y-coordinate.
     */
    public float getY()
    {
        return vector.y;
    }

    /**
     * Setter for the y-coordinate.
     *
     * @param y the new y-coordinate.
     */
    public void setY(float y) {
        vector.set(vector.x, y, vector.z);
    }

    /**
     * Getter for the z-coordinate.
     *
     * @return the z-coordinate.
     */
    public float getZ()
    {
        return vector.z;
    }

    /**
     * Setter for the z-coordinate.
     *
     * @param z the new z-coordinate.
     */
    public void setZ(float z) {
        vector.set(vector.x, vector.y, z);
    }

    /**
     * This changes the location of the <code>Vertex</code>.
     *
     * @param newLocation the new <code>PVector</code> location.
     */
    public void setLocation(PVector newLocation) {
        this.vector.set(newLocation);
    }

    /**
     * Returns the degree of the <code>Vertex</code>.
     *
     * @return the degree.
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Setter for the boolean visited field.
     *
     * @param visited the new boolean visited flag.
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Returns true if the <code>Vertex</code> has been marked as visited.
     *
     * @return True if visited, false if unvisited.
     */
    public boolean hasBeenVisited() {
        return visited;
    }

    /**
     * Tests whether this <code>Vertex</code> is adjacent to the other
     * <code>Vertex</code>, meaning they are contained in an <code>Edge</code>.
     *
     * @param v the <code>Vertex</code> in question.
     *
     * @return true if the two vertices are contained in an <code>Edge</code>.
     */
    public boolean isAdjacentTo(Vertex v) {
        return adjacentVertices.contains(v);
    }

    /**
     * Tests whether this <code>Vertex</code> is incident to the given
     * <code>Edge</code>.
     *
     * @param e the <code>Edge</code> to be tested.
     *
     * @return True if the <code>Edge</code> is incident to this.
     */
    public boolean isIncidentTo(Edge e) {
        return incidentEdges.contains(e);
    }

    /**
     * Tests for the equality of two vertices.
     *
     * @return true if the vertices are in the same location.
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vertex)) {
            return false;
        }
        Vertex otherVertex = (Vertex) obj;
        Float thisX = vector.x;
        Float thisY = vector.y;
        Float otherX = otherVertex.getPVector().x;
        Float otherY = otherVertex.getPVector().y;
        return((thisX.equals(otherX)) && thisY.equals(otherY));
    }

    /**
     * The method inhereted from the Comparable interface. <br>This compares
     * two vertices based on their Euclidean distance from the origin.
     *
     * @param otherVertex the other <code>Vertex</code> to be compared.
     *
     * @return -1 if this is closer, <br>1 if this is farther, <br>0 if this is the same
     * distance from the origin as the other <code>Vertex</code>.
     *
     */
    public int compareTo(Vertex otherVertex)
    {
        PVector origin = new PVector(0, 0, 0);
        float thisLength = Math.abs(vector.dist(origin));
        float otherLength = Math.abs(otherVertex.getPVector().dist(origin));

        if(thisLength < otherLength) {
            return -1;
        }
        else if(thisLength > otherLength) {
            return 1;
        }
        else {
            return 0;
        }

    }



}
