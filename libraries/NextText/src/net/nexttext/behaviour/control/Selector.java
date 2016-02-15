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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.Property;

/**
 * This action maintains a collection of action (in form of a HashMap)
 * in which any one of the contained actions can be set to act as the
 * current action.
 * 
 * <p>TextObjects are passed on to (and only to) the current action. 
 * This allows for the creation of behaviours with state, i.e. behaviours 
 * that display different dynamics at different times.
 * 
 * For each action contained in the selector a collection of textObjects that 
 * the action has finished processing is maintained. This maintains the 'completed'
 * semantics of contained actions but allows textObjects to continually be acted upon
 * when the current action changes.
 * </p>
 */
/* $Id$ */
public class Selector extends AbstractAction {
    
    HashMap<String, Action> actions;
    Action current;          
    HashMap<Action, HashMap<TextObject, Boolean>> actionDoneProcessing;             
    
    public Selector (){
        actions = new HashMap<String, Action>();
        actionDoneProcessing = new HashMap<Action, HashMap<TextObject, Boolean>>(); 
    }
    
    /**
     * If [name] matches the name of a previously added
     * action then that action will be made the current action
     * 
     * @param name - the name of the action to be selected.
     */
    public synchronized void select(String name){
        current = actions.get(name);
        if(current == null)
            throw new NullPointerException("This selector does not contain the action: " + name);
              
    }
    
    /**
     * Add an action to the selector
     * @param name
     * @param action
     */
    public void add(String name, Action action){
        actions.put(name, action);
        actionDoneProcessing.put(action, new HashMap<TextObject, Boolean>());
    }
    
    /**
     * Applies the current action
     * 
     * @return ActionResult(false, false, true) if the current action
     * completes or signals an event. Else returns ActionResult(false, false, false)
     * 
     */
    public ActionResult behave(TextObject to) {
       //Check if the current action previously indicated that it was finished 
       //with this textObject
       HashMap<TextObject, Boolean> doneWith = actionDoneProcessing.get(current);
       Boolean completed = (Boolean)doneWith.get(to);
       if (completed != null && completed.booleanValue() == true){
           return new ActionResult(false, false, false);
       }
       else {
           ActionResult currResult = current.behave(to);
           /*
            * If the current action returns complete, we do not
            * want the selector to return complete, because the
            * current action may change.
            * 
            * Instead we signal that an event has occurred.
            */ 
           if(currResult.complete){
               current.complete(to);
               doneWith.put(to, new Boolean(true));
               return new ActionResult(false, false, true);
           }           
           else
              return new ActionResult(false, false, false);
       }
    }
    
    public Object[] getActionNames(){
        return actions.keySet().toArray();
    }

    public Map<String, Property> getRequiredProperties() {
        Map<String, Property> props = new HashMap<String, Property>();
        Collection<Action> actionList = actions.values();
        
        for (Iterator<Action> i = actionList.iterator(); i.hasNext(); ) {
            Action a = i.next();
            props.putAll(a.getRequiredProperties());
        }        
        return props;
    }
}
