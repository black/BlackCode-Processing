PImage b;
int x=0;
float x1=600,y1=600;
int counter=0;
float xfreez,  yfreez;
String path;
void setup()
{
  size(600,500);
  frameRate(30);
}

void draw()
{
  background(124);
  x1 = x1 - (random(0,100))/100;
  y1 = y1 - (random(0,100))/100;
  println(x1 + " "+ y1 );
  path =(counter+".png");
  println(path);  
  b = loadImage(path);
  image(b,x1,y1);
  /*----------------------------*/
  fill(255, 255, 255, 255 - x);
  stroke(225, 225, 225);
  strokeWeight(7);
  strokeJoin(ROUND);
  line(mouseX, mouseY, pmouseX, pmouseY);
  /*----------------------------*/
  if(mousePressed==false){
         x =x+5;
    }else{
         x=0;
    }
  /*---------fruit------------------
 if (dist( x1,y1,mouseX,mouseY) < 50)
      {
        counter =1;
  
      } 
 -------------fly---------------*/
 if (dist( x1,y1,mouseX,mouseY) < 50)
      {
        counter =1;
        xfreez = x1;
        yfreez = y1;
  
      } 
}
