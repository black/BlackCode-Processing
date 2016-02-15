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

package net.nexttext;

import processing.core.PVector;

/**
 * The PLocatableVector encapsulates a PVector to use as a Locatable object.  
 * 
 * <p>The PVector class is part of the Processing core library so we are
 * unable to have it implement the Locatable interface which is used by
 * the behaviours. As a workaround, the PLocatableVector class is used,
 * mainly internally to accept PVectors as argument.</p>
 * 
 * <p>Instead of extending from the PVector class, we store a PVector as
 * variable. This allows to pass a PVector as argument to the constructor
 * and store a pointer to it. This way modifying the PVector outside of
 * the PLocatableVector object will change the value returned by the
 * PLocatableVector object.</p>
 */
public class PLocatableVector implements Locatable {

	PVector vector;
	
	/**
	 * Default constructor -- x, y and z gets assigned the value 0 by default.
	 */
	public PLocatableVector() {
		this(0, 0, 0);
	}

	/**
	 * Constructs and initialize using xy properties ( z = 0 by default ).
	 */
	public PLocatableVector( float x, float y )
	{
		this(x, y, 0);
	}

	/**
	 * Constructs and initialize using xyz properties.
	 */
	public PLocatableVector( float x, float y, float z )
	{
		this.vector = new PVector(x, y, z);
	}
	
	/** Constructs and store pointer to the PVector. */
	public PLocatableVector( PVector vector ) {
		this.vector = vector;
	}
	
    /**
     * A PLocatableVector implements the Locatable interface so that it can be used as a 
     * "location constant".
     */
	public PVector getLocation() {
		return vector.get();
	}

}
