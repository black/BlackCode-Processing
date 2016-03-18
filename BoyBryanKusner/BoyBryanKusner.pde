import java.lang.Math;  // Math.sinh(x)
import peasy.*;
PeasyCam cam;
void setup() {
  size(300, 300, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(1000);
}
void draw() {
  background(-1);
  beginShape();
  for (int i=0; i<360; i+=10) {
    for (int j=0; j<360; j+=10) {
      for (float b=0; b<1; b+=0.025f) {
        PVector  P = parametricPlot(i, j, b);
        vertex(P.x, P.y, P.z);
      }
    }
  }
  endShape(CLOSE);
}
float b; 
/* u controls how far the tip goes 
 v controls the girth 
 b varies from 0 to 1 */
PVector parametricPlot(float u, float v, float b) {
  float r = 1-sq(b);
  float w = sqrt(r);
  float den = b*(sq((float)(w*Math.cosh(b*u))) + sq((float)(b*Math.sinh(w*v))));
  double bx = -u+ (2*r*Math.cosh(b*u)*Math.sinh(w*v))/den;
  double by = (2*w*Math.cosh(b*u)*(-(w*cos(v)*cos(w*v)) - sin(v)*sin(w*v)))/den;
  double bz = (2*w*Math.cosh(b*u)*(-(w*sin(v)*cos(w*v)) + cos(v)*sin(w*v)))/den;
  return new PVector((float)bx, (float)by, (float)bz);
}