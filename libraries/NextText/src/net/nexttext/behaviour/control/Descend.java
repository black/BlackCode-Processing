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
import java.util.LinkedList;
import java.util.WeakHashMap;

import net.nexttext.TextObject;
import net.nexttext.TextObjectGroup;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.behaviour.Action;
import net.nexttext.property.Property;

/**
 * Perform the given action on the TextObject's descendants.
 *
 * <p>The given action is not performed on the TextObject passed to the behave
 * method, but rather on its descendants.  The number of levels to descend can
 * be specified upon construction.  If the TextObject has no descendants at the
 * appropriate level, then nothing is done.  </p>
 */
/* $Id$ */
public class Descend extends AbstractAction {

    protected Action descendantAction;
    protected int depth;

    // Behaviours perform a property initialisation on each TextObject added to
    // them.  However, since descendants are not initialised by the Behaviour,
    // this Action has to initialise them itself.  Required properties are only
    // determined once, and stored in descendantReqProps.  Each TextObject only
    // needs to be initialised once, so those that have been initialised are
    // remembered in initedDescendants.
    protected Map<String, Property> descendantReqProps;    
    /* 
     * Using a WeakHashMap here makes this action less prone to memory
     * leaks should the structure of a TextObject being processed by this
     * action changes (e.g. if a descendant is removed after the textObjects has
     * already passed through this action). However it does not offer any protection
     * to the descendantAction. Which may still cause a memory leak if it stores metadata
     * on a textObject that suddenly disappears from the descendants list of the TextObject
     * being processed by descend.
     * 
     */
    protected WeakHashMap<TextObject, Boolean> initedDescendants = new WeakHashMap<TextObject, Boolean>();

    public Descend(Action descendantAction) {
        this(descendantAction, 1);
    }

    /**
     * Construct a new Descend action with the given action and depth.
     *
     * @param depth is a non-negative integer indicating the number of levels
     * to descend in the TextObject hierarchy to get the TextObjects to be
     * acted on.
     */
    public Descend(Action descendantAction, int depth) {
        this.descendantAction = descendantAction;
        this.depth = depth;
        this.descendantReqProps = descendantAction.getRequiredProperties();
    }

    /**
     * Apply the given action to the TextObject's descendants.
     *
     * <p>The results of the action calls are combined using the method
     * described in Action.ActionResult.  </p>
     */
    public ActionResult behave(TextObject to) {

        ActionResult res = new ActionResult();
        LinkedList<TextObject> descendants = getDescendants(to);
        while (!descendants.isEmpty()) {
            TextObject desc = descendants.removeFirst();
            initRequiredProperties(desc);
            res.combine(descendantAction.behave(desc));
        }
        res.endCombine();
        // Descend can return complete even if descendantAction didn't return
        // complete on all the children, so if necessary inform all children
        // that it's complete.
        if (res.complete) complete(to);
        return res;
    }

    /**
     * End this action for this object.
     */
    public void complete(TextObject to) {
        super.complete(to);
        LinkedList<TextObject> descendants = getDescendants(to);
        while (!descendants.isEmpty()) {
            TextObject descendant = descendants.removeFirst();
            descendantAction.complete(descendant);
            initedDescendants.remove(descendant);
        }        
    }

    /**
     * Get the descendants to be acted upon.
     */
    private LinkedList<TextObject> getDescendants(TextObject to) {
        // Loop over depth, creating a new List of objects at each step,
        // populating it with the children of the objects from the previous
        // iteration.
        LinkedList<TextObject> descendants = new LinkedList<TextObject>();
        descendants.add(to);
        for (int i = 0; i < depth; i++) {
            // objects from the last iteration become the parents of the next
            // iteration.
            LinkedList<TextObject> parents = descendants;
            descendants = new LinkedList<TextObject>();
            while (!parents.isEmpty()) {
                TextObjectGroup tog = (TextObjectGroup) parents.removeFirst();
                TextObject child = tog.getLeftMostChild();
                while (child != null) {
                    descendants.add(child);
                    child = child.getRightSibling();
                }
            }
        }
        return descendants;
    }

    /**
     * Initialise the required properties of descendantAction on a child.
     *
     * <p>Since the behaviour does not and cannot initialise the children of
     * the TextObject automatically, it needs to be done each time behave() is
     * called.  </p>
     */
    private void initRequiredProperties(TextObject to) {
        if (initedDescendants.get(to) == null) {
            to.initProperties(descendantReqProps);
            initedDescendants.put(to, true);
        }
    }
}
