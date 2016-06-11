import processing.video.*;
import neurosky.*;
import org.json.*;
import java.net.*;


ThinkGearSocket neuroSocket;
int blinkSt = 0;
PFont font;
int blink = 0;
Capture cap;

void setup() 
{
  size(640, 480);
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try 
  {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    e.printStackTrace();
  }
  smooth();
  font = loadFont("ArialNarrow-48.vlw");
  textFont(font);
  frameRate(25);
  cap = new Capture(this, width, height);
  noStroke();
}


void draw() 
{
  background(0);
  image(cap, 0, 0);
  if (blink>0) 
  {
    fill(255, 255, 0);
    text("Blink: " + blinkSt, 20, 350);
    if (blink>15) 
    {
      blink = 0;
    } 
    else 
    {
      blink++;
    }
  }
}

void captureEvent(Capture _c) 
{
  _c.read();
}

void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  blink = 1;
}
 
void stop() {
  neuroSocket.stop();
  super.stop();
}
