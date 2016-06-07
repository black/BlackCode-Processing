class Particle {
  float x, y, r,fx,fy;
  color c;
  int i=1, j=1;
  Particle( )
  {
    x = random(0, width);
    y = random(0, height);
    r = random(1, 4);
    fx = random(-0.9,0.9);
    fy = random(-0.9,0.9);
  }

  void display()
  { 
    //---------------blur/glow ----------
    float h = 3;
    for (float r1 = r*4; r1 > 0; --r1) {
      fill(0, h);
      noStroke();
      ellipse(x, y, r1, r1);
      h=(h+1)%90;
    }
    //---------------blur / glow end
    noStroke();
    int t=0; 
    int ik = (int)random(0,2);
    if(ik==0) t=0;
    if(ik==1) t=255;
    fill(t);
    ellipse(x, y, r, r);
  }

  void update()
  {
    x = x + j*fx;
    y = y + i*fy;
    if (y > height-r) i=-1;
    if (y < 0+r) i=1;
    if (x > width-r) j=-1;
    if (x < 0+r) j=1;
  }
}

