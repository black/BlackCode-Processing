//The code has been written by Abhinav Kumar,IDC, IIT Bombay
//It is available for free use and can be modify for non commercial use.
//Please refer the owner's name if you are using the code
//Improve and share the code with others
//blykmonky@gmail.com
//http://www.behance.net/greyfrog
// "It is the era of OPEN SOURCE "

import processing.video.*; 
Capture video; 
PGraphics topLayer; 
color trackColor; 
int xprev=width/2,yprev=height/2;
void setup() { 
  size(640, 480); 
  smooth();
  video = new Capture(this, width, height);
  video.start(); 
  trackColor = color(255, 0, 0);
  topLayer = createGraphics(width, height, g.getClass().getName());
} 
void draw() { 
  if (video.available()) { 
    video.read();
  } 
  video.loadPixels(); 
  image(video, 0, 0); 
  float worldRecord = 500; 
  int closestX = 0; 
  int closestY = 0; 
  for (int x = 0; x < video.width; x ++ ) { 
    for (int y = 0; y < video.height; y ++ ) { 
      int loc = x + y*video.width; 
      color currentColor = video.pixels[loc]; 
      float r1 = red(currentColor); 
      float g1 = green(currentColor); 
      float b1 = blue(currentColor); 
      float r2 = red(trackColor); 
      float g2 = green(trackColor); 
      float b2 = blue(trackColor); 
      float d = dist(r1, g1, b1, r2, g2, b2); 
      if (d < worldRecord) { 
        worldRecord = d; 
        closestX = x; 
        closestY = y;
      }
    }
  }
  topLayer.beginDraw();
  topLayer.stroke(trackColor);
  if (worldRecord < 10) { 
   topLayer.strokeWeight(3);  
    topLayer.line(xprev,yprev,closestX, closestY);
    xprev = closestX;
    yprev = closestY;
  }
  topLayer.endDraw();
  image(topLayer,0,0);
} 
void mousePressed() { 
  int loc = mouseX + mouseY*video.width; 
  trackColor = video.pixels[loc];
}
