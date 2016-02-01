ArrayList<PVector> poop = new ArrayList();
int z = 2000, d=50;
void setup() {
  size(300, 300, P3D); 
  for (int i=0; i<20; i++) {
    poop.add(new PVector(0, 0, -z-i*d*2));
    poop.add(new PVector(width, 0, -z-i*d*2));
    poop.add(new PVector(0, height, -z-i*d*2));
    poop.add(new PVector(width, height, -z-i*d*2));
  }
}

void draw() {
  background(0);
  noStroke();
  lights();
  for (int i=0; i<poop.size (); i++) {
    PVector P = poop.get(i);
    pushMatrix();
    translate(P.x, P.y, P.z);
    box(d, d, d);
    popMatrix();
    P.z+=5;
    if (P.z>50) {
      P.z=-z;
    }
  }
  println(poop.size());
} 

