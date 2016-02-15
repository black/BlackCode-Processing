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
 
package net.nexttext.behaviour.physics;

import processing.core.PApplet;
import net.nexttext.Book;
import net.nexttext.behaviour.AbstractBehaviour;
import net.nexttext.behaviour.Behaviour;
import net.nexttext.behaviour.control.OnCollision;
import net.nexttext.behaviour.control.OnDrag;
import net.nexttext.behaviour.standard.DoNothing;
import net.nexttext.behaviour.standard.FollowSibling;

/**
 * The factory of Physics behaviours.
 */
/* $Id$ */
public class PhysicsFactory {
    
    /**
     * Collide is a behaviour which performs collision response when TextObjects
     * collide with each other
     */
    public static final AbstractBehaviour collide() {        
	    Behaviour collide;
	    collide = new Behaviour( new OnCollision( new Bounce( 1, 0 )));               
        collide.setDisplayName("Collide");        
        return collide;
    }
    
    /**
     * Keeps objects inside the visible window, bouncing them off when they reach an edge.
     */
    public static AbstractBehaviour stayInWindow(PApplet p) {        
	    Behaviour b;
	    b = new Behaviour(new StayInWindow(p, true));               
        b.setDisplayName("Stay In Window");        
        return b;
    }
    
    /**
     * Explode gives a one time velocity push to objects in a random direction.
     */
    public static final AbstractBehaviour explode() {
        Behaviour b;
        b = new Behaviour( new Explode() );
        b.setDisplayName("Explode");
        return b;
    }
    
    /**
     * Basic move.  Applies Velocity and Acceleration, changing the Position.
     */
    public static final AbstractBehaviour move() {
        Behaviour b;
        b = new Behaviour( new Move() );
        b.setDisplayName("Move");
        return b;
    }
    
    public static final AbstractBehaviour throwable() {
        Approach approach = new Approach(Book.mouse, 1, 1);
        OnDrag   onDrag   = new OnDrag(approach, new DoNothing());
        approach.setTarget(onDrag);
        Behaviour b = new Behaviour(onDrag);
        b.setDisplayName("Throwable");
        return b;
    }
    
    public static AbstractBehaviour follow() {        
        Behaviour b = new Behaviour( new FollowSibling( new Approach( null, 1, 1 ) ) );
        b.setDisplayName("Follow");
        return b;
    }
    
    /**
     * Returns a descriptive name for this factory.
     */
    public String toString() { return "Physics"; }
}
