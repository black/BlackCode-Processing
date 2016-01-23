// Normal (Gaurssian) Distribution
ArrayList<Point> RandomVal = new ArrayList();
void setup() {
  size(400, 200);
  background(0);
}
float tt=0 ;
void draw() {
  // background(0);
  float val = noise(random(width))*width;
  println(val);
  RandomVal.add(new Point(val));
  float mu = calMue();
  float sig = calSigma(mu);
  float ND = normalDistribution(sig, mu, val);
  ND = ND*3000; //magnify the values
  stroke(-1, 50);
  point(val, height-ND);
  tt++;
  if (tt>1000) {
    line(mu, 0, mu, height);
    //noLoop();
  }
}


// Calculate sample mean = mu 
float calMue( ) {
  float mu=0; 
  for (int i=0; i<RandomVal.size (); i++) { 
    Point val = RandomVal.get(i);
    mu=mu+val.x;
  }
  mu = mu/RandomVal.size();
  return mu;
}

// Calculate Standard deviation = sigma
float calSigma(float mu) {
  float sig = 0;
  for (int i=0; i<RandomVal.size (); i++) {
    Point val = RandomVal.get(i);
    sig = sig + sq(val.x-mu);
  }
  sig  = sqrt(sig/RandomVal.size());
  return sig;
}

// Calculate Normal Distribution
float normalDistribution(float sig, float mu, float x) {
  float e = 2.71828182845904523536; 
  float ND  =  (1/sqrt(TWO_PI * sig ))*pow (e, (-(sq(x - mu))/(2*sq(sig ))));
  return ND;
}

class Point {
  float x;
  Point(float x) {
    this.x = x ;
  }
}

