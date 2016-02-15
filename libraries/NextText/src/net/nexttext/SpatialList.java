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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
 
/**
 * The SpatialList class is used to keep track of the TextObjects in a spatially
 * organised fashion in order to facilitate proximity and collision queries.
 *
 * <p>At the moment, the list will only contain glyph objects and no groups.  Group
 * behaviour based on proximity will have to be deduced from the collisions 
 * between the different glyphs.</p>
 *
 * <p>The SpatialList is updated once each frame by the Simulator, meaning that
 * as objects move during the behaviours step of the simulation the SpatialList
 * becomes unsorted.  For this reason, queries for possibly colliding objects
 * will not always be completely accurate.  However, this has not proved to be
 * problematic in practice.  </p>
 *
 * <p>The spatial list is maintained using a sweep and prune collision 
 * approximation algorithm which use dynamic AABBs (Axis-Aligned Bounding Boxes)
 * to determine proximity or overlap between two objects.</p>
 *
 * <p>This algorithm is optimised based on the assumption that objects maintain
 * their spatial coherence from frame to frame (ie: object don't travel very far
 * within the span of one frame).  As such, insertion of objects in the 
 * spatial list is costly (because the list has to be resorted), however once 
 * objects have been inserted, maintaining a sorted order is done in nearly 
 * O(n) most of the time.</p>
 *
 * <p>Add description of how to use the class</p>
 *
 * TO DO: Add/Remove function for Groups
 */
/* $Id$ */
public class SpatialList {
	
    static final int LEFT = 0, RIGHT = 1, TOP = 2, BOTTOM = 3;

    // Each object's bounding box is projected on the X and Y axis and the
    // endpoints of the resulting interval are stored in these sorted lists.
    LinkedList<Edge> xAxis = new LinkedList<Edge>();
    LinkedList<Edge> yAxis = new LinkedList<Edge>();

    /**
     * An edge of a TextObject.
     */
    class Edge implements Comparable<Edge> {

        int position;
        TextObjectGlyph to;

        Edge(TextObjectGlyph to, int position) {
            this.to = to;
            this.position = position;
        }

        float getValue() {
            if (position == LEFT)
                return (float)to.getBounds().getMinX();
            if (position == RIGHT)
                return (float)to.getBounds().getMaxX();
            if (position == TOP)
                return (float)to.getBounds().getMinY();
            if (position == BOTTOM)
                return (float)to.getBounds().getMaxY();
            throw new RuntimeException("Invalid Position: " + position);
        }

        public int compareTo(Edge e) {
            return Float.compare(getValue(), e.getValue());
        }
    }

	// Each object has an entry (the key is the object itself) in these data 
	// structure leading to a HashSet of objects it collides with on each axis.
	HashMap<TextObject, HashSet<TextObjectGlyph>> xCollisions = new HashMap<TextObject, HashSet<TextObjectGlyph>>();
	HashMap<TextObject, HashSet<TextObjectGlyph>> yCollisions = new HashMap<TextObject, HashSet<TextObjectGlyph>>();
	
	// These two values are used to maintain an average number of collision
	// tests for each frame.  They are mainly provide statistical information
	// to evaluate the algorithm's performance.
	int tests = 0;
	int avrg = 0;
	
	/**
	 * Sorts the X and Y axis interval lists. 
	 */
	public void update() {
		// sort each Edge list
		sort( xAxis, 0 );
 		sort( yAxis, 1 );
		// calculate stats
		avrg += tests;
		avrg /= 2;
		tests = 0;
	}
	
	/**
     * Get position in the xAxis and yAxis lists for each edge of a TextObject.
     *
     * <p>An array of 4 elements is returned, each of which is an index within
     * the axes (xAxis and yAxis) where the corresponding edge should be
     * inserted.  The returned array is indexed using the static finals 'LEFT',
     * 'RIGHT', 'TOP', and 'BOTTOM'.  </p>
     *
     * <p>The returned indices are determined from the current state of the
     * list.  When both edges have been added to the list in their correct
     * places, the positions of the RIGHT, and BOTTOM edges will be off by one
     * from the returned values, because the LEFT and TOP edges have also been
     * inserted in the lists in a lower position.  </p>
	 */
	private int[] getPosition( TextObjectGlyph to ) {
		int[] position = {0,0,0,0};	// left, right, top, and bottom position
		
		// not an empty list for xAxis
		if (xAxis.size() != 0)
		{
			// binary search for the position
            position[LEFT] = binarySearch(xAxis, (float)to.getBounds().getMinX());
            position[RIGHT] = binarySearch(xAxis, (float)to.getBounds().getMaxX());
		}
		
		// not an empty list for yAxis
		if (yAxis.size() != 0)
		{
            position[TOP] = binarySearch(yAxis, (float)to.getBounds().getMinY());
            position[BOTTOM] = binarySearch(yAxis, (float)to.getBounds().getMaxY());
		}
		
		return position;
	}
	
	/**
     * Determine the index in the provided list where an Edge with the given
     * value should be placed.
	 */
    private int binarySearch(LinkedList<Edge> list, float value) {
		int lower = 0, middle, upper = list.size() - 1;
		
		while ( upper >= lower )
		{
			middle = ( upper + lower ) / 2;
            int result = Float.compare(value, list.get(middle).getValue());
			if ( result > 0 )
				lower = middle + 1;
			else if ( result < 0 )
				upper = middle - 1;
			else return middle;
		}
		
		return lower;
	}
	
	/**
	 * Adds a single TextObjectGlyph to the spatial list
	 */
	public void add( TextObjectGlyph to ) {
		
	    if (to.toString().equals(" ")) {
	        // don't add spaces..
	        return;
	    }
		
        // Add the object's 4 edges to the Axis lists, and calculate its
        // collisions.  In order to maintain the xCollisions and yCollisions
        // data structures, sort() is used to get the edges to the right place
        // in the lists.  One edge from each axis is added to the correct place
        // in the list, the other is added to an end of the list so that the
        // new edges are out of order (eg. RIGHT before LEFT in the list).
        // This guarantees that sort() will be forced to swap edges with all
        // overlapping objects.

		int[] position = getPosition( to );
		
		// left point is closer to the end of the list
		if (position[RIGHT] >= xAxis.size() - position[LEFT]) {
            xAxis.add(position[RIGHT], new Edge(to, RIGHT));
            xAxis.add(new Edge(to, LEFT));
		}
		else {	// right point is closer to the beginning of the list
            xAxis.add(position[LEFT], new Edge(to, LEFT));
            xAxis.addFirst(new Edge(to, RIGHT));
		}
		
		// top point is closer to the end of the list
		if (position[BOTTOM] >= yAxis.size() - position[TOP]) {
            yAxis.add(position[BOTTOM], new Edge(to, BOTTOM));
            yAxis.add(new Edge(to, TOP));
		}
		else {	// bottom point is closer to the end of the list
            yAxis.add(position[TOP], new Edge(to, TOP));
            yAxis.addFirst(new Edge(to, BOTTOM));
		}
		
		// add an entry in each hash table for that object
		xCollisions.put( to, new HashSet<TextObjectGlyph>() );
		yCollisions.put( to, new HashSet<TextObjectGlyph>() );
		
		// re-sort the lists.
		sort( xAxis, 0 );
		sort( yAxis, 1 );
	}
	
	/**
	 * Removes an object from the spatial list
	 */
	public void remove( TextObjectGlyph to ) {

        Iterator<Edge> ei = xAxis.iterator();
        while (ei.hasNext()) { if (ei.next().to == to) ei.remove(); }
        ei = yAxis.iterator();
        while (ei.hasNext()) { if (ei.next().to == to) ei.remove(); }
		
		// see with which other objects this object used to collide
		HashSet<TextObjectGlyph> xcol = xCollisions.get(to);
		HashSet<TextObjectGlyph> ycol = yCollisions.get(to);
		
		// remove all references to this object from any colliding objects
		if ( xcol != null ) {
			for ( Iterator<TextObjectGlyph> i = xcol.iterator(); i.hasNext(); ) {
				HashSet<TextObjectGlyph> temp = xCollisions.get( i.next() );
				temp.remove(to);
			}
		}
		
		if ( ycol != null ) {
			for ( Iterator<TextObjectGlyph> i = ycol.iterator(); i.hasNext(); ) {
			    HashSet<TextObjectGlyph> temp = yCollisions.get( i.next() );
                temp.remove(to);
			}
		}
		
		// finally, remove the object from the collisions lists
		xCollisions.remove(to);
		yCollisions.remove(to);	
	}
	
