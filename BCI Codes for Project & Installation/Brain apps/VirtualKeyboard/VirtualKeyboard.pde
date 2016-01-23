import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import com.sun.awt.AWTUtilities;
import java.awt.GraphicsDevice.*; 
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*; 

Robot robot;
PFont font; 
Boxgrid b;
int x, y, row, col;
int i=0, j=20, px, py, r=5;

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
  font = createFont("HelveticaNeueLight", 48);
  textFont(font);
  px = i;
  py = j;
}
/*------------------------------------*/
void draw() {

  background(0);
  fill(255);
  noStroke();
  rect(width-21, 1, 19, 19, r, r, r, r);
  fill(150);
  textSize(18);
  text("X", width-17, 17);
  fill(255);
  text("Click Here & Drag to Move ", 10, 17);
  /*-----------------------------------------------------*/
  if  (mousePressed &&  width-21< mouseX && mouseY<20 )
  { 
    exit();
  }
  b = new Boxgrid(px, py); 
  b.display();
  /*-----------------------------------------------------*/
  for ( y= 20; y< height; y += 50) {
    for ( x= 0; x< width; x += 50) {
      if (mouseX >= x && mouseX <= x+50 &&
        mouseY >= y && mouseY <= y+50) {
        fill(0, 50);
        noStroke();
        if ( x > 350  && y==120)
        { 
          if (x==400) {
            rect(x, y, 100, 50, r, r, r, r);
          }else if (x==450){
             rect(x-50, y, 100, 50, r, r, r, r);
          }
        }
        else if ( (x== 150 || x ==200 ) && y==70)
        { 
          if (x==150) {
            rect(x, y, 100, 50, r, r, r, r);
          }else if (x==200){
             rect(x-50, y, 100, 50, r, r, r, r);
          }
        }
        else {
          rect(x, y, 50, 50, r, r, r, r);
        }
      }
    }
  }
}

int mX;
int mY;

void mousePressed()
{
  mX = mouseX;
  mY = mouseY;
  if (mouseY > 20) {
    for ( y= 20; y< height-20; y += 50) {
      for ( x= 0; x< width; x += 50) {
        if (mouseX >= x && mouseX <= x+50 &&
          mouseY >= y && mouseY <= y+50) {
          row = (y-20)/50;
          col = x/50;
          println(row  + " " + col);
          robot.keyPress(letter[row][col]);
          robot.keyRelease(letter[row][col]);
        }
      }
    }
  }
}
void mouseDragged()
{
  java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
  frame.setLocation(p.x - mX, p.y - mY);
}

