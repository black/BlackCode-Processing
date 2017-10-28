import processing.video.*;
import neurosky.*;
import org.json.*;
import java.net.*;

ThinkGearSocket neuroSocket;
int blinkSt = 0;

String address=""; 
int k=0, m=0, num;
int[] set = new int[4];
boolean pressed;
void setup() {
  size(600, 400);
  resetCounter();
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try 
  {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    e.printStackTrace();
  }
}

void draw() {
  background(-1);  
  scanner(k, set); 

  if (m>100) {
    if (k<4) {
      if (pressed) {
        address = address+'1';
        pressed = false;
        set[k]=1;
      } else {
        address = address+'0';
        set[k]=0;
      }
      k++;
    } else {
      num = unbinary(address);
      k=0;
      address = "";
      pressed = false;
      resetCounter();
    }
    m=0;
  } else {
    m++;
  }

  menuCall(num); 
  text("Blinked: " + blinkSt, 10, 10);
}


void captureEvent(Capture _c) 
{
  _c.read();
}

void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  if (blinkSt>50)pressed = true;
  println("blinked " + blinkSt);
}

void stop() {
  neuroSocket.stop();
  super.stop();
}

//void mousePressed() {
//  pressed = true;
//}

