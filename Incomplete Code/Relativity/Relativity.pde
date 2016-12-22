ArrayList<Star> poop = new ArrayList();

Steller S;
float k=0;
void setup() {
  size(700, 400);
  S = new Steller(width/2, height/2, 50, 5);
  for (int i=0; i<100; i++) {
    poop.add(new Star(random(width), random(height)));
  }
}


void  draw() {
  background(-1); 
  for (int i=0; i<poop.size (); i++) {
    Star K = poop.get(i);
    K.show();
    K.update(m);
    if (K.x<0) {
      poop.remove(i);
      poop.add(new Star(width, random(height)));
    }
  } 

  if (right) {
    if (k<5)k+=0.005f; 
    else k=5;
  } else if (left) {
    if (0<k)k-=0.005f; 
    else k=0;
  }

  S.display();
  S.move(k); 
  S.update();
}

boolean left, right; 
void keyPressed() {
  if (key==CODED) {
    if (keyCode==LEFT) left =true; 
    if (keyCode==RIGHT) right =true;
  }
}

void keyReleased() {
  left = right = false;
}

