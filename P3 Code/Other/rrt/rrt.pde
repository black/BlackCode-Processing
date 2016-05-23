ArrayList<Node> nodes;
int lastRedraw;

/*float[][] rotMtx(float ang)
{
  float c = cos(ang);
  float s = sin(ang);
  float[][] a = {{c, -s, 0},{s, c, 0}};
  return a;
}

float[][] M = rotMtx(0.1);

PVector transformVec(float[][] M, PVector v)
{
  float x = M[0][0]*v.x + M[0][1]*v.y + M[0][2];
  float y = M[1][0]*v.x + M[1][1]*v.y + M[1][2];
  return new PVector(x, y);
}*/

void setup()
{
  size(800, 800);
  smooth();
  reset();
}

void reset()
{
  nodes = new ArrayList();
  background(192);
  lastRedraw = 0;
}

void keyPressed()
{
  reset();
}

Node findNearest(Node p)
{
  float minDist = 1e10;
  int minIdx = -1;
  for (int i = 0; i < nodes.size(); ++i)
  {
    float d = p.dist(nodes.get(i));
    if (d < minDist)
    {
      minDist = d;
      minIdx = i;
    }
  }
  return nodes.get(minIdx);
}

void grow()
{
  float x, y;
  do
  {
    x = random(-40, 40);
    y = random(-40, 40);
  } while (sq(x) + sq(y) > sq(40));
  x += mouseX;
  y += mouseY;
  
  Node sample = new Node(x, y);
  Node base = findNearest(sample);
  if (base.dist(sample) < 10.0)
      return;
  Node newNode = new Node(base, sample);
  nodes.add(newNode);
  newNode.draw();
}

void updateWeights()
{
  for (int i = 0; i < nodes.size(); ++i)
    nodes.get(i).weight = 1;
  for (int i = nodes.size()-1; i >= 0; --i)
  {
    Node node = nodes.get(i);
    if (node.parent != null)
        node.parent.weight += node.weight;
  }
}

void redraw_all()
{
  background(192);
  updateWeights();
  for (Node node : nodes)
    node.draw();
}

void draw()
{
  if (mousePressed)
  {
    if (nodes.size() == 0)
      nodes.add(new Node(mouseX, mouseY));
    for (int i = 0; i < 10; ++i)
      grow();
    if (nodes.size() - lastRedraw > 50)
    {
      redraw_all();  
      lastRedraw = nodes.size();
    }
  }
}
