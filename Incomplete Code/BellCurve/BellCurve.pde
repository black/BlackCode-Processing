 //The probability of any given value occurring as a function of the mean (often written as μ, the Greek letter mu) and standard deviation (σ, the Greek letter sigma).

FloatList floatValue;
float mu, sd;
int t=0;

void setup() {
  size(600, 300);
  floatValue = new FloatList(); 
  for (int i = 0; i < 20; i++) {
    floatValue.append(random(width));
  }
  mu = mean(); // calculate mean
  sd = SD(mu); // calculate standard deviation
}


void draw() {
  background(0); 



  // floatValue.sort(); // Sort FloatList
  for (int i=0; i<floatValue.size (); i++) {
    float P = floatValue.get(i);
    println(P);
    float z = zScore(P, mu, sd); // calculate z score if you want to represets Standard distribution
    float nd = normalDistribution(P, mu, sd); // calculate Normal distribution   
    float y = height-nd*6000;
    fill(#FC003B);
    ellipse(P, y, 2, 2);
    lable(P, y-40);
  }
  stroke(255, 0, 255);
  line(mu, height, mu, 0); 
  //  stroke(-1, 50);
  //  line(mouseX, 0, mouseX, height);
  //  line(0, mouseY, width, mouseY);
}

float mean() {
  float mu=0;
  for (int i=0; i<floatValue.size (); i++) {
    float P = floatValue.get(i);
    mu=mu+P;
  }
  mu = mu/floatValue.size();
  return mu;
}


float SD(float mu) {
  float sd=0;
  for (int i=0; i<floatValue.size (); i++) {
    float P = floatValue.get(i);
    sd = sd + sq(P-mu);
  }
  sd = sqrt(sd/floatValue.size());
  return sd;
}


float zScore(float num, float mu, float sd) {
  float z = 0;
  z = (num - mu)/sd;
  return z;
}


float normalDistribution(float num, float mu, float sd) {
  float e = 2.71828182845904523536; 
  float ND  =  (1/sqrt(TWO_PI * sd ))*pow (e, (-(sq(num - mu))/(2*sq(sd ))));
  return ND;
}


class Point {
  float x;
  Point(float x) {
    this.x =x;
  }
}


void lable(float x, float y) {
  fill(#FAC903);
  pushMatrix();
  translate(x, y);
  rotate(3*PI/2);
  textAlign(CENTER);
  text(x, 0, 0);
  popMatrix();
}

