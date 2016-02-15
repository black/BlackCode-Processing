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

package net.nexttext.property;

import java.util.Date;

/**
 * A datetime property of a TextObject or Behaviour.
 */
/* $Id$ */
public class DateTimeProperty extends Property {

    Date original;
    Date value;
  
    public DateTimeProperty() {
        original = new Date();
        value = new Date();
    }

    public DateTimeProperty(Date date) {
        original = new Date(date.getTime());
        value = new Date(date.getTime());
    }

    /**
     * Do not modify the returned value, use set() to make changes instead.
     */
    public Date get() {
        return new Date(value.getTime());
    }

    public Date getOriginal() {
        return new Date(original.getTime());
    }

    public void set(Date date) {         
        value.setTime(date.getTime());
        firePropertyChangeEvent();
    }

    /** Reset this property to its original value. */
    public void reset() {         
        set(getOriginal());
        firePropertyChangeEvent();
    }

    // New Date objects are created in case someone misuses the
    // DateTimeProperty by modifying the internal objects.
    public DateTimeProperty clone() {
        DateTimeProperty that = (DateTimeProperty) super.clone();
        that.original = new Date(original.getTime());
        that.value = new Date(value.getTime());
        return that;
    }
}
