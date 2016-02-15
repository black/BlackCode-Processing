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

import java.util.Stack;

/**
 * A utility class used to traverse the children of a TextObjectGroup.
 *
 * <p>The traversal is done depth-first, meaning that all of a node's children
 * will be traversed before that node is.  This behaviour is necessary for the
 * Simulator to update bounding boxes correctly.  If you find a need for a
 * bread-first traversal, then you should make it an option in this class. </p>
 *
 * <p>The node itself will be returned as part of the traversal. </p>
 */
/* $Id$ */
public class TextObjectIterator {

    // The state of the iteration is maintained in a stack, which has the next
    // node on top, with all of its ancestors to the top of them traversal
    // above it.  Getting the next node means returning the top of the stack,
    // then finding the next node to traverse and pushing it, and any
    // appropriate ancestors on top of the stack.

	Stack<TextObject> ancestors = new Stack<TextObject>();

	/** Construct an iterator over the group and its descendants. */
	TextObjectIterator( TextObjectGroup group ) {
        descend(group);
	}

    // Push the provide TextObject, and all of its left descendants onto the
    // stack.  This causes the traversal to start at the bottom.
    private void descend(TextObject to) {
        while (to != null) {
            ancestors.push(to);
            if (to instanceof TextObjectGroup) {
                to = ((TextObjectGroup) to).getLeftMostChild();
            } else {
                to = null;
            }
        }
    }

	/** If the traversal is complete. */
	public boolean hasNext() {
		return !ancestors.empty();
	}

	/** Get the next node in the traversal. */
	public TextObject next() {

		TextObject current = (TextObject) ancestors.pop();

        // Put the next object on the stack.  If we're returning the object
        // originally provided (the stack is empty), then there's nothing left
        // to traverse, so don't push anything onto the stack.  If there's no
        // right sibling, then the next object is the parent, which is already
        // on the stack.
		if ( (!ancestors.empty()) && (current.getRightSibling() != null) ) {
            descend(current.getRightSibling());
		}

	 	return current;
	}
}
