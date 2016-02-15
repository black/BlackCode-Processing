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

package net.nexttext.behaviour.dform;

import net.nexttext.TextObject;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectGlyphIterator;
import net.nexttext.TextObjectGroup;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.property.PVectorListProperty;

/**
 * A super class for DForms.
 *
 * <p>These are actions which modify the appearance of TextObjectGlyphs. </p>
 *
 * <p>This class provides an implementation of behave() which recursively calls
 * it on all Glyphs.  </p>
 */
/* $Id$ */
public abstract class DForm extends AbstractAction {
    
    /**
     * The control points used to deform a glyph.
     */
    public PVectorListProperty getControlPoints( TextObjectGlyph tog ) {
        return tog.getControlPoints();
    }

    /**
     * DForms generally just make sense on TextObjectGlyphs.
     */
    public abstract ActionResult behave(TextObjectGlyph to);

    /**
     * Default implementation which recursively calls behave on all children.
     *
     * <p>The results of the called actions are combined using the method
     * outlined in ActionResult.  </p>
     */
    public ActionResult behave(TextObject to) {
        if (to instanceof TextObjectGlyph) {
            return behave((TextObjectGlyph) to);
        } else {
            ActionResult result = new ActionResult();
            TextObjectGlyphIterator i = ((TextObjectGroup) to).glyphIterator();
            while (i.hasNext()) {
                ActionResult tres = behave(i.next());
                result.combine(tres);
            }
            return result.endCombine();
        }
    }
}