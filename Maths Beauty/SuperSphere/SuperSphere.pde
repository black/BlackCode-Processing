import peasy.*; 
PeasyCam cam; 
void setup() {
  size(300, 300, P3D); 
  colorMode(HSB);
  cam = new PeasyCam(this, 100);
}
int N = 360;
int r =100;
int t=-90;
void draw() {
  background(0);
  strokeWeight(2);

  noFill();

  float m = map(mouseX, 0, width, -90, 90);
  stroke(-1, 200);
  for (int  i=0; i<360; i+=36) {
    float thetaA = radians(i*360/N);
    beginShape();
    for (int  j=-90; j<=90; j++) {
      float thetaB = radians(j*360/N);
      float x = r*cos(thetaA+radians(j))*cos(thetaB);
      float y = r*sin(thetaA+radians(j))*cos(thetaB); 
      float z = r*sin(thetaB);
      vertex(x, y, z);
    }
    endShape();
  }
  if (t<90)t++;
  else t=-90;
}

