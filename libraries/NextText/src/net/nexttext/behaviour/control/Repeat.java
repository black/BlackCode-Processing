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
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.NumberProperty;
import net.nexttext.property.Property;

/**
 * Repeats an action for a fixed number of times, then stops that action.  
 * 
 * <p>The repeat count is incremented whenever the called Action can't complete
 * but returns an event, or when it does complete.  Repeat will return event in
 * the ActionResult when the count is incremented, and complete when it reaches
 * its repeat count.  </p>
 *
 * <p>Repeat will have no effect when used with an action that does not return
 * events or complete.  </p>
 * 
 * XXXBUG: how could we identify a "Countable" object explicitly?
 */
/* $Id$ */
public class Repeat extends AbstractAction {

    Action action;

    /**
     * Repeat an action indefinitely.
     * @param action the action to repeat
     */
    public Repeat ( Action action ) {
    	this(action, 0);
    }
    
    /**
     * Repeat an action a certain amount of repetitions.
     * @param action the action to repeat
     * @param repetitions the number of times to repeat, use 0 to repeat forever
     */
    public Repeat( Action action, int repetitions ) {
        this.action = action;
        properties().init("Repetitions", new NumberProperty(repetitions));
    }

    /**
     * See class description. 
     */
    public ActionResult behave(TextObject to) {
        
        // get the repetition property
        long rep = ((NumberProperty)properties().get("Repetitions")).getLong();
        
        ActionResult tres = action.behave(to);
        if (rep > 0) {
            // get the counter for that object
            Integer counter = (Integer) textObjectData.get(to);

            // if there was no counter for that object create a new one.
            if ( counter == null ) {
                counter = new Integer(1);
            } else {
                // increment the counter
                counter = new Integer(counter.intValue()+1);    
            }
            
            // check if we reached the max number of repetitions
            if (counter.intValue() >= rep) {
                // remove the counter
                textObjectData.remove(to);
                return new ActionResult(true, true, tres.event);
            } else {
                // put the updated counter back
                textObjectData.put(to, counter);
                return new ActionResult(false, true, tres.event);
            }
        }
        // if the Repeat is set to infinite repetitions 
        return new ActionResult(false, false, tres.event);
    }

    public Map<String, Property> getRequiredProperties() {
        return action.getRequiredProperties();
    }
}
