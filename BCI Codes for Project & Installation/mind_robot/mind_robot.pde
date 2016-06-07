import neurosky.*;
import org.json.*;
ThinkGearSocket neuroSocket;
int attention=10;
int meditation=10;
int blinkSt = 0;
int blink = 0;
//-----------------------
float amplitude = (PI/2)/2, pos, xy;
int xmove=0, ymove=0;
float rotbody=0, shift=0;
//-----------------------
boolean keyup = false;
boolean keyright = false;
boolean keyleft = false;
boolean keydown = false;
//-----------------------
void setup()
{
  size(600, 300);
  smooth();
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try {
    neuroSocket.start();
  } 
  catch (Exception e) {
    //println("Is ThinkGear running??");
  }
}

void draw()
{
  background(255);
  fill(0);
  text("Attention: "+attention, 10, 30);
  text("Meditation: "+meditation, 10, 50);
  text("Blink: " + blinkSt, 10, 70);
  translate(xmove, ymove);
  man();

  if (blink>0) 
  {
    fill(255, 255, 0);

    if (blink>15) 
    {
      blink = 0;
    } 
    else 
    {
      blink++;
    }
  }

  if (attention>50) { 
    xy = random(0, 1);
    if (ymove>0) {
      ymove = ymove-1;
    }
  }
  else {
    ymove = ymove+2;
  }


  if (keyleft) { 
    pos = (amplitude*cos(millis()/75.0));
    xy = random(0, 1);
    xmove=xmove-1;
  }
  if (keyright) {
    pos = (amplitude*cos(millis()/70.0));
    xy = random(0, 1);
    xmove=xmove+1;
    rotbody = PI/8;
    shift = 15;
  }
}
//----------------------------------
void man()
{
  //---------HAND 1 -----
  pushMatrix();
  translate(shift, 0);
  noFill();
  stroke(0, 255, 0); 
  strokeWeight(2);
  if (meditation>50) {
    ellipseMode(CENTER);
    ellipse(0, 0, 100, 100);
  }
  noStroke();
  fill(#FC0000);
  limbs(pos, 2, 5, 6, 17);
  pushMatrix();
  rotate(rotbody);
  fill(0);
  rect(0, -17+1*xy, 15, 15);
  beginShape();
  vertex(0, 0);
  vertex(10, 0);
  vertex(15, 5);
  vertex(15, 35);
  vertex(0, 35);
  vertex(0, 0);
  endShape();
  popMatrix();
  //---------HAND 2----
  fill(#FC0000);
  limbs(-pos, 2, 5, 6, 17);
  popMatrix();
  //---------LEG 1----
  limbs(pos, 4, 36, 8, 30);
  if (attention>0) { 
    booster(4, 67);
  }
  //---------LEG 2----
  limbs(-pos, 4, 36, 8, 30);
}
void limbs(float pos, int xt, int yt, int w, int h)
{
  pushMatrix();
  translate(xt, yt);
  rotate(pos);
  rect(0, 0, w, h);
  popMatrix();
}
//-------------------------
void keyPressed() {
  if (key == CODED) {
    if (keyCode == LEFT) keyleft = true; 
    if (keyCode == RIGHT) keyright = true;
  }
}
void keyReleased() {
  if (key == CODED) {
    if (keyCode == LEFT) keyleft = false; 
    if (keyCode == RIGHT) keyright = false;
    pos=0;
    rotbody = 0;  
    shift=0;
  }
}

void booster(int _x, int _y)
{
  pushMatrix();
  translate(_x, _y);
  pushStyle();
  fill(255, 0, 0, random(50, 100));
  noStroke();
  beginShape();
  vertex(0, 0);
  vertex(8, 0);
  vertex(4, 12);
  vertex(0, 0);
  endShape();
  popStyle();
  popMatrix();
}
//-----------------------------

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
  blinkSt = blinkStrength;
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

