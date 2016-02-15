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

import java.util.HashMap;
import java.util.Map;

import net.nexttext.TextObject;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectGroup;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.Property;

/**
 * An action which logs the TextObjects it processes.
 *
 * <p>This action is useful for debugging, since it can be used to track the
 * order that various actions process objects.  It can be created with an
 * action, which it will call, allowing it to be inserted anywhere in the
 * action tree.  Messages are sent to the Book's log.</p>
 */
/* $Id$ */
public class DebugLog extends AbstractAction {

    Action action = null;
    String prefix;

    public DebugLog(String prefix) {
        this.prefix = prefix;
    }

    public DebugLog(String prefix, Action action) {
        this.prefix = prefix;
        this.action = action;
    }

    // Creates a string representation of the TextObject.
    StringBuffer asString(TextObject to) {
        StringBuffer msg = new StringBuffer();
        if (to instanceof TextObjectGroup) {
            msg.append("[ ");
            TextObject to2 = ((TextObjectGroup)to).getLeftMostChild();
            while (to2 != null) {
                msg.append(to2);
                to2 = to2.getRightSibling();
            }
            msg.append(" ]");
        } if (to instanceof TextObjectGlyph) {
            msg.append(((TextObjectGlyph)to).getGlyph());
        }
        return msg;
    }

    public ActionResult behave(TextObject to) {
        StringBuffer msg = asString(to);
        msg.insert(0,prefix);
        ActionResult res = new ActionResult(true, true, false);
        if (action != null) {
            res = action.behave(to);
            msg.append(" returning (");
            msg.append(res.complete ? "t" : "f");
            msg.append(res.canComplete ? "t" : "f");
            msg.append(res.event ? "t" : "f");
            msg.append(")");
        }
        to.getBook().log(msg.toString());
        return res;
    }

    public Map<String, Property> getRequiredProperties() {
        if (action != null)
            return action.getRequiredProperties();
        else
            return new HashMap<String, Property>(0);
    }

    public void complete(TextObject to) {
        super.complete(to);
        if (action != null) {
            action.complete(to);
        }
    }
}
