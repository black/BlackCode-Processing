import neurosky.*;
import org.json.*;
import processing.video.*;

Capture video;
ThinkGearSocket neuroSocket;
int attention=10;
int meditation=10;
PFont font;
void setup() {
  size(640, 480);
  video = new Capture(this, 640, 480);
  video.start();  
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try {
    neuroSocket.start();
  } 
  catch (Exception e) {
    //println("Is ThinkGear running??");
  }
}

void draw() { 
  background(0);
  image(video, 0, 0); 
  showArc("FOCUS", attention, width/2, height/2, 200, #FF0000); 
  showArc("MEDITATION", meditation, width/2, height/2, 250, #0057FF);
}

void showArc(String lable, float variable, int x, int y, int r, int col) {
  float val = map(variable, 0, 100, 0, TWO_PI);
  stroke(col);
  strokeWeight(10);
  noFill();
  arc(x, y, r, r, 0, val);
  float xx = x+r*cos(val)/2;
  float yy = y+r*sin(val)/2;
  fill(col);
  textSize(24);
  text(int(variable) + lable, xx, yy);
}

//void addLabble(float val, int x, int y, int col) {
//  float xx = r*
//  fill(col);
//  text(val, xx, yy);
//}

void captureEvent(Capture c) {
  c.read();
}

void poorSignalEvent(int sig) {
  println("SignalEvent "+sig);
}

public void attentionEvent(int attentionLevel) {
  println("Attention Level: " + attentionLevel);
  attention = attentionLevel;
}


void meditationEvent(int meditationLevel) {
  println("Meditation Level: " + meditationLevel);
  meditation = meditationLevel;
}

void blinkEvent(int blinkStrength) {

  println("blinkStrength: " + blinkStrength);
}

public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) {
  println("delta Level: " + delta);
  println("theta Level: " + theta);
  println("low_alpha Level: " + low_alpha);
  println("high_alpha Level: " + high_alpha);
  println("low_beta Level: " + low_beta);
  println("high_beta Level: " + high_beta);
  println("low_gamma Level: " + low_gamma);
  println("mid_gamma Level: " + mid_gamma);
}

void rawEvent(int[] raw) {
  //println("rawEvent Level: " + raw);
}  

void stop() {
  neuroSocket.stop();
  super.stop();
}

