import net.nexttext.TextObject;
import net.nexttext.property.BooleanProperty;
import net.nexttext.behaviour.physics.PhysicsAction;

import java.util.Map;


/**
 * Parent Action for all I Will Follow actions.
 *
 * <p>Adds the Leader and Follower properties to the set of basic properties.</p>
 */
public class IWillFollowAction extends PhysicsAction {
    
    /**
     * Gets the set of properties required by all IWillFollowActions
     *
     * @return Map containing the properties
     */
    public Map getRequiredProperties() {
        Map properties = super.getRequiredProperties();

        BooleanProperty leader = new BooleanProperty(false);
        properties.put("Leader", leader);
        
        BooleanProperty follower = new BooleanProperty(false);
        properties.put("Follower", follower);

        return properties;
    }

    
    /**
     * Gets the value of the Leader property
     *
     * @param to the concerned TextObject
     *
     * @return BooleanProperty whether this TextObject has the Leader property or not
     */
    public BooleanProperty isLeader(TextObject to) {
        return (BooleanProperty)to.getProperty("Leader");
    }
    
    
    /**
     * Gets the value of the Follower property
     *
     * @param to the concerned TextObject
     *
     * @return BooleanProperty whether this TextObject has the Follower property or not
     */
    public BooleanProperty isFollower(TextObject to) {
        return (BooleanProperty)to.getProperty("Follower");
    }
}