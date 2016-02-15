import org.philhosoft.p8g.svg.P8gGraphicsSVG;

/*
Try the various settings that can be used with P8gGraphicsSVG.
*/

int hw, hh;
PFont font;

void setup()
{
  // With screen display
  size(800, 200);
  smooth();
  noLoop();

  // Half size
  hw = width / 2;
  hh = height / 2;

  font = createFont("Times New Roman", 32);

  // File name is provided later
  P8gGraphicsSVG svg = (P8gGraphicsSVG) createGraphics(width, height, P8gGraphicsSVG.SVG);
  beginRecord(svg);

  drawImage();

  // Same as endRecord() but saves to given path
  svg.endRecord(sketchPath("Settings-1"));


  // A new one
  svg = (P8gGraphicsSVG) createGraphics(width, height, P8gGraphicsSVG.SVG);
  // Uses attributes instead of CSS
  svg.setUseInlineCSS(false);
  // Save the image(s) in Base64 directly in the file
  svg.setImageFileFormat(P8gGraphicsSVG.ImageFileFormat.INTERNAL);
  beginRecord(svg);

  drawImage();

  svg.endRecord(sketchPath("Settings-2"));


  // A last one
  svg = (P8gGraphicsSVG) createGraphics(width, height, P8gGraphicsSVG.SVG);
  // Save the image(s) as Jpeg file. Bad here as transparency is lost, OK for photos, for example.
  svg.setImageFileFormat(P8gGraphicsSVG.ImageFileFormat.EXTERNAL_JPEG);
  // Store the letter shapes in the SVG file
  svg.textMode(SHAPE);
  beginRecord(svg);

  drawImage();

  svg.endRecord(sketchPath("Settings-3"));


  println("Done");
}

void drawImage()
{
  background(#CC7777);

  fill(#AAAADD);
  stroke(#BBBBFF);
  strokeWeight(20);
  float baseX = 0.4 * width;
  float diam = 0.9 * height;
  ellipse(baseX, hh, diam, diam);

  fill(#9999CC);
  stroke(#AAAAEE);
  strokeWeight(16);
  diam = 0.7 * height;
  ellipse(baseX + diam, hh, diam, diam);

  fill(#8888BB);
  stroke(#9999DD);
  strokeWeight(12);
  diam = 0.5 * height;
  ellipse(baseX + 2 * diam, hh, diam, diam);

  PGraphics subG = getImage();
  image(subG, 100, 0);
  pushMatrix();
  scale(-1, 1);
  translate(100 - width, 0);
  image(subG, 0, 0);
  popMatrix();

  // Show some text too
  textFont(font);
  fill(#000055);
  text("PhiLhoSoft presents:", 0.333 * width, 3 * height / 5);
  fill(#000088);
  textSize(48);
  text("P8gGraphicsSVG", 0.28 * width, 4 * height / 5);
}

PGraphics getImage()
{
  PGraphics subG = createGraphics(200, 200, JAVA2D);
  subG.beginDraw();

  subG.fill(#BBBB77, 128);
  subG.stroke(#CCCC99, 192);
  subG.strokeWeight(10);

  subG.beginShape();
  subG.vertex(20, 180);
  subG.vertex(90, 180);
  subG.vertex(160, 40);
  subG.endShape(CLOSE);

  subG.fill(#CCBB77, 128);
  subG.stroke(#DDCC99, 192);
  subG.strokeWeight(8);

  subG.strokeJoin(ROUND);
  subG.beginShape();
  subG.vertex(110, 180);
  subG.vertex(180, 180);
  subG.vertex(40, 40);
  subG.endShape(CLOSE);

  subG.endDraw();

  return subG;
}

