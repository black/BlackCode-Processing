import peasy.*;
PeasyCam cam;
ArrayList noises;
int l=10, d=1200, i=0;
circle c, c1;
void setup()
{
  size(400, 400, P3D);
  //smooth();
  noises = new ArrayList();
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(50);
  cam.setMaximumDistance(500);
}
void mousePressed()
{ 
  for (int i=0; i<3 ; i++)
  {
    circle c = new circle((int)random(200), (int)random(200), (int)random(50, 100), random(PI/2), random(PI/2), random(PI/2)/*, color(random(255), random(255), random(255))*/);
    noises.add(c);
  }
}


void draw()
{
  background(0, 50);
  pushMatrix();
  beginShape(QUADS);
  translate(110, 110, 1000);
  rotateZ(PI/2);
  normal(0, 0, 1);
  fill(50, 50, 200);
  vertex(-d, +d);
  vertex(+d, +d);
  fill(200, 50, 50);
  vertex(+d, -d);
  vertex(-d, -d);
  endShape();  
  popMatrix();
  pushMatrix();
  rotate(PI/i);
  for (int k =0; k < noises.size(); k++)
  {
    circle mb = (circle) noises.get(k);
    mb.run();
  }
  axis();
  popMatrix();
  i+=10;
}

class circle {
  int x, y, z, i, f=100;
  color Colox=color(255);
  int r = (int)random(5, 21), l=0;
  float ax, ay, az;
  circle(int _x, int _y, int _z, float _ax, float _ay, float _az/*, color _color*/)
  {
    x = _x;
    y = _y;
    z = _z;
    az = _az;
    ax = _ax;
    ay = _ay;
    // Colox = _color;
  }
  void run()
  {
    rotateZ(az);
    rotateY(ay);
    rotateX(ax);
    pushStyle();
    noStroke();
    fill(Colox, f);
    pushMatrix();
    translate(0, 0, z);
    ellipse(0, 0, r+l, r+l); 
    popMatrix();
    stroke(255, 90);
    line(0, 0, z, 0, 0, 0);
    popStyle();
    f=f-1;
    l=l+1;
    if (f==0) 
    {
      f = 100;
    }
    if (l>50)
    {
      l=0;
    }
  }
}

void axis()
{
  pushStyle();
  stroke(255, 0, 0);//RED
  line(0, 0, 0, 0, 0, l);// Z - Axis
  stroke(0, 255, 0);//GREEN
  line(0, 0, 0, 0, l, 0);// Y - Axis
  stroke(0, 0, 255); //BLUE
  line(0, 0, 0, l, 0, 0);// X - Axis
  popStyle();
}

