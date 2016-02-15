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

import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;

/**
 * Does nothing to a TextObject.
 */
/* $Id$ */
public class DoNothing extends AbstractAction {
    
    ActionResult result;

    /**
     * Do nothing, returning the default ActionResult(false, false, false).
     */
    public DoNothing() {
        this(false, false, false);
    }

    /**
     * Do nothing, returning an ActionResult constructed with the given values.
     */
    public DoNothing(boolean complete, boolean canComplete, boolean event) {
        this.result = new Action.ActionResult(complete, canComplete, event);
    }

    /**
     * Does nothing to the TextObject.
     */
    public ActionResult behave(TextObject to) {
        return result;
    }
}
