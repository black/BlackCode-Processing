import net.nexttext.*;
import net.nexttext.behaviour.physics.*;
import net.nexttext.property.*;
import processing.core.*;

/**
 * An Action which applies inertia to TextObjects that are dragged then released
 * based on the mouse's current and previous coordinates.
 */
public class MouseInertia extends PhysicsAction {
  private PApplet p;
  private float fScale;
  private float aScale;

  public MouseInertia(PApplet p, float fScale, float aScale) {
    this.p = p;
    this.fScale = fScale;
    this.aScale = aScale;
  }

  public ActionResult behave(TextObject to) {
    BooleanProperty dragging = (BooleanProperty)to.getProperty("Dragging");
    if (dragging != null) {
      if (dragging.get()) {
        // the TextObject was being dragged at the last frame, add inertia
        int dX = p.mouseX-p.pmouseX;
        int dY = p.mouseY-p.pmouseY;
        
        PVector inertia = new PVector(dX, dY);
        inertia.mult(fScale);
        
        // an attempt at calculating angular force... it seems to work but
        // if anyone knows of a better way of doing this please let me know
        float angle = p.min(p.abs(dX), p.abs(dY))/(inertia.mag()+.001f)/100.0f;
        int aFactor;
        if (inertia.x*inertia.y < 0) {
          aFactor = -1;
        } else {
          aFactor = 1;
        }

        applyForce(to, inertia);
        applyAngularForce(to, inertia.mag()*angle*aFactor);

        dragging.set(false);
      }
    }

    return new ActionResult(false, false, false);
  }
}