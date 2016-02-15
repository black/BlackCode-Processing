package jto.p5graphtheory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;
import processing.core.PApplet;
import java.util.TreeSet;

/**
 *  This is the <code>Graph</code> class. This is the main class for the
 *  graph theory library. </br></br>
 *  A graph is an ordered pair of sets (V, E), where V is a set of Vertices
 *  and can be any set. E is a set of 2-element subsets of V, called Edges.
 *  </br></br>
 *  This class allows for the modeling of Graph objects.
 *  This class also contains several static methods for performing operations
 *  on graphs and creating certain types of graphs.</br></br>
 *  This library is meant to provide simple ways to visualize graphs and
 *  graph theory algorithms. Graph data structures can also be used to model
 *  many things.
 *
 *
 *  @author J. Taylor O'Connor
 *  @version 2012.08.14
 */
public class Graph
{
    private TreeSet<Vertex> vertices;
    private TreeSet<Edge> edges;

    /**
     * This is the constructor for <code>Graph</code> objects.
     */
    public Graph()
    {
        edges = new TreeSet<Edge>();
        vertices = new TreeSet<Vertex>();
    }

    /**
     * Adds a vertex to the TreeSet of Vertices or does nothing if
     * the vertex is already in the graph.
     *
     * @param v the <code>Vertex</code>
     */
    public void addVertex(Vertex v) {
        vertices.add(v);
    }

    /**
     * Removes the <code>Vertex</code> from the <code>Graph</code> if it is
     * present, or does nothing if it is not present. If the vertex is removed
     * all of the edges incident to it are also removed.
     *
     * @param v the <code>Vertex</code> to be removed.
     */
    public void removeVertex(Vertex v) {
        vertices.remove(v);
        Iterator<Edge> it = edges.iterator();
        while(it.hasNext()) {
            Edge nextEdge = it.next();
            if(nextEdge.isIncidentTo(v)) {
                it.remove();
            }
        }
    }

    /**
     * This clears all the vertices in the graph.</br>
     * This also clears all of the edges, because the edges cannot
     * exist in the absense of vertices.
     *
     */
    public void clearVertices() {
        vertices.clear();
        edges.clear();
    }

    /**
     * Searches the <code>Graph</code> for the given <code>Vertex</code>.
     *
     * @param v the <code>Vertex</code> in question.
     *
     * @return true if the <code>Vertex</code> is contained in this
     * <code>Graph</code>.
     */
    public boolean containsVertex(Vertex v) {
        if(vertices.contains(v)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This creates a new <code>Edge</code> connecting the two
     * given vertices and adds it to the set of Edges.</br>
     * If either of the two vertices of the new <code>Edge</code> are not in this
     * <code>Graph</code>, then they are also added.
     *
     * @param a the first <code>Vertex</code>.
     * @param b the second <code>Vertex</code>.
     */
    public void addEdge(Vertex a, Vertex b) {
        Edge edge = new Edge(a, b);
        edges.add(edge);
        a.addIncidentEdge(edge);
        b.addIncidentEdge(edge);
        this.addVertex(a);
        this.addVertex(b);
    }

    /**
     * This adds the given <code>Edge</code> to the <code>Graph</code>.</br>
     * This also adds the Edge's vertices if they are not in the <code>Graph</code>.
     *
     * @param e the new <code>Edge</code> to be added.
     */
    public void addEdge(Edge e) {
        edges.add(e);
        this.addVertex(e.getVertexA());
        this.addVertex(e.getVertexB());
    }

    /**
     * This removes the <code>Edge</code> containing the two vertices from the set
     * of edges if it exists in the <code>Graph</code>.
     *
     * @param a the first <code>Vertex</code> in the <code>Edge</code>.
     * @param b the second <code>Vertex</code> in the <code>Edge</code>.
     */
    public void removeEdge(Vertex a, Vertex b) {
        Edge e = new Edge(a, b);
        edges.remove(e);
        for(Vertex v : vertices) {
            if(v.equals(a) ||
                v.equals(b)) {
                v.removeIncidentEdge(e);
            }
        }
    }

    /**
     * This removes the <code>Edge</code> from the set
     * of edges if it exists in the <code>Graph</code>.
     *
     * @param e the <code>Edge</code> to be removed.
     */
    public void removeEdge(Edge e) {
        edges.remove(e);
        for(Vertex v : vertices) {
            if(v.equals(e.getVertexA()) ||
                v.equals(e.getVertexB())) {
                v.removeIncidentEdge(e);
            }
        }
    }

    /**
     * This clears the set of Edges.
     */
    public void clearEdges()
    {
        edges.clear();
    }

    /**
     * This tests to see if the <code>Graph</code> contains the given edge.
     *
     * @param e the <code>Edge</code> in question.
     *
     * @return <code>true</code> if the <code>Edge</code> is contained in the
     * <code>Graph</code>.
     */
    public boolean containsEdge(Edge e) {
        if(edges.contains(e)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the TreeSet of vertices.
     *
     * @return a TreeSet of <code>Vertex</code> objects that are the
     * vertices of the <code>Graph</code>.
     */
    public TreeSet<Vertex> getVertices()
    {
        return vertices;
    }

    /**
     * Returns the TreeSet of edges.
     *
     * @return a TreeSet of <code>Edge</code> objects that are the
     * edges of the <code>Graph</code>.
     */
    public TreeSet<Edge> getEdges()
    {
        return edges;
    }

    /**
     * This will generate a complete <code>Graph</code> with the specified
     * number of vertices that are randomly distributed so that the coordinates
     * of each vertex lie somewhere on the canvas of the parent
     * <code>PApplet</code>.
     *
     * @param numberOfVertices The number of vertices.
     * @param parent the <code>PApplet</code> that is creating this
     * <code>Graph</code>.
     *
     * @return A random complete <code>Graph</code>.
     *
     */
    public static Graph randomCompleteGraph(int numberOfVertices,
        PApplet parent) {

        Graph graph = new Graph();

        for(int i = 0; i < numberOfVertices; i++) {
            graph.addVertex(new Vertex(
                parent.random(parent.width), parent.random(parent.height)));
        }

        for(Vertex a : graph.getVertices()) {
            for(Vertex b : graph.getVertices()) {
                if(!a.equals(b)) {
                    graph.addEdge(a, b);
                }
            }
        }

        return graph;
    }

    /**
     * This is an implementation of Kruskal's Algorithm for finding the minimal
     * spanning tree of a graph.</br>
     * This will work for disconnected graphs, but it will return a Graph
     * which contains multiple distinct trees.
     *
     * @param g The source <code>Graph</code> for the tree.
     * @return The minimal spanning tree.
     */
    public static Graph minimalSpanningTree(Graph g) {
        Graph spanningTree = new Graph();

        TreeSet<Edge> edges = g.getEdges();
        TreeSet<Vertex> vertices = g.getVertices();
        DisjointSet<Vertex> vertexSet = new DisjointSet<Vertex>();

        for(Vertex v : vertices) {
            vertexSet.makeSet(v);
        }
        if(edges.size() > 0) {
            Object[] edgeArray = edges.toArray();
            Edge firstEdge = (Edge) edgeArray[0];

            spanningTree.addEdge(firstEdge);
            vertexSet.union(firstEdge.getVertexA(), firstEdge.getVertexB());

            for(int i = 1; i < edgeArray.length; i++) {
                Edge edge = (Edge) edgeArray[i];
                if(vertexSet.find(edge.getVertexA()) != null) {
                    if(vertexSet.find(edge.getVertexA()).contains(edge.getVertexB())) {
                        //This edge would create a cycle.
                    }
                    else {
                        spanningTree.addEdge(edge);

                        vertexSet.union(edge.getVertexA(), edge.getVertexB());
                    }
                }
            }
        }
        return spanningTree;
    }

    /**
     * This is an implementation of Kruskal's Algorithm for finding the minimal
     * spanning tree for the given collection of Vertex objects.</br></br>
     *
     * This will return a minimal spanning tree for the complete graph that
     * contains these vertices.
     *
     * @param vertices A <code>Collection</code> of <code>Vertex</code>
     * @return The minimal spanning tree.
     */
    public static Graph minimalSpanningTree(Collection<Vertex> vertices) {
        Graph source = new Graph();
        for(Vertex v : vertices) {
            source.addVertex(v);
        }
        for(Vertex a : source.getVertices()) {
            for(Vertex b : source.getVertices()) {
                source.addEdge(a, b);
            }
        }
        return minimalSpanningTree(source);
    }

    /**
     * This is a breadth-first algorithm for finding a spanning
     * tree of the given <code>Graph</code> starting from the specified
     * <code>Vertex</code>. The resulting tree contains all the vertices
     * in the original <code>Graph</code>.</br></br>
     *
     * If this method is passed a complete graph, the spanning tree that
     * results will just be a Graph with Edges connecting the initial
     * Vertex to the rest of the Vertices in the source Graph.
     *
     * @param graph The source <code>Graph</code>.
     * @param initialVertex The initial <code>Vertex</code>.
     *
     * @return A spanning tree containing the vertices of the source Graph.
     */
    public static Graph spanningTree(Graph graph, Vertex initialVertex) {

        Graph spanningTree = new Graph();
        Queue<Vertex> vertexQueue = new LinkedList<Vertex>();

        vertexQueue.offer(initialVertex);
        spanningTree.addVertex(initialVertex);

        while(!vertexQueue.isEmpty()) {
            Vertex v = vertexQueue.remove();

            TreeSet<Edge> incidentEdges = v.getIncidentEdges();
            for(Edge edge : incidentEdges) {
                if(edge.getVertexA().equals(v)) {
                    if(!spanningTree.getVertices().contains(edge.getVertexB())) {
                        spanningTree.addEdge(edge);
                        vertexQueue.offer(edge.getVertexA());
                    }
                }
                else if(edge.getVertexB().equals(v)) {
                    if(!spanningTree.getVertices().contains(edge.getVertexA())) {
                        spanningTree.addEdge(edge);
                        vertexQueue.offer(edge.getVertexB());
                    }
                }
            }
        }

        return spanningTree;
    }

    /**
     * This will return a delaunay triangulation of the vertices in the
     * source graph.<br>
     * This uses the Triangulate library which can be found at:<br>
     * <a href="http://wiki.processing.org/w/Triangulation">
     * http://wiki.processing.org/w/Triangulation</a><br><br>
     * Note: This method does not take the edges of the source graph into
     * consideration. You will get the same result regardless of which
     * edges are present in the graph.
     *
     * @param graph The source <code>Graph</code>.
     *
     * @return A planar triangulated <code>Graph</code>.
     */
    public static Graph triangulatedGraph(Graph graph) {

        return Graph.triangulatedGraph(graph.getVertices());
    }

    /**
     * This will return a delaunay triangulation of the collection of
     * vertices.<br>
     * This uses the Triangulate library which can be found at:<br>
     * <a href="http://wiki.processing.org/w/Triangulation">
     * http://wiki.processing.org/w/Triangulation</a>
     *
     * @param vertices A collection of Vertex objects to be triangulated.
     *
     * @return A <code>graph</code> that is a planar triangulation of the given
     * collection of vertices.
     *
     */
    public static Graph triangulatedGraph(Collection<Vertex> vertices) {
        Graph result = new Graph();
        TreeSet<Vertex> vertexSet = new TreeSet<Vertex>();
        for(Vertex v : vertices) {
            vertexSet.add(v);
            result.addVertex(v);
        }
        ArrayList<Triangle> triangles = Triangulate.triangulate(vertexSet);
        for(Triangle t : triangles) {
            result.addEdge(new Edge(t.getVertexA(), t.getVertexB()));
            result.addEdge(new Edge(t.getVertexC(), t.getVertexB()));
            result.addEdge(new Edge(t.getVertexC(), t.getVertexA()));
        }
        return result;
    }

    /**
     * This returns a list of triangles that make up the delaunay triangulation
     * of the vertices in the source <code>Graph</code>.
     * This is identical to the triangulate() method of the Triangulation
     * library, which can be found here:</br>
     * <a href="http://wiki.processing.org/w/Triangulation">
     * http://wiki.processing.org/w/Triangulation</a>
     *
     * @param graph The source <code>Graph</code>.
     *
     * @return a Collection of <code>Triangle</code> objects that represent the
     * trianglulation of the source <code>Graph</code>.
     */
    public static Collection<Triangle> toTriangles(Graph graph) {
        return Triangulate.triangulate(graph.getVertices());
    }

    /**
     * This returns a list of triangles that make up the delaunay triangulation
     * of the vertices in the source <code>Graph</code>.
     * This is identical to the triangulate() method of the Triangulation
     * library, which can be found here:</br>
     * <a href="http://wiki.processing.org/w/Triangulation">
     * http://wiki.processing.org/w/Triangulation</a>
     *
     * @param vertices The collection of vertices to make up the triangulation.
     *
     * @return a Collection of <code>Triangle</code> objects that represent the
     * trianglulation of the source <code>Graph</code>.
     */
    public static Collection<Triangle> toTriangles(Collection<Vertex> vertices) {
        return Triangulate.triangulate(vertices);
    }


    /**
     *  This is the <code>DisjointSet</code> class.
     *  This is a simple implementation of a disjoint set data structure
     *  for use in the minimal spanning tree algorithm of the <code>
     *  Graph</code> class.</br>
     *
     *  The structure is esentially a LinkedList of LinkedLists. <br><br>
     *  I've made this class public in case anyone who hasn't ever used
     *  this kind of data structure wants to mess around with it.
     *  @param <E>
     *
     *  @author J. Taylor O'Connor
     *  @version 2012.08.14
     */
    public static class DisjointSet<E>
    {
        private LinkedList<LinkedList<E>> sets;

        /**
         * This is the constructor for <code>DisjointSet</code> objects.
         */
        public DisjointSet() {
            sets = new LinkedList<LinkedList<E>>();
        }

        /**
         * This creates a new LinkedList that contains the one object that
         * is the parameter, and adds it to the list of LinkedLists.
         *
         * @param singleton the single data member that will make up the new
         * LinkedList.
         */
        public void makeSet(E singleton) {
            LinkedList<E> newSet = new LinkedList<E>();
            newSet.add(singleton);
            sets.add(newSet);
        }

        /**
         * This method takes the list containing b and appends it to the list
         * containing a. Then the original list containing b is removed from
         * the overall list of LinkedLists.
         *
         * @param a the member whose list will get b's list appended to it.
         * @param b the member whose list will be appended to a's list.
         */
        public void union(E a, E b) {
            LinkedList<E> foundSet = null;
            LinkedList<E> appendedSet = null;
            for(LinkedList<E> set : sets) {
                if(set.contains(a)) {
                    foundSet = set;
                }
                else if(set.contains(b)) {
                    appendedSet = set;
                }
            }
            if((foundSet != null) && appendedSet != null) {
                for(E element : appendedSet) {
                    foundSet.add(element);
                }
                sets.remove(appendedSet);
            }
        }

        /**
         * This returns the LinkedList in the list of LinkedLists that
         * contains the element specified, or null if none of the lists
         * contain the element.
         *
         * @param element the element to be found.
         *
         * @return the LinkedList containing the element.
         */
        public LinkedList<E> find(E element) {
            for(LinkedList<E> set : sets) {
                if(set.contains(element)) {
                    return set;
                }
            }
            return null;
        }

    }
}


