class Cube {
  int id;
  int size;
  float x, y;
  float ax, ay;
  color c;

  public Cube(int id, int size, float x, float y, color c) {
    this.id = id;
    this.size = size;
    this.x = x;
    this.y = y;
    this.c = c;
  }

  void rotate(float x, float y) {
    ax += x;
    ay += y;
  }

  void draw() {
    fill(c);
    pushMatrix();
    translate(x, y);
    rotateX(ay);
    rotateY(-ax);
    box(size);
    popMatrix();
  }
}