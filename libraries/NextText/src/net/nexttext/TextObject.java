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

import net.nexttext.property.*;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import processing.core.PVector;

/**
 * An object in the core text data.
 *
 * <p>The core data of a NextText application is a bunch of text, with
 * properties to define its behaviour on the screen.  The text and properties
 * are stored in a tree data structure, whose nodes are TextObjects.  The
 * terminal and non-terminal nodes of this tree are instances of two different
 * subclasses of TextObject.  </p>
 *
 * <p>TextObject contains the common facilities of these subclasses, most
 * importantly a list of properties.  There is intentionally no
 * removeProperty() method, so that behaviours can call properties.init() for all
 * the properties that they need when they are attached to an object, and then
 * they do not need to handle the case where getProperty() returns null.  To
 * change the value of a property, modify the returned Property object.  </p>
 *
 * <p>The tree structure is maintained inside the TextObject.  This allows tree
 * traversal and modification to be easily done by several methods in this
 * class.  </p>
 */
/* $Id$ */
public abstract class TextObject implements Locatable {
	
    // The parent node in the TextObject tree.  If parent is null, it means
    // either that this is not part of the hierarchy attached to the book, or
    // it means that this is the root of that hierarchy, is attached directly
    // to the book, and is an instance of TextObjectRoot.
    TextObjectGroup parent;

    // The siblings of this TextObject, either of these may be null.
    TextObject leftSibling;
    TextObject rightSibling;
    
    // A TextObject has two flags to determine if it has a fill colour or a
    // stroke colour. This flag is updated when the colour of either one is
    // changed in order to avoid calling getStrokeColorAbsolute() and 
    // getColorAbsolute() when there is no need to.
    // false -> alpha=0 (transparent colour)
    // true -> alpha>0 (opaque colour)
    boolean stroked;
    boolean filled;
    
    /**
     * Boring constructor with boring default property values.
     */
    protected TextObject() {
        this(new HashMap<String, Property>(0));
    }

    /**
     * Constructor with initial position.
     */
    protected TextObject(PVector pos) {
        this(posToMap(new HashMap<String, Property>(0), pos));
    }

    /**
     * Constructor with initial position and extra properties.
     */
    protected TextObject(Map<String, Property> props, PVector pos) {
        this(posToMap(props, pos));
    }

    /**
     * Stupid function which inserts a position into a property map.  This
     * can't be inlined because constructors can only call other constructors
     * on the first line.
     */

    private static Map<String, Property> posToMap(Map<String, Property> map, PVector pos) {
        Map<String, Property> nMap = new HashMap<String, Property>(map);
        nMap.put("Position", new PVectorProperty( pos ) );
        return nMap;
    }

    /**
     * Constructor which can preset property values.
     *
     * <p>This is useful where the initial defaults are not acceptable.  It is
     * better than modifying the properties after construction because it also
     * sets the Original values of those properties.  </p>
     */
    protected TextObject(Map<String, Property> propertyMap) {
        properties.init(propertyMap);
        properties.init("BirthDateTime", new DateTimeProperty());
        properties.init("Position", new PVectorProperty(new PVector(0,0,0)));
        properties.init("OriginalPosition", new PVectorProperty(new PVector(0,0,0)));
        properties.init("Rotation", new NumberProperty( 0 ));
        properties.init("Color", new ColorProperty() );
        properties.init("Stroke", new StrokeProperty());
        properties.init("StrokeColor", new ColorProperty());
        PropertyChangeListener pcl = new PropertyChangeListener() {
                public void propertyChanged(Property propertyThatChanged) {
                    if (propertyThatChanged == properties.get("Position") ||
                        propertyThatChanged == properties.get("Rotation"))
                        coordChanged();
                    if (propertyThatChanged == properties.get("Color") ||
                        propertyThatChanged == properties.get("StrokeColor"))
                        colourFlagChanged((ColorProperty)propertyThatChanged);
                }
        };

        properties.get("Position").addChangeListener(pcl);
        properties.get("Rotation").addChangeListener(pcl);
        properties.get("Color").addChangeListener(pcl);
        properties.get("StrokeColor").addChangeListener(pcl);
        
        filled = ((ColorProperty)properties.get("Color")).get().getAlpha() > 0;
        stroked = ((ColorProperty)properties.get("StrokeColor")).get().getAlpha() > 0;;
    }

