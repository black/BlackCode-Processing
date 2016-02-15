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

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;

import java.util.HashMap;
import java.util.Map;

import processing.core.PVector;

import net.nexttext.property.ColorProperty;
import net.nexttext.property.Property;

/**
 * TextObjectGroup is a TextObject which is made up of other TextObjects.
 *
 * <p>The meaning of the grouping can be defined by the application, but it is
 * typically something like glyphs being grouped into a word.  It is a
 * non-terminal node in the TextObject tree.  It differs from a regular
 * TextObject only in that it has children.  </p>
 */
/* $Id$ */
public class TextObjectGroup extends TextObject {

	/**
	 * Default Constructor.  Creates a new TextObjectGroup at Position (0,0,0)
	 */
	public TextObjectGroup() {
        this(new PVector());
	}
	
	/**
	 * Creates a new TextObjectGroup at the specified position.
	 */
	public TextObjectGroup( PVector pos ) {
        this(new HashMap<String, Property>(0), pos);
	}

	/**
	 * Creates a new TextObjectGroup at the specified position, and with extra
	 * properties.
	 */
	public TextObjectGroup(Map<String, Property> props, PVector pos) {
        super(props, pos);
	}

    TextObject leftMostChild;
    TextObject rightMostChild;
    int numChildren;

    /** Get the left most Child, or null if there isn't one. */
    public TextObject getLeftMostChild() { return leftMostChild; }

    /** Get the right most Child, or null if there isn't one. */
    public TextObject getRightMostChild() { return rightMostChild; }

    /** Get the number of children. */
    public int getNumChildren() { return numChildren; }
    
    /** Get whether the group is empty or not. */
    public boolean isEmpty() { return ((numChildren == 0) || toString().compareTo("") == 0); }

    /**
     * Attach a child to the end of the list of children.
     */
    public void attachChild(TextObject newChild) {
        attachChild(newChild, numChildren +1);
    }

    /**
     * Attach a child at the given location.
     *
     * Location must be between 1 and numChildren + 1, inclusive.  The provided
     * TextObject is attached so it is at that location in the list, counting
     * from the left most child to the right most child.
     */
    public void attachChild(TextObject newChild, int location) {
        if (location < 1 || location > (numChildren + 1)) {
            throw new DataTreeException
                ("Location "+ location +" is out of range: 1-"+ (numChildren+1));
        }
        if (newChild.parent != null) {
            throw new DataTreeException("New sibling is attached elsewhere.");
        }

        // Update the new child, and find its new siblings.
        newChild.parent = this;
        newChild.globalCoordChanged();
        newChild.setBook(this.book);
        if (location == numChildren + 1) {
            newChild.leftSibling = rightMostChild;
            newChild.rightSibling = null;
        } else if (location == 1) {
            newChild.leftSibling = null;
            newChild.rightSibling = leftMostChild;
        } else {
            // Traverse the children to find the correct location.
            newChild.leftSibling = leftMostChild;
            for (int i = location-1; i > 1; i--) {
                newChild.leftSibling = newChild.leftSibling.rightSibling;
            }
            newChild.rightSibling = newChild.leftSibling.rightSibling;
        }

        // Update the siblings
        if (newChild.leftSibling != null) {
            newChild.leftSibling.rightSibling = newChild;
        }
        if (newChild.rightSibling != null) {
            newChild.rightSibling.leftSibling = newChild;
        }

        // Update the parent
        invalidateLocalBoundingPolygon();
        if (location == 1)
            leftMostChild = newChild;
        if (location == numChildren + 1)
            rightMostChild = newChild;
        numChildren++;
    }

