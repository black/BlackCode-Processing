import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import com.sun.awt.AWTUtilities;
import java.awt.GraphicsDevice.*; 
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.net.*;

Robot robot;
/*---------------*/
import neurosky.*;
import org.json.*;  
ThinkGearSocket neuroSocket;
/*--------*/
Boxgrid b;
int blinkSt = 0;
int blink = 0;
int k=0, r=5;

/*------------------------------------*/
int[][] letter = {
  {
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
  } 
  , {
    75, 76, 77, 32, 32, 78, 79, 80, 81, 82
  }
  , { 
    83, 84, 85, 86, 87, 88, 89, 90, 8, 8
  }
};
/*------------------------------------*/
int t=0, a=30, c=30;
PFont font;
int i=0, j=20, px, py;
int press=0, xmove, ymove;
/*------------------------------------*/
void setup() 
{
  size (501, 171);
  smooth();   
  frame.removeNotify();
  frame.setAlwaysOnTop(true);
  frame.setUndecorated(true);
  frame.setFocusableWindowState(false);
  frame.setFocusable(false);
  frame.enableInputMethods(false);
  try {
    robot = new Robot();
  }
  catch(AWTException a) {
    println(a);
    a.printStackTrace();
  }
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try 
  {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    e.printStackTrace();
  }
  font = createFont("HelveticaNeueLight", 48);
  textFont(font);
  px = i;
  py = j;
}

void draw() {
  background(140);
  strokeWeight(3);
  noFill();
  rect(0, 30, 500, 50);
  b = new Boxgrid(px, py); 
  b.display();
  /*-----------------------------------------------------*/
  noStroke();
  fill(25);
  rect(170, 0, width-170, 20, r, r, r, r);
  fill(255);
  textMode(CENTER);
  textSize(15);
  text("CLICK & DRAG TO MOVE WINDOW ", 195, 15);
  /*-----------------------------------------------------*/
  fill(25);
  rect(0, 0, 75, 20, r, r, r, r);
  fill(255);
  textSize(15);
  text("BLINK " + blinkSt, 5, 15);
  /*-----------------------------------------------------*/
  fill(25);
  rect(80, 0, 75, 20, r, r, r, r);
  fill(255);
  textSize(15);
  text("CLOSE", 90, 15);
  if (mousePressed && 80< mouseX && mouseX< 155 &&  0< mouseY && mouseY<20) {
    exit();
  }
  /*--------------------SCANNING START HERE---------------------------------*/
  if (t==50) {   
    if (press==1) {
      ymove = j;
      if (i<=450) {
        if (j==70 && i==150) {
          i = 250;
        } else if (j==120 && i==400) {
          i= 0;
        } else {
          i=i+50;
        }
      } else {
        i=0;
      }
    } else {
      if (j==120)
      {
        j=20;
      } else {
        j=j +50;
      }
    }
    if (press==2) {
      println(" J " + j +  " " + " I " + i  );
      xmove = i;
      int col = (xmove)/50;
      int row = (ymove-20)/50;
      println(" ymove " + ymove +  " " + " xmove " + xmove  );
      println(" ROW " + row +  " " + " COL " + col  );
      robot.keyPress(letter[row][col]);
      robot.keyRelease(letter[row][col]);
      i=0;
      press = 0;
      j=20;
    }

    t=0;
  }

  if (press==0) {
    fill(0, 80);
    rect(i, j, 500, 50, r, r, r, r);
  } else if (press==1)
  {
    if ((j==70 && i==150)|| (j==120 && i==400)) {
      fill(#006AD8, 80);
      rect(i, j, 100, 50, r, r, r, r);
    } else {
      fill(0, 80);
      rect(i, j, 50, 50, r, r, r, r);
    }
  }
  t++;
}

/*---- Dragging the frame ------------ */
int mX;
int mY;

void mousePressed()
{
  mX = mouseX;
  mY = mouseY;
}

void mouseDragged()
{
  java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
  frame.setLocation(p.x - mX, p.y - mY);
}

void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  if (blinkSt > 45) {
    press = press+1;
  }
  if (press>2) {
    press=0;
  }
}

void stop() {
  neuroSocket.stop();
  super.stop();
}

