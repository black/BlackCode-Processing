class Bubble {
  float x, y, r, br, a, b;
  color c;
  Bubble(float x, float y) {
    this.x = x;
    this.y = y;
    a  = b = random(5, 15);
    r = random(15, 20);
    br = r+5;
  }
  void show() {    
    noStroke();
    fill(255, 224, 64);
    ellipse(x, y, 2*r, 2*r);
  } 
  void blur() {
    noStroke();  
    fill(0, 60);
    ellipse(x+a, y+b, 2*br, 2*br);
  }
}

