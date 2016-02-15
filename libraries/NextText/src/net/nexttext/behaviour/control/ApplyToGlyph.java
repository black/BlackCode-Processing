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

import java.util.Map;

import net.nexttext.TextObject;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectGlyphIterator;
import net.nexttext.TextObjectGroup;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.Property;


/**
 * Perform the given action on the TextObject's glyphs.
 *
 * <p>The given action is not performed on the TextObject passed to the behave
 * method, but rather on its glyphs.</p>
 *
 */
/* $Id$ */
public class ApplyToGlyph extends AbstractAction {

    private Action action;
    
    public ApplyToGlyph(Action descendantAction) {
        this.action = descendantAction;
    }

    /**
     * Gets the set of properties required by the descendant Action.
     * @return Map containing the properties
     */
    public Map<String, Property> getRequiredProperties() 
    {
    	Map<String, Property> reqProps = super.getRequiredProperties();
    	reqProps.putAll(action.getRequiredProperties());
        return reqProps;
    }
    
    /**
     * Apply the given action to the TextObject's descendants.
     *
     * <p>The results of the action calls are combined using the method
     * described in Action.ActionResult.  </p>
     */
    public ActionResult behave(TextObject to) {
        if (to instanceof TextObjectGlyph) {
            return action.behave((TextObjectGlyph) to);
        } 
        else {
            ActionResult res = new ActionResult();
            TextObjectGlyphIterator i = ((TextObjectGroup) to).glyphIterator();            
            while (i.hasNext()) {                
                ActionResult tres = action.behave(i.next()); 
                res.combine(tres);
            }
            /*
             * see the ActionResult class for details on how
             * ActionResults are combined.
             */
            res.endCombine();
            if (res.complete){
                action.complete(to);
                complete(to);
            }
            return res;
        }
    }

    /**
     * End this action for this object and end the passed in 
     * action for all its descendants.
     */
    public void complete(TextObject to) {
        super.complete(to);
        if (to instanceof TextObjectGlyph) {
            action.complete(to);
        }
        else{
            TextObjectGlyphIterator i = ((TextObjectGroup) to).glyphIterator();
            while (i.hasNext()) {
                TextObject next = i.next();
                action.complete(next);
            }
        }
    }
}