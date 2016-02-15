import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// PathFinding_02
Graph graph;
// These next 2 are only needed to display 
// the nodes and edges.
GraphEdge[] edges;
GraphNode[] nodes;

// Need these to animate the person
World world;
StopWatch sw;
Vehicle person;
Picture view;

// Used to identify route selcted using mouse
GraphNode startNode, endNode;
boolean dragging;
IGraphSearch pf;

public void setup() {
  size(640, 300);
  cursor(CROSS);
  world = new World(width, height);
  graph = getGraph();
  edges = graph.getEdgeArray();
  nodes = graph.getNodeArray();
  pf = new GraphSearch_Astar(graph, new AshCrowFlight());

  GraphNode start = graph.getNode(0);
  person = new Vehicle(new Vector2D(start.x(), start.y()), // position
  20, // collision radius
  new Vector2D(0, 0), // velocity
  50, // maximum speed
  new Vector2D(1, 0), // heading
  1, // mass
  4, // turning rate
  200 // max force
  ); 
  view = new PersonPic(this, 24);  
  person.renderer(view);
  person.AP().pathFactors(6, 1).pathOn();
  world.add(person);
  sw = new StopWatch();
}

public void draw() {
  double elapsedTime = sw.getElapsedTime();
  world.update(elapsedTime);
  if (person.AP().pathRouteLength() == 0)
    person.velocity(0, 0);
  background(60, 160, 60);
  drawEdges();
  drawNodes();
  world.draw(elapsedTime);
  // Draw node selector
  if (dragging) {
    if (endNode != null) {
      strokeWeight(4);
      stroke(0, 255, 255);
      line(startNode.xf(), startNode.yf(), endNode.xf(), endNode.yf());
    }
    else {
      strokeWeight(3);
      stroke(64, 0, 64);
      line(startNode.xf(), startNode.yf(), mouseX, mouseY);
    }
  }
}

public void mousePressed() {
  // Only consider a mouse press if near a node
  startNode = graph.getNodeNear(mouseX, mouseY, 10.0f);
}

public void mouseDragged() {
  if (startNode != null) {
    dragging = true;
    endNode = graph.getNodeNear(mouseX, mouseY, 10.0f);
  }
}

public void mouseReleased() {
  if (startNode != null && endNode!= null && startNode != endNode) {
    person.AP().pathSetRoute(pf.search(startNode.id(), endNode.id()));
    person.moveTo(startNode.x(), startNode.y());
    person.velocity(0, 0);
  }
  startNode = endNode = null;
  dragging = false;
}

public void drawNodes() {
  stroke(0);
  fill(200, 200, 255);
  strokeWeight(1);
  for (GraphNode n : nodes)
    ellipse(n.xf(), n.yf(), 20, 20);
  fill(0);
  noStroke();
  for (GraphNode n : nodes)
    text(n.id(), n.xf() - 4, n.yf() + 4);
}

public void drawEdges() {
  strokeWeight(6);
  stroke(20, 120, 20);
  noFill();
  for (GraphEdge edge : edges) {
    GraphNode n0 = edge.from();
    GraphNode n1 = edge.to();
    line(n0.xf(), n0.yf(), n1.xf(), n1.yf());
  }
}

public Graph getGraph() {
  Graph graph = new Graph();
  // Create and add node
  GraphNode node;
  //                   ID   X    Y
  node = new GraphNode(0, 40, 45);
  graph.addNode(node);
  node = new GraphNode(1, 395, 30);
  graph.addNode(node);
  node = new GraphNode(2, 80, 130);
  graph.addNode(node);
  node = new GraphNode(3, 175, 110);
  graph.addNode(node);
  node = new GraphNode(4, 295, 155);
  graph.addNode(node);
  node = new GraphNode(5, 410, 125);
  graph.addNode(node);
  node = new GraphNode(6, 530, 65);
  graph.addNode(node);
  node = new GraphNode(6, 600, 115);
  graph.addNode(node);
  node = new GraphNode(7, 60, 265);
  graph.addNode(node);
  node = new GraphNode(8, 330, 255);
  graph.addNode(node);
  node = new GraphNode(9, 510, 260);
  graph.addNode(node);

  // Edges for node 0
  graph.addEdge(0, 1, 0, 0);
  graph.addEdge(0, 2, 0, 0);
  graph.addEdge(0, 3, 0, 0);
  // Edges for node 1
  graph.addEdge(1, 3, 0, 0);
  graph.addEdge(1, 4, 0, 0);
  graph.addEdge(1, 5, 0, 0);
  graph.addEdge(1, 6, 0, 0);
  // Edges for node 2
  graph.addEdge(2, 3, 0, 0);
  graph.addEdge(2, 7, 0, 0);
  // Edges for node 3
  graph.addEdge(3, 4, 0, 0);
  // Edges for node 4
  graph.addEdge(4, 8, 0, 0);
  // Edges for node 5
  graph.addEdge(5, 6, 0, 0);
  graph.addEdge(5, 8, 0, 0);
  graph.addEdge(5, 9, 0, 0);
  // Edges for node 6
  graph.addEdge(6, 9, 0, 0);
  // Edges for node 7
  graph.addEdge(7, 8, 0, 0);

  return graph;
}
