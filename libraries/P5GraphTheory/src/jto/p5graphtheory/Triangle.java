package jto.p5graphtheory;


/**
 *  This is the <code>Triangle</code> class.</br></br>
 *  This represents a triangle and is included in this library for use
 *  with the static triangulation method of <code>Graph</code>.
 *  <br>
 *  To put this in a similar context as the rest of the libary, a <code>Triangle</code>
 *  is a <code>Graph</code> with three vertices and two edges. In other words, is
 *  the complete Graph when k = 3.
 *
 *  @author Taylor
 *  @version Aug 14, 2012
 */
public class Triangle {

  private Vertex vertexA;
  private Vertex vertexB;
  private Vertex vertexC;

  /**
   * This is the default constructor for <code>Triangle</code> objects.<br>
   * The three vertices are set to <code>null</code>.
   */
  public Triangle() {
      this(null, null, null);
  }

  /**
   * This is the constructor for <code>Triangle</code> objects.
   *
   * @param vertexA vertex A.
   * @param vertexB vertex B.
   * @param vertexC vertex C.
   */
  public Triangle(Vertex vertexA, Vertex vertexB, Vertex vertexC) {
      this.vertexA = vertexA;
      this.vertexB = vertexB;
      this.vertexC = vertexC;
  }

  /**
   * Getter for Vertex A.
   *
   * @return vertex A.
   */
  public Vertex getVertexA() {
      return vertexA;
  }

  /**
   * Setter for Vertex A.
   *
   * @param vertex the new <code>Vertex</code> location for vertex A.
   */
  public void setVertexA(Vertex vertex) {
      vertexA = new Vertex(vertex.getPVector());
  }

  /**
   * Getter for Vertex B.
   *
   * @return vertex B.
   */
  public Vertex getVertexB() {
      return vertexB;
  }

  /**
   * Setter for Vertex B.
   *
   * @param vertex the new <code>Vertex</code> location for vertex B.
   */
  public void setVertexB(Vertex vertex) {
      vertexB = new Vertex(vertex.getPVector());
  }

  /**
   * Getter for Vertex C.
   *
   * @return vertex C.
   */
  public Vertex getVertexC() {
      return vertexC;
  }

  /**
   * Setter for Vertex C.
   *
   * @param vertex the new <code>Vertex</code> location for vertex C.
   */
  public void setVertexC(Vertex vertex) {
      vertexC = new Vertex(vertex.getPVector());
  }


  /**
   * This tests to see if any of the vertices of this <code>Triangle</code>
   * are the same as any of the vertices of another <code>Triangle</code>.
   *
   * @param other the other <code>Triangle</code> to be compared.
   *
   * @return <code>true</code> if any of the vertices of the other
   * <code>Triangle</code> are the same as any of the vertices of this
   * <code>Triangle</code>.
   */
  public boolean sharesVertex(Triangle other) {
    return vertexA.equals(other.getVertexA()) ||
        vertexA.equals(other.getVertexB()) ||
        vertexA.equals(other.getVertexC()) ||
        vertexB.equals(other.getVertexA()) ||
        vertexB.equals(other.getVertexB()) ||
        vertexB.equals(other.getVertexC()) ||
        vertexC.equals(other.getVertexA()) ||
        vertexC.equals(other.getVertexB()) ||
        vertexC.equals(other.getVertexC());
  }

}
