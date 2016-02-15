/*
  This file is part of the NextText project.
  http://www.nexttext.net/

  Copyright (c) 2004-08 Obx Labs / Jason Lewis

  NextText is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 2 of the License, or (at your option) any later 
  version.

  NextText is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  NextText.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.nexttext.property;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * A property value of a TextObject or Behaviour.
 *
 * <p>TextObjects and Behaviours both keep properties accessible by name.
 * Subclasses of this class are used to hold the values of these properties.
 * Each property keeps an origin value and a current value.</p> 
 *
 * <p>Property implements Cloneable so that a TextObject's properties can be
 * copied easily.  </p>
 */
/* $Id$ */
public abstract class Property implements Cloneable {
    
    private Collection<PropertyChangeListener> listeners = new Vector<PropertyChangeListener>();
    private String name = "";
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Registers a new PropertyChangeListener for this property.
     */
    public void addChangeListener( PropertyChangeListener listener ) {
        listeners.add( listener );
    }
   
    protected void firePropertyChangeEvent() {         
        for ( Iterator<PropertyChangeListener> i = listeners.iterator(); i.hasNext(); ) {
                i.next().propertyChanged(this);
        }
    }

    /**
     * Get a new property with the same values as this one.
     *
     * <p>The name is copied because that's what makes it a Property and not
     * just a value.  </p>
     *
     * <p>PropertyChangeListeners are not copied to the new Property.  </p>
     */
    public Property clone() {
        try {             
            Property that = (Property)super.clone();
            that.listeners = new Vector<PropertyChangeListener>();
            return that;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }
    
    /** Reset this property to its original value. */
    public abstract void reset();    
}
