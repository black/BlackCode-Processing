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

/**
 * This exception is thrown when an unsupported behave() method is called on on
 * an Action.
 */
/* $Id$ */
public class UnsupportedActionException extends RuntimeException {
    
    static final String REVISION = "$CVSHeader: obx/NextText/src/net/nexttext/behaviour/UnsupportedActionException.java,v 1.1.2.1 2005/04/11 18:09:26 david_bo Exp $";

    public UnsupportedActionException(String message) { super(message); }
}
