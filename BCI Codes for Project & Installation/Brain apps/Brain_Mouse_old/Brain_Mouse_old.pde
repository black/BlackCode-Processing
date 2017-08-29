import com.sun.awt.AWTUtilities;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import javax.swing.JFrame;
import java.awt.Robot;
import java.awt.GraphicsDevice.*; 
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.net.*;

/*----------------------------------------*/
import neurosky.*;
import org.json.*;
/*-------------------------------*/
ThinkGearSocket neuroSocket;
Robot robot;
/*-------------------------------*/
int blinkSt = 0;
int blink = 0;
/*-------------------------------*/
float dx, dy;
float x, y;
int x1=0, y1=0, count=0;
PFont f;
int win_w = 1280, win_h =780;
DashedCircle DC;


void setup()
{
  size(win_w, win_h); 
  smooth();
  frame.removeNotify();
  frame.setUndecorated(true);
  AWTUtilities.setWindowOpacity(frame, 0.5f);
  /*------------------------------*/
  frame.setResizable(true);  
  /*------------------------------*/
  // frame.addNotify();
  f = createFont("CALIBRI", 20, true);
  DC = new DashedCircle(35, 6, 4);
  x = win_w/2;
  y = win_h/2; 
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    e.printStackTrace();
    println("Is ThinkGear running??");
  }
  try { 
    robot = new Robot();
  } 
  catch (AWTException e) {
    e.printStackTrace();
  }
  /*-----------------*/
  super.init();
}

void draw()
{
  background(255, 0);
  if (frameCount==5) {
    frame.resize(win_w, win_h);
    frame.setLocation(0, 0);
  }

  textFont(f);
  noStroke();
  //   text("Blink: " + blinkSt,30,38);
  /*---------------------------------*/
   
   if (blink>0 && count==0) 
   {
   blink = 0;
   count=1;
   delay(100);
   } 
   else if(blink>0 && count==1)
   {
   blink = 0;
   count=0;
   delay(100);
   }
   
  /*----------------------------------*/
  if (count ==0) {
    text("ROTATE", 30, 20);
  } else
  {   
    text("MOVE", 30, 20);
  }
  /*---------------------------------*/
  if (keyPressed  && count ==0) {
    count=1;
    delay(100);
  } else if (keyPressed && count ==1)
  {
    count=0;
    delay(100);
  }
  /*------------SCAN LINE CODE----------------------*/
  if (count==0) {
    fill(0);
    textSize(15);
    text("x1: "+x1+ " y1:"+ y1, 30, 54); 
    text("x: "+x+ " y:"+ y, 30, 74); 
    /*---------------Under graphics-------------*/
    noStroke();
    fill(255, 3, 41);
    ellipse(x, y, 50, 50);
    fill(0);
    ellipse(x, y, 10, 10);
    stroke(2);
    /*---------------Rotating line code is below-------------*/
    if ((y1==0) || (y1==win_h)) {
      if ((x1 <win_w) && (y1 ==0)) { 
        line(x, y, x1, y1); 
        x1= x1+5;
      }
      if ((x1>0) && (y1==win_h)) {
        line(x, y, x1, y1); 
        x1= x1-5;
      }
    }
    if ((x1==0) || (x1==win_w)) {
      if ((x1==0) && (y1>0)) {
        line(x, y, x1, y1);
        y1= y1-5;
      }
      if ((x1==win_w) && (y1<win_h)) {
        line(x, y, x1, y1);
        y1= y1+5;
      }
    }
    /*---------------Dotted Circle useless-------------*/
    dx = x1 - x;
    dy = y1 - y;
    float angle = atan2(dy, dx);
    pushMatrix();
    translate(x, y);
    rotate(angle);
    DC.display();
    popMatrix();
    /*---------------Dotted Circle useless-------------*/
  }
  /*------------SCAN LINE CODE----------------------*/
  /*-----------CIRCLE MOVING----------------------*/
  fill(250, 213, 50);
  if (count ==1 ) {
    /*---------------Under graphics-------------*/
    noStroke();
    fill(255, 3, 41);
    ellipse(x, y, 50, 50);
    stroke(2);
    line(x, y, x1, y1); 
    pushMatrix();
    translate(x, y);
    DC.display();
    popMatrix();
    noStroke();
    fill(3, 71, 255);
    ellipse(x, y, 10, 10);   
    /*---------------Under graphics-------------*/
    if (x < x1 ) {
      if (y1 ==0 || y1==win_h  ) {
        x = x + 0.5;
      } else {
        x =x + 1;
      }
      y =(dy/dx)*(x-x1)+y1;
    }
    if (x > x1  ) {
      if (y1 ==0  || y1==win_h) {
        x = x - 0.5;
      } else {
        x =x - 1;
      }
      y =(dy/dx)*(x-x1)+y1;
    }
    /* -------------Under graphics-------------*/
    fill(0);
    textSize(15);
    text("x1: "+x1+ " y1:"+ y1, 30, 54); 
    text("x: "+x+ " y:"+ y, 30, 74); 
    robot.mouseMove(frame.getLocation().x+(int)x, frame.getLocation().y+(int)y);
  }
  /*-----------CIRCLE MOVING----------------------*/
}

void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  if(blinkSt>50){
  blink = 1;
  }
  println(count);
}


void stop() {
  neuroSocket.stop();
  super.stop();
}


