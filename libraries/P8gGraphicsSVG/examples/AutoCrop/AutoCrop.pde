import org.philhosoft.p8g.svg.P8gGraphicsSVG;

/*
Show simultaneous on-screen display and SVG export, and how the viewport clips the graphics.
Also a simple text test.
*/

int hw, hh;

void setup()
{
  // With screen display
  size(200, 200);
  noLoop(); // No animation

  // Half size
  hw = width / 2;
  hh = height / 2;

  // Start the recording
  beginRecord(P8gGraphicsSVG.SVG, "AutoCrop.svg");
}

void draw()
{
  background(#FF7777);

  // Draw bigger than the screen
  fill(#AAAA55);
  noStroke();
  ellipse(hw, hh, 1.2 * width, 1.1 * height);

  fill(#AAEEEE);
  pushMatrix();
  rotate(PI / 4);
  translate(hw * 1.414, 0);
  rectMode(CENTER);
  rect(0, 0, width * 2, height / 5);
  fill(#FF7777);
  ellipse(0, 0, 10, 10);
  popMatrix();

  // Show some text too
  fill(#000055);
  textSize(24);
  text("P8gGraphicsSVG\nBy PhiLhoSoft", 4, height / 2.2);

  // End the recording, mandatory to save the file
  endRecord();
  println("Done");
}
