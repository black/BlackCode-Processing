package net.nexttext.property;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PVector;

/**
 * A property for list of PVectors.
 *
 * <p>This class is half way between a list of PVectorProperties and a Property
 * containing a list of PVectors, so is is not fully consistent with the other
 * property classes.  To make it consistent it would need these changes: </p>
 *
 * <ul>
 * <li>Replace <code>Vector3Property get()</code> with <code>Vector3
 * get()</code> and <code>Vector3 getOriginal()</code>.
 *
 * <li>Add all of the <code>Vector3</code> mathematical methods.  </li>
 * </ul>
 */
public class PVectorListProperty extends Property implements PropertyChangeListener {
	
	private ArrayList<PVectorProperty> list;
	
	/**
	 * Default constructor.  Creates an empty PVectorListProperty.
	 */
	public PVectorListProperty() {
        list = new ArrayList<PVectorProperty>();
	}
	
	/**
	 * Add a PVector object to the list.
	 * <p>This will automatically convert the PVector to a PVectorProperty.</p>
	 */
	public void add( PVector v1 ) {
		add(list.size(), new PVectorProperty(v1));
	}
	
	/**
	 * Adds a PVectorProperty object to the list.
	 */
	public void add( PVectorProperty v1 ) {
        add(list.size(), v1);
	}
	
	/**
	 * Adds a PVectorProperty object at the specified position in the list.
	 */
	public void add( int position, PVector v1 ) {
        add(position, new PVectorProperty(v1));
	}

	/**
	 * Adds a PVectorProperty object at the specified position in the list.
	 */
	public void add( int position, PVectorProperty v1 ) {
        v1.addChangeListener(this);
		list.add( position, v1 );
		firePropertyChangeEvent();
	}
	
	/**
	 * Returns the PVectorProperty object at the specified position in the list.
	 */
	public PVectorProperty getProp( int position ) {
        return list.get(position);
	}
	
	/**
	 * Returns the PVector object at the specified position in the list.
	 */
	public PVector get( int position ) {
        return list.get(position).get();
	}
	
	/**
	 * Returns the original PVector object at the specified position in the list.
	 */
	public PVector getOriginal( int position ) {
        return list.get(position).getOriginal();
	}
	
    /**
     * Resets each PVectorProperty in the list to its original value.
     */
    public void reset() {
        Iterator<PVectorProperty> i = list.iterator();
        while (i.hasNext()) {
            i.next().reset();
        }
        firePropertyChangeEvent();
    }
    
    /**
     * Clears out the content of the list
     */
    public void clear() {
    	list.clear();
    	firePropertyChangeEvent();
    } 
   
    public String toString() {
        StringBuffer ret = new StringBuffer();
        Iterator<PVectorProperty> i = list.iterator();
        while (i.hasNext()) {
            ret.append(i.next().toString());
        }
        return ret.toString();
	}

	/**
	 * Returns an iterator for the list of PVectorProperty objects.
     *
     * <p>Don't use this iterator to remove items from the list, since this
     * won't trigger the necessary PropertyChangeEvents.  If you need to remove
     * points, write a remove method for this class, and call that.  </p>
	 */
	public Iterator<PVectorProperty> iterator() {
		return list.iterator();
	}
	   
	/**
	 * Returns the number of PVectorProperty objects contained in this list.
	 */
	public int size() {
		return list.size();
	}	

    /**
     * For interface PropertyChangeListener, called when one of the properties
     * in the list changes.
     */
    public void propertyChanged(Property pc) {
        firePropertyChangeEvent();
    }

    public PVectorListProperty clone() {
    	PVectorListProperty that = (PVectorListProperty) super.clone();
        that.list = new ArrayList<PVectorProperty>(list.size());
        Iterator<PVectorProperty> i = list.iterator();
        while (i.hasNext()) {
            that.add(i.next().clone());
        }
        return that;
    }
}