    /** Get the left Sibling, or null if there isn't one. */
    public TextObject getLeftSibling() { return leftSibling; }

    /** Get the right Sibling, or null if there isn't one. */
    public TextObject getRightSibling() { return rightSibling; }

    /** Get the parent, or null if this is the root. */
    public TextObjectGroup getParent() { return parent; }

    /** Get the flag telling if the stroke is activated or not. **/
    public boolean isStroked() { return stroked; }
    
    /** Get the flag telling if the fill colour is activated or not. **/
    public boolean isFilled() { return filled; }
    
    /** Attach the given TextObject to the right of this one. */
    public void attachToRight(TextObject newRightSibling) {
        attach(newRightSibling, true);
    }

    /** Attach the given TextObject to the left of this one. */
    public void attachToLeft(TextObject newLeftSibling) {
        attach(newLeftSibling, false);
    }

    private void attach(TextObject newSibling, boolean rightSide) {
        if (parent == null) {
            throw new DataTreeException("Root cannot have siblings.");
        }

        // Find where the new TextObject should be added.  Child TextObjects
        // are indexed as an array, starting with the left-most child at
        // position 1.  See TextObjectGroup.attachChild() for more details.
        int position = 0;
        for (TextObject c = this; c != null; c = c.leftSibling)
            position++;
        if (rightSide)
            position++;
        parent.attachChild(newSibling, position);
    }
    
    /** Detach this from the TextObjectTree.  It can be reattached elsewhere.*/
    public void detach() {
        if (parent == null) {
            throw new DataTreeException("Root cannot be detached.");
        }

        if (rightSibling != null) {
            rightSibling.leftSibling = leftSibling;
        } else {
            parent.rightMostChild = leftSibling;
        }

        if (leftSibling != null) {
            leftSibling.rightSibling = rightSibling;
        } else {
            parent.leftMostChild = rightSibling;
        }
        
        parent.numChildren--;
        parent.invalidateLocalBoundingPolygon();
        
        parent = null;
        leftSibling = null;
        rightSibling = null;

        setBook(null);
        globalCoordChanged();
    }
	
    /**
     * Get the greatest number of layers between this TextObject and the leaves
     * of the tree.
     *
     * @return 0 if this node is a leaf of the tree, 1 + the greatest depth of
     * its children otherwise.  </p>
     */
    public abstract int getHeight();

    ////////////////////////////////////////
    // Bounding Polygon Calculations

    // For performance reasons, bounding polygon (BP) calculations are
    // performed only on demand, and their results are cached.  The caches are
    // invalidated when TextObjects change shape or position, and when the
    // TextObject hierarchy changes.  Some BPs are calculated using other BPs,
    // creating dependencies between them, and requiring cache invalidations to
    // cascade; the details are described below.  In addition, calculations
    // will not be performed more than once per frame, to prevent excessive
    // work when multiple behaviours change the same TextObject on the same
    // frame.  The minor innaccuracies resulting from this caching scheme have
    // not proved to be a problem in pratice.

    // TextObjects' BPs are calculated in 3 coordinate systems, local
    // coordinates, relative coordinates which is the same as their parent's
    // local coordinates, and global coordinates.

    // The dependencies of BPs are as follows: A TextObject's global BP depends
    // on its relative BP, which in turn depends on its local BP.  A
    // TextObject's relative BP depends on its position and rotation.  A
    // TextObject's global BP depends on all its ancestors' positions and
    // rotations.  A TextObjectGroup's local BP depends on its childrens'
    // relative BPs, while a TextObjectGlyph's local BP depends on its control
    // points.

    // Correctly managing multiple thread access to the BP caches is important,
    // and is primarily handled by limiting recalculations to at most once per
    // frame.  The most naive approach of synchronizing all of the methods will
    // not work because the BP dependencies go both up and down the TextObject
    // hierarchy, thus causing dead-lock.  Instead, BPs are marked to be
    // invalidated on the next frame change, which prevents a BP from becoming
    // invalid at the same time as it is being calculated.  To handle the
    // situation where one thread is calculating BPs at the same time as
    // another one is invalidating them, it's always important to update the
    // valid-to-frame value _before_ fetching any dependant polygons.  Not
    // doing so could result (on the next frame) in the situation where a BP is
    // valid while one of its dependencies is not, especially given that
    // invalidation is a lot faster than recalculation.  The get() methods are
    // synchronized, which saves a small amount of effort in the case where two
    // threads attempt to simultaneously calculate the same BP.

