import peasy.*;
PeasyCam cam; 

void setup() {
  size(300, 300, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(50);
  cam.setMaximumDistance(5000);
}

int R =100, k=0;
void draw() {
  background(0);

  noFill();
  strokeWeight(4);
  for (int i=0; i<180; i+=10) {
    beginShape();
    for (int j=0; j<360; j++) { 
      float x = R*sin(radians(j))*sin(radians(i));
      float y = R*cos(radians(j))*sin(radians(i));
      float z = R*cos(radians(i)); 
      if (i==k)stroke(0, 255, 0);
      else stroke(-1, 50);
      vertex(x, y, z);
    }
    endShape(CLOSE);
  }  
  if (k<180)k++;
  else k=0;
  frameRate(10);
}

