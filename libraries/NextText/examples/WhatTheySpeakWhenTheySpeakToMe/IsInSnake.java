import net.nexttext.TextObject;
import net.nexttext.behaviour.Action;
import net.nexttext.behaviour.control.Condition;
import net.nexttext.property.BooleanProperty;


/**
 * Condition that checks if a {@link net.nexttext.TextObject} is in the snake or not
 */
public class IsInSnake extends Condition {
        
    /** 
     * Creates a new instance of IsInSnake
     *
     * @param trueAction the actions to perform if the TextObject is in the snake
     * @param falseAction the actions to perform if the TextObject is not in the snake
     */
    public IsInSnake(Action trueAction, Action falseAction) {
        super(trueAction, falseAction);
    }
    
    
    /** 
     * Checks whether or not the condition holds on the TextObject
     *
     * @param to the concerned TextObject
     *
     * @return the outcome of the condition
     */
    public boolean condition(TextObject to) {
        BooleanProperty followProperty = (BooleanProperty)to.getProperty("Follower");
    	  if (followProperty.get()) 
            return true;
        return false;
    }
}