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

import java.util.LinkedHashSet;
import java.util.Set;

import net.nexttext.TextObject;

/**
* AbstractBehaviours act on a list of TextObjects, and are included in the
* simulation.
*
* <p>The TextObjects to act on are stored in a list internal to the Behaviour.
* To include an AbstractBehaviour in the simulation, it is added to the book,
* which calls the behaveAll() method, triggering the behaviour to call actions
* on each of its TextObjects.  Subclasses of AbstractBehaviour differ in how
* they implement the behaveAll() method. </p>
*/
/* $Id$ */
public abstract class AbstractBehaviour  {
 	 
	 /*
	  * This displayName code is duplicated in AbstractAction, however it is quite
	  * simple so it doesn't justify creating a new baseclass just for this..
	  */
	 private String displayName;
	 
	 /**
	  * Sets the display name of this Action instance to the specified string.   
	  */
	 public void setDisplayName( String name ) {
	     displayName = name;
	 }
	 
	 /**
	  * Returns the display name of this instance.  If no particular display name 
	  * was specified, the class name is used by default.
	  */
	 public String getDisplayName() {
	      
	     if ( displayName == "" ) {
	     	// use the class name if no name was specified
	         displayName = this.getClass().getName();
	     }
	     return displayName;
	 }
	 
	 //////////////////////////////////////////////////////////////////////
	 // TextObjects to act on.
	
	 // Objects are stored in a Set to guarantee uniqueness, and LinkedHash is
	 // nice because it provides a consistent order.  In general performance is
	 // not a major issue, because most accesses are iteration over the list.
	 
	 protected Set<TextObject> objects = new LinkedHashSet<TextObject>();
	 
	 /**
	  * Behave on every TextObject in the list.
	  *
	  * <p>This method is called as part of the simulation, and when a behaviour
	  * has been added to the Book.  Different subclasses will implement this
	  * method in different ways.  </p>
	  */
	 public abstract void behaveAll();
	 
	 /**
	  * Add a TextObject to this Behaviour's list of objects to act on.
	  *
	  * <p>Each Action requires a specific set of properties in TextObjects that
	  * it acts on.  These properties are added to the TextObjects when they are
	  * added to a behaviour for each Action which is a part of that behaviour.
	  * Subclasses of AbstractBehaviour have to be sure to perform this
	  * necessary step, by overriding this method, (but don't forget to call
	  * super.addObject()).  Behaviour provides a good example of how to do
	  * this.  </p>
	  *
	  * <p>If the TextObject is already in the set, nothing will be done, a
	  * single remove will still remove the object from the list.  </p>
	  */
	
	 public synchronized void addObject(TextObject to) {
	     objects.add(to);
	 }
	
	 /**
	  * Stop this behaviour from acting on a TextObject.
	  */    
	 public synchronized void removeObject(TextObject to) {
	     objects.remove(to);
	 }        
	 
	 public String toString() {
	     return getDisplayName();
	 }
}
