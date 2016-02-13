int R=50;
float t=0, x, y; 
void setup() {
  size(300, 300);
  x = 0;
  y = R;
  background(-1);
} 
void draw() {
  fill(#FF3E3E, 10);
  rect(0, 0, width, height);
  float y = R-R*sin(radians(t));
  pushMatrix();
  translate(width>>1, height>>1); 
  stroke(-1,150);
  line(-width/2, R, width/2, R);
  translate(x, y);
  rotate(PI/2+radians(t));  
  line(0, R, 0, -R);
  //polygon( R);
  popMatrix();
  if (t<180)t+=2;
  else t= 0;
}
//int N=4;
//void polygon(float R) {
//  float Rd = (R/2)*(1/sin(TWO_PI/(N*2)));
//  beginShape();
//  for (int i=0; i<N; i++) {
//    float ang = i*360/N;
//    float xx = Rd*sin(radians(ang));
//    float yy = Rd*cos(radians(ang));
//    vertex(xx, yy);
//  }
//  endShape(CLOSE);
//}