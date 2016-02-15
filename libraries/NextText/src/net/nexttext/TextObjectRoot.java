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

/**
 * The TextObjectRoot is just like a regular {@link TextObjectGroup} except that 
 * it contains a reference to the {@link Book} it belongs too. 
 * 
 * <p>As such, one can get to the Book from any {@link TextObject} by moving up 
 * the hierarchy until the instance of TextObjectRoot is found. </p>
 */
/* $Id$ */
public class TextObjectRoot extends TextObjectGroup {
    
    /**
     * The constructor has private access so the only way to obtain a
     * TextObjectRoot is through the package-only method create()
     */
    TextObjectRoot( Book book ) {
        super();
        this.book = book;
    }
}
