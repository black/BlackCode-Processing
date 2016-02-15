import seltar.motion.*;
Direction d;

void setup()
{
  size(200,200);  
  smooth();
  d = new Direction(random(width),random(height));
}

void draw()
{
  background(0);
  d.display();
}

class Direction
{
  Motion m;
  Direction(float X, float Y)
  {
    m = new Motion(X,Y);  
  }
  
  void move()
  {
    m.followTo(mouseX,mouseY);
    m.wrap(0,0,width,height);
    m.move();  
  }
  
  void display()
  {
    move();
    stroke(128);
    noFill();
    strokeWeight(2);
    ellipse(m.getX(),m.getY(),11,11);  
    strokeWeight(1);
    drawVector(20);
  }
  
  // Thanks to shiffman
  void drawVector(float scayl) {
    stroke(200);
    float arrowsize = 7;
    
    pushMatrix();
      translate(m.getX(),m.getY());
      rotate(m.v.getDirection());
      float len = m.v.getVelocity()*scayl;
    
      // Draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
      line(0,0,len,0);
      line(len,0,len-arrowsize,+arrowsize/2);
      line(len,0,len-arrowsize,-arrowsize/2);
    popMatrix();
  }  
}
