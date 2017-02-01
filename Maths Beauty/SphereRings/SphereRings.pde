import peasy.*; 
PeasyCam cam; 
void setup() {
  size(300, 300, P3D); 
  cam = new PeasyCam(this, 100);
  //frameRate(5);
}
int N = 360;
int r =150;
int t=-90, k=-90;
void draw() {
  background(0);
  stroke(-1, 200);
  noFill();
  for (int  i=-90; i<t; i+=10) {
    float thetaB = radians(i*360/N);  
    if (i>k) {
      beginShape();
      for (int  j=0; j<360; j+=1) {
        float thetaA = radians(j*360/N);
        float x = r*cos(thetaA)*cos(thetaB);
        float y = r*sin(thetaA)*cos(thetaB);
        float z = r*sin(thetaB);
        vertex(x, y, z);
      }
      endShape();
    }
  }
  if (t<90)t+=5;
  else {
    if (k<90)k+=5;
    else {
      t =-90;
      k = -90;
    }
  }
}

