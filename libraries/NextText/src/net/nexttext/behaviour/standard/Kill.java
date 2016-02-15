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

/**
 * The Kill action flags an object for removal from the {@link net.nexttext.Book}, causing it 
 * to cease to exist completely. 
 * 
 * <p>Note that the object is not immediately removed for synchronization 
 * reasons.  Rather, it will be eliminated at the end of the current frame. </p>
 * 
 * <p>Killing a {@link net.nexttext.TextObjectGroup} will destroy all of its children as 
 * well. </p>
 */
/* $Id$ */
public class Kill extends AbstractAction {
    
    /**
     * Kills a TextObject.   
     * 
     * <p>See class comments for details on the exact time of death. </p>
     * 
     * @return ActionResult will always be set as complete, although this is
     * really a formality because apply Kill will cause an object to be removed
     * from any Behaviour regardless.
     * 
     * @throws NullPointerException if the object has not been attached to 
     * a Book. 
     */
    public ActionResult behave(TextObject to) {
        // find the TextObject root
        to.getBook().removeObject(to);            
        return new ActionResult(true, true, false);
    }
}
