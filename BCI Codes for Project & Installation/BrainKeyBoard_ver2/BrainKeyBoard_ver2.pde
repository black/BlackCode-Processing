import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import com.sun.awt.AWTUtilities;
import java.awt.GraphicsDevice.*; 
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

Robot robot;

Button[] button = new Button[28];
void setup() {
  size(600, 300);
  settingUpButtons();
  //--- exception handling -------
  //Let's get a Robot...
  try { 
    robot = new Robot();
  } 
  catch (AWTException e) {
    e.printStackTrace();
    exit();
  }
}

void draw() {
  background(-1);
  drawKeyBoard();
  keyScanning();
}

void settingUpButtons() {
  int w = 50;
  int index=0;
  for (int y=0; y<3; y++) {
    for (int x=0; x<10; x++) {
      int xx = x*w;
      int yy = y*w;
      if ((x==2 && y==1) || (x==8 && y==2)) {
        button[index] = new Button(x*w, y*w, 2*w, w, index);
        if (x==2)x = 3;
        else if (x==8) x=9;
      } else {
        button[index] = new Button(x*w, y*w, w, w, index);
      }
      index++;
      println(index);
    }
  }
}

void drawKeyBoard() {
  for (int i=0; i<button.length; i++) {
    button[i].show();
    // println(i);
  }
}
int tx=0, ty=0, xspeed, yspeed, xblink, yblink, index=0;
void keyScanning() {
  if (yblink==0) { //------------- row selection ---
    fill(#FF0D0D, 50);
    noStroke();
    rect(0, ty*50, 500, 50, 10 );
    if (yspeed==100)ty++;
    if (ty>2)ty=0;
    if (yspeed>100)yspeed=0; // delay for row-y movement
    else  yspeed++;
  } else {
    if (xspeed==100)tx++; //---------------- col selection ------
    
    if (ty==0)ty*10+tx;
    else if (ty==1)ty*10+tx;
    else if (ty==1)ty*10+tx;
    
    button[index].update();
    if (xblink>0) {
      button[index].pressKey(); // robot keyPressed
      /*-- reset everything ---- */
      xblink=0;
      yblink=0;
      xspeed=0;
      yspeed=0;
      ty=0;
      tx=0;
    }

    if (xspeed>100)xspeed=0; // delay for row-col-x  movement
    else xspeed++;
  }
}

void mousePressed() {
  if (mouseButton==RIGHT)yblink++;
  if (mouseButton==LEFT)xblink++;
}

