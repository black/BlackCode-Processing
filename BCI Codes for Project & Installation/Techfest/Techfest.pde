int x=0;
void setup()
{
  size(600,500);
  ellipseMode(RADIUS);
}

void draw()
{
  background(124);
  fill(255, 255, 255, 255 - x);
  stroke(225, 225, 225);
  strokeWeight(3.0);
  strokeJoin(ROUND);
  line(pmouseX, pmouseY, mouseX, mouseY);
  if(mousePressed==false){
         x =x+10;
    }else{
         x=0;
    }
    
   if (dist(myFruit.x, myFruit.y, mouseX,mouseY) < myFruit.r)
      {
        myFruit.apple = loadImage("appleCut.png");
        pointCounter++;
      } 
}
