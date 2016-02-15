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

package net.nexttext.behaviour.standard;

import processing.core.PVector;
import net.nexttext.TextObject;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectGroup;
import net.nexttext.behaviour.AbstractAction;

/**
 * 
 * Resets the spacing between children of a textObjectGroup. It 
 * uses the width of the bounding box of the children of that group
 * plus a constant passed in at construction to space the children.
 * 
 * <p>When applied to a word this operation is known as Kerning, it was
 * originally designed to do just this, hence the name. </p>
 *
 */
/* $Id$ */
public class Kern extends AbstractAction {
    
    public float kern;
    
    public Kern(float kern){
        this.kern = kern;
    }
    
    public ActionResult behave(TextObject to) {
        if (to instanceof TextObjectGlyph) {
            return new ActionResult(true, true, false);
        }        
        TextObjectGroup tog = (TextObjectGroup)to;
        TextObject left = tog.getLeftMostChild();
        TextObject right = left.getRightSibling();
        while(right != null){
        	PVector leftBottomLeftCorner = new PVector((float)left.getBounds().getMinX(),(float)left.getBounds().getMaxY());
        	PVector rightBottomLeftCorner = new PVector((float)right.getBounds().getMinX(),(float)right.getBounds().getMaxY());
            //Find the distance between the two glyphs 
            rightBottomLeftCorner.sub(leftBottomLeftCorner);
            //Move the right sibling onto the same position as its left neighbour
            right.getPosition().sub(rightBottomLeftCorner);
            //Add the width of the left sibling plus the kern constant to the position
            //of the right sibling
            /*
             * We should check whether we can do this in one set() rather than a sub() 
             * followed by an add(). I (Yannick) think i tried it and it didnt work but i 
             * am not sure.
             */
            float width = (float)left.getBounds().getWidth() + kern;
            right.getPosition().add(new PVector(width,0));
            //Move onto the next pair
            left = right;            
            right = right.getRightSibling();            
        }
        return new ActionResult(true, true, false);
    }
}
