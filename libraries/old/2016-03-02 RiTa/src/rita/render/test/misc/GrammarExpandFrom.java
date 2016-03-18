package rita.render.test.misc;

import processing.core.PApplet;
import processing.core.PFont;
import rita.*;

public class GrammarExpandFrom extends PApplet
{
  String haikuGrammar = 
      "{\"<start>\": \"<5-line> % <7-line> % <5-line>\","
      + "\"<5-line>\": \"<1> <4> |<1> <3> <1> |<1> <1> <3> | <1> <2> <2> | <1> <2> <1> <1> | <1> <1> <2> <1> | <1> <1> <1> <2> | <1> <1> <1> <1> <1> | <2> <3> | <2> <2> <1> | <2> <1> <2> | <2> <1> <1> <1> | <3> <2> | <3> <1> <1> | <4> <1> | <5>\","
      + "\"<7-line>\": \"<1> <1> <5-line> | <2> <5-line> | <5-line> <1> <1> | <5-line> <2>\","
      + "\"<1>\": \"red | white | black | sky | dawns | breaks | falls | leaf | rain | pool | my | your | sun | clouds | blue | green | night | day | dawn | dusk | birds | fly | grass | tree | branch | through | hell | zen | smile | gray | wave | sea | through | sound | mind | smoke | cranes | fish\","
      + "\"<2>\": \"drifting | purple | mountains | skyline | city | faces | toward | empty | buddhist | temple | japan | under | ocean | thinking | zooming | rushing | over | rice field | rising | falling | sparkling | snowflake\","
      + "\"<3>\": \"sunrises | pheasant farms | people farms | samurai | juniper | fishing boats | far away | kimonos | evenings | peasant rain | sad snow fall\","
      + "\"<4>\": \"aluminum | yakitori | the east village | west of the sun |  chrysanthemums | cherry blossoms\","
      + "\"<5>\": \"resolutional | non-elemental | rolling foothills rise | toward mountains higher | out over this country | in the springtime again\"}";
  
  RiText[] rts = new RiText[3];
  RiGrammar grammar;

  public void setup()
  {
    size(650, 200);

    RiText.defaultFont("Times", 30);
    RiText.defaults.alignment=CENTER;

    rts[0] = new RiText(this, "click to", width / 2, 75);
    rts[1] = new RiText(this, "generate", width / 2, 110);
    rts[2] = new RiText(this, "a 7-7-7 haiku", width / 2, 145);

    grammar = new RiGrammar(haikuGrammar);
    //grammar.print();
  }

  public void draw()
  {
    background(230, 240, 255);
    for (int k = 0; k < rts.length; k++)
      rts[k].draw();
  }

  public void mouseClicked()
  {
    // all 7s
    for (int i = 0; i < rts.length; i++)
    {
      String result = grammar.expandFrom("<7-line>");
      rts[i].text(result);
    }
  }

}
