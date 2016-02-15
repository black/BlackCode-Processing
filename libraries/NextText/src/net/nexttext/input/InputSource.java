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

/**
 * An interface to external events.
 *
 * <p>InputSources are generally used by Behaviours which want to change what
 * they do based on external information.  An InputSource can be accessed in
 * two ways, as a series of events, or as an object with state.  To access
 * events, a Behaviour gets an InputSourceIterator from the input source, and
 * reads events from it.  To access state, a Behaviour uses InputSource
 * specific state accessors.  </p>
 *
 * <p>Events are buffered internally in the InputSource, so that all Behaviours
 * will see all events.  If a Behaviour is slow in accessing events, they may
 * be flushed from the buffer, and it will miss events.  </p>
 */
/* $Id$ */
public abstract class InputSource {

    // Events are stored internally in an array, with a pointer to the most
    // recent event.  Events are added to the array, and then the pointer is
    // incremented, or wrapped as appropriate.

	InputEvent[] events = new InputEvent[1024];

	// The array index of the latest event generated (added) to the list.
	// -1 for an empty list
	int latestEventIndex = -1;

    /** Used in the iterator also. */
    int incrementIndex(int oldIndex) { return (oldIndex + 1) % events.length; }

	/**
	 * Adds an event object to the list.
	 *
	 * @param	event	the event object to insert
	 */
	protected void addEvent(InputEvent event) {
        synchronized (events) {
            latestEventIndex = incrementIndex(latestEventIndex);
            events[latestEventIndex] = event;
        }
	}

	/**
	 * Gets an iterator over the list of events of the input source.
	 *
	 * @return		an iterator over the list of events
	 */
	public InputSourceIterator getIterator() {
		return new InputSourceIterator(this);
	}

}
