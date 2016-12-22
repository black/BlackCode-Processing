import peasy.*;
PeasyCam cam;

ArrayList<Dot> poop1= new ArrayList();
ArrayList<Dot> poop2 = new ArrayList();
int R = 450, m;
void setup() {
  size(600, 600, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(1500);
}
int k=0; 
void draw() {
  background(0); 
  lights();
  float x = sin(radians(k));
  float y = cos(radians(k));
  float z1 = 0;
  float z2 = 0;
  pushMatrix();
  translate(0, 0, -250);
  poop1.add(new Dot(x, y, z1, k));
  for (int i=0; i<poop1.size (); i++) {
    Dot D = poop1.get(i);
    D.show(1);
    D.update(); 
    if (D.r>R)poop1.remove(i);
  }
  popMatrix();
  pushMatrix();
  translate(0, 0, 250);
  poop2.add(new Dot(x, y, z2, k));
  for (int i=0; i<poop2.size (); i++) {
    Dot D = poop2.get(i);
    D.show(-1);
    D.update(); 
    if (D.r>R)poop2.remove(i);
  }
  popMatrix();
  k+=30;
}

class Dot {
  float x, y, z, r, m, vz, az, rad;
  float easing = 0.001f;
  Dot(float x, float y, float z, float ang) {
    this.x = x;
    this.y = y;
    this.z = z; 
    vz = 5;
    rad = radians(ang);
    r =2;
  }
  void update() {
    r+=4;
    x = r*cos(rad);
    y = r*sin(rad);
    vz=vz-0.05f; 
    z=z+vz;
  }
  void show(int k) {
    fill(-1); 
    noStroke();
    pushMatrix();
    translate(x, y, z*k);
    sphere(10);
    popMatrix();
  }
}