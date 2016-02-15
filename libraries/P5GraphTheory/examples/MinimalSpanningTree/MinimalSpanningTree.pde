import jto.p5graphtheory.*;
/*
 * This is an example showing the use of the Graph.minimalSpanningTree() method.
 * Click on the canvas to add vertices and edges to the graph.
 * Right Click on the canvas to clear the graph.
 * This recalculates the minimal spanning tree each time the screen is drawn.
 */
Graph myGraph; 
 
void setup()
{
  size(500,500);
  background(255);
  stroke(0,0,0,100);
  fill(255);
  smooth();
  
  myGraph = new Graph(); //make a new empty graph.
}

void draw()
{
  background(255);
  
  //First we check to see if the Graph is not empty.
  if(myGraph.getVertices().size() > 0) {
    
    //Now we create a complete graph by added edges from each
    //vertex to every other vertex.
    for(Vertex a : myGraph.getVertices()) {
       for(Vertex b : myGraph.getVertices()) {
         if(!a.equals(b)) {
           Edge edge = new Edge(a,b);
           myGraph.addEdge(edge);  
         }  
       }
    }
    //We use the static minimalSpanningTree method to create
    //the minimal spanning tree of our original graph.
    Graph spanningTree = Graph.minimalSpanningTree(myGraph);
    
    //Draw a line for each edge of the tree.
    for(Edge e : spanningTree.getEdges()) {
      line(e.getVertexA().getX(), e.getVertexA().getY(),
        e.getVertexB().getX(), e.getVertexB().getY());
    }
    //Draw an ellipse for each vertex of the tree.
    for(Vertex v : spanningTree.getVertices()) {
      ellipse(v.getX(), v.getY(), 5, 5); 
    }    
  }
}

void mousePressed()
{
  //If the left moust buttons is clicked, we add a new vertex
  //to the graph at the mouse location.
  if(mouseButton == LEFT) {
    myGraph.addVertex(new Vertex(mouseX, mouseY));  
  }
  else {
    myGraph.clearVertices(); //A right click will cause the
                             //vertices to be cleared.  
  }
}
