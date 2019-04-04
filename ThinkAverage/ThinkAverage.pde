import java.net.*; 
import neurosky.*;
import org.json.*;
ThinkGearSocket neuroSocket;
int attention=10;
int meditation=10;
int[] curr = new int[512]; 
int[] prev = new int[512]; 
int[] finalAry = new int[512]; 
int ymax = 0;
int ymin = 0;
boolean pressed=false;
void setup() {
  size(512, 512);
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    println("Is ThinkGear running??");
  }    
}
int t=0;
void draw() {
  background(255);
  //if(t%10==0)pressed=!pressed;
  fill((pressed)?0:255);
  ellipse(width/2,200,100,100);
  stroke(255,0,0);
  line(0,ymax,width,ymax);
  stroke(0,255,0);
  line(0,ymin,width,ymin);
  println(ymin,ymax);
  translate(0,height/2);
  noFill();
  stroke(0);
  beginShape();
  for(int i=0;i<finalAry.length;i++){
    ymax = (max(finalAry)>ymax && ymax<height)?max(finalAry):ymax; 
    ymin = (min(finalAry)<ymin && ymin>0)?min(finalAry):ymin; 
    int fval = (int)map(finalAry[i],ymin,ymax,0,height/2);
    vertex(i,fval);
  }
  endShape();
  arrayCopy(curr, prev);
  t++;
}

void poorSignalEvent(int sig) {
 // println("SignalEvent "+sig);
}

public void attentionEvent(int attentionLevel) {
  //println("Attention Level: " + attentionLevel);
  attention = attentionLevel;
}


void meditationEvent(int meditationLevel) {
 // println("Meditation Level: " + meditationLevel);
  meditation = meditationLevel;
}

void blinkEvent(int blinkStrength) {

 // println("blinkStrength: " + blinkStrength);
}

public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) {
  //println("delta Level: " + delta);
  //println("theta Level: " + theta);
  //println("low_alpha Level: " + low_alpha);
  //println("high_alpha Level: " + high_alpha);
  //println("low_beta Level: " + low_beta);
  //println("high_beta Level: " + high_beta);
  //println("low_gamma Level: " + low_gamma);
  //println("mid_gamma Level: " + mid_gamma);
}

void rawEvent(int[] raw) {
  pressed=!pressed;
   arrayCopy(raw, curr);
   arrayCopy(addArray(curr,prev),finalAry);
}   

int[] addArray(int[] array1,int[] array2){
  int[] temp = new int[512];
  for(int i=0;i<512;i++){
    temp[i] = array1[i]+array2[i];
  }
  return temp;
}

void stop() {
  neuroSocket.stop();
  super.stop();
}
