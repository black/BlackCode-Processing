
ArrayList poop;
boolean flag=false;
int distance=50;
void setup()
{
  size(displayWidth, displayHeight);
  smooth();
  poop = new ArrayList();
  for (int i=0;i<2000;i++)
  {
    Particle P = new Particle();
    poop.add(P);
  }
}


void draw()
{
  background(255);
  for (int i=0;i<poop.size();i++)
  {
    Particle Pn = (Particle) poop.get(i);
    Pn.display();
    Pn.update();
    if(dist(mouseX,mouseY,Pn.x,Pn.y)<50){
      Pn.x = mouseX;
      Pn.y = mouseY;
    }
  }
}

void keyPressed()
{
  flag=!flag;
}

