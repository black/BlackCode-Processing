float rot=0;
color c, d;
int flag=0, x, y;
void setup()
{
  size(600, 400);
  smooth();
}
void draw()
{
  background(-1);
  stroke(0);
  line(0, height/2, width, height/2);
  line(width/2, 0, width/2, height);
  noCursor();
  //-----------------------------
  if (mousePressed) {
    x = mouseX;
    y = mouseY;
    flag=1;
  }

  if (flag==1)
  { 
    noStroke();
    fill(0);
    ellipse(x, y, 4, 4);
    y = y-4;
    if (y< 0)flag=0;
  }

  //-----------------------------
  pointer();
  if (mouseX < width/2 ) 
  {
    rot =  rot -0.1;
    c = color(#FFAB03);
    d = color(#3E3E3E);
  }
  else {
    rot =  rot +0.1;
    c = color(#00D7FF);
    d = color(#FF007C);
  }
}

void pointer()
{

  pushMatrix();
  translate(mouseX, mouseY);
  noStroke();
  rotate(rot);
  fill(c);
  arc(0, 0, 50, 50, PI/2, PI);
  arc(0, 0, 50, 50, -PI/2, 0);
  fill(255);
  arc(0, 0, 40, 40, PI/2, PI);
  arc(0, 0, 40, 40, -PI/2, 0);
  popMatrix();
  fill(d);
  ellipse(mouseX, mouseY, 35, 35);
  fill(255);
  textSize(20);
  text("+", mouseX-6, mouseY+7);
}


