import org.philhosoft.p8g.svg.P8gGraphicsSVG;

/*
Simple test, exercing Bézier curves and simple geometry
(fill, stroke...).
*/

Heart h1, h2, h3, h4;

void setup()
{
  // Renders only on the SVG surface, no display
  size(400, 400, P8gGraphicsSVG.SVG, "Hearts.svg");
  smooth();
  background(#88AAFF);

  h1 = new Heart(70, 150, 50, 2.0, 1.0, 1.0, 0.0, 2, #FF0000, #FFA0A0);
  h2 = new Heart(200, 50, 50, 1.2, 0.8, 0.5, 60.0, 8, #550000, #882020);
  h3 = new Heart(250, 220, 100, #800000, #AA5555);
  h4 = new Heart(50, 320, 50, 1.5, 0.7, 0.5, 30.0, 7, #FFFF00, #00FFAA);
}

void draw()
{
  fill(#EEEEAA);
  stroke(#FFFFCC);
  strokeWeight(20);
  ellipse(width / 2, height / 2, width * 0.75, height * 0.75);

  h1.draw();
  h2.draw();
  h3.draw();
  // Shows that Java2D (and SVG) also scales the stroke!
  scale(4, 1);
  h4.draw();

  println("Done");
  // Mandatory, so that proper cleanup is called, saving the file
  exit();
}
