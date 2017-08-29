import com.sun.awt.AWTUtilities;
import java.awt.*;
import javax.swing.JFrame;
/*----------------------------------------*/
JFrame topFrame = null;
PGraphics pg3D;
int framePosX = 0;
int framePosY = 0;
float opacitatTopFrame = 0.9f;
/*----------------------------------------*/


int t=0;
float dx, dy, l=0;
float x, y;
int x1=0, y1=0, count=0;
PFont f;
int win_w = 1280, win_h =780;
DashedCircle DC;
/*----------------------------------------*/

public void init() {
  frame.removeNotify();
  frame.setUndecorated(true);
  AWTUtilities.setWindowOpaque(frame, false);
  AWTUtilities.setWindowOpacity(frame, 0.0f);  
  frame.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));                
  frame.setVisible(false);
  frame.setLayout( null );
  frame.addNotify();

  GraphicsConfiguration translucencyCapableGC;
  translucencyCapableGC = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

  topFrame = new JFrame(translucencyCapableGC);
  topFrame.setUndecorated(true);
  AWTUtilities.setWindowOpaque(topFrame, false);
  AWTUtilities.setWindowOpacity(topFrame, 1.0f);    
  topFrame.setAlwaysOnTop(true);
  topFrame.setLocationRelativeTo(null);
  topFrame.setLocation(framePosX, framePosY);
  topFrame.setSize(win_w, win_h);
  topFrame.setBackground(new Color(0, 0, 0, 0));
  topFrame.setVisible(true);
  topFrame.setTitle( frame == null? "":frame.getTitle() );
  topFrame.setIconImage( frame.getIconImage() );
  topFrame.setLayout( null ); 
  topFrame.addNotify();
  super.init();
  g.format = ARGB;
  g.setPrimary(false);
}
/*----------------------------------------*/

void setup()
{
  size(win_w, win_h);
  pg3D = createGraphics(win_w, win_h, JAVA2D);
  pg3D.colorMode(RGB, 255, 255, 255, 255); 
  //pg3D.smooth();
  /*------------------------------*/
  f =  createFont("CALIBRI", 20, true);
  DC = new DashedCircle(35, 6, 4);
  x = win_w/2;
  y = win_h/2;
}

void draw()
{   

  background(0, 0);
  pg3D.beginDraw();
  pg3D.background(0, 0, 0, 0);

  if (mousePressed) {
    println("Pressed");
    t = t+1;
    delay(100);
  }
  if (t==1 && count ==0) {
    count=1;
    t=0;
  }
  else if (t==1 && count ==1)
  {
    count=0;
    t=0;
  }
  /*------------SCAN LINE CODE----------------------*/
  if (count==0) {
    /*---------------Under graphics-------------*/
    pg3D.noStroke();
    pg3D.fill(255, 3, 41);
    pg3D.ellipse(x, y, 50, 50);
    pg3D.fill(0);
    pg3D.ellipse(x, y, 10, 10);
    pg3D.stroke(#0074FF);
    pg3D.strokeWeight(3);
    /*---------------Rotating line code is below-------------*/
    if ((y1==0) || (y1==win_h)) {
      if ((x1 <win_w) && (y1 ==0)) { 
        pg3D.line(x, y, x1, y1); 
        x1= x1+5;
      }
      if ((x1>0) && (y1==win_h)) {
        pg3D.line(x, y, x1, y1); 
        x1= x1-5;
      }
    }
    if ((x1==0) || (x1==win_w)) {
      if ((x1==0) && (y1>0)) {
        pg3D.line(x, y, x1, y1);
        y1= y1-5;
      }
      if ((x1==win_w) && (y1<win_h)) {
        pg3D.line(x, y, x1, y1);
        y1= y1+5;
      }
    }
    /*---------------Dotted Circle useless-------------*/
    dx = x1 - x;
    dy = y1 - y;
    float angle =  atan2(dy, dx);
    pg3D.pushMatrix();
    pg3D.stroke(255, 0, 53);
    pg3D.translate(x, y);
    pg3D.rotate(angle);
    pg3D.strokeWeight(1);
    DC.display();
    pg3D.popMatrix();
    /*---------------Dotted Circle useless-------------*/
  }
  /*------------SCAN LINE CODE----------------------*/
  /*-----------CIRCLE MOVING----------------------*/
  if (count ==1 ) {
    /*---------------Under graphics-------------*/
    pg3D.noStroke();
    pg3D.fill(255, 3, 41);
    pg3D.ellipse(x, y, 50, 50);
    pg3D.stroke(0);
    pg3D.strokeWeight(3);
    pg3D.line(x, y, x1, y1); 
    pg3D.pushMatrix();
    pg3D.stroke(255, 0, 53);
    pg3D.translate(x, y);
    pg3D.strokeWeight(1);
    DC.display();
    pg3D.popMatrix();
    pg3D.noStroke();
    pg3D.fill(255);
    pg3D.ellipse(x, y, 10, 10);   
    /*---------------Under graphics-------------*/
    if (x < x1 ) {
      if ( abs(dx) < 200  ) {
        println(abs(dx)+ " Y = 600 " ); 
        x = x + 0.05;
      }
      else
      {
        x= x+1;
      }
      y =(dy/dx)*(x-x1)+y1;
    }
    if (x > x1  ) {
      if ( abs(dx) < 50 ) {
        println(abs(dx)+ " Y = 0 " );  
        x = x - 0.05;
      }
      else
      {
        x= x-1;
      }
      y =(dy/dx)*(x-x1)+y1;
    }
    if (y < y1 ) {
      if ( abs(dy) < 200  ) {
        println(abs(dy) + " X = 600 " ); 
        y = y + 0.05;
      }
      else
      {
        y= y+1;
      }
      x =(dx/dy)*(y-y1)+x1;
    }
    if (y > y1  ) {
      if ( abs(dy) < 200 ) {
        println(abs(dy)+ " X = 0 " ); 
        y = y - 0.05;
      }
      else
      {
        y= y-1;
      }
      x =(dx/dy)*(y-y1)+x1;
    }
    /* -------------Under graphics-------------*/
    pg3D.fill(0);
    pg3D.textSize(15);
    /*-----------CIRCLE MOVING----------------------*/
  }
  pg3D.endDraw();
  image(pg3D, 0, 0);
  frame.setVisible(false);
  topFrame.add(this);
}

