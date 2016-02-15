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

import java.awt.BasicStroke;

/**
 * A stroke property for a TextObject (using the BasicStroke implementation).
 * Allows to draw outlines of glyphs in NextText.
 *
 * <p> The stroke is centered on the control points of a glyph, which means
 * that for larger strokes, the control points will be inside of the stroke. 
 * Thus, large strokes will exceed the bounding polygon. </p>
 */
/* $Id$ */
public class StrokeProperty extends Property {
    
    BasicStroke value;
    BasicStroke original;
    
    boolean isInherited = true;
    
    /**
     * Creates a new StrokeProperty using a stroke of width 1. 
     * The default attributes are a solid line of width 1.0, CAP_BUTT,
     * JOIN_ROUND, which give the best rendering with most glyphs.
     *
     * <p>The stroke property is inherited by default. </p>
     */
    public StrokeProperty() {
        original = value = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND); 
    }
    
    /**
     * Creates a new StrokeProperty from the java.awt.BasicStroke object.
     *
     * <p>The stroke property is not inherited by default. </p>
     */
    public StrokeProperty( BasicStroke stroke ) {
        original = value = stroke;
        setInherited( false );
    }
    
    /**
     * Do not modify the returned value, use set() to make changes instead.
     */
    public BasicStroke get() {         
        return value;
    }
    
    /**
     * Sets the Stroke property to the specified value.  Also sets the Inherited
     * property to false. 
     */
    public void set( BasicStroke newStroke ) {    
        value = newStroke;
        // property change event fired in setInherited
        setInherited( false );
    }
    
    /**
     * Returns the original stroke.
     */
    public BasicStroke getOriginal() {         
        return original;
    }
    
    /**
     * Modifies the original stroke value.
     */
    public void setOriginal( BasicStroke newStroke ) {       
        original = newStroke;
        firePropertyChangeEvent();
    }
    
    /**
     * Setting inherited to true on a StrokeProperty will cause it to bypass
     * it's current stroke value in favor of the stroke value of its parent.
     * By default a StrokeProperty is not inherited.
     */
    public void setInherited( boolean inherited ) {
        this.isInherited = inherited;
        firePropertyChangeEvent();
    }
     
    /**
     * Returns the inherited status of this StrokeProperty.
     */
    public boolean isInherited() {
        return isInherited;
    }
    
    /**
     * Reset interface from superclass.  Resets the stroke to its original value.
     */
    public void reset() {
        value = original;
        firePropertyChangeEvent();
    }
}
