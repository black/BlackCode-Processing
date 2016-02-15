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

package net.nexttext.behaviour;

import java.util.Iterator;
import java.util.Map;

import net.nexttext.TextObject;
import net.nexttext.property.Property;

/**
 * Basic Behaviour class.  
 * 
 * <p>Behaviours are used by the Book, to apply Actions to a set of
 * TextObjects.  </p>
 * 
 * <p>When the Action indicates that the processing of that object is complete
 * the object is removed from the Behaviour's set of TextObjects.  </p>
 */
/* $Id$ */
public class Behaviour extends AbstractBehaviour {
    
    protected Action action;
    
    /**
     * Creates a Behaviour which will perform the specified action.
     */
    public Behaviour( Action action ) {
        this.action = action;
    }

    /**
     * Calls behave() on every object in its list.
     * 
     * <p>Objects will be removed from the list if the Action completes.  </p>
     */
    public synchronized void behaveAll() {
        for (Iterator<TextObject> i = objects.iterator(); i.hasNext(); ) {
            TextObject to = i.next();
            Action.ActionResult res = action.behave(to);
            if (res.complete) {
                i.remove();
                action.complete(to); //In case the action forgot to call complete itself
            }
        }
    }
    
    public synchronized void addObject( TextObject to ) {
        super.addObject(to);
        Map<String, Property> properties = action.getRequiredProperties();
        to.initProperties( properties );        
    }
        
     /**
      * Stop this behaviour from acting on a TextObject.
      */    
     public synchronized void removeObject(TextObject to) {
         action.complete(to);
         super.removeObject(to);
     }  
}
