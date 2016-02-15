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

package net.nexttext.input;

import java.util.HashMap;

/**
 * A manager for the input sources.
 *
 * <p>The input manager keeps input sources accessible by name.  The application
 *    adds input source objects to the manager and behaviours can fetch these
 *    objects to access their data.  All sources are stored in a
 *    {@link HashMap}.</p>
 *
 */
/* $Id$ */
public class InputManager {

    // The hash sources are kept in.
    HashMap<String, InputSource> sources = new HashMap<String, InputSource>();
    
    /** 
     * Builds an InputManager.
	 *
	 * @param mouse the default Mouse InputSource
	 * @param keyboard the default Keyboard InputSource
	 */
    public InputManager(Mouse mouse, Keyboard keyboard) {
	    add("Mouse", mouse);          	
        add("Keyboard", keyboard);          		 	
	 }
    
    /**
     * Adds an input source to the list.
     *
     * @param	name	key of the new input source
     * @param	source	the new input source object
     */
    public void add(String name, InputSource source) {
        if (!sources.containsKey(name)) {
            sources.put(name, source);
        }
    }
    
    /**
     * Removes an input source from the list.
     *
     * @param	name	key of the input source to remove
     */
    public void remove(String name) {
	   	sources.remove(name);
	}
    
    /**
     * Gets an input source from the list.
     *
     * @param	name	key of the input source to return
     * @return			input source specified by the key parameter.
     *					null if no source is attached to the specified key.
     * @see				InputSource
     */
    public InputSource get(String name) {
    	return sources.get(name);
    }

}
