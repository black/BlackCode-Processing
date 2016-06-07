int cols, rows;
int w=5, h=5;
float[][] zval;

float fly = 0;
void setup() {
  size(400, 400, P3D); 
  cols = width/w;
  rows = height/h;
  zval  = new float[rows][cols];
}

void draw() {
  background(0);
  lights();

  fly -=0.05;
  float yoff = fly;
  for (int j=0; j<rows; j++) { 
    float xoff = 0;
    for (int i=0; i<cols; i++) {
      zval[i][j]= map( noise(xoff, yoff), 0, 1, -20, 20);
      xoff +=0.1;
    }
    yoff+=0.1;
  } 

  fill(-1);
  noStroke();
  translate(width/2, height/2);
  rotateX(PI/3);
  scale(1.6);
  translate(-width/2, -height/2);
  for (int j=0; j<rows-1; j++) {
    beginShape(TRIANGLE_STRIP);
    for (int i=0; i<cols; i++) { 
      vertex(i*w, j*h, zval[i][j]);
      vertex(i*w, (j+1)*h, zval[i][j+1]);
    }
    endShape();
  }
}

