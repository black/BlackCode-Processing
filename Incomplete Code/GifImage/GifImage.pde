PImage img;
PVector[] point; 
int N = 3000, n = 30, k = 3;
void setup() {
  size(500, 500);
  img = loadImage("1.png");
  point = new PVector[N];
  for (int i=0; i<N; i++) {
    point[i] = new PVector(random(-width/k-i/n, width/k+i/n), random(-height/k-i/n, height/k+i/n));
  }
}
int m = 0;
void draw() {
  noStroke();
  fill(0, 2);
  strokeWeight(2);
  rect(0, 0, width, height);
  PVector org = new PVector(0, 0);
  translate(width/2, height/2);
  for (int i=0; i<point.length; i++) {
    stroke(-1, random(2, 50));
    float dis = org.dist(point[i]);
    float ang = map(dis, 0, width/2, 0.01, 0);
    point[i].rotate(ang);
    point(point[i].x+noise(m)*3, point[i].y+noise(m)*3);
  }
  m++;
  image(img, -width/6, -40);
}