    // Each BP cache consists of a Polygon and a frame count.  The cached
    // Polygon is valid as long as the current frame is not greater than the
    // stored frame count.  If a BP is not valid, an attempt to fetch it will
    // trigger a recalculation.  To invalidate the cache, the frame count is
    // set to the current frame count, meaning it will become invalid on the
    // next frame.

    protected Polygon localBoundingPolygon = null;
    protected long localBoundingPolygonValidToFrame = -1;

    protected Polygon relativeBoundingPolygon = null;
    protected long relativeBoundingPolygonValidToFrame = -1;

    protected Polygon globalBoundingPolygon = null;
    protected long globalBoundingPolygonValidToFrame = -1;

    // If a BP will already be invalid on the next frame, there is no need to
    // invalidate it or those which depend on it.

    public void invalidateLocalBoundingPolygon() {
        if (localBoundingPolygonValidToFrame > getFrameCount()) {
            localBoundingPolygonValidToFrame = getFrameCount()-1;
            invalidateRelativeBoundingPolygon();
        }
    }

    protected void invalidateRelativeBoundingPolygon() {
        if (relativeBoundingPolygonValidToFrame > getFrameCount()) {
            relativeBoundingPolygonValidToFrame = getFrameCount()-1;
            invalidateGlobalBoundingPolygon();
            if (parent != null) {
                parent.invalidateLocalBoundingPolygon();
            }
        }
    }

    protected void invalidateGlobalBoundingPolygon() {
        if (globalBoundingPolygonValidToFrame > getFrameCount()) {
            globalBoundingPolygonValidToFrame = getFrameCount()-1;
        }
    }

    /**
     * Returns the object's bounding polygon in absolute (screen) coordinates.
     * 
     * <p>Do not modify the returned Polygon, because it may be cached. </p>
     *
     * <p>XXXBUG: This method always returns a box-shaped unless we write an algorithm
     * to find the convex hull of a set of points.</p>
     * 
     * @see TextObjectGlyph#getLocalBoundingPolygon
     * @see TextObjectGroup#getLocalBoundingPolygon
     */
    public synchronized Polygon getBoundingPolygon() {

        if (globalBoundingPolygonValidToFrame >= getFrameCount()) {
            return globalBoundingPolygon;
        }
        globalBoundingPolygonValidToFrame = Long.MAX_VALUE;

        // For the root, global and relative BPs are the same.
        if ( parent == null ) {
            globalBoundingPolygon = getRelativeBoundingPolygon();
        } else {
            // Transform the relative bounding polygon to absolute coordinates
            // by applying rotations and translations from the parent systems.

            CoordinateSystem ac = parent.getAbsoluteCoordinateSystem();
            globalBoundingPolygon = ac.transform(getRelativeBoundingPolygon());
        }

        return globalBoundingPolygon;
    }
    
    /**
     * Convenience wrapper around getBoundingPolygon().getBounds().
     */
    public Rectangle getBounds() {
        return getBoundingPolygon().getBounds();
    }
    
    /**
     * Returns the center coordinates of the globalBoundingPolygon
     *
     * @return Vector3 the center point of the globalBoundingPolygon
     */
    public PVector getCenter() {
        int centerX = (int)(getBounds().getX()+getBounds().getWidth()/2);
        int centerY = (int)(getBounds().getY()+getBounds().getHeight()/2);
        
        return new PVector(centerX, centerY, 0);
    }

    /**
     * Returns a convex polygon which encloses all of the object's geometry.
     *
     * <p>Do not modify the returned Polygon, because it may be cached. </p>
     *
     * <p>The returned polygon is in the same coordinate system as the object's
     * position, which is its parent's coordinate system.  <p>
     */
    public synchronized Polygon getRelativeBoundingPolygon() {

        if (relativeBoundingPolygonValidToFrame >= getFrameCount()) {
            return relativeBoundingPolygon;
        }
        relativeBoundingPolygonValidToFrame = Long.MAX_VALUE;
        
        CoordinateSystem lc = getRelativeCoordinateSystem();
        relativeBoundingPolygon = lc.transform(getLocalBoundingPolygon());
             
        return relativeBoundingPolygon;
    }
    
