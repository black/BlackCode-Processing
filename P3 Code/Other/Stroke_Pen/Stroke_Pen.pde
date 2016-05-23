int cp = 5;
int r = 80;
int g = 10;
int b = 220;
float rinc = 1;
float ginc = 5;
float binc = 1.5;
float easing = 0.2;
float diamEase = 0.1;
float diam;
float distance;
float x, y, d;

PVector easeLoc;
PVector mLoc;
PVector pLocLoc;
PVector bPoints[];
color pencil = color(180, 20, 100);


void setup() {
  size(800, 800);
  background(-1);
  smooth();
  bPoints = new PVector[cp];  
  for (int i = 0; i < cp; i ++) {
    bPoints[i] = new PVector(0, 0);
  }
}

void draw() {
  cursor(CROSS);
  //background(-1);
  mLoc = new PVector(mouseX, mouseY);
  easeMovements(mLoc.x, mLoc.y);
  distance = easeLoc.dist(bPoints[1]);
  arrayIterate(easeLoc);
  mainDraw();

  fill(-1, 1);
  noStroke();
  rect(0, 0, width, height);
}


void mainDraw() {

  pencil = color(r, g, b);
  stroke(pencil, 255);
  diam = easeDiam(map(distance, 0, 50, 20, 4));
  //diam = map(distance, 0, 60, 30, 4);

  if (diam < 4) {
    diam = 4;
  }
  strokeWeight(diam);  //brush size
  r += rinc;
  g += ginc;
  b += binc;

  if (r >= 255 || r <= 40) {
    rinc *= -1;
  }
  if (g >= 40 || g <= 10) {
    ginc *= -1;
  }
  if (b >= 180 || b <= 90) {
    binc *= -1;
  }

  lineShape();
}

void lineShape() {
  beginShape();  
  curveVertex(bPoints[0].x, bPoints[0].y); //1st point is also control point
  curveVertex(bPoints[0].x, bPoints[0].y); 
  curveVertex(bPoints[1].x, bPoints[1].y);
  curveVertex(bPoints[2].x, bPoints[2].y);
  curveVertex(bPoints[3].x, bPoints[3].y);
  curveVertex(bPoints[3].x, bPoints[3].y); // the last point is also 2nd control point
  endShape();
}

void arrayIterate(PVector _mouseLoc) {
  bPoints[4].set(bPoints[3]);
  bPoints[3].set(bPoints[2]);
  bPoints[2].set(bPoints[1]);
  bPoints[1].set(bPoints[0]);
  bPoints[0].set(_mouseLoc);
}      

void easeMovements(float targetX, float targetY) {
  //pLoc.set(easeLoc);
  float dx = targetX - x;
  if (abs(dx) > .02) {
    x += dx * easing;
  }
  float dy = targetY - y;
  if (abs(dy) > .02) {
    y += dy * easing;
  }
  easeLoc = new PVector(x, y);
  //return easeLoc;
}


float easeDiam(float targetX) {
  float dx = targetX - d;
  if (abs(dx) > .02) {
    d += dx * diamEase;
  }
  return d;
}

void keyPressed() {
  switch(key) {
  case ' ' :
    background(0);
    break;
  case 'a' :
    saveFrame("###.jpg");
    break;
  }
}

