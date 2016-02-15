package hermes;

import hermes.postoffice.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Basic game object class.
 * <p>
 * All parts of game are represented as HObjects.
 * <p>
 * For most uses, extend the Being class
 * <p>
 * Extend HObject directly when you want to represent something more abstract,
 * like game state.
 */
public abstract class HObject implements KeySubscriber, MouseSubscriber, MouseWheelSubscriber, OscSubscriber {
	
	private LinkedList<GenericGroup> _groups;	// groups the being is a member of
	
	protected HObject() {
		_groups = new LinkedList<GenericGroup>();
	}
	
	/**
	 * adds the HObject to the group
	 * @param group		the group to add to
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addToGroup(GenericGroup group) {
		// need to lock on the group
		synchronized(group) {
			group.getObjects().add(this);
			_groups.add(group);
		}
	}
	
	/**
	 * removes the HObject from this group
	 * @param group		the group to remove from
	 */
	@SuppressWarnings("rawtypes")
	protected void removeFromGroup(GenericGroup group) {
		// need to lock on the group
		synchronized(group) {
			group.getObjects().remove(this);
			_groups.remove(group);
		}
	}
	
	/**
	 * removes the HObject from all containing groups
	 */
	protected void delete() {
		// go through all the groups, deleting this being
		for(Iterator<GenericGroup> iter = _groups.iterator(); iter.hasNext(); ) {
			GenericGroup group = iter.next();
			// need to lock on the group
			synchronized(group) {
				group.getObjects().remove(this);
				iter.remove();
			}
		}
	}
	
	/**
	 * Returns an iterator over all the groups the object is a member of.
	 * @return	iterators over the groups containing this object
	 */
	protected Iterator<GenericGroup> getGroups() {
		return _groups.iterator();
	}
	
	/**
	 * Used for multisampling -- if true the object needs to be sampled more on the current update.
	 * @return	whether the object needs more samples this update
	 */
	public boolean needsMoreSamples() {
		return false;
	}
	
	//Methods for receiving methods from PostOffice, defined in subscriber interfaces
	//Left blank here, must be overridden by user to add functionality
	/**
	 * Override if you want your <code>Being</code> to handle Key messages
	 */
	public void receive(KeyMessage m) {
		//VOID
	}
	/**
	 * Override if you want your <code>Being</code> to handle Mouse messages
	 */
	public void receive(MouseMessage m) {
		//VOID
	}
	/**
	 * Override if you want your <code>Being</code> to handle Mouse Wheel messages
	 */
	public void receive(MouseWheelMessage m) {
		//VOID
	}
	/**
	 * Override if you want your <code>Being</code> to handle OSC messages
	 */
	public void receive(OscMessage m) {
		//VOID
	}
}
