ArrayList<PVector> poop = new ArrayList();
int  z = 2000, d=50, t =0;
float m=0, k =0;
boolean pressed;
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
    P.z+=k;
    if (P.z>50) {
      P.z=-z;
    }
  }

  float ang = map(m, 0, 10, 0, PI/2);
  k=20*sin(ang);
  if (t>0)t-=5*sin(ang);
  else t=180;

  if (pressed) { 
    if (m<10)m+=0.05f;
    else m = 10;
  } else {
    if (m>0)m-=0.1f;
    else m =0;
  }
} 


void keyPressed() {
  pressed = true;
}

void keyReleased() {
  pressed = false;
}

