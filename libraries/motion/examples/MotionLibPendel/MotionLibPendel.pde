import seltar.motion.*;
Motion Pendel;

void setup()
{
  size(200,200);  
  Pendel = new Motion(width/2,height/2);
  Pendel.setDamping(0.99);
  smooth();
}

void draw()
{
  if(!mousePressed) background(0);  
  stroke(255);
  fill(255);
  Pendel.springTo(mouseX,mouseY);
  Pendel.move();
  
  line(mouseX,mouseY,Pendel.getX(),Pendel.getY());
  ellipse(Pendel.getX(),Pendel.getY(),5+Pendel.getDistance(),5+Pendel.getDistance());
}
