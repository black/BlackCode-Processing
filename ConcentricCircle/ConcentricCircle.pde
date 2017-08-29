int r = 20, N = 10;
int[] col = new int[600*400];
float m=0;
color myCol;
void setup() {
  size(600, 400);
  for (int i=0; i<width; i++) {
    for (int j=0; j<height; j++) {
      int index = i+j*width;
      int r = int(255-random(-85, 85));
      int g = int(255-random(-85, 85));
      int b = int(255-random(-85, 85)); 
      col[index] = color(r, g, b);
    }
  }
}

void draw() {
  background(250);
  pushMatrix();
  translate(width>>1, height>>1);
  noStroke();
  if (mousePressed) {
    myCol = (color) random(#000000);
    mousePressed = false;
  }
  for (int k=14; k>=0; k--) {
    int num = N+k;
    pushMatrix();
    rotate(radians((k/2)*((k%2==0)?m*1:m*-1))); 
    fill(myCol, 255-pow(k, 2.5));
    stroke(myCol, 255-pow(k, 2.5));
    beginShape();
    for (int i=0; i<num; i++) {
      float ang = radians(i*360/num);
      float x = k*r*cos(ang);
      float y = k*r*sin(ang);
      vertex(x, y);
    }
    endShape(CLOSE);
    popMatrix();
  } 
  popMatrix();
  m+=0.5;
  for (int i=0; i<width; i++) {
    for (int j=0; j<height; j++) {
      int index = i+j*width;
      noStroke();
      fill(col[index], 40);
      rect(i, j, 1, 1);
    }
  }
  if (keyPressed) {
    saveFrame(millis()/1000 + ".png");
    keyPressed = false;
  }
}

