public class InteractiveRect extends InteractiveFrame {
  float  halfWidth  = 40, halfHeight = 40;
  int alpha = 125;
  int    colour  = color(0, 255, 255, alpha);

  public InteractiveRect(Scene scn) {
    super(scn);      
    updateRect();
  }
  
  public void changeShape(DOF1Event event) {
    halfWidth += event.dx()*5;
    updateRect();
  }

  public void changeShape(DOF2Event event) {
     halfWidth += event.dx();
     halfHeight += event.dy();
     updateRect();
  }

  public void changeColor() {
    colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
    updateRect();
  }
  
  public void colorBlue() {
    colour = color(0, 0, 255, alpha);
    updateRect();
  }
  
  public void colorRed() {
    colour = color(255, 0, 0, alpha);
    updateRect();
  }
  
  public void updateRect() {
    setShape(createShape(RECT, 80, 0, 2 * halfWidth, 2 * halfHeight));
    shape().setFill(color(colour));
  }
}