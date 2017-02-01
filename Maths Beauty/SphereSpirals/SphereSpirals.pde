import peasy.*; 
PeasyCam cam; 

int N = 360, r =100, t=-90;
float m;

void setup() {
  size(300, 300, P3D); 
  colorMode(HSB);
  cam = new PeasyCam(this, 100);
}
void draw() {
  background(0);
  strokeWeight(2);

  noFill();

  stroke(-1, 200);
  for (int  i=0; i<360; i+=36) {
    float thetaA = radians(i*360/N); 
    strokeWeight(map(i, 0, 360, 1,4));
    beginShape();
    for (int  j=-90; j<=90; j++) { 
      m = map(mouseX, 0, width, -4*j, 2*j);
      float thetaB = radians(j*360/N);
      float x = r*cos(thetaA+radians(j+m))*cos(thetaB);
      float y = r*sin(thetaA+radians(j+m))*cos(thetaB); 
      float z = r*sin(thetaB);
      vertex(x, y, z);
    } 
    endShape();
  }
  if (t<90)t++;
  else t=-90;
}

