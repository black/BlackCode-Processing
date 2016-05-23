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
AudioPlayer[] player = new AudioPlayer[15];
LinkedHashMap<PVector, Integer> blobValues = new LinkedHashMap< PVector, Integer>();
void setup() {
  size(400, 400);
  testLayer = createGraphics(width, height);
  bs = new Detector(this, 0, 0, 400, 400, 255);
  testLayer.beginDraw();
  testLayer.endDraw();
  //----------------------------------------------------
  minim = new Minim(this); // initialaizing minim object
  for (int i=0; i<player.length;i++) {
    String str = "Incredibox.com_"+i  +".mp3"; // file name & path
    //String str = i+".mp3";
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
  if (blobValues.size()>0) {
    final Set<Entry<PVector, Integer>> mapValues = blobValues.entrySet();
    final int maplength = mapValues.size();
    final Entry<PVector, Integer>[] test = new Entry[maplength];
    mapValues.toArray(test);
    // System.out.print("First Key:"+test[0].getKey());
    // System.out.println(" First Value:"+test[0].getValue());
    // System.out.print("Last Key:"+test[maplength-1].getKey());
    // System.out.println(" Last Value:"+test[maplength-1].getValue());
    myinterest_key = test[maplength-1].getKey();
    //-------------------------------------------------------------------------
    if (mousePressed) {
      int blobcol = colimg.pixels[(int)myinterest_key.x + width*(int)myinterest_key.y]; // getting blob color from testLayer
      if (blobcol!=lastcol) {
        int r = (blobcol & 0x00ff0000) >> 16; // red channel (0-255)
        int g = (blobcol & 0x0000ff00) >> 8;  // green channel (0-255)
        int b = (blobcol & 0x000000ff);       // blue channel (0-255)
        int maxChannelValue = max(r, g, b);  
        if (maxChannelValue == r) {
          if (0<r && r< 51)  R = 0;
          if (50<r && r< 101)  R = 1;
          if (100<r && r< 151)  R = 2;
          if (150<r && r< 201)  R = 3;
          if (200<r && r< 256)  R = 4;

          R = (int)random(0, 5); // effect
          println("R:" + R);
          player[R].loop();
          player[G].setVolume(0.5);
          //        if (!player[R].isPlaying()) {
          //          player[R].rewind();  
          //          player[R].play();
          //        }
        }
        if (maxChannelValue == g) {
          if (0<g && g< 51)  G = 5;
          if (50<g && g< 101)  G = 6;
          if (100<g && g< 151)  G = 7;
          if (150<g && g< 201)  G = 8;
          if (200<g && g< 256)  G = 9;

          println("G:" + G);
          player[G].loop();
          player[G].setVolume(0.5);
          //        if (!player[G].isPlaying()) { 
          //          player[G].rewind();  
          //          player[G].play();
          //        }
        }  
        if (maxChannelValue == b) {
          if (0<b && b< 51)  B = 10;
          if (50<b && b< 101)  B = 11;
          if (100<b && b< 151)  B = 12;
          if (150<b && b< 201)  B = 13;
          if (200<b && b< 256)  B = 14;
          // melodies     
          println("B:" + B);
          player[B].loop();
           player[B].setVolume(0.5);
          //        if (!player[B].isPlaying()) {
          //          player[B].rewind();  
          //          player[B].play();
          //        }
        }
        lastcol =blobcol;
      }
    }
  }
  image(colimg, 0, 0);
}

