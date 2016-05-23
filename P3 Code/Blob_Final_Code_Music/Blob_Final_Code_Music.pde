import blobscanner.*;
PGraphics edges; // draw blob on this layer
PGraphics testLayer; // drawing canvas: user draws here
PImage img, colimg;
Detector bs; // blob object for blob detection
int minimumWeight = 10;
final int MAXEDGES = (500*500)/4; 
int[] X ;
int[] Y ;
color c;
int R, G, B;
float rprev, gprev, bprev;
void setup() {
  size(500, 500);
  bs = new Detector(this, 0, 0, width, height, 255);  // initialaizing blob detector
  //----------------------------------------------------
  edges = createGraphics(width, height);  // creating blob detector layer
  testLayer = createGraphics(width, height); // creating canvas layer
  testLayer.beginDraw(); // initialaizing blank canvas layer otherwise it will throw null point exception
  testLayer.endDraw();
}
void draw() {
  background(255);
  //--------- RANDOM COLOR GENERATOR ---------
  if (keyPressed) {
    c = color(random(255), random(255), random(255)); // random color
  }
  //-------------------------------------------
  fill(c);
  noStroke();
  ellipse(mouseX, mouseY, 10, 10);
  testLayer.beginDraw();
  if (mousePressed) {
    //    testLayer.fill(c);
    //    testLayer.noStroke();
    testLayer.stroke(c);
    testLayer.strokeWeight(10);
    testLayer.line(mouseX, mouseY, pmouseX, pmouseY);
    //testLayer.ellipse(mouseX, mouseY, 30, 30);
  }
  testLayer.endDraw();
  //---------------------------------
  img = testLayer.get(0, 0, width, height);
  //---------------------------------
  int k = 0;
  colimg = img.get();
  img.filter(THRESHOLD, 0.3);
  img.loadPixels();
  bs.findBlobs(img.pixels, img.width, img.height);  // blob fiding in testLayer PGraphics 
  bs.loadBlobsFeatures();
  bs.weightBlobs(false); // weight of the blob is false. you can set it true and set the weight threshold
  getEdgeCoordinates();
  edges.beginDraw();
  for (int i = 0; boolean( X[i]); i++) {
    edges.stroke(0, 255, 0); 
    edges.strokeWeight(1);
    edges.point(X[i], Y[i]);//or do what you want
  } 
  //--------------------------------------------------
  bs.findCentroids();
  colimg.loadPixels();
  int lastCol = 255; // transparent black not going to have this
  for (int i = 0; i < bs.getBlobsNumber(); i++) {
    edges.point(bs.getCentroidX(i), bs.getCentroidY(i));
    text("G:"+ i, (int)bs.getCentroidX(i), (int)bs.getCentroidY(i));
    int c = colimg.pixels[(int)bs.getCentroidX(i) + width*((int)bs.getCentroidY(i))]; // getting blob color from testLayer
    if (c != lastCol) {
      lastCol = c;
      int r = (c & 0x00ff0000) >> 16; // red channel (0-255)
      int g = (c & 0x0000ff00) >> 8;  // green channel (0-255)
      int b = (c & 0x000000ff);       // blue channel (0-255)
      int maxChannelValue = max(r, g, b);
      if (maxChannelValue == r) {
        R = (int)random(0, 5);
        println("R:" + R);
      }
      if (maxChannelValue == g) {
        G = (int)random(5, 10);
        println("G:" + G);
      }  
      if (maxChannelValue == b) {
        B = (int)random(10, 16);
        println("B:" + B);
      }
    }
  }
  //--------------------------------------------------
  edges.endDraw();
  image(colimg, 0, 0);
  image(edges, 0, 0);
}
//----------------------------------------------------
void  getEdgeCoordinates() { 
  X = new int[MAXEDGES];
  Y = new int[MAXEDGES];
  int i = 0;
  for (int y = 0; y < img.height; y++) {
    for (int x = 0; x < img.width; x++) {
      if (bs.isEdge(x, y)  && 
        bs.getBlobWeightLabel(bs.getLabel(x, y)) >=  minimumWeight) {
        X[i] = x;
        Y[i] = y;
        i++;
      }
    }
  }
}

