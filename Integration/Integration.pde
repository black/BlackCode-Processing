float N, M, a, b;
void setup() {
  size(600, 600);
  a = 10;
  b = 400;
  N=100;
  noCursor();
}

void draw() {
  background(-1);  
  float dx = (b-a)/N;   
  /*------ control number of steps -----*/
  if (keyCode==UP && keyPressed==true)N++;
  if (keyCode==DOWN && keyPressed==true)N--;
  /*------ get function integration & differentiation -----*/
  float inte = integration(a, b, N); // call integration on function
  float diff = diffrentiation(mouseX, dx); // call diffrentiation on function
  /*------ draw f(x) -----*/
  strokeWeight(1);
  stroke(0);
  noFill();
  beginShape();
  for (int i=0; i<N; i++) {
    point(i*dx+a, f(i*dx+a) );
    vertex(i*dx+a, f(i*dx+a) );
  }
  endShape();
  drawTangent(mouseX, f(mouseX), diffrentiation(mouseX, dx)); // draw tangent curve
  fill(#EA1A1A);
  text("Integration: "+inte + "\nDiffrentiation: " +diff+ "\n at "  + mouseX, mouseX, mouseY);
}

float f(float x) {
  return   x+10000/x; // put your equation here
}

float integration(float lowerlimit, float upperlimit, float steps) {
  float y=0;
  float dx = (upperlimit-lowerlimit)/steps;
  noStroke();
  fill(0, 50);
  for (int i=0; i<steps; i++) {
    y = y+f(i*dx+lowerlimit)*dx;
    if (i%2==0)fill(0, 50);
    else fill(0, 150);
    rect(i*dx+a-dx/2, 0, dx, f(i*dx+lowerlimit));
  }
  return y;
}

float diffrentiation(float x, float h) {
  float theta = atan2((f(x+h)-f(x)), h);
  return theta;
}

void drawTangent(float x, float y, float t) {
  pushMatrix();
  translate(x, y);
  rotate(t);
  strokeWeight(1);
  stroke(0);
  line(-1050, 0, 1050, 0);
  strokeWeight(4);
  stroke(#FFE200);
  line(-50, 0, 50, 0);
  popMatrix();
}

