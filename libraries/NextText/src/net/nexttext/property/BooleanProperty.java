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

/**
 * A boolean property type. 
 * 
 */
/* $Id$ */
public class BooleanProperty extends Property {

    private boolean original;
    private boolean value;
    
    public BooleanProperty(boolean value){
        this.original = value;
        this.value = value;
    }
    
    public boolean get(){
        return value;
    }
    
    public boolean getOriginal(){
        return original;        
    }
    
    public void set(boolean value){
        this.value = value;
    }    
        
    public void reset() {
        value = original;
    }

}
