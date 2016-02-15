import org.philhosoft.p8g.svg.P8gGraphicsSVG;

/*
Allows the user to choose to save a particular frame of an animation.
Key 's' saves the currently displayed image to a SVG file (always overwriting the previous one);
key 'r' records this image to a numbered file;
key 'q' quits the sketch without saving a file.
*/

float level = 0.1;
P8gGraphicsSVG svg;

void setup()
{
  size(500, 500);
  smooth();
  frameRate(10);

  svg = (P8gGraphicsSVG) createGraphics(width, height, P8gGraphicsSVG.SVG, "SineOnBezier.svg");
  beginRecord(svg);

  println("Use s to save the current frame,\nr to save the current frame in a numbered file.\nUse q to end the sketch.");
}

void draw()
{
  // Call background() only for the visual part, not on the SVG renderer,
  // otherwise it will accumulate them
  g.background(255);

  svg.clear(); // Discard previous frame
  svg.beginDraw(); // And record this one

  noFill();
  stroke(0);
  int curveLength = 400;
  int curveHeight = 100;
  bezier(15, 120, 210, 90, 290, 420, 415, 380);

  stroke(#FF0000);
  int steps = 50;
  for (int i = 0; i < steps; i++)
  {
    float t = i / float(steps);
    float x = bezierPoint(15, 210, 290, 415, t);
    float y = bezierPoint(120, 90, 420, 380, t);

    float v = curveHeight * sin(t * TWO_PI * level);
    line(x, y, x, y + v);
  }

  fill(0);
  text("L: " + level, width - 100, 30);

  level += 0.1;
}

void keyPressed()
{
  if (key == 's') // Save the current image (and overwrite the previous one)
  {
    svg.endRecord();
    println("Saved.");
  }
  else if (key == 'r') // Record the current image to a new numbered file
  {
    svg.recordFrame("SineOnBezier-###.svg");
    println("Saved #" + svg.savedFrameCount);
  }
  else if (key == 'q')
  {
    // Don't overwrite the last saved frame!
    svg.clear();
    exit();
  }
}

