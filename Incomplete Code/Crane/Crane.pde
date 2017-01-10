Arm a, b;
void setup() {
  size(640, 360);
  a = new Arm(0, 0);
  b = new Arm(a.xe, a.ye);
}
int t=0, k=0;
void draw() {
  background(-1);   
  a.show();
  b.show(); 
  b.xe = mouseX;  
  b.ye = mouseY;
  float ang = atan((mouseY-b.y)/(mouseX-b.x));
  println(ang);
  b.angV = ang;
  b.x = b.xe-b.armlength*cos(ang);
  b.y = b.ye-b.armlength*sin(ang);
  a.xe = b.x; 
  a.ye = b.y;
  float ang2 = atan((a.ye )/(a.xe ));
  a.angV = ang2;
}

