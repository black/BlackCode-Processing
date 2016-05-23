import peasy.*;
PeasyCam cam;

Dot[][][] D;
int N = 5, w =100, ang = 45, R =400; 
void setup() {
  size(500, 500, P3D);
  cam = new PeasyCam(this, 1000);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(2000);
  D = new Dot[2*N][2*N][2*N];
  int m=0;
  for (int i=-N; i<N; i++) {
    for (int j=-N; j<N; j++) { 
      for (int k=-N; k<N; k++) {
        D[i+N][j+N][k+N] = new Dot(i, j, k);
        m++;
      }
    }
  }
}

void draw() {
  background(0);
  stroke(-1);  

  float xx =  R*sin(radians(ang));
  float yy =  R*cos(radians(ang));

  pushMatrix();
  translate(xx, yy);
  sphere(100);
  popMatrix();
  noFill();
  stroke(-1, 100);
  ellipse(0, 0, 2*R, 2*R);

  Dot T = new Dot(xx, yy, 0); 
  ang++;

  for (int i=-N; i<N; i++) {
    for (int j=-N; j<N; j++) { 
      for (int k=-N; k<N; k++) {
        stroke(-1);
        strokeWeight(3);
        point(D[i+N][j+N][k+N].loc.x*w, D[i+N][j+N][k+N].loc.y*w, D[i+N][j+N][k+N].loc.z*w);

        D[i+N][j+N][k+N].applyForce(T);
        D[i+N][j+N][k+N].update();


        PVector[] P = D[i+N][j+N][k+N].check(i, j, k); 
        for (int m=0; m<P.length; m++) {
          stroke(-1, 10);  
          strokeWeight(1);
          line(D[i+N][j+N][k+N].loc.x*w, D[i+N][j+N][k+N].loc.y*w, D[i+N][j+N][k+N].loc.z*w, P[m].x*w, P[m].y*w, P[m].z*w);
        }
      }
    }
  }
}

