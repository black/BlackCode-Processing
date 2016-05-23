limb L1, L2;
float g1, g2;
void setup() {
  size(500, 500);
  background(255, 224, 150);
    L1 = new limb(0, 35, 10, g1);
  L2 = new limb(15, 35, 10, g2);
}

void draw() {
  background(255);
  noCursor();
  translate(mouseX, mouseY);
  human();
}

void human()
{
  fill(0);
  rect(0, -17, 15, 15);
  beginShape();
  vertex(0, 0);
  vertex(10, 0);
  vertex(15, 5);
  vertex(15, 35);
  vertex(0, 35);
  vertex(0, 0);
  endShape();
  L1.drawlimbs();
  L1.update();
  L2.drawlimbs();
  L2.update();
}

class limb {
  int armLength, ua, la;
  float uad, lad;
  int sx, sy, ex, ey, hx, hy;
  int mx=5, my=100, i=1, j=1; 
  float f;
  limb(int _joint1, int _joint2, int _armLength, float _f)
  {
    sx =  _joint1;
    sy =  _joint2;
    armLength = _armLength;
    f = _f;
  }
  void drawlimbs() {
    stroke(255, 0, 0, 100);
    stroke(0);
    line(sx, sy, ex, ey);
    line(ex, ey, hx, hy);
  }
  void update() {
    int r =armLength/4;
    float ang = TWO_PI *f;
    float mx = cos(ang) * r + sx ;
    float my = sin(ang) * r + sy+ 2*armLength;
    ellipse(mx, my, 4, 4);
    float dx = mx - sx;
    float dy = my - sy;
    float distance = sqrt(dx*dx+dy*dy);
    int a = armLength;
    int b = armLength;
    float c = min(distance, a + b);
    float B = acos((b*b-a*a-c*c)/(-2*a*c));
    float C = acos((c*c-a*a-b*b)/(-2*a*b));
    float D = atan2(dy, dx);
    float E = D + B + PI + C;
    ex = int((cos(E) * a)) + sx;
    ey = int((sin(E) * a)) + sy;
    hx = int((cos(D+B) * b)) + ex;
    hy = int((sin(D+B) * b)) + ey;
  }
}


void keyPressed()
{  L1 = new limb(0, 35, 10, g2);
  L2 = new limb(15, 35, 10, g1);
  if (key == CODED)
  {
    if (keyCode == RIGHT)
    {
      g1 = g1+ 0.01;
      g2 = g2-0.01;
    }
    if (keyCode == LEFT)
    {
      g1 = g1-0.01;
      g2 = g2+ 0.01;
    }
  }
}

