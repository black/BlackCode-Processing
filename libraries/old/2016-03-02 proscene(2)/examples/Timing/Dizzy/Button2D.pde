/**
 * Button 2D.
 * by Jean Pierre Charalambos.
 * 
 * Base class of "2d buttons" that shows how simple is to implement
 * a MouseGrabber which can enable complex mouse interactions.
 */

public abstract class Button2D extends GrabberObject {
  public Scene scene;  
  String myText;
  PFont myFont;
  float myWidth;
  float myHeight;
  PVector position;

  public Button2D(Scene scn, PVector p, PFont font) {
    this(scn, p, font, "");
  }

  public Button2D(Scene scn, PVector p, PFont font, String t) {
    scene = scn;
    position = p;
    myText = t;
    myFont = font;
    textFont(myFont);
    textAlign(LEFT);
    setText(t);
    scene.motionAgent().addGrabber(this);
  }

  public void setText(String text) {
    myText = text;
    myWidth = textWidth(myText);
    myHeight = textAscent() + textDescent();
  }

  public void display() {
    pushStyle();    
    fill(255);
    if (grabsInput(scene.motionAgent()))
      fill(255);
    else
      fill(100);
    scene.beginScreenDrawing();
    text(myText, position.x, position.y, myWidth+1, myHeight);
    scene.endScreenDrawing();
    popStyle();
  }
  
  @Override
  public boolean checkIfGrabsInput(DOF2Event event) {
    float x = event.x();
    float y = event.y();
    return ((position.x <= x) && (x <= position.x + myWidth) && (position.y <= y) && (y <= position.y + myHeight));
  }
}
