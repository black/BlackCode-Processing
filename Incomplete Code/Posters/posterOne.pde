class DotPoster {
  int x, y, alpha, k; 
  int rmax, rmin; 
  float r;
  DotPoster(int x, int y, int k) {
    this.x = x;
    this.y = y; 
    rmax = 4;
    rmin = 1;
    r = random(rmin, rmax); 
    this.k = k;
  }
  void show() {
    noStroke();
    fill(#E55C28, alpha);
    ellipse(x, y, 2*r, 2*r);
  }
  void update() {
    r = rmax*sin(radians(k)); 
    alpha = (int)map(r, rmin, rmax, 0, 255);
    k+=2;
  }
}

