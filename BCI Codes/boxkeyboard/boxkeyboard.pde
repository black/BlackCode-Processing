import com.sun.awt.AWTUtilities;
import java.awt.GraphicsDevice.*; 
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
/*-----------------------*/
import neurosky.*;
import org.json.*;
ThinkGearSocket neuroSocket;
Boxgrid b;
int blinkSt = 0;
int blink = 0;
int k=0, r=5;

/*------------------------------------*/
String[] letter = {
  "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", " ", " ", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", " ", " "
};
/*------------------------------------*/
String input = " "; 
String lockinput=" ";
int t=0, a=30, c=30;
PFont font;
int i=30, j=110, px, py;
/*------------------------------------*/
void setup() 
{
  size (550, 300);
  smooth();
  frame.removeNotify();
  frame.setUndecorated(true);
  frame.setAlwaysOnTop(true);
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try 
  {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    e.printStackTrace();
  }
  fill(0);
  font = createFont("HelveticaNeueLight", 48);
  textFont(font);
  px = i;
  py = j;
}
/*------------------------------------*/
void draw() {
  background(255);
  //translate(-a,-c );
  strokeWeight(3);
  noFill();
  rect(30, 30, 500, 50);
  b = new Boxgrid(px, py); 
  b.display();
  /*----------------------------------------*/
  if (blinkSt>0) 
  {   
    input = input+letter[k];
    lockinput = input;
    fill(0);
    textSize(18);
    text(input, 40, 48, 195, 30);
    textSize(48);
  }
  /*-----------------------------------------------------*/
  fill(25);
  noStroke();
  rect(30, 85, 75, 20, r, r, r, r);
  fill(255);
  textSize(15);
  text("Blink: " + blinkSt, 40, 100);
  blinkSt=0;
  /*-----------------------------------------------------*/
  fill(25);
  rect(110, 85, 75, 20, r, r, r, r);
  fill(255);
  textSize(15);
  text("Close", 120, 100);
  if (mousePressed && 110< mouseX && mouseX< 185 &&  85< mouseY && mouseY<105) {
    exit();
  }
  /*-----------------------------------------------------*/
  fill(0);
  textSize(18);
  text(lockinput, 40, 48, 500, 30);
  textSize(48);
  /*------------------------------------------------------*/
  noFill();
  stroke(0);
  strokeWeight(2);
  println(i);
  if (i==180 && j==160)
  {
    fill(0, 50);
    rect(i, j, 100, 50, r, r, r, r);
  }
  else if (i==430 && j==210)
  {
    fill(0, 50);
    rect(i, j, 100, 50, r, r, r, r);
  }
  else {
    fill(0, 50);
    rect(i, j, 50, 50, r, r, r, r);
  }
  /*---------------------------------------------*/
  if (t ==50) {
    /*------------------rect long---------------------------*/
    if (i==180 && j==160)
    {
      i=230;
    }
    if (i==430 && j==210)
    {
      j=110;
      i=-20;
    }
    /*---------------------rect long------------------------*/
    if (i<450) {
      i=i+50;
    }
    else {
      i=30;
      if (j<200)
      {
        j=j+50;
      }
    }
    /*-------------------------------------*/
    t=0;
  }
  t++;
}
/*------------------------------------*/
void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  // blink = 1;
}
/*------------------------------------*/
void stop() {
  neuroSocket.stop();
  super.stop();
}

class  Boxgrid {
  int bx, by;
  int lx, ly;
  int r=5;
  String[] words = {
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", " ", " ", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", " ", " "
  };
  Boxgrid(int _x, int _y) 
  {
    bx = _x;
    by = _y;
  }
  void display()
  {
    for (int j=0; j<3; j++) {
      for (int i=0; i<10; i++) {
        lx = i*50+bx;
        ly = j*50+by;
        fill(255, 176, 3);
        stroke(0);
        strokeWeight(1);
        if ( i == 3 && j == 1  ) {
          rect(lx, ly, 100, 50, r, r, r, r);
          fill(255);
          textSize(25);
          text("SPACE", lx+7, ly+35);
          i = 4;
        }
        else if ( i == 8 && j == 2  ) {
          rect(lx, ly, 100, 50, r, r, r, r);
          fill(255);
          textSize(25);
          text("DEL", lx+30, ly+35);
          i= 9;
        }
        else
        {
          rect(lx, ly, 50, 50, r, r, r, r);
        }
        fill(255);
        textSize(30);
        if (j==0) {
          text(words[i], lx+15, ly+35);
        }
        if (j==1) {
          text(words[i+10], lx+15, ly+35);
          println(words[i+10] + " " + lx+15);
        }
        if (j==2) {
          text(words[i+20], lx+15, ly+35);
        }
      }
    }
  }
}

