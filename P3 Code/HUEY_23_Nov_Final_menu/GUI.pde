PImage Logo;
PImage Brush, Music, Splash, Grid, BrushB, MusicB, SplashB, GridB, Network, NetworkB, Root, RootB, Erase, EraseB;
PGraphics GUILAYER;
boolean drawmode = false, splashmode = false, musicmode = false, rootmode = false, networkmode = false, erasemode = false ;
PShape Kids;
AudioOutput out;
SineWave sine;
//--------Splash -------------------------
ArrayList splatpoop = new ArrayList();
//--------network Brush variable----------
ArrayList hist = new ArrayList();
float joinDist = 100;
//--------root Brush variable----------
ArrayList<Node> nodes = new ArrayList<Node>();
int lastRedraw;
//----------------------------------------
void GUIimages() {
  GUILAYER = createGraphics(displayWidth, displayHeight, P3D);
  //---------NEUTRAL------------------------
  BrushB = loadImage("Brushb.jpg");
  MusicB = loadImage("Musicb.jpg");
  SplashB = loadImage("splashb.jpg");
  GridB = loadImage("GridB.jpg");
  RootB = loadImage("rootb.jpg");
  NetworkB = loadImage("networkb.jpg");
  EraseB = loadImage("EraserB.jpg");
  //---------ACTIVE-------------------------
  Brush = loadImage("Brush.jpg");
  Music = loadImage("Music.jpg");
  Splash = loadImage("splash.jpg");
  Grid = loadImage("Grid.jpg");
  Root = loadImage("root.jpg");
  Network = loadImage("network.jpg");
  Erase = loadImage("Eraser.jpg");
  Logo = loadImage("logo.png");
  Kids = loadShape("kids.svg");
  //---------Resing Images-------------------
  MusicB.resize(int(width*0.04), int(width*0.04));
  Music.resize(int(width*0.04), int(width*0.04));
  Brush.resize(int(width*0.04), int(width*0.04));
  BrushB.resize(int(width*0.04), int(width*0.04));
  Splash.resize(int(width*0.04), int(width*0.04));
  SplashB.resize(int(width*0.04), int(width*0.04));
  Network.resize(int(width*0.04), int(width*0.04));
  NetworkB.resize(int(width*0.04), int(width*0.04));
  Root.resize(int(width*0.04), int(width*0.04));
  RootB.resize(int(width*0.04), int(width*0.04));
  Erase.resize(int(width*0.04), int(width*0.04));
  EraseB.resize(int(width*0.04), int(width*0.04));
}

void GUI() {
  //--------------------------START MAIN GUI ---------------------------------
  GUILAYER.beginDraw();
  GUILAYER.background(-1, 0);
  //GUILAYER.smooth();
  GUILAYER.beginShape();
  GUILAYER.noStroke();
  GUILAYER.fill(#FF005E);
  GUILAYER.vertex(0, 0);//1
  GUILAYER.vertex(width, 0);//2
  GUILAYER.vertex(width, 30);//3
  GUILAYER.vertex(7*width/10+25, 30);//4
  GUILAYER.vertex(7*width/10, 45);//5
  GUILAYER.vertex(3*width/10, 45);//6
  GUILAYER.vertex(3*width/10-25, 30);//7
  GUILAYER.vertex(0, 30);//8
  GUILAYER.endShape();
  GUILAYER.pushStyle();
  GUILAYER.strokeWeight(3);
  GUILAYER.stroke(0);
  GUILAYER.noFill();
  GUILAYER.beginShape();
  GUILAYER.vertex(10, height-BrushB.height-20);
  GUILAYER.vertex(width/6, height-BrushB.height-20);
  GUILAYER.vertex(width/6+50, height-20);
  GUILAYER.vertex(width-width/6-50, height-20);
  GUILAYER.vertex(width-width/6, height-BrushB.height-20);
  GUILAYER.vertex(width-10, height-BrushB.height-20);
  GUILAYER.endShape();
  GUILAYER.popStyle();
  //----------------- Music -----------------------------------------------
  for (int iconPos=0;iconPos<2*width;iconPos+=width) {
    if (!musicmode) GUILAYER.image(MusicB, (iconPos==width)?width-MusicB.width:0, height/2-MusicB.height-50 );
    if (musicmode) GUILAYER.image(Music, (iconPos==width)?width-MusicB.width:0, height/2-MusicB.height-50);
    //----------------- Brush -----------------------------------------------
    if (!drawmode) GUILAYER.image(BrushB, (iconPos==width)?width-BrushB.width:0, height/2-BrushB.height/2);
    if (drawmode) GUILAYER.image(Brush, (iconPos==width)?width-BrushB.width:0, height/2-BrushB.height/2);
    //  //----------------- Splash -----------------------------------------------
    if (!splashmode) GUILAYER.image(SplashB, (iconPos==width)?width-SplashB.width:0, height/2+50 );
    if (splashmode) GUILAYER.image(Splash, (iconPos==width)?width-SplashB.width:0, height/2+50);
  }
  //----------------- Brsuh Network -----------------------------------------
  if (drawmode && !networkmode) {
    GUILAYER.image(NetworkB, width/2+100, height/2-NetworkB.height);
    // drawmode =!drawmode;
  }
  if (drawmode && networkmode) {
    GUILAYER.image(Network, width/2+100, height/2-Network.height);
    // drawmode =!drawmode;
  }
  //  //----------------- Brush Root-----------------------------------------------
  if (drawmode && !rootmode) {
    GUILAYER.image(RootB, width/2-RootB.width-100, height/2-RootB.height);
    // drawmode =!drawmode;
  }
  if (drawmode && rootmode ) {
    GUILAYER.image(Root, width/2-Root.width-100, height/2-Root.height);
    //drawmode =!drawmode;
  }
  //  //----------------- ERASER Left -----------------------------------------------
  if (drawmode && !erasemode) {
    GUILAYER.image(EraseB, width/2-EraseB.width/2, height/2-EraseB.height);
    // drawmode =!drawmode;
  }
  if (drawmode && erasemode) {
    GUILAYER.image(Erase, width/2-EraseB.width/2, height/2-EraseB.height);
    //drawmode =!drawmode;
  }
  //------------------ LOGO -----------------------------------------------
  GUILAYER.noStroke();
  Logo.resize(int(width*0.04), int(width*0.04));
  GUILAYER.ellipse(Logo.width + 40, Logo.height/2, Logo.width+10, Logo.width+10);
  GUILAYER.pushStyle();
  GUILAYER.imageMode(CENTER);
  GUILAYER.image(Logo, Logo.width + 40, Logo.height/2);
  GUILAYER.popStyle();
  GUILAYER.endDraw();
  //--------------------------END MAIN GUI ---------------------------------
  //image(GUILAYER, 0, 0);
}

//-------------------ERASER FUNCTION ------------------

void eraseFunction(float linex, float liney) {
  color c = color(0, 0);
  pg.beginDraw();
  pg.loadPixels();
  for (int x=0; x<pg.width; x++) {
    for (int y=0; y<pg.height; y++ ) {
      float distance = dist(x, y, linex, liney);
      if (distance <= 25) {
        int loc = x + y*pg.width;
        pg.pixels[loc] = c;
      }
    }
  }
  pg.updatePixels();
  pg.endDraw();
}

//-------------------root brush functions ------------
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


void grow(float linex, float liney)
{
  float x, y;
  do
  {
    x = random(-40, 40);
    y = random(-40, 40);
  } 
  while (sq (x) + sq(y) > sq(40));
  x += linex;
  y += liney;

  Node sample = new Node(x, y);
  Node base = findNearest(sample);
  if (base.dist(sample) < 10.0)
    return;
  Node newNode = new Node(base, sample);
  nodes.add(newNode);
  newNode.display();
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

//void redraw_all()
//{
//  //pg.background(192);
//  updateWeights();
//  for (Node node : nodes)
//    node.display();
//}

class Node
{
  PVector pos;
  Node parent;
  int weight;

  Node(float x, float y)
  {
    pos = new PVector(x, y);
    weight = 1;
  }

  Node(Node base, Node sample)
  {
    PVector step = PVector.sub(sample.pos, base.pos);
    step.limit(5.0);
    pos = PVector.add(base.pos, step);
    parent = base;
    weight = 1;
  }

  float dist(Node other)
  {
    return PVector.dist(pos, other.pos);
  }

  void display()
  {
    if (parent == null)
      return;
    pg.strokeWeight(1+log(weight)*0.5);  
    pg.line(parent.pos.x, parent.pos.y, pos.x, pos.y);
  }
}

