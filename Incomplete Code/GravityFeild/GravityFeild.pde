import peasy.*;
PeasyCam cam;

Dot[][][] D;
int N = 4, w=10, x, y, z;
void setup() {
  size(500, 500, P3D);
  cam = new PeasyCam(this, 1000);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(2000); 
  x = 0;
  y = 0;
  z = 0; 
  D = new Dot[2*N][2*N][2*N];
  for (int i=-N; i<N; i++) {
    for (int j=-N; j<N; j++) { 
      for (int k=-N; k<N; k++) {
        D[i+N][j+N][k+N] = new Dot(i*w, j*w, k*w, 1);
      }
    }
  }
}
void draw() {
  background(0);
  axis();
  if (keyleft)x--;
  if (keyright)x++;
  if (keyup)y--;
  if (keydown)y++;
  if (keyzpos)z++;
  if (keyzneg)z--;

  println(x + " " + y + " " + z );
  Dot M = new Dot(x, y, z, 5);
  pushMatrix();
  translate(M.loc.x, M.loc.y, M.loc.z);
  noStroke();
  fill(#FAC903);
  sphere(100);
  popMatrix(); 

  for (int i=-N; i<N; i++) {
    for (int j=-N; j<N; j++) { 
      for (int k=-N; k<N; k++) {
        D[i+N][j+N][k+N].show();
        float dis = PVector.dist(D[i+N][j+N][k+N].loc, M.loc);
        if (dis<120) {
          D[i+N][j+N][k+N].applyForce(M);
        }
        D[i+N][j+N][k+N].update();
      }
    }
  }
}


void axis() {
  stroke(255, 0, 0);
  line(-300, 0, 300, 0);
  stroke(0, 255, 0);
  line(0, -300, 0, 300);
  stroke(0, 0, 255);
  line(0, 0, -300, 0, 0, 300);
}

