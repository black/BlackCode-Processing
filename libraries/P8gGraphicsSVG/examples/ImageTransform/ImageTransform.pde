import org.philhosoft.p8g.svg.P8gGraphicsSVG;

/*
PImage testing, using various transformations.
Simple transforms like translate, scale or rotate are used directly in the SVG, reusing the original image.
Bitmap-based transforms, like filter or blend (or mask) can currently be done only in an off-screen
PGraphics, that Batik will export as a new image.
*/

PImage niceImage;
color bgColor = #AAFFDD;

void setup()
{
  // Also display
  size(1200, 800);
  noLoop();

  // Start the recording
  beginRecord(P8gGraphicsSVG.SVG, "ImageTransform.svg");
  smooth();
  background(bgColor);

  // 393x500
  niceImage = loadImage("Globe.png");
  niceImage.resize(400, 400);
  image(niceImage, 0, 0, niceImage.width, niceImage.height);

  translate(0, niceImage.height);
  drawReflected(niceImage);
  translate(niceImage.width, 0);
  drawInverted(niceImage);

  translate(0, -niceImage.height);
  drawTint(niceImage);
  translate(niceImage.width, 0);
  drawGray(niceImage);
  translate(0, niceImage.height);
  drawBlend(niceImage);

  // End the recording
  endRecord();
  println("Done");
}

void drawInverted(PImage img)
{
  pushMatrix();
  rotate(PI);
  translate(-img.width, -img.height);
  image(img, 0, 0, img.width, img.height);
  popMatrix();
}

void drawReflected(PImage img)
{
  pushMatrix();
  scale(1.00, -1.00);
  image(img, 0, -img.height, img.width, img.height);
  popMatrix();
}

void drawGray(PImage img)
{
  PGraphics gr = createGraphics(img.width, img.height, JAVA2D);
  gr.beginDraw();
  gr.background(bgColor);
  gr.image(img, 0, 0, img.width, img.height);
  gr.filter(GRAY);
  gr.filter(POSTERIZE, 4);
  gr.endDraw();

  image(gr, 0, 0);
}

void drawTint(PImage img)
{
  PGraphics gr = createGraphics(img.width, img.height, JAVA2D);
  gr.beginDraw();
  gr.background(bgColor);
  gr.tint(255, 126);
  gr.image(img, 0, 0, img.width, img.height);
  gr.endDraw();

  image(gr, 0, 0);
}

void drawBlend(PImage img)
{
  PGraphics gr = createGraphics(img.width, img.height, JAVA2D);
  gr.beginDraw();
  gr.background(bgColor);
  gr.rotate(HALF_PI);
  gr.translate(0, -img.height);
  gr.image(img, 0, 0);
  gr.blend(img, 0, 0, img.width, img.height, 0, 0, img.width, img.height, BLEND);
  gr.endDraw();

  image(gr, 0, 0);
}

