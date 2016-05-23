import blobscanner.*; 
import java.util.*;
import java.util.Map.*;
//import java.util.Map.Entry;
PGraphics testLayer;
PImage img, colimg;
Detector bs;
PVector myinterest_key;
import ddf.minim.*;
Minim minim;
AudioPlayer[] player = new AudioPlayer[16];
LinkedHashMap<PVector, Integer> blobValues = new LinkedHashMap< PVector, Integer>();
void setup() {
  size(600, 400, P3D);
  testLayer = createGraphics(width, height);
  bs = new Detector(this, 0, 0, 400, 400, 255);
  testLayer.beginDraw();
  testLayer.endDraw();
  //----------------------------------------------------
  minim = new Minim(this); // initialaizing minim object
  for (int i=0; i<player.length;i++) {
    // String str = "Incredibox.com_"+i  +".mp3"; // file name & path
    String str = i+".mp3";
    player[i] = minim.loadFile(str); // load file in audio player array loadFile ( "FILE NAME");
  }  
  //----------------------------------------------------
}
color lastcol = color(255);
color c;
int R, G, B;
void keyPressed() {
  c = (color) random(#000000);
}
void draw() {
  background(255);
  //--------- RANDOM COLOR GENERATOR ---------
  fill(c);
  noStroke();
  ellipse(mouseX, mouseY, 10, 10);
  testLayer.beginDraw();
  if (mousePressed) {
    testLayer.stroke(c);
    testLayer.strokeWeight(10);
    testLayer.line(mouseX, mouseY, pmouseX, pmouseY);
  }
  testLayer.endDraw();
  //---------------------------------
  img = testLayer.get(0, 0, width, height);
  colimg = img.get();
  img.filter(THRESHOLD, 0.2);
  img.loadPixels();
  bs.imageFindBlobs(img);
  bs.loadBlobsFeatures();
  bs.weightBlobs(false); 
  //---------------------------------------
  for (int i=0;i<bs.getBlobsNumber();i++) {
    PVector[] pixloc = bs.getBlobPixelsLocation(i);
    PVector p = new PVector(pixloc[0].x, pixloc[0].y);
    blobValues.put(p, i);
  }
  if (!mousePressed)
    if (blobValues.size()>0) {
      final Set<Entry<PVector, Integer>> mapValues = blobValues.entrySet();
      final int maplength = mapValues.size();
      final Entry<PVector, Integer>[] test = new Entry[maplength];
      mapValues.toArray(test);
      myinterest_key = test[maplength-1].getKey();
      //-------------------------------------------------------------------------
      int blobcol = colimg.pixels[(int)myinterest_key.x + width*(int)myinterest_key.y]; // getting blob color from testLayer
      if (blobcol!=lastcol) {
        int r = (blobcol & 0x00ff0000) >> 16; // red channel (0-255)
        int g = (blobcol & 0x0000ff00) >> 8;  // green channel (0-255)
        int b = (blobcol & 0x000000ff);       // blue channel (0-255)
        int maxChannelValue = max(r, g, b);  
        if (maxChannelValue == r) {
          R = (int)random(0, 5);
          println("R:" + R);
          player[R].loop();
        }
        if (maxChannelValue == g) {  
          G = (int)random(5, 10);
          println("G:" + G);
          player[G].loop();
        }  
        if (maxChannelValue == b) {
          B = (int)random(10, 15);
          println("B:" + B);
          player[B].loop();
        }
        lastcol =blobcol;
      }
    }
  image(colimg, 0, 0);
}

