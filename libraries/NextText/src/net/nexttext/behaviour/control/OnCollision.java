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

package net.nexttext.behaviour.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.nexttext.TextObject;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectRoot;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.Property;

/**
 * This control applies an Action when an object overlaps with another object.
 * 
 * <p>Objects affected by a OnCollision action must be added to the SpatialList,
 * otherwise the behave() method will catch an exception. </p>
 */
/* $Id$ */
public class OnCollision extends AbstractAction {
    
    protected Action action;
     
    /**
     * Creates an OnCollision which applies the specified Action to every
     * object that are being collided with.
     */
    public OnCollision( Action action ) {
        this.action = action;
    }
     
    /**
     * For each object this one is colliding with, apply the action to the pair
     * of colliding objects.
     *
     * <p>The results of the called actions are combined using the method
     * outlined in ActionResult.  </p>
     */
    public ActionResult behave(TextObject to) {
        
        // get the glyph collision set for that object.
        Set<TextObjectGlyph> col = to.getBook().getSpatialList().getPotentialCollisions(to);

        if ( col.size() == 0 )
            return new ActionResult(false, false, false);

        // find out what is the depth of descendants of to.
        int height = to.getHeight();

        // It is important to use a set here to prevent duplicates
        HashSet<TextObject> colliders = new HashSet<TextObject>();
                
        // build the set of object whose height matches to's
        for ( Iterator<TextObjectGlyph> i = col.iterator(); i.hasNext(); ) {
                    
            TextObject collider = (TextObject)i.next();
                    
            // from the glyphs, go up to parents a number of times
            // specified by height, or stop if the parent is the 
            // TextRoot.
            TextObject tmp;
            for ( int j=0; j < height; j++ ) {
                tmp = collider.getParent();
                if ( (tmp instanceof TextObjectRoot)) {
                    break;
                } 
                collider = tmp;
            }                    
            colliders.add( collider );
        }

        ActionResult res = new ActionResult();

        for ( Iterator<TextObject> i = colliders.iterator(); i.hasNext(); ) {
            TextObject collider = i.next();
            ActionResult tres = action.behave(new TextObject[] { collider, to });
            res.combine(tres);
        }

        return res.endCombine();
    }

    public Map<String, Property> getRequiredProperties() {
        return action.getRequiredProperties();
    }
}
