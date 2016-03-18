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
  public int myWidth;
  public int myHeight;
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
    scene.motionAgent().addInPool(this);
  }

  public void setText(String text) {
    myText = text;
    myWidth = (int) textWidth(myText);
    myHeight = (int) (textAscent() + textDescent());
  }

  public void display() {
    pushStyle();    
    fill(255);
    if (grabsInput(scene.motionAgent()))
       fill(255);
    else
      fill(150);
    scene.beginScreenDrawing();
    text(myText, position.x, position.y, myWidth, myHeight);
    scene.endScreenDrawing();
    popStyle();
  }

  @Override
  public boolean checkIfGrabsInput(BogusEvent event) {
    if (event instanceof DOF2Event) {
      float x = ((DOF2Event)event).x();
      float y = ((DOF2Event)event).y();
      return ((position.x <= x) && (x <= position.x + myWidth) && (position.y <= y) && (y <= position.y + myHeight));
    }
    else
      return false;
  }
}
