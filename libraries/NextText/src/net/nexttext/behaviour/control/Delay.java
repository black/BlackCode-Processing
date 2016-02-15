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
import net.nexttext.property.NumberProperty;

/**
 * Delays a given action for a number of seconds.
 * 
 * @see Timer
 */
/* $Id$ */
public class Delay extends AbstractAction {

    Action action;
    long timeLeft; // for handling clock discrepancies
    
    /**
     * Creates a Delay for the given action.
     * 
     * @param duration In seconds
     */
    public Delay( Action action, float duration ) {
        this.action = action;
        this.timeLeft = 1000*(long)duration;
        properties().init("Duration", new NumberProperty(1000*duration));
    }
    
    /**
     * Applies the delay.
     * 
     * <p>During the delay the ActionResult will set neither event nor
     * complete.  Once the delay is complete, the result of the delayed Action
     * will be returned.  </p>
     */
    public ActionResult behave(TextObject to) {
        
        // get the time elapsed for that object
        Long startTime = (Long)textObjectData.get(to);
        
        // create a map entry for new objects
        if ( startTime == null ) {
            startTime = new Long( System.currentTimeMillis() );   
            textObjectData.put(to, startTime);
        }
        
        // get duration property
        long duration = ((NumberProperty)properties().get("Duration")).getLong();
        
        // It happens that the clock gets out of sync and jumps back in time
        // the result is that the Delay object will wait much longer before
        // getting to the end time.
        //
        // timeLeft keeps track of the time difference between the startTime 
        // and the total duration. When the clock jumps back in time, we reset 
        // the startTime to adjust to the new clock timing and the time left.
        //
        long now = System.currentTimeMillis();
         if (now-startTime.longValue() < 0) {
            startTime = new Long(now + timeLeft - duration);
            textObjectData.put(to, startTime);
        } else {
            timeLeft = startTime.longValue() + duration - now;
        }
        if ( (now-startTime.longValue()) >= duration ) {
            ActionResult res = action.behave(to);
            if (res.complete) {
                complete(to);
            }
            return res;
        }        
        return new ActionResult(false, true, false);
    }
}