    /**
     * Returns the object's bounding box expressed in local untransformed 
     * coordinates.
     *
     * <p>Do not modify the returned Polygon, because it may be cached. </p>
     */
    public abstract Polygon getLocalBoundingPolygon(); 
    
    // Coordinate systems are cached and stored in the same way as BPs.
    protected CoordinateSystem relativeCoordinateSystem = null;
    protected long relativeCoordinateSystemValidToFrame = -1;

    protected CoordinateSystem globalCoordinateSystem = null;
    protected long globalCoordinateSystemValidToFrame = -1;

    protected void invalidateRelativeCoordinateSystem() {
        if (relativeCoordinateSystemValidToFrame > getFrameCount()) {
            relativeCoordinateSystemValidToFrame = getFrameCount()-1;
            invalidateGlobalCoordinateSystem();
        }
    }

    protected void invalidateGlobalCoordinateSystem() {
        if (globalCoordinateSystemValidToFrame > getFrameCount()) {
            globalCoordinateSystemValidToFrame = getFrameCount()-1;
        }
    }

    /**
     * Get the coordinate system which maps between this object's and its
     * parent's coordinates.
     *
     * <p>A TextObjectGlyph's control points can be transformed out of this
     * coordinate system into its parent's coordinates.  </p>
     */
    public CoordinateSystem getRelativeCoordinateSystem() {
        if (relativeCoordinateSystemValidToFrame < getFrameCount()) {
            relativeCoordinateSystemValidToFrame = Long.MAX_VALUE;
            relativeCoordinateSystem =
                new CoordinateSystem(getPosition().get(), getRotation().get());
        }
        return relativeCoordinateSystem;
    }

    /**
     * Get the coordinate system which maps between this object's and global
     * coordinates.
     *
     * <p>A TextObjectGlyph's control points can be transformed out of this
     * coordinate system into the global (or screen) coordinates.  </p>
     */
    public CoordinateSystem getAbsoluteCoordinateSystem() {
        if (globalCoordinateSystemValidToFrame < getFrameCount()) {
            globalCoordinateSystemValidToFrame = Long.MAX_VALUE;
            if (parent == null)
                globalCoordinateSystem = getRelativeCoordinateSystem();
            else {
                globalCoordinateSystem =
                    new CoordinateSystem(getPosition().get(),
                                         getRotation().get(),
                                         parent.getAbsoluteCoordinateSystem());
            }
        }
        return globalCoordinateSystem;
    }

    // Called when either the position or rotation of the object has changed.
    protected void coordChanged() {
        invalidateRelativeBoundingPolygon();
        invalidateRelativeCoordinateSystem();
        globalCoordChanged();
    }

    // A call to this is triggered when there's a change to this object's
    // global position or rotation, as inherited from its ancestors.
    protected void globalCoordChanged() {
        invalidateGlobalBoundingPolygon();
        invalidateGlobalCoordinateSystem();
    }

    //////////////////////////////////////////////////////////////////////
    // Property manipulation code.
    
    /**
     * The properties of this TextObject.
     *
     * <p>Properties are added to the set using the initProperties() method,
     * and accessed using getProperty().  A property is initialized with a
     * value, which is preserved and called its original value.  </p>
     *
	 * <p>Note that an action's properties are not accessed in the same way as
	 * here.  In AbstractAction the property set is made public.  Consistency
	 * in these two places may benefit us.  </p>
     */
    protected PropertySet properties = new PropertySet();
	
    /**
     * Initialize a bunch of properties.
     */
    public void initProperties( Map<String, Property> propertyMap ) {
        properties.init(propertyMap);
    }
    
    /**
     * Initialize a single of property.
     */
    public void init( String name, Property property ) {
        properties.init(name, property);
    }
    
    /**
     * Get a single of property.
     */
    public Property getProperty( String name ) {
        return properties.get(name);
    }
	 
    /**
     * Get the names of all properties.
     */
    public Set<String> getPropertyNames() { 
        return properties.getNames();
    }
    
