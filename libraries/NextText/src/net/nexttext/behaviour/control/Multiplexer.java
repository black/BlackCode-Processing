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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.Property;

/**
 * The Multiplexer applies a series of Actions in parallel.
 */
/* $Id$ */
public class Multiplexer extends AbstractAction {

    protected List<Action> actions;
    protected HashMap<Action, HashSet<TextObject>> doneWithObject;
    
    /**
     * @param actions a List containing Action objects.
     */
    public Multiplexer( List<Action> actions ) {
        this.actions = actions;
        doneWithObject = new HashMap<Action, HashSet<TextObject>>();

        Action action;
        for (ListIterator<Action> i = actions.listIterator(); i.hasNext(); ) {
        	action = i.next();
        	doneWithObject.put(action, new HashSet<TextObject>());
        }
    }
    
    /**
     * Create a new Multiplexer with no actions.
     */
    public Multiplexer() {
        actions = new ArrayList<Action>();
        doneWithObject = new HashMap<Action, HashSet<TextObject>>();
    }

    /**
     * Add an action to the Multiplexer.
     */
    public void add(Action action) {
        actions.add(action);
        doneWithObject.put(action, new HashSet<TextObject>());
    }

    /**
     * Apply all the actions to the TextObject.
     *
     * <p>The results of the called actions are combined using the method
     * described in ActionResult.  </p>
     */
    public ActionResult behave(TextObject to) {

        ActionResult res = new ActionResult();
                
        for (Iterator<Action> i = actions.iterator(); i.hasNext(); ) {
            Action current = i.next();
            ActionResult tres = null;
            //If textObjects do not have entries in this hashSet
            //then this behaviour has not finished with them            
            if ( doneWithObject.get(current).contains(to) )
                tres = new ActionResult(true,true,false);                     
            else 
                tres = current.behave(to);
                            
            if (tres.complete){
            	doneWithObject.get(current).add(to);                
            }
            res.combine(tres);
            
        }
        res.endCombine();
        // The multiplexer can return complete even if all its actions did not,
        // so those ones need to be informed that it is complete.
        if(res.complete){
            for (Iterator<Action> i = actions.iterator(); i.hasNext(); ) {
                Action current = i.next();
                doneWithObject.get(current).remove(to);
            }
            complete(to);
        }
        return res;
    }
    
    /**
     * Reset the multiplexer.
     */
    public void reset(TextObject to) {
    	for (Iterator<Action> i = actions.iterator(); i.hasNext(); ) {
            Action current = i.next();
            doneWithObject.get(current).remove(to);
        }
    }

    /**
     * End the multiplexer for this object.
     */
    public void complete(TextObject to) {
        super.complete(to);
        for (Iterator<Action> i = actions.iterator(); i.hasNext(); ) {
            i.next().complete(to);
        }
    }

    /**
     * The required properties are the union of all properties in the action
     * chain.
     */
    public Map<String, Property> getRequiredProperties() {
        HashMap<String, Property> rP = new HashMap<String, Property>();
        for ( Iterator<Action> i = actions.iterator(); i.hasNext(); ) {
            rP.putAll( i.next().getRequiredProperties() );
        }
        return rP;
    }
}
