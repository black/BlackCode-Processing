PGraphics pgFill, pgStroke, mask;
ArrayList <PVector> points = new ArrayList <PVector> ();
boolean bPoints, bDrawing;
int strokeWeight = 10;
color transparent = color(255,0);
color black = color(0);
color drawColor;
 
void setup() {
  size(500, 500);
  initPGraphics();
}
 
void draw() {
  background(255);
 
  image(pgFill, 0, 0);
  image(pgStroke, 0, 0);
 
  drawPoints(g, points, transparent, black);
 
  if (bPoints) {
    points.add( new PVector(mouseX, mouseY) );
  } else if (bDrawing) {
    drawInShapes(mouseX, mouseY, drawColor);
  }
}
 
void keyPressed() {
  if (key==' ') { initPGraphics(); }
  if (key=='c') { bPoints = false; points.clear(); }
}
 
void mousePressed() {
  if (alpha(mask.get(mouseX, mouseY)) == 0) {
    bPoints = true;
  } else {
    bDrawing = true;
    colorMode(HSB, 1);
    drawColor = color(random(1), 1, 1);
    colorMode(RGB, 255);
  }
}
 
void mouseReleased() {
  if (bPoints) {
    bPoints = false;
    drawPoints(mask, points, black, transparent);
    drawPoints(pgStroke, points, transparent, black);
    points.clear();
  } else {
    bDrawing = false;
  }
}
 
void initPGraphics() {
  pgStroke = createGraphics(width, height, JAVA2D);
  pgStroke.beginDraw();
  pgStroke.endDraw();
  pgFill = createGraphics(width, height, JAVA2D);
  pgFill.beginDraw();
  pgFill.endDraw();
  mask = createGraphics(width, height, JAVA2D);
  mask.beginDraw();
  mask.endDraw();
}
 
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
 
void drawInShapes(int mouseX, int mouseY, color c) {
  pgFill.beginDraw();
  pgFill.noStroke();
  pgFill.fill(c);
  pgFill.ellipse(mouseX, mouseY, 50, 50);
  pgFill.endDraw();
  mask(pgFill, mask);
}
 
void mask(PImage target, PImage mask) {
  mask.loadPixels();
  target.loadPixels();
  if (mask.pixels.length != target.pixels.length) {
    println("Images are not the same size");
  } else {
    for (int i=0; i<target.pixels.length; i++) {
      if (mask.pixels[i]!=black) {
        target.pixels[i] = transparent;
      }
    }
    target.updatePixels();
  }
}
