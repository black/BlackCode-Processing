import seltar.motion.*;
Motion m;
float Angle = random(TWO_PI), Strokeweight = 1;

void setup()
{
  size(200,200);  
  m = new Motion(width/2,height/2);
  smooth();
  background(0);  
}

void draw()
{
  if(mousePressed) background(0);  
  Angle += radians(random(-5,5));
  Strokeweight += random(-0.5,0.5);
  m.moveDir(Angle,random(1,6));
  m.wrap(0,0,width,height);
  m.move();
  stroke(255);
  strokeWeight(abs(Strokeweight));
  line(m.getPX(),m.getPY(),m.getX(),m.getY());
}
