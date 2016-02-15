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

import java.util.NoSuchElementException;

/**
 * An iterator over the events of a specifed input source.
 *
 * <p>The iterator keeps the array index of the last event it fetched.  When 
 * new events are added to the list, the iterator can fetch the new events, 
 * one by one, until the last one is reached.</p>
 */
/* $Id$ */
public class InputSourceIterator {

	// The event list of the iterator.
	InputSource source;
	
	// The last index fetched by the iterator.
	// -1 when the iterator never fetched from the list.
	int lastFetchedEventIndex = -1;

	/**
	 * Class constructor.
	 *
	 * @param	source		the input source to iterator over
	 */
	InputSourceIterator(InputSource source) {
		this.source = source;
	}

	/**
	 * If there is an event waiting.
	 *
	 * <p>Even if it returns false, it may return true later, if a new event
	 * has occurred.  This is different behaviour than java.util.Iterator.  <p>
	 */
	public boolean hasNext() {
        synchronized (source.events) {
            return ((source.latestEventIndex != -1) &&
                    (lastFetchedEventIndex != source.latestEventIndex));
        }
	}
	
	/**
	 * Returns the next object in the iteration
	 *
     * @throws NoSuchElementException if there's no element available.
	 */
	public InputEvent next() {
        synchronized (source.events) {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");

            lastFetchedEventIndex = source.incrementIndex(lastFetchedEventIndex);
            return source.events[lastFetchedEventIndex];
        }
	}
}
