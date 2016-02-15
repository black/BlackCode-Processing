import game2dai.entities.*;
import game2dai.entityshapes.ps.*;
import game2dai.maths.*;
import game2dai.*;
import game2dai.entityshapes.*;
import game2dai.fsm.*;
import game2dai.steering.*;
import game2dai.utils.*;
import game2dai.graph.*;

// PathFinding_01
Graph graph;
// These next 2 are only needed to display 
// the nodes and edges.
GraphEdge[] edges;
GraphNode[] nodes;

public void setup() {
  size(640, 300);
  cursor(CROSS);
  graph = getGraph();
  edges = graph.getEdgeArray();
  nodes = graph.getNodeArray();
}

public void draw() {
  background(60, 160, 60);
  drawEdges();
  drawNodes();
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