    /**
     * When a TextObjectGroup colour flag is modified, its children flags
     * need to be updated according to the new value, and the change needs to be
     * propagated based on the inheritance of ColorProperty.
     * @param newColProp the colour property that was changed
     */
    protected void colourFlagChanged(ColorProperty newColProp) {
        
        String colPropName = newColProp.getName();
        Color newColor = newColProp.get();
        
        // set the value of the current node
        // change the flags only if it has changed
        if (colPropName == "StrokeColor" && 
            newColor.getAlpha()>0 && !stroked || newColor.getAlpha()==0 && stroked) 
            stroked = newColor.getAlpha()>0;
        else if (colPropName =="Color" && 
            newColor.getAlpha()>0 && !filled || newColor.getAlpha()==0 && filled)
            filled = newColor.getAlpha()>0;
        else
            return;
        
        TextObject child = this.leftMostChild;
        // loop through all children to propagate the change
        while (child != null) {
            // If the child TextObject does not inherit its colour from a parent, 
            // we skip it because it has its own colour properties.
            if (((ColorProperty)child.getProperty(colPropName)).isInherited())
                child.colourFlagChanged(newColProp);
            child = child.getRightSibling();
        }
    }
    
    /**
     * Override setBook() to propagate the new book to all its descendants.
     */
    protected void setBook(Book book) {
        super.setBook(book);
        TextObjectIterator i = iterator();
        while (i.hasNext()) {
            i.next().book = book;
        }
    }

    /**
     * Get an iterator over this group and all of its descendants.
     */
    public TextObjectIterator iterator() {
        return new TextObjectIterator(this);
    }
    
    /**
     * Get an iterator over all the descendant glyphs of this group.
     */
    public TextObjectGlyphIterator glyphIterator() {
        return new TextObjectGlyphIterator(this);
    }
    
    /**
     * Get the greatest number of layers between this TextObject and the leaves
     * of the tree.
     */
    public int getHeight() {
        int height = 0;

        TextObject child = leftMostChild;
        while (child != null) {
            int newHeight = 1 + child.getHeight();
            if (newHeight > height)
                height = newHeight;
            child = child.getRightSibling();
        }
        return height;
    }
    
     /**
      * Returns the string representation of a group
      */
     public String getString() {
     	
     	String s = new String();
     	
     	// get a glyph iterator
     	TextObjectGlyphIterator i = this.glyphIterator();
     	while ( i.hasNext() ) {
     		s += i.next().getGlyph();	
     	}

     	return s;
    }

    // When the global coords change the global BPs and Coordinate Systems of
    // children all become invalid.
    protected void globalCoordChanged() {
        super.globalCoordChanged();
        TextObjectIterator i = iterator();
        while (i.hasNext()) {
            TextObject to = i.next();
            to.invalidateGlobalBoundingPolygon();
            to.invalidateGlobalCoordinateSystem();
        }
    }
    
    /** 
     * See TextObject's getLocalBoundingPolygon() description for details.
     *  
     * <p>Do not modify the returned Polygon, because it may be cached. </p>
     *
     * <p>XXXBUG: This method always returns a rectangle unless we write some 
     * convex hull calculations.</p>
     * 
     * @return A bounding polygon; the polygon could be empty if the group has
     * no glyph descendants.
     * 
     * @see net.nexttext.TextObject#getLocalBoundingPolygon()    
     */
    public synchronized Polygon getLocalBoundingPolygon() {

        if (localBoundingPolygonValidToFrame >= getFrameCount()) {
            return localBoundingPolygon;
        }
        localBoundingPolygonValidToFrame = Long.MAX_VALUE;

        TextObject to = getLeftMostChild();
		
		// if to has no children and is a group, then return an empty polygon
        if ( to == null ) {
            localBoundingPolygon = new Polygon();
            return localBoundingPolygon;
		}
		
		Rectangle bounds = to.getRelativeBoundingPolygon().getBounds();
		
		while( (to = to.getRightSibling()) != null ) {
            bounds.add(to.getRelativeBoundingPolygon().getBounds());
		} 
		
        // Return the box as a polygon object  
        
		int[] x = new int[] { bounds.x, bounds.x + bounds.width, bounds.x + bounds.width, bounds.x };
		int[] y = new int[] { bounds.y , bounds.y , bounds.y + bounds.height, bounds.y + bounds.height };
        
        localBoundingPolygon = new Polygon(x, y, 4);
        return localBoundingPolygon;
    }
    
    public String toString() {
        return getString();
    }
}
