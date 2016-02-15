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

package net.nexttext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.nexttext.property.Property;

/**
 * The PropertySet encapsulates the named properties of an object.  
 * 
 * <p>The PropertySet is a map from property names to Property objects.  It is
 * similar to java.util.Map, except that once a Property object has been added
 * to the PropertySet that object cannot be removed or replaced, the name will
 * always map to the very same Property object.  Making the Properties static
 * in this way makes it easier to write code which uses Properties.  It allows
 * original Property values to be stored correctly, it reduces the number of
 * checks that have to be done in behaviours, and means that concurrent access
 * is feasible.  </p>
 */
/* $Id$ */
public class PropertySet {
    
    HashMap<String, Property> properties = new HashMap<String, Property>();
    
    /**
     * Initialize the property with this value, if it's not already defined.
     *
     * <p>The provided property is cloned before it is added to the property
     * list.</p>
     */
    public void init(String name, Property value) {
        if (!properties.containsKey(name)) {
            value.setName(name);
            properties.put(name, value.clone());
        }
    }

    /**
     * Initialize all the properties in the map, if not already defined.
     *
     * <p>See initProperty(String, Property).  Map keys must be Strings, values
     * must be Properties.</p>
     */
    public void init(Map<String, Property> properties) {
        // Initialize the required properties on the TextObject.
        Iterator<Map.Entry<String, Property>> i = properties.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Property> e = i.next();
            init(e.getKey(), e.getValue());
        }
    }
    
    /** Get the named property, null if it's not there. */
    public Property get(String name) {
        return (Property) properties.get(name);
    }

    /** Names of all the properties, in an unmodifiable set. */
    public Set<String> getNames() {
        return java.util.Collections.unmodifiableSet(properties.keySet());
    }
    
    /**
     * Resets all the properties to their original value.
     */
    public void reset() {        
        Iterator<String> i = getNames().iterator();
        while (i.hasNext()) {
            get(i.next()).reset();
        }
    }
}
