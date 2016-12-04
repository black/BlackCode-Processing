// New technique to rotate line
PVector U, V;
int df=2;
boolean move;
int r=15;
void setup() {
  size(500, 300);
  U = new PVector(width/2, height/2);
  V = new PVector(0, 0);
  move = false;
}

void draw() {
  background(-1);
  ellipse(U.x, U.y, 2*r, 2*r);
  line(U.x, U.y, V.x, V.y);
  if (!move) {
    lineScanner();
    t =0;
  } else {
    moveCircle(); 
    if (t < 1.0) {
      t += 0.0025f;
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
  //  float dir = (U.x<V.x)?1:-1;
  //  U.x = U.x+dir;
  //  U.y = equationOfline(U.x);
  U.x = U.x+t*(V.x-U.x);
  U.y = U.y+t*(V.y-U.y);
}

float equationOfline(float x) {
  float dy = V.y-U.y;
  float dx = V.x-U.x; 
  float y =(dy/dx)*(x-V.x)+V.y;
  return y;
}


void mousePressed() {
  move=!move;
}

