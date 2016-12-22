PImage img;
void setup() {
  img = loadImage("1.jpg"); 
  int w = img.width/3;
  int h = img.height/3;
  img.resize(w, h);
  size(w, h);
}
int k=0;
void draw() {
  background(img);
  for (int i=0; i<width; i++) {
    for (int j=0; j<height; j++) {
      color c = get(i, j);
      if (j>2*height/3) {
        float x = i+0.5*sin(4*radians(k));
        stroke(c);
        point(x, j);
      }
    }
  }
  if (k<90)k++;
  else k=0;
}

