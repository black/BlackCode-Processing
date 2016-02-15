import org.gicentre.handy.HandyRenderer;

//*****************************************************************************************
/** Simple sketch to show a hand-drawn bar graph.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 1.1, 3rd January 2012.
 */
// *****************************************************************************************

// ----------------------------- Object variables ------------------------------

private HandyRenderer h;
private PFont titleFont, bodyFont;
private float sWeight;
private color fillColour;

private PFont hLargeFont, hMediumFont, nLargeFont, nMediumFont;

private float[] data = new float[] {
  57, 75, 60, 49, 18, 36, 34, 14, -40, 17, -26, 3, -15, -30, 
  -50, -31, -86, -42, -64, -70, -67, -126, -66, 0, -94, -221
};

// ---------------------------- Processing methods -----------------------------

void setup()
{   
  size(1000, 400);
  smooth();

  hLargeFont  = loadFont("AmarelinhaBold-36.vlw");
  hMediumFont = loadFont("Amarelinha-32.vlw");

  nLargeFont = hLargeFont;
  nMediumFont = hMediumFont;

  titleFont = nLargeFont;
  bodyFont = nMediumFont;
  sWeight = 1;
  fillColour = color(162, 187, 243);
  
  h = new HandyRenderer(this);
  h.setSecondaryColour(color(0, 10));
  h.setUseSecondaryColour(true);
}


void draw()
{
  background(255);
  stroke(80);
  strokeWeight(sWeight);
  textAlign(PConstants.LEFT, PConstants.TOP);

  fill(80);
  textFont(titleFont);
  textSize(48);

  textAlign(PConstants.CENTER, PConstants.TOP);
  text("What's in a name?", width/2, 10);

  noFill();
  textFont(bodyFont);
  textSize(32);
  fill(40);
  textLeading(32);
 
  textAlign(PConstants.LEFT, PConstants.TOP);
  text("Numbers of extra votes received as a bonus or deprived from a candidate depending on the first letter of their surname.", 60, 210, 450, 180);


  textAlign(PConstants.CENTER, PConstants.CENTER);

  // Draw bars
  float cy = height*.31f;
  float barWidth = (width-70)/26f;
  textFont(titleFont);
  fill(40);
  for (int i=0; i<data.length; i++)
  {
    float barLength = data[i];
    fill(fillColour);
    h.setHachureAngle(-37+random(-7, 7));
    h.rect(50+i*barWidth, cy, barWidth-4, -barLength);

    fill(100);
    if (barLength>0)
    {
      text((char)('A'+i), 50+(i+0.4f)*barWidth, cy+15);
    }
    else
    {
      text((char)('A'+i), 50+(i+0.4f)*barWidth, cy-20);
    }
  }

  // Draw scale
  stroke(180);
  fill(80);
  h.line(40, cy+250, 40, cy-100);

  textAlign(RIGHT, CENTER);
  textSize(20);
  for (int y=-100; y<=250; y+=50)
  {
    text(-y, 30, cy+y);
    h.line(35, cy+y, 40, cy+y);
  }


  noLoop();
}


void keyPressed()
{

  if (key == '1')
  {
    sWeight = 0.3f;
    fillColour = color(162, 187, 243);
    loop();
  }
  else if (key == '2')
  {
    sWeight = 1;
    fillColour = color(162, 187, 243);
    loop();
  }
  else if (key == '3')
  {
    sWeight = 3;
    fillColour = color(162, 187, 243);
    loop();
  }
  else if (key == '4')
  {
    sWeight = 0.8f;
    h.setFillGap(0.8);
    h.setFillWeight(0.3);
    fillColour = color(180,110,110);
    h.setSecondaryColour(color(0,20));
    loop();
  }
}

