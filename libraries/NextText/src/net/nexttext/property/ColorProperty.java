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

import java.awt.Color;

/**
 * A colour property of a TextObject or a Behaviour.
 */
/* $Id$ */
public class ColorProperty extends Property {
    
    Color value;
    Color original;
    
    boolean isInherited = true;
    
    /**
     * Creates a new ColorProperty using java.awt.Color.black by default.
     */
    public ColorProperty() {
        original = value = Color.black; 
    }
    
    /**
     * Creates a new ColorProperty from the java.awt.Color object.
     * 
     * <p>This color property is no longer inherited by default. </p>
     */
    public ColorProperty( Color color ) {
        original = value = color;
        setInherited( false );
    }
    
    /**
     * Do not modify the returned value, use set() to make changes instead.
     */
    public Color get() {         
        return value;
    }
    
    /**
     * Sets the Color property to the specified value.  Also sets the Inherited
     * property to false. 
     */
    public void set( Color newColor ) {       
        value = newColor;
        // property change event fired in setInherited
        setInherited( false );
    }
    
    public Color getOriginal() {         
        return original;
    }
    
    public void setOriginal( Color newColor ) {       
        original = newColor;
        firePropertyChangeEvent();
    }
    
    /**
     * Setting inherited to true on a ColorProperty will cause it to bypass
     * it's current color value in favor of the color value of it's parent.
     * By default a ColorProperty is inherited.
     */
    public void setInherited( boolean inherited ) {
        this.isInherited = inherited;
        firePropertyChangeEvent();
    }
     
    /**
     * Returns the inherited status of this ColorProperty.
     */
    public boolean isInherited() { return isInherited; }
    
    /**
     * Reset interface from superclass.  Resets the color to its original value.
     */
    public void reset() {
        value = original;
        firePropertyChangeEvent();
    }

    // New Color objects are created in case someone misuses the ColorProperty
    // by modifying the internal objects.
    public ColorProperty clone() {
        ColorProperty that = (ColorProperty) super.clone();
        that.value = new Color(value.getRed(),
                               value.getGreen(),
                               value.getBlue(),
                               value.getAlpha());
        that.original = new Color(original.getRed(),
                                  original.getGreen(),
                                  original.getBlue(),
                                  original.getAlpha());
        return that;
    }
}
