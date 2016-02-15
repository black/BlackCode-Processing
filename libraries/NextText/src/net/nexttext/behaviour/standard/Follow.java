package net.nexttext.behaviour.standard;

import processing.core.PVector;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.Locatable;
import net.nexttext.PLocatableVector;
import net.nexttext.TextObject;
import net.nexttext.behaviour.TargetingAction;
import net.nexttext.behaviour.Action.ActionResult;
import net.nexttext.property.PVectorProperty;

public class Follow extends AbstractAction implements TargetingAction {

	Locatable followed; //the followed object
	PVector lastLocation; // keep track of the last position of the object
	long lastFrame; // last frame (of the book) that the action was applied
	
	/**
	 * Follow the motion of an object.
	 * @param followed the followed object
	 */
	public Follow(Locatable followed) {
		this.followed = followed;
		this.lastLocation = null;
		this.lastFrame = -1;
	}
	
	/**
	 * Follow the motion of a vector point.
	 * @param vector point to follow
	 */
	public Follow(PVector vector) {
		this(new PLocatableVector(vector));
	}
	
	/**
	 * Set the followed object.
	 * @param target the followed object 
	 */
	public void setTarget(Locatable target) {
		this.followed = target;
		this.lastLocation = null;
		this.lastFrame = -1;
	}

    /**
     * Sets a target to approach.
     */
    public void setTarget( float x, float y ) {
    	setTarget(x, y, 0);
    }
    
    /**
     * Sets a target to approach.
     */
    public void setTarget( float x, float y, float z ) {
    	setTarget(new PLocatableVector(x, y, z));
    }
    
    /**
     * Sets a target to approach.
     */
    public void setTarget( PVector target ) {
    	setTarget(new PLocatableVector(target));
    }
    
	/**
	 * Apply the action to a TextObject.
	 * @param to the affected text object
	 */
	public ActionResult behave(TextObject to) {	
		//if we never checked the followed object location then
		//store it for next time
		if ((lastLocation == null) || (to.getBook().getFrameCount()-lastFrame > 1)) {
			lastLocation = followed.getLocation();
			lastFrame = to.getBook().getFrameCount();	
			return new ActionResult(false, false, false);
		}
		
		//calculate the followed object's movement since last time
		PVector currLocation = followed.getLocation();
		currLocation.sub(lastLocation);
		
		//apply the same movement to the text object
    	PVectorProperty posProp = getPosition(to);
       	posProp.add(currLocation);
       	
       	//save the current location and frame for next time
       	lastLocation.add(currLocation);
		lastFrame = to.getBook().getFrameCount();
		
        return new ActionResult(false, false, false);		
	}
}
