PVector U, V;
int df=2;
boolean move;
void setup() {
  size(500, 300);
  U = new PVector(width/2, height/3);
  V = new PVector(0, 0);
  move = false;
}

void draw() {
  background(-1);
  ellipse(U.x, U.y, 40, 40);
  line(U.x, U.y, V.x, V.y);
  if (!move) {
    lineScanner();
    t =0;
  } else {
    moveCircle(); 
    if (t < 1.0) {
      t += 0.0005f;
    }
  }
}

void lineScanner() {
  if (V.y==0 && V.x<width) {
    V.x+=df;
  } else if (V.x==width && V.y<height) {
    V.y+=df;
  } else if (V.y==height && 0<V.x) {
    V.x-=df;
  } else if (V.x==0 && 0<V.y) {
    V.y-=df;
  }
}

float t=0.0;
void moveCircle() { 
  U.x = U.x+t*(V.x-U.x);
  U.y = U.y+t*(V.y-U.y);
}
 


void mousePressed() {
  move=!move;
}

