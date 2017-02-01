import peasy.*; 
PeasyCam cam; 
void setup() {
  size(300, 300, P3D); 
  cam = new PeasyCam(this, 100);
}
int N = 360;
int r =100;
int t=0;
void draw() {
  background(0);
  stroke(-1, 100);
  noFill();
  for (int  i=0; i<360; i+=10) {
    float thetaB = radians(i*360/N);
    beginShape();
    for (int  j=0; j<360; j+=1) {
      float thetaA = radians(j*360/N)+thetaB;
      float x = r*cos(thetaA)*cos(thetaB);
      float y = r*sin(thetaA)*cos(thetaB);
      float z = r*sin(thetaB);
      vertex(x, y, z);
    }
    endShape();
  }
  if (t<360)t+=10;
  else t = 0;
}

