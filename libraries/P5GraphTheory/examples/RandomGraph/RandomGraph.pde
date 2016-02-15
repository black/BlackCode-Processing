import jto.p5graphtheory.*;
/*
 * This is an example for the Graph.randomCompleteGraph() method.
 * 
 */
Graph myGraph;

void setup()
{
  size(500,500);
  background(255);
  stroke(0,0,0,100);
  fill(255);
  smooth();
  
  //create a random graph with 10 vertices.
  myGraph = Graph.randomCompleteGraph(10, this);
  
}

void draw()
{
  background(255);
  //First we draw a line for each edge in the graph.
  for(Edge e : myGraph.getEdges()) {
    line(e.getVertexA().getX(), e.getVertexA().getY(),
      e.getVertexB().getX(), e.getVertexB().getY());
  }
  //Then we draw a circle at each vertex in the graph.
  for(Vertex v : myGraph.getVertices()) {
    ellipse(v.getX(), v.getY(), 5, 5);
  }
}


