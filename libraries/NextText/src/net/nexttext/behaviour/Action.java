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

import java.util.Map;

import net.nexttext.TextObject;
import net.nexttext.property.Property;

/**
 * Actions are building blocks used to create behaviours.
 *
 * <p>Some Actions modify TextObjects in simple ways, typically incrementally
 * over many frames.  Some Actions provide controls for invoking other Actions
 * and do not themselves modify TextObjects directly.  Actions are plugged
 * together and wrapped into a Behaviour, which is then used by the Book.  </p>
 *
 * <p>The way Actions are plugged together, and the fact that they are often
 * interactive, makes it very difficult to predict when or if an Action will be
 * called.  This unpredictability causes difficulty with two aspects of an
 * Action's functioning: the way it stores state in order to maninpulate a
 * TextObject continuously across frames; and knowing if and when an action
 * will be done with a specific TextObject.  This interface defines two
 * mechanisms to help with this problem: a method complete(TextObject) which
 * can be called to let an Action know that it can clean up state, because it
 * will not be called with that TextObject again; and boolean fields in
 * ActionResult to indicate if an Action is complete, or can be expected to
 * complete.  An Action may be called again after completeness is indicated
 * through these mechanisms, which is interpreted as the Action &quot;starting
 * again&quot;.  </p>
 */
/* $Id$ */
public interface Action {
    
    /** Used to communicate results of Action.behave(). */
    public class ActionResult {
        /** Indicate that the action has completed. */
        public boolean complete;
        /** Indicate if the Action can ever complete for this object. */
        public boolean canComplete;
        /** Indicate that an event occured in this step. */
        public boolean event;

        public ActionResult(boolean complete,boolean canComplete,boolean event) {
            this.complete = complete;
            this.canComplete = canComplete;
            this.event = event;
        }

        /**
         * Combine multiple ActionResults into a single one.
         *
         * <p>Sometimes multiple ActionResults need to be combined.  For
         * example Multiplexer calls several actions, but can only return a
         * single result.  Use the default constructor to start the combination
         * process, use this method to process each ActionResult to combine,
         * and use endCombine when they have all been combined.  </p>
         *
         * <p>The resulting ActionResult is set using these rules:  </p>
         *
         * <ul><li>complete: true means that at least one ActionResult had
         * canComplete true, and all those with canComplete true also had
         * complete true.  </li>
         *
         * <li>canComplete: true means that at least one ActionResult had
         * canComplete true.  </li>
         *
         * <li>event: true means at least one Action Result had event true.
         * </li></ul>
         */
        public void combine(ActionResult ar) {
            if (ar.canComplete && !ar.complete) complete = false;
            if (ar.event) event = true;
            if (ar.canComplete) canComplete = true;
        }

        /** Create an action result suitable for use in combine(). */
        public ActionResult() {
            this(true, false, false);
        }

        /**
         * Ends the combining of several ActionResults, so it can be returned.
         *
         * <p>This step is necessary because what is returned depends on
         * whether any of the actions set canComplete.  </p>
         *
         * @return this
         */
        public ActionResult endCombine() {
            if (!canComplete)
                complete = false;
            return this;
        }
    }
    
    /**
     * Performs the action on a TextObject.
     */
    public ActionResult behave(TextObject to);
    
    /**
     * Performs the action on a pair of TextObjects
     */
    public ActionResult behave(TextObject toA, TextObject toB);
    
    /**
     * Performs the action on a set of TextObjects
     */
    public ActionResult behave(TextObject[] to);
    
    /**
     * Inform an Action that it won't need to work on this object any more.
     *
     * <p>This is a forced completion of an action.  When this method is
     * called, an action should clean up any state it is maintaining for the
     * provided TextObject.  The action may be called again with that
     * TextObject, but this case will be considered as starting the action
     * again.  </p>
     *
     * <p>There is no need for a corresponding start() method, since that
     * information is implicitly included in the first call to behave().  </p>
     */
    public void complete(TextObject to);

    /**
     * The properties that this action requires on a TextObject.
     *
     * <p>When the behave() method of an action is called, it will be assumed
     * that these properties have been set on the object.  This initialization
     * is usually handled by the Behaviour containing the action.  </p>
     *
     * <p>Typically an Action inherits the implementation of this method from a
     * base class, such as {@link AbstractAction} or {@link net.nexttext.behaviour.physics.PhysicsAction}.
     * </p>
     */    
    public Map<String, Property> getRequiredProperties();
}
