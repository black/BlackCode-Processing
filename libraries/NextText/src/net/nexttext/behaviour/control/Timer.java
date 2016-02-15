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
 * A Timer executes a given action for an absolute period of time given in 
 * seconds.  
 * 
 * @see Delay
 */
/* $Id$ */
public class Timer extends AbstractAction {
    
    Action action;
    long timeLeft; // for handling clock discrepancies
    
    /**
     * Creates a Timer for the given action.
     * 
     * @param duration In seconds  
     */
    public Timer( Action action, float duration ) {
        this.action = action;
        this.timeLeft = (long)(1000*duration);
        properties().init("Duration", new NumberProperty(1000*duration));
    }
    
    public ActionResult behave(TextObject to) {
        
        // get the time elapsed for that object
        Long startTime = (Long) textObjectData.get(to);
        
        // create a map entry for new objects
        if ( startTime == null ) {
            startTime = new Long( System.currentTimeMillis() );   
            textObjectData.put(to, startTime);
        }
        
        // get duration property
        long duration = ((NumberProperty)properties().get("Duration")).getLong();
        
        long now = System.currentTimeMillis();
        
        // It happens that the clock gets out of sync and jumps back in time
        // the result is that the Timer object will wait much longer before
        // getting to the end time.
        //
        // timeLeft keeps track of the time difference between the startTime 
        // and the total duration. When the clock jumps back in time, we reset 
        // the startTime to adjust to the new clock timing and the time left.
        //
        if (now-startTime.longValue() < 0) {
            startTime = new Long(now + timeLeft - duration);
            textObjectData.put(to, startTime);
        } else {
            timeLeft = startTime.longValue() + duration - now;
        }
        if ( (now-startTime.longValue()) >= duration ) {
            // time's up my friend!
            // remove the map entry for this object
            complete(to);
            return new ActionResult(true, true, true);
        }        
        
        ActionResult tres = action.behave(to);
        tres.canComplete = true;
        return tres;
    }

    public Map<String, Property> getRequiredProperties() {
        return action.getRequiredProperties();
    }

    public void complete(TextObject to) {
        super.complete(to);
        action.complete(to);
    }
}