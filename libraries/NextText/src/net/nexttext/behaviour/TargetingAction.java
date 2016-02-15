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

import processing.core.PVector;
import net.nexttext.Locatable;
import net.nexttext.PLocatableVector;

/**
 * A TargetingAction is an Action that uses a target.
 *
 * <p>A common type of TargetingAction is one which moves its object to the
 * target.  The purpose of this class is to make it easy to switch around
 * targeted movement in behaviours, by using different TargetingActions.  </p>
 */
/* $Id$ */
public interface TargetingAction extends Action {

    /**
     * Set a new target for this action.
     */
    public void setTarget( Locatable target );
    
    /**
     * Set a new target for this action.
     */
    public void setTarget( float x, float y );
    
    /**
     * Set a new target for this action.
     */
    public void setTarget( float x, float y, float z );
    
    /**
     * Set a new target for this action.
     */
    public void setTarget( PVector target );
}