	/**
	 * Adds all the glyphs part of a TextObjectGroup to the spatial list.
	 */
	public void add( TextObjectGroup tog ) {
		
		TextObjectIterator toi = new TextObjectIterator( tog );
		
		while ( toi.hasNext() ) {
			TextObject to = toi.next();
			if ( to instanceof TextObjectGlyph ) {
				add( (TextObjectGlyph)to );	
			}	
		}
	}
	
	/**
	 * Adds a TextObject to the spatial list.  Use this method to avoid casting
	 * the object as group or a glyph
	 */
	public void add( TextObject to ) {
		if ( to instanceof TextObjectGlyph ) {
			add( (TextObjectGlyph)to );
		}
		if ( to instanceof TextObjectGroup ) {
			add( (TextObjectGroup)to );	
		}	
	}
	
	/**
	 * Removes a TextObject to the spatial list.  Use this method to avoid casting
	 * the object as group or a glyph
	 */
	public void remove( TextObject to ) {
		if ( to instanceof TextObjectGlyph ) {
			remove( (TextObjectGlyph)to );
		}
		if ( to instanceof TextObjectGroup ) {
			remove( (TextObjectGroup)to );	
		}	
	}
	 
	
	/**
	 * Removes all the glyphs part of a TextObjectGroup from the spatial list
	 */
	public void remove( TextObjectGroup tog ) {
		
		TextObjectIterator toi = new TextObjectIterator( tog );
		
		while ( toi.hasNext() ) {
			TextObject to = toi.next();
			if ( to instanceof TextObjectGlyph ) {
				remove( (TextObjectGlyph)to );	
			}	
		}
	}
	
	/**
	 * Redirects to the proper implementation of getPotentialCollisions based
	 * on type (TextObjectGlyph or TextObjectGroup)
	 */
	public HashSet<TextObjectGlyph> getPotentialCollisions( TextObject to ) {
		
		if ( to instanceof TextObjectGlyph ) {
			return getPotentialCollisions( (TextObjectGlyph)to );
		}
		if ( to instanceof TextObjectGroup ) {
			
			return getPotentialCollisions( (TextObjectGroup)to );	
		}
		
		// anything else return null.
		return null;	
	}
	
 	/**
	 * Given a TextObjectGlyph, get a list of objects which's bounding box
	 * are overlapping.  It returns an empty hashset if there is none. 
	 *
	 * @param to  A TextObjectGlyph to test for collisions
	 */
	public HashSet<TextObjectGlyph> getPotentialCollisions( TextObjectGlyph to ) {
		
		// create a new set to store potential collisions
		HashSet<TextObjectGlyph> collisions = new HashSet<TextObjectGlyph>();
		
		HashSet<TextObjectGlyph> xCol = xCollisions.get(to);
		HashSet<TextObjectGlyph> yCol = yCollisions.get(to);
		
		// if the HashSets are non-existent, it means we queried an object which
		// was not part of the spatial list
		if (xCol == null || yCol == null) {
            String msg = "Collisions query for object not in SpatialList: " + to;
			throw new ObjectNotFoundException(msg);
		}
			
		// if the collision sets are empty for either axis, then the object is
		// in the list but cannot be colliding with any other object
		if (xCol.size() == 0 || yCol.size() == 0) 
			return collisions;
		
		// for every element in xCol, see if that element is yCol.  it is, then
		// the boxes are overlapping
		Iterator<TextObjectGlyph> i = xCol.iterator();
		while (i.hasNext()) {	
			TextObjectGlyph someTo = i.next();
			if (yCol.contains(someTo)) {
				collisions.add(someTo);	
			}	
		}	
		
		// otherwise returns the list of object's colliding for this query
		return collisions;
	}
	
	/**
	 * Given a TextObjectGroup, find all the glyphs which's bounding boxes are 
	 * overlapping with any of the given group's glyphs.  Returns an empty set if 
	 * no objects are colliding with the group.
	 */
	public HashSet<TextObjectGlyph> getPotentialCollisions( TextObjectGroup tog ) {
		
		HashSet<TextObjectGlyph> collisions = new HashSet<TextObjectGlyph>();
		
		TextObjectIterator toi = new TextObjectIterator( tog ) ;
		
		while ( toi.hasNext() ) {	
			TextObject to = toi.next();
			if ( to instanceof TextObjectGlyph ) {
				// add all collisions for this glyph to the collider's set
				collisions.addAll( getPotentialCollisions( (TextObjectGlyph)to ) );
			}	
		}
		return collisions;		
	}
		
