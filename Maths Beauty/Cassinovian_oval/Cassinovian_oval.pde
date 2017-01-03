PVector A, B;

void setup() {
  size(450, 300);
  A = new PVector(-100, 0);
  B = new PVector(100, 0);
  stroke(0);
}

void draw() {
  background(-1);
  translate(width>>1, height>>1);
  strokeWeight(2);
  point(A.x, A.y);
  point(B.x, B.y);
  for (float b=0; b<360; b+=0.5) {
    if (b==k) {
      //println(k + " " + b);
      for (float i=0; i<width; i+=0.5f) {
        for (float j=0; j<height; j+=0.5f) {
          float Aq = dist(A.x, A.y, i, j);
          float Bq = dist(A.x, A.y, i, j);

          println(Aq+ " " + Bq);
          if (Aq*Bq==sq(b)) {
            point(i, j);
          }
        }
      }
    }
  }
  if (up)k+=0.5f;
  if (down)k-=0.5f;
}

float k=0;
boolean up, down;
void keyPressed() {
  if (key==CODED) {
    if (keyCode==UP)up=true;
    if (keyCode==DOWN)down=true;
    ;
  }
}

void keyReleased() {
  up=down=false;
}