    //////////////////////////////////////////////////////////////////////
    // Convenient property getters
    
    /**
     * A getter for the standard "Position" property.  
     */
    public PVectorProperty getPosition() {
    	return (PVectorProperty)(properties.get("Position"));    	
    }
    
    /**
     * A getter for the standard "Color" property.
     */
    public ColorProperty getColor() { 
        return (ColorProperty)(properties.get("Color"));
    }
    
    /**
     * A getter for the standard "StrokeColor" property.
     */
    public ColorProperty getStrokeColor() { 
        return (ColorProperty)(properties.get("StrokeColor"));
    }
    
    /**
     * A getter for the standard "Stroke" property.
     */
    public StrokeProperty getStroke() { 
        return (StrokeProperty)(properties.get("Stroke"));
    }
    
    /**
     * A getter for the standard "Rotation" property.
     */
    public NumberProperty getRotation() { 
        return (NumberProperty)(properties.get("Rotation"));
    }

    
    //////////////////////////////////////////////////////////////////////
    // Colour Property manipulation
    
    /**
     * When the colour of a TextObject changes, we need to check its new
     * alpha value in order to update the flag associated to the colour,
     * either 'stroked' or 'filled'. When a change is done, we need to 
     * recursively propagate it to the children since ColorProperties can be 
     * inherited from parents.
     * 
     * @param colProp the ColorProperty that changed
     */
    protected abstract void colourFlagChanged(ColorProperty colProp);
    
    //////////////////////////////////////////////////////////////////////
    // Read-only viewers
    
    // Storing the book in each TextObject saves tree traversals.
    protected Book book = null;

    /**
     * Get the book whose TextObject hierarchy this object is attached to.
     *
     * @return null if this TextObject is not connected to any book.
     */
    public Book getBook() {
        return book;
    }

    protected synchronized void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the current NextText frame count.
     *
     * @return 0 if this TextObject is not connected to any book.
     */
    protected synchronized long getFrameCount() {
        return (book == null) ? 0 : book.getFrameCount();
    }

    /**
     * The absolute position, rather than the usual relative position.
     * 
     * <p>The absolute position is expressed in terms of screen coordinates.</p>     
     */
    
    public PVector getPositionAbsolute() {              
        return getAbsoluteCoordinateSystem().getOrigin();
    }
    
    /**
     * Returns the absolute color of an Object, inheriting from the parent if 
     * the ColorProperty has been configured as such.
     */
    public Color getColorAbsolute() {
        
        ColorProperty colProp = getColor();
        
        if ( !colProp.isInherited() ) {
            return colProp.get();
        }
        else {
            TextObject parent;
            if ( ( parent = getParent() ) != null ) {                
                return parent.getColorAbsolute();
            }
            else {
                // if the parent is null we can't inherit anything, so 
                // so return the current color
                return colProp.get();
            }
        }
    }
    
    /**
     * Returns the absolute stroke color of an Object, inheriting from the parent if 
     * the ColorProperty has been configured as such.
     */
    public Color getStrokeColorAbsolute() {
        
        ColorProperty stColProp = getStrokeColor();
        
        if ( !stColProp.isInherited() ) {
            return stColProp.get();
        }
        else {
            TextObject parent;
            if ( ( parent = getParent() ) != null ) {                
                return parent.getStrokeColorAbsolute();
            }
            else {
                // if the parent is null we can't inherit anything, so 
                // so return the current stroke color
                return stColProp.get();
            }
        }
    }
    
    /**
     * Returns the absolute stroke of an Object, inheriting from the parent if 
     * the StrokeProperty has been configured as such.
     */
    public BasicStroke getStrokeAbsolute() {
        
        StrokeProperty stProp = getStroke();
        
        if ( !stProp.isInherited() ) {
            return stProp.get();
        }
        else {
            TextObject parent;
            if ( ( parent = getParent() ) != null ) {                
                return parent.getStrokeAbsolute();
            }
            else {
                // if the parent is null we can't inherit anything, so 
                // so return the current stroke color
                return stProp.get();
            }
        }
    }
    
    public abstract String toString();
    
    /**
     * Returns the object's location in absolute coordinates.   
     */
    public PVector getLocation() {
        return getPositionAbsolute();
    }
}
