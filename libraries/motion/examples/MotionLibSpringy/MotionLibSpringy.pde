// Click and drag to draw, press any key to clear 
import seltar.motion.*;
Motion mf, m;

void setup()
{
  size(200,200);
  mf = new Motion(width/2,height/2);
  mf.setConstant(100);
  m = new Motion(width/2,height/2);
  m.setConstant(1000);
  background(0);
  smooth();
}

void draw()
{
  if(keyPressed) background(0);
  strokeWeight(mf.getDistance());
  mf.followTo(mouseX,mouseY);
  mf.move();
  
  m.springTo(mf.getX(),mf.getY());
  m.move();
  
  if(mousePressed){
    stroke(255,20);
    line(mf.getX(), mf.getY(), m.getX(),m.getY());
    fill(255);
    stroke(255);
    ellipse(m.getX(),m.getY(),m.getDistance(),m.getDistance());
  }
}
