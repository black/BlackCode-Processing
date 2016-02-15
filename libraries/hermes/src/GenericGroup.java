package hermes;

import hermes.postoffice.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Groups together generic <code>HObject</code>s using the specified collection
 * that share common attributes and interact with another group of <code>HObject</code>s.
 * <p>
 * Like <code>Being</code>s, <code>GenericGroup</code>s can have their own update methods.
 * <p>
 * The primary purpose of grouping is for use in interactions.
 * However, <code>GenericGroup</code>s can also be used to store data about or provide
 * access to the contained objects.
 * For example, groups can keep track of the ages of the objects it contains
 * and direct messages to either the oldest or the newest object.
 * <p>
 * See {@link hermes.World World} for more details on registering interactions or Updates.
 *
 * @see	hermes.Interactor Interactor
 * @see	hermes.HObject HObject
 *
 * @param <A>	the type of the objects in the group
 * @param <B>	the type of underlying collection used
 */
public class GenericGroup<A extends HObject, B extends Collection<A>> 
							implements KeySubscriber, MouseSubscriber, MouseWheelSubscriber, OscSubscriber {

	private B _objects;		// the underlying collection
	private LinkedList<A> _needsMoreSamples;	// keeps track of any beings that need 
													// more samples this update
  protected World _world;	// the world containing the groups
	
	/**
	 * Instantiates a group storing HObjects in the given collection.
	 * @param objects	the collection objects will be stored in
	 * @param world		the world where the group will be used
	 */
	public GenericGroup(B objects, World world) {
		_objects = objects;
		_world = world;
		_needsMoreSamples = new LinkedList<A>();
	}
	
	/**
	 * Returns the underlying collection containing all objects in the group.
	 * WARNING -- DO NOT ADD TO OR REMOVE FROM THIS COLLECTION DIRECTLY
	 * @return	the data structure containing all objects in the group
	 */
	public B getObjects() {
		return _objects;
	}
	
	/**
	 * An iterator over the underlying collection.
	 * WARNING -- DO NOT REMOVE BEINGS USING Iterator.remove()
	 * @return	an iterator over all elements in the group
	 */
	public Iterator<A> iterator() {
		return getObjects().iterator();
	}
	
	/**
	 * Performs an update on the group. Override to use.
	 */
	public void update() {}

	/**
	 * Adds a being to the group at the end of the next update loop.
	 * @param being		the being to add
	 * @return			the added object
	 */
	public A add(A being) {
		_world.addToGroup(being, this);
		return being;
	}
	
	/**
	 * Removes an object from the group at the end of the next update loop.
	 * @param object	the object to remove
	 * @return			the removed object
	 */
	public A remove(A object) {
		_world.remove(object, this);
		if(hasNeedsMoreSamples() && object.needsMoreSamples()) {
			_needsMoreSamples.remove(object);
		}
		return object;
	}
	
	/**
	 * Adds the contents of another group to this group.
	 * Will always be O(n) regardless of the underlying collection.
	 * @param group		the objects to add
	 */
	public void addAll(GenericGroup<A,?> group) {
		for(Iterator<A> iter = group.iterator(); iter.hasNext(); ) {
			_world.addToGroup(iter.next(), this);
		}
	}
	
	/**
	 * Removes the contents of a group from this group.
	 * @param group		the objects to remove
	 */
	public void removeAll(GenericGroup<A,?> group) {
		for(Iterator<A> iter = group.iterator(); iter.hasNext(); ) {
			A object = iter.next();
			if(hasNeedsMoreSamples() && object.needsMoreSamples()) {
				_needsMoreSamples.remove(object);
			}
			_world.remove(object, this);
		}
	}
	
	/**
	 * Clears everything from the group at the end of the update.
	 * This will always be O(n), regardless of the underlying collection.
	 */
	public void clear() {
		for(Iterator<A> iter = iterator(); iter.hasNext(); ){
			_world.remove(iter.next(), this);
		}
		_needsMoreSamples.clear();
	}
	
	/**
	 * Deletes everything in the group from the world at the end of the update.
	 * Note: this means the group and its beings are totally destroyed!
	 * They will be removed from any other groups they are in.
	 * This will always be O(n), regardless of the underlying collection.
	 */
	public void destroy() {
		for(Iterator<A> iter = iterator(); iter.hasNext(); ) {
			_world.delete(iter.next());
		}
		_needsMoreSamples.clear();
	}
	
	/**
	 * @return	the number of beings contained by the group
	 */
	public int size() {
		return _objects.size();
	}
	
	/**
	 * @param world	the world the group should be contained by
	 */
	public void setWorld(World world) {
		this._world = world;
	}

	/**
	 * @return	the world currently containing the group
	 */
	public World getWorld() {
		return _world;
	}

	void addNeedsMoreSamples(A object) {
		_needsMoreSamples.addLast(object);
	}
	
	Iterator<A> getNeedsMoreSamples() {
		return _needsMoreSamples.iterator();
	}
	
	/**
	 * Used internally.
	 * Whether the group contains objects that need more samples on this update.
	 * @return	true if objects within the group need more samples, false if no objects do
	 */
	public boolean hasNeedsMoreSamples() {
		return !_needsMoreSamples.isEmpty();
	}
	
	void clearNeedsMoreSamples() {
		_needsMoreSamples.clear();
	}
	
	//Methods for receiving methods from PostOffice, defined in subscriber interfaces
	//Left blank here, must be overrided by user to add functionality
	/**
	 * Override if you want your group to handle Key messages
	 */
	public void receive(KeyMessage m) {
		//VOID
	}
	/**
	 * Override if you want your group to handle Mouse messages
	 */
	public void receive(MouseMessage m) {
		//VOID
	}
	/**
	 * Override if you want your group to handle Mouse Wheel messages
	 */
	public void receive(MouseWheelMessage m) {
		//VOID
	}
	/**
	 * Override if you want your group to handle OSC messages
	 */
	public void receive(OscMessage m) {
		//VOID
	}
	
}
