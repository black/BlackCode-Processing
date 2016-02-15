import jto.p5graphtheory.*;

/*
 * This is example of using the Graph.toTriangles() method to split the
 * graph into a list of triangles.
 * Click to add vertices. You need at least three to see anything.
 *
 */
Graph myGraph;
ArrayList<Integer> colors;
 
void setup()
{
  size(500,500);
  background(255);
  noStroke();
  smooth();
  
  myGraph = new Graph(); //make a new empty graph.
  
  //create a list of random colors and add enough for 1000 triangles.
  colors = new ArrayList<Integer>();
  for(int i = 0; i < 1000; i++) {
    colors.add(color(random(255),random(255),random(255)));
  }
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
    //We use the static toTriangles() method to create
    //a list of Triangle objects.
    Collection<Triangle> triangles = Graph.toTriangles(myGraph);
    
    //Now we draw a shape for each triangle and fill it with a color
    //from our list of colors.
    int i = 0;
    for(Triangle t : triangles) {    
      beginShape();
      fill(colors.get(i));
      vertex(t.getVertexA().getX(),t.getVertexA().getY());
      vertex(t.getVertexB().getX(),t.getVertexB().getY());
      vertex(t.getVertexC().getX(),t.getVertexC().getY());
      endShape();
      i++;
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