	/**
	 * Returns the average number of collision tests performed by the sorting
	 * function
	 */
	public int getNumCollisionTests() {
		return avrg;
	}

	///////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	
	/**
	 * Sort objects  in a list using  insertion sort.
	 *
	 * Normally insertion sort has O(n2) running time, however because of 
	 * spatial coherence we can expect the lists to be almost sorted, resulting
	 * in an expected O(n) running time.
	 *
	 * Every time a swap is peformed, update the "overlap" status for the two
	 * objects invovled is updated.
	 * 
	 * @param list a list of edges to sort
	 * @param axis 0-X axis, 1-Y axis
	 */
	
    private void sort(LinkedList<Edge> list, int axis) {
	
		int n = list.size();
		int j = 0;
		for (int i=1; i < n; i++) {
			// check from the first element to the end of the sorted section
			j = i-1;
            Edge a = list.get(i);
		 	// bump down the list until we find the correct place to 
			// insert Edge a
			while( j >= 0 && ( a.compareTo(list.get(j)) < 0 ) ) {	
		 		swap( list, j+1, j, axis );
				j--;
				// ## debug count the number of swaps for each sort
				tests++;
			}				
		}
	}
	
	/**
	 * swaps two elements i and j in the interval list.  updates their overlap 
	 * status to reflect the new changes
	 */
    private void swap(LinkedList<Edge> list, int i, int j, int axis) {
	
	 	// swap elements at i and j
		list.set( i, (list.set(j, list.get(i))) );
		
		// based on the two edges, find out if we should add (or remove) an 
		// overlap for these two objects
		
        TextObjectGlyph glyphA = list.get(i).to;
        TextObjectGlyph glyphB = list.get(j).to;
	 	
	 	// BUGFIX:
	 	// This check was added to prevent detecting an overlap between edges
	 	// belonging to the same object.
	 	// Failing to do this could result in an object colliding with itself,
	 	// and subsequently into concurrent modifications in the xColl and yColl
	 	// HashSets when trying to remove the object from the spatial list.
	 	if ( glyphA.parent == glyphB.parent ) {
	 		// do nothing if the edges belong to the same object.
	 		return;
	 	}
	 	
	 	float s1, e1;   // start-endpoints
	 	float s2, e2;	 
	 	
	 	// 
	 	// Ugly code follows:
	 	//
	 	
	 	// Short of a more elegant solutions, if/else statements are used to
	 	// determine which axis we are sorting on, since different values must
	 	// be taken into account ( left/right edges or top/bottom) as well as
	 	// insertion into different HashMaps (xCollisions/yCollisions).
	 
	 	if (axis == 0) {
            s1 = (float)glyphA.getBounds().getMinX();
            e1 = (float)glyphA.getBounds().getMaxX();
            s2 = (float)glyphB.getBounds().getMinX();
            e2 = (float)glyphB.getBounds().getMaxX();
	 	}
	 	else {
            s1 = (float)glyphA.getBounds().getMinY();
            e1 = (float)glyphA.getBounds().getMaxY();
            s2 = (float)glyphB.getBounds().getMinY();
            e2 = (float)glyphB.getBounds().getMaxY();
	 	}
	 	
		if ( intervalOverlap( s1, e1, s2, e2 ) ) {
			
			if ( axis == 0 ) {			  	
				xCollisions.get( glyphA ).add( glyphB );	
				xCollisions.get( glyphB ).add( glyphA );	
			}
			else {
				yCollisions.get( glyphA ).add( glyphB );	
				yCollisions.get( glyphB ).add( glyphA );	
			}
		}
		else {
			if ( axis == 0 ) {
				xCollisions.get( glyphA ).remove( glyphB );	
				xCollisions.get( glyphB ).remove( glyphA );
			}
			else {
				yCollisions.get( glyphA ).remove( glyphB );	
				yCollisions.get( glyphB ).remove( glyphA );
			}
		}
	}
	
	/**
	 * Determine if intervals [s1, e1] and [s2, e2] overlap or not
	 */
	private boolean intervalOverlap( float s1, float e1, float s2, float e2 ) {
		if ((e2 > s1) && (e1 > s2))	return true;
		return false;
	}
}
