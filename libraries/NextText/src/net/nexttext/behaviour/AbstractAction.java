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

package net.nexttext.behaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.nexttext.PropertySet;
import net.nexttext.TextObject;
import net.nexttext.property.*;

/**
 * An implementation of {@link Action} with some broadly useful functionality.
 *
 * <p>Abstract Action provides:  </p>
 *
 * <ul><li>Public PropertySet to hold Actions' modifiable properties.  It is
 * public so that components like UIs can tweak the way Actions work.  </li>
 *
 * <li>Public DisplayName, useful for UIs which need to distinguish between
 * multiple instances of the same action.  </li>
 *
 * <li>Protected accessors for core TextObject properties.  These are just
 * short-cut methods to make it easier for subclasses to access standard
 * properties.  Some abstract subclasses (eg. PhysicsAction) provide similar
 * accessors for other TextObject properties.  </li>
 *
 * <li>Default implementations of the Action methods, which subclasses may wish
 * to override.  </li>
 *
 * <li>A protected map from TextObjects to action-specific data.  This is used by
 * subclasses to persist state information across frames about TextObjects
 * currently being processed.  The Action.complete() method will remove state
 * information from this map, for the relevant TextObject.  </li>
 */
/* $Id$ */
public abstract class AbstractAction implements Action {
        
    private String displayName = "";
    protected PropertySet properties = new PropertySet();
    
    /**
     * Sets the display name of this Action instance.
     *
     * <p>Setting the empty string will return it to the default value, which
     * is the name of the Action class.  </p>
     */
    public void setDisplayName( String name ) {
        displayName = name;
    }
    
    /**
     * Returns the display name of this instance.  If no particular display name 
     * was specified, the class name is used by default.
     */
    public String getDisplayName() {
         
        if ( displayName == "" ) {
            // use the class name if no name was specified
            displayName = this.getClass().getName();
        }
        return displayName;
    }
    
	////////////////////////////////////////////////////////////////////////////
    // Action's Properties manipulation
    
    /**
     * Exposes this Action's property set.
     * 
     * @see PropertySet
     */
    public PropertySet properties() {
        return properties;
    }
        
    ////////////////////////////////////////////////////////////////////////////
    // TextObject Property accessors
    
    /**
     * Returns a TextObject's Position property.  
     */
    protected PVectorProperty getPosition(TextObject to) {
        // we assume Position exists on to since it's a core property
        return (PVectorProperty)to.getProperty("Position");
    }
    
    /**
     * Returns a TextObject's Rotation property.  
     */
    protected NumberProperty getRotation(TextObject to) {
        // we assume Rotation exists on to since it's a core property
        return (NumberProperty) to.getProperty("Rotation");
    }
    
    /**
     * Returns a TextObject's Color property.       
     */
    protected ColorProperty getColor(TextObject to) {
        return (ColorProperty)to.getProperty("Color");
    }
    
	////////////////////////////////////////////////////////////////////////////
    // TextObject Data Store

    /**
     * Map from TextObjects to action-specific data.
     *
     * <p>Provided for subclasses to persist information about TextObjects
     * between frames. A WeakHashMap is used to help prevent memory leaks;
     * data on textObjects should not persist beyond the life of the textObject
     * and should not prevent a textObject from being garbage collected. </p>
     */
    protected Map<TextObject, Object> textObjectData = new WeakHashMap<TextObject, Object>();

    ////////////////////////////////////////////////////////////////////////////
    // Action interface
    
    /**
     * Default behave method for single objects.  
     * 
     * <p>This method does nothing; it should be overriden by subclasses if 
     * they wish to support actions on single objects. </p>
     * 
     * <p>Trying to call this method on an Action which doesn't override it
     * will throw an UnsupportedActionException. </p>
     * 
     * @throws UnsupportedActionException
     */
    public ActionResult behave(TextObject to) {
        throw new UnsupportedActionException("This action doesn't support single objects");
    }
     
    /**
     * Default behave method for object pairs.
     * 
     * <p>This method does nothing; it should be overriden by subclasses if 
     * they wish to support actions on pairs of objects. </p>
     * 
     * <p>Trying to call this method on an Action which doesn't override it
     * will throw an UnsupportedActionException. </p>
     * 
     * @throws UnsupportedActionException
     */
    public ActionResult behave(TextObject toA, TextObject toB) {
        throw new UnsupportedActionException("This action doesn't support pairs of objects");
    }
     
    /**
     * Default behave method for object arrays.
     * 
     * It will redirect to the proper behave method if the array is of size 
     * 1 or 2, otherwise it will throw an exception.
     * 
     * @throws UnsupportedActionException  
     */
    public ActionResult behave(TextObject[] to) {
        
        if ( to.length == 1 ) return behave( to[0] ); 
        if ( to.length == 2 ) return behave( to[0], to[1] );
        
        throw new UnsupportedActionException("This action doesn't support arrays of objects"); 
    }
    
    /**
     * Default implementation which removes state information for the object.
     */
    public void complete(TextObject to) {
        textObjectData.remove(to);
    }

    /**
     * The properties that this action requires on a TextObject.
     * 
     * <p>Right now this method always returns an empty map, since basic
     * Actions should be restricted to use only the core set of TextObject
     * properties. </p>
     * 
     * <p>If a subclass of Action requires a set of additional properties
     * then it should override this method and return the appropriate
     * Map containing the property objects. </p>
     * 
     * <p>This method is required by the {@link Action} interface. </p>
     */
    public Map<String, Property> getRequiredProperties() {
    	return new HashMap<String, Property>(0);
    }
    
    ///////////////////////////////////////////
    // Utility methods
    
    /**
     * Creates a new Behaviour and wraps this action in it.
     * 
     * @return the created Behaviour
     */
    public Behaviour makeBehaviour() {
    	return new Behaviour(this);
    }
}
