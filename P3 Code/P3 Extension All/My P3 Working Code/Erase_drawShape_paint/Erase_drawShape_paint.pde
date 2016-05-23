PGraphics pgFill, pgStroke, mask;
ArrayList <PVector> points = new ArrayList <PVector> ();
boolean bPoints, bDrawing;
int strokeWeight = 2;
color transparent = color(255, 0);
color black = color(0);
color drawColor;
int brushsize=25;
void setup() {
  size(500, 500);
  initPGraphics();
}

void draw() {
  background(255);
  grids();
  image(pgFill, 0, 0);
  image(pgStroke, 0, 0);

  drawPoints(g, points, transparent, black);

  if (bPoints) {
    points.add( new PVector(mouseX, mouseY) );
  } 
  else if (bDrawing) {
    drawInShapes(mouseX, mouseY, drawColor);
  }
}
// -------------------------- KEY EVENT --------------------------- 
void keyPressed() {
// if (key==' ') { 
// initPGraphics();
// }
 if (key=='a') { 
 bPoints = false; 
 points.clear();
 }
 }
//------------------------MOUSE EVENT ---------------------------
void mousePressed() {
  if (mouseButton == LEFT && key == ' ' ) {
    if (alpha(mask.get(mouseX, mouseY)) == 0) {
      bPoints = true;
    } 
  }
   if (mouseButton == RIGHT) {
      bDrawing = true;
      colorMode(HSB, 1);
      drawColor = color(random(1), 1, 1);
      colorMode(RGB, 255);
    }
  
  
}

void mouseDragged()
{
  if (keyPressed==true && key=='a')
  {
    color c = color(0, 0);
    //-----------------------------------
    pgFill.beginDraw();
    pgFill.loadPixels();
    for (int x=0; x<pgFill.width; x++) {
      for (int y=0; y<pgFill.height; y++ ) {
        if (dist(x, y, mouseX, mouseY)<brushsize) {
          int  i = x + y*pgFill.width;
          pgFill.pixels[i] = c;
        }
      }
    }
    pgFill.updatePixels();
    pgFill.endDraw();

    //---------------------------
    pgStroke.beginDraw();
    pgStroke.loadPixels();
    for (int x=0; x<pgStroke.width; x++) {
      for (int y=0; y<pgStroke.height; y++ ) {
        if (dist(x, y, mouseX, mouseY)<brushsize) {
          int  i = x + y*pgStroke.width;
          pgStroke.pixels[i] = c;
        }
      }
    }
    pgStroke.updatePixels();
    pgStroke.endDraw();
    //----------------------------------
  }
}
//--------------MOUSE FUNCTION----------------------------------


void mouseReleased() {
  if (bPoints) {
    bPoints = false;
    drawPoints(mask, points, black, transparent);
    drawPoints(pgStroke, points, transparent, black);
    points.clear();
  } 
  else {
    bDrawing = false;
  }
}
//-----------------------PGRAPHICS LAYERS------------------------- 
void initPGraphics() {
  pgStroke = createGraphics(width, height);
  pgStroke.beginDraw();
  pgStroke.endDraw();
  pgFill = createGraphics(width, height);
  pgFill.beginDraw();
  pgFill.endDraw();
  mask = createGraphics(width, height);
  mask.beginDraw();
  mask.endDraw();
}
//----------------------DRAW POINTS---------------------------------- 
void drawPoints(PGraphics pg, ArrayList <PVector> points, color fill, color stroke) {
  pg.beginDraw();
  pg.fill(fill);
  pg.strokeWeight(strokeWeight);
  pg.stroke(stroke);
  pg.beginShape();
  for (PVector p : points) {
    pg.vertex(p.x, p.y);
  }
  pg.endShape();
  pg.endDraw();
}
//-----------------------DRAW SHAPES ----------------------- 
void drawInShapes(int mouseX, int mouseY, color c) {
  pgFill.beginDraw();
  pgFill.noStroke();
  pgFill.fill(c);
  pgFill.ellipse(mouseX, mouseY, 50, 50);
  pgFill.endDraw();
  mask(pgFill, mask);
}
//------------------MASK------------------------------------ 
void mask(PImage target, PImage mask) {
  mask.loadPixels();
  target.loadPixels();
  if (mask.pixels.length != target.pixels.length) {
    println("Images are not the same size");
  } 
  else {
    for (int i=0; i<target.pixels.length; i++) {
      if (mask.pixels[i]!=black) {
        target.pixels[i] = ((mask.pixels[i] & 0xff) << 24) | (target.pixels[i] & 0xffffff);
      }
    }
    target.updatePixels();
  }
}
//-----------------GRIDS-----------------------------------
int dodge=50;
void grids() {  
  for (int i=0;i <width;i+=dodge) { 
    for (int j=0;j<height;j+=dodge) {  
      fill(0);    
      textSize(8);      
      text("+", i, j);
    }
  }
}

