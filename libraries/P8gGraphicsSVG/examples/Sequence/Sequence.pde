import org.philhosoft.p8g.svg.P8gGraphicsSVG;

/*
Growing balls with collision checking.
Illustrates the usage of recordFrame().
*/

final int BALL_NB = 1000;
final int MARGIN = 50;
Ball[] balls = new Ball[BALL_NB];
P8gGraphicsSVG svg;

void setup()
{
  size(800, 800, P8gGraphicsSVG.SVG);
  // Cast the sketch's PGraphics to our type
  svg = (P8gGraphicsSVG) g;
  smooth();

  for (int i = 0; i < BALL_NB; i++)
  {
    balls[i] = new Ball();
  }
}

void draw()
{
  boolean bOneCanGrow = false;
  // Check ball collisions
  for (int i = 0; i < BALL_NB; i++)
  {
    for (int j = 0; j < BALL_NB; j++)
    {
      if (i != j && // Not the same ball!
          // If none of the ball can grow (is already static), no check to do.
          // Avoids a costly dist() calculation
          (balls[i].bCanGrow || balls[j].bCanGrow))
      {
        // If the distance between the two balls is below the summed up radii, must stop growing
        if (dist(balls[i].posX, balls[i].posY, balls[j].posX, balls[j].posY) <=
            balls[i].radius + balls[j].radius)
        {
          balls[i].bCanGrow = balls[j].bCanGrow = false;
        }
      }
    }
    if (balls[i].bCanGrow)
    {
      bOneCanGrow = true;
    }
  }
  if (bOneCanGrow)
  {
    background(#AAFFEE);
  }
  else
  {
    // Show this is the end, no more move
    background(#EEFFEE);
    noLoop(); // No more update needed
    println("Done at frame " + frameCount);
  }

  // Grow the balls that can still grow, and display all the balls
  for (int i = 0; i < BALL_NB; i++)
  {
    balls[i].grow();
    balls[i].display();
  }

  svg.recordFrame("Sequence-###.svg");
}

class Ball
{
  float posX, posY; // Position
  float radius = 1;
  color fillColor;
  boolean bCanGrow = true;

  // Base constructor just take care of randomness itself...
  Ball()
  {
    this(
        random(MARGIN, width - MARGIN),
        random(MARGIN, height - MARGIN),
        color(random(50, 150), random(100, 200), random(150, 255))
    );
  }
  // In case I need a finer, external control of parameters
  Ball(float px, float py, color pc)
  {
    posX = px; posY = py;
    fillColor = pc;
  }

  void grow()
  {
    if (bCanGrow)
    {
      radius += 1;
    }
  }

  void display()
  {
    noStroke();
    fill(fillColor);
    ellipse(posX, posY, radius * 2, radius * 2);
  }
}

