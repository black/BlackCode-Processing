import blobscanner.*; 
import java.util.*;
import java.util.Map.*;
//import java.util.Map.Entry;
PGraphics canvas;
PImage img, colimg;
Detector bs;
PVector myinterest_key;
import ddf.minim.*;
Minim minim;
AudioPlayer[] player = new AudioPlayer[16];
LinkedHashMap<PVector, Integer> blobValues = new LinkedHashMap< PVector, Integer>();
void setup() {
  size(400, 400);
  canvas = createGraphics(width, height);
  bs = new Detector(this, 0, 0, 400, 400, 255);
  canvas.beginDraw();
  canvas.endDraw();
  //----------------------------------------------------
  minim = new Minim(this); // initialaizing minim object
  for (int i=0; i<player.length;i++) {
    //String str = "Incredibox.com_"+i  +".mp3"; // file name & path
    String str = i+".mp3";
    player[i] = minim.loadFile(str); // load file in audio player array loadFile ( "FILE NAME");
  }    
  canvas.beginDraw();
  //----------------------------------------------------
}
color lastcol = color(255);
color c;
int R, G, B;
int flagd=0, stopflag=0;
void keyPressed() {
  c = (color) random(#000000);
  if (key=='a' || key=='A') c = color(255);
}
void mouseDragged() {
  if (mouseButton == RIGHT) { 
    eraseFunction(); 
    flagd=0;
    stopflag =1;
  }
  if (mouseButton == LEFT) flagd =1;
}

void draw() {
  background(255);
  //--------- RANDOM COLOR GENERATOR ---------
  fill(c);
  noStroke();
  ellipse(mouseX, mouseY, 10, 10);
  if (flagd==1) {
    canvas.stroke(c);
    canvas.strokeWeight(10);
    canvas.line(mouseX, mouseY, pmouseX, pmouseY);
    flagd=0;
  }
  canvas.endDraw();
  //---------------------------------
  img = canvas.get(0, 0, width, height);
  img = canvas.get(0, 0, width, height);
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
  if (blobValues.size()>0 && !mousePressed ) {
    final Set<Entry<PVector, Integer>> mapValues = blobValues.entrySet();
    final int maplength = mapValues.size();
    final Entry<PVector, Integer>[] test = new Entry[maplength];
    mapValues.toArray(test);
    myinterest_key = test[maplength-1].getKey();
    //-------------------------------------------------------------------------
    int blobcol = colimg.pixels[(int)myinterest_key.x + width*(int)myinterest_key.y]; // getting blob color from canvas
    if (blobcol!=lastcol) {
      int r = (blobcol & 0x00ff0000) >> 16; // red channel (0-255)
      int g = (blobcol & 0x0000ff00) >> 8;  // green channel (0-255)
      int b = (blobcol & 0x000000ff);       // blue channel (0-255)
      int maxChannelValue = max(r, g, b);  
      if (maxChannelValue == r) {
        R = (int)random(0, 5); // PROBLEM IS HERE IT GETS EXECUTE MORE THAN ONCE
        println("R:" + R);
        player[R].loop();
      }
      if (maxChannelValue == g) {
        G = (int)random(5, 10);
        println("G:" + G); // PROBLEM IS HERE IT GETS EXECUTE MORE THAN ONCE
        player[G].loop();
      }  
      if (maxChannelValue == b) {
        B = (int)random(10, 16);
        println("B:" + B); // PROBLEM IS HERE IT GETS EXECUTE MORE THAN ONCE
        player[B].loop();
      }
      lastcol =blobcol;
    }
  }
  else if (stopflag ==1) {
    super.stop();
  }
  image(colimg, 0, 0);
} 

void stop()
{
  println("the first line of stop()");
  for (int pl=0; pl<16;pl++)player[pl].close();
  super.stop();
  println("the last line of stop()");
}


void eraseFunction() {
  color c = color(0, 0);
  canvas.beginDraw();
  canvas.loadPixels();
  for (int x=0; x<canvas.width; x++) {
    for (int y=0; y<canvas.height; y++ ) {
      float distance = dist(x, y, mouseX, mouseY);
      if (distance <= 25) {
        int loc = x + y*canvas.width;
        canvas.pixels[loc] = c;
      }
    }
  }
  canvas.updatePixels();
  canvas.endDraw();
}

