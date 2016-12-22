void setup() {
  size(600, 300);
  background(-1);
}
float t=0;
void draw() {
  background(-1); 
  if (t<9)t+=0.005f;
  else t=0;  
  float L = FunL(t);
  point(t+50, -L);  
  float O = FunO(t);
  point(t+50, -O);
  float V = FunV(t);
  point(t+50, -V);
  float E = FunE(t);
  point(E+250, -t);
}

float FunL(float x) {
  float y = 1/x;
  return y;
}

float FunO(float x) {
  float y = (x<0)?sqrt(9-sq(x)):-1*sqrt(9-sq(x)); 
  return y;
}

float FunV(float x) { 
  float y = abs(x);
  return y;
}

float FunE(float x) {
  float y = -1*3*abs(sin(x));
  return y;
}

