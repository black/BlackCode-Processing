
int t=1; 
int w, h; 
Ring R_one, R_two;
void setup() {
  size(800, 300);
  w = 100;
  h = height/2;
  int r = 50;
  R_one = new Ring(r);
  R_two = new Ring(r*2);
}

void draw() {
  background(-1);
  translate(100, height>>1);
  strokeWeight(4);
  R_one.show(); 
  R_one.drawLine();
  R_one.updatePoint( ); 
  if (R_one.points.size()<=0)
    R_one.reset();

  R_two.show();
  R_two.drawLine();
  R_two.updatePoint( );
  if (R_two.points.size()<=0)
    R_two.reset();

  if (t<360)t++;
  else { 
    t =1;
  }
}

