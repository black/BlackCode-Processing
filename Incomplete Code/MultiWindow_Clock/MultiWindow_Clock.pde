import com.sun.awt.AWTUtilities;
import java.awt.GraphicsDevice.*; // PC only
import java.awt.Shape;
import java.awt.AWTException;
import java.awt.geom.*;
//----------------------
import java.awt.Frame; 
import java.awt.Shape;
//----------------------

PFrame[] f = new PFrame[3]; 
secondApplet[] s =new secondApplet[3];
color[] c = new color[3];

PFrame centerFrame;
secondApplet centerApplet;

int R = 200;
int cx, cy;
float secondsRadius;
float minutesRadius;
float hoursRadius;
float clockDiameter;

void setup() {
  size(100, 100);
  int radius = min(displayWidth, displayHeight) /4;
  secondsRadius = radius ;
  minutesRadius = radius ;
  hoursRadius = radius ;
  clockDiameter = radius;

  cx = displayWidth / 2;
  cy = displayHeight / 2;


  centerFrame = new PFrame(cx, cy);
  Shape centerShape = null;
  centerShape = new Ellipse2D.Float(0, 0, 10, 10);
  AWTUtilities.setWindowShape(centerFrame, centerShape);

  for (int i=0; i<f.length; i++) {
    int x = (int)(R*cos(radians(i*360/3)));
    int y = (int)(R*sin(radians(i*360/3)));
    f[i] = new PFrame(x, y);
    Shape shape = null;
    shape = new Ellipse2D.Float(0, 0, 100, 100);
    AWTUtilities.setWindowShape(f[i], shape);
    c[i] = (color)random(#000000);
  }
}

void draw() { 
  background(0);



  int time=0;
  for (int i=0; i<3; i++) {
    if (i==0) time = second();
    else if (i==1) time = minute();
    else if (i==2) time = hour();
    s[i].background(c[i]);
    s[i].fill(-1); 
    s[i].textAlign(CENTER);
    s[i].text(time, 50, 50);
    s[i].redraw();
  }

  float s = map(second(), 0, 60, 0, TWO_PI) - HALF_PI;
  float m = map(minute() + norm(second(), 0, 60), 0, 60, 0, TWO_PI) - HALF_PI; 
  float h = map(hour() + norm(minute(), 0, 60), 0, 24, 0, TWO_PI * 2) - HALF_PI;

  println(s + " " + m + " " + h);

  f[0].setLocation(cx + (int)(cos(s)*secondsRadius), (int)(cy+sin(s)*secondsRadius));  
  f[1].setLocation(cx + (int)(cx + cos(m) * minutesRadius), (int)(cy + sin(m) * minutesRadius));
  f[2].setLocation(cx + (int)(cos(h) * hoursRadius), (int)(cy + sin(h) * hoursRadius));
}

public class PFrame extends Frame {
  public PFrame(int x, int y) {
    setBounds(x, y, 100, 100);
    for (int i=0; i<3; i++) {
      s[i] = new secondApplet();
      add(s[i]);
      s[i].init(); 
      removeNotify(); 
      setUndecorated(true);
      addNotify();
      show();
    }
  }
}

public class secondApplet extends PApplet {
  public void setup() {
    size(100, 100);
    noLoop();
  }

  public void draw() {
  }
} 

