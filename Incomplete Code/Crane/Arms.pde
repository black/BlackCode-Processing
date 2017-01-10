class Arm {
  float x, y, xe, ye;
  float angV;
  int armlength;
  color c;
  Arm(float x, float y) {  
    this.x = x;
    this.y = y;
    angV = 0;
    armlength = 100;
    xe = x + armlength*cos(angV);    
    ye = y + armlength*sin(angV);
    c = (color)random(#000000);
  }
  void show() {
    noStroke();
    fill(c);
    pushMatrix();
    translate(x, y);
    rotate(angV);
    rect(0, 0, armlength, 10);
    popMatrix();
  }
  void update() {
  }
}

