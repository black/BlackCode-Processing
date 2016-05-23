class Particle {
  float x, y, z;
  color c;
  float gravity = 0.98;
  float vy;
  Particle(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
    c  = (color) random(#000000);
    vy = 20;
  }
  void show() {
    stroke(-1, 40);
    strokeWeight(4);
    point(x, y, z);
  }
  void update() { 
    y -= vy;
  }
} 

