import net.nexttext.*;
import net.nexttext.behaviour.*;
import net.nexttext.behaviour.control.*;
import net.nexttext.behaviour.physics.*;
import net.nexttext.behaviour.standard.*;
import net.nexttext.property.*;
import processing.core.*;

/**
 * An OnDrag which also sets the TextObject property "Dragging" when it is 
 * being dragged. This property is read and used by the MouseInertia action.
 */
public class Throw extends PhysicsAction {
  private OnDrag onDrag;
  private Action trueAction;
  private Action falseAction;
  
  public Throw(Action trueAction, Action falseAction) {
    this(PApplet.LEFT, trueAction, falseAction);
  }
  
  public Throw(int buttonToCheck, Action trueAction, Action falseAction) {
    onDrag = new OnDrag(buttonToCheck, new DoNothing(), new DoNothing());
    this.trueAction = trueAction;
    this.falseAction = falseAction;
  }

  public boolean condition(TextObject to) {
    boolean dragging = onDrag.condition(to);
    
    // get the "Dragging" property for this TextObject
    BooleanProperty draggingProp = (BooleanProperty)to.getProperty("Dragging");

    // update the property
    if (dragging) {
      if (draggingProp == null) {
        // this is the first time setting the property, initialize it
        to.init("Dragging", new BooleanProperty(true));
      } else if (!draggingProp.get()) {
        // the property is already initialized
        draggingProp.set(true);
      }
      
      // zero out any movement
      getVelocity(to).set(new PVector(0, 0, 0));
      getAngularVelocity(to).set(0);
         
    } else {
      if (draggingProp == null) {
        // this is the first time setting the property, initialize it
        to.init("Dragging", new BooleanProperty(false));
      }
    }

    return dragging;
  }
  
  /** 
   * This behaviour is similar to Condition, but since we need to
   * extend PhysicsAction, the following code is copied over.
   */
  public ActionResult behave(TextObject to) {
    if (condition(to)) {
      return trueAction.behave(to);
    } else {
      return falseAction.behave(to);
    }
  }
  
  public OnDrag getOnDrag() {
    return onDrag;
  }
}