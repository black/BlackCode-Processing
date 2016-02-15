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

import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;

/**
 * Tracks the TextObjects processed by an action.
 *
 * <p>Tracker can be used to determine how many and which TextObjects are
 * currently being processed.  It is constructed with an action to track, and
 * is used in place of the action being tracked.  <p>
 */
/* $Id$ */
public class Tracker extends AbstractAction {

    Action action;

    /**
     * Construct a Tracker for the given Action.
     */
    public Tracker(Action action) {
        this.action = action;
    }

    /**
     * Pass the TextObject on to the contained Action, tracking the object.
     */
    public ActionResult behave(TextObject to) {

        textObjectData.put(to, null);

        ActionResult res = action.behave(to);
        if (res.complete) {
            complete(to);
        }
        return res;
    }

    /**
     * Get the count of objects currently being processed by the action.
     */
    public int getCount() {
        return textObjectData.size();
    }

    /**
     * Determine if a specific object is being processed by the action.
     */
    public boolean isProcessing(TextObject to) {
        return textObjectData.containsKey(to);
    }
}
