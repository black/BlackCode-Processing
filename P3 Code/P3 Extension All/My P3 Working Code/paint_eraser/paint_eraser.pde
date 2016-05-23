PGraphics canvas;
int x=100;
void setup() {
  size(500, 500, JAVA2D);
  smooth();
  canvas = createGraphics(width, height, JAVA2D);
  canvas.beginDraw();
  canvas.smooth();
  canvas.endDraw();
}

void draw() {
  background(0);
  grid();
  noStroke();
  image(canvas, 0, 0);
}

void grid()
{
  for(int i=0;i <width;i+=x)
  {
    for(int j=0;j<height;j+=x)
    {
      //stroke(255);
      //strokeWeight(2);
      textSize(8);
      text("+",i,j);
    }
    
  }
}

void mouseDragged() {
  if (mouseButton == LEFT) { 
    drawFunction();
  }
  else if(mouseButton == RIGHT){ 
    noFill(); 
    stroke(0, 255, 0); 
    ellipse(mouseX, mouseY, 10, 10); 
    eraseFunction();
  }
}

void drawFunction() {
  canvas.beginDraw();
  canvas.stroke(255, 0, 0);
  canvas.line(pmouseX,pmouseY, mouseX, mouseY);
  canvas.endDraw();
}

void eraseFunction() {
  color c = color(0, 0);
  canvas.beginDraw();
  canvas.loadPixels();
  for (int x=0; x<canvas.width; x++) {
    for (int y=0; y<canvas.height; y++ ) {
      float distance = dist(x, y, mouseX, mouseY);
      if (distance <= 25) {
        int loc = x + y*canvas.width;
        canvas.pixels[loc] = c;
      }
    }
  }
  canvas.updatePixels();
  canvas.endDraw();
}

