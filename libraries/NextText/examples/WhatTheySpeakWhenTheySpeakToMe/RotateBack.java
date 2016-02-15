import net.nexttext.TextObject;
import net.nexttext.property.NumberProperty;


/**
 * Behaviour that rotates a TextObject back to 0 degrees.
 */
public class RotateBack extends IWillFollowAction {
  
    /**
     * 
     * Creates a new instance of RotateBack
     */
    public RotateBack() {
        properties().init("speed", new NumberProperty((float)Math.PI / 40));
    }

    
    /**
     * 
     * Creates a new instance of RotateBack with custom speed
     * 
     * @param speed rotation speed
     */
    public RotateBack(float speed) {
        properties().init("speed", new NumberProperty(speed));
    }

    
    /** 
     * Rotates a TextObject back to 0 degrees.
     *
     * @param to the TextObject to act upon
     */
    public ActionResult behave(TextObject to) {
        float speed = ((NumberProperty)properties().get("speed")).get();

        // get the rotation
        NumberProperty rotation = to.getRotation();
        
        // if the TextObject is currently in quadrant 1, rotate CCW
        if ((rotation.get() > 0) && (rotation.get() <= Math.PI/2)) {
            rotation.set(rotation.get() - speed);
            if (rotation.get() < 0) { 
                rotation.set(0); 
            }
        // if the TextObject is currently in quadrant 4, rotate CW
        } else if ((rotation.get() >= Math.PI/2*3) && (rotation.get() <= Math.PI*2)) {
            rotation.set(rotation.get() + speed);
            if (rotation.get() > Math.PI*2) { 
                rotation.set(0); 
            }
        }
    
        return new ActionResult(false, false, false);
    }  
}