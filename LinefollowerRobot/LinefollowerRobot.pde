PGraphics pg;
boolean left, right;
void setup() {
  size(400, 400);
  pg = createGraphics(width, height);
  pg.beginDraw();
  pg.clear();
  pg.endDraw();
  x = width/2;
  y = height/2;
  ang = 0;
  dx = 0;
} 
int t=0, ang, R = 10, r=2;
float x, y, dx, dy;
void draw() {
  background(-1);
  if (mousePressed) {  
    pg.beginDraw();
    pg.stroke(0);
    pg.strokeWeight(4);
    pg.line(mouseX, mouseY, pmouseX, pmouseY);
    pg.endDraw();
  }
  tint(255, 50);
  image(pg, 0, 0);
  if (left) { 
    ang+=10;
    dx = sin(radians(ang));
    dy = cos(radians(ang));
    left = false;
  }   
  if (right) {
    ang-=10;
    dx =  sin(radians(ang));
    dy =  cos(radians(ang));
    right = false;
  }
  robot(x, y, ang);
  x+=dx/2;
  y+=dy/2; 
  if (x<0) x = width; 
  if (x>width) x = 0;
  if (y<0) y = height; 
  if (y>height) y = 0;
}
void robot(float x, float y, float ang) {
  noFill();
  strokeWeight(3);
  stroke(#FC0808);
  ellipse(x, y, 2*R, 2*R);
  noStroke();
  fill(0, 150);
  ellipse(x, y, 2*r, 2*r);
  for (int i=0; i<2; i++) {
    float xx = x + R*sin(i*3*PI/2+radians(ang)+PI/2); 
    float yy = y + R*cos(i*3*PI/2+radians(ang)+PI/2);
    ellipse(xx, yy, 2*r, 2*r);
    color c = pg.get((int)xx, (int)yy);
    if (i==0 && c!=0 ) left = true;
    if (i==1 && c!=0 ) right = true;
  }
}

