ArrayList<PVector> poop = new ArrayList(); 
float m = 0, k=0;
int t=0, N = 180;
boolean pressed;
void setup() {
  size(300, 400);
  for (int i=0; i<50; i++) {
    poop.add(new PVector(random(width), random(height), random(1, 4)));
  }
}

void draw() {
  background(0);  
  noFill();
  stroke(-1, 200);
  strokeWeight(2); 
  for (int j=0; j<4; j++) { 
    beginShape();
    for (int i=0; i<N; i+=5) {
      float r = map(i, 0, 360, 1, 200);
      float x = width/2+r*cos(radians(i+t)+j*TWO_PI/4);
      float y = map(i, 0, 180, 40, height);
      vertex(x, y);
    }
    endShape();
  }


  for (int i=0; i<poop.size (); i++) {
    PVector P = poop.get(i); 
    if (P.y>height) {
      poop.remove(i);
      poop.add(new PVector(random(width), 0, random(1, 4)));
    } else P.y+=k;
  }

  for (int i=0; i<poop.size (); i++) {
    PVector P = poop.get(i);
    strokeWeight(P.z);
    point(P.x, P.y);
  }

  float ang = map(m, 0, 10, 0, PI/2);
  k=10*sin(ang);
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

