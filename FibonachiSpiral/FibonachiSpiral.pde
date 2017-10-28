import gifAnimation.*;
PImage[] allFrames = Gif.getPImages(this, "lavalamp.gif");
int N = 1600;
int w, temp_w, k=0;
color c;
int[] col; 
void setup() {
  size(500, 500);
  col = new int[width*height];
  w = 1;
  c = (color) random(#000000);
  for (int i=0; i<width; i++) {
    for (int j=0; j<height; j++) {
      int index = i+j*width;
      int r = int(255-random(-85, 85));
      int g = int(255-random(-85, 85));
      int b = int(255-random(-85, 85)); 
      col[index] = color(r, g, b);
    }
  }
}

void draw() {
  background(0);
  paperBackground();
  stroke(-1, 2); 
  fill(-1, 1);
  translate(width/2, height/2);
  scale(0.3);
  for (int i=N; i>0; i-=8) {
    float ang = i*360/N;
    pushMatrix();
    rotate(radians(ang+k));  
    rect(0, 0, w*i, w*i);
    popMatrix();
  }
  k+=2;
}

void paperBackground() {
  for (int i=0; i<width; i++) {
    for (int j=0; j<height; j++) {
      int index = i+j*width;
      noStroke();
      fill(col[index], 40);
      rect(i, j, 1, 1);
    }
  }
}

