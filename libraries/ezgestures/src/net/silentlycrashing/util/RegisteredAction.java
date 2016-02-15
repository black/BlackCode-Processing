/*
  This file is part of the ezjlibs project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezjlibs is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezjlibs is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezjlibs.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.util;

import java.lang.reflect.*;
import processing.core.*;

/**
 * An action that can be saved and then invoked by the code.
 */
/* $Id: RegisteredAction.java 29 2008-03-13 13:06:35Z prisonerjohn $ */
public class RegisteredAction {
    Object registeredObject;
    Method registeredMethod;
    
    /**
     * Builds a RegisteredAction without Method arguments.
     * 
     * @param name the name of the method to register
     * @param o the Object holding the method
     */
    public RegisteredAction(String name, Object o) {
        Class c = o.getClass();
        try {
            Method method = c.getMethod(name, new Class[]{});
            link(o, method);
        } catch (Exception e) {
            PApplet.println("Could not register "+name+"() for "+o);
            e.printStackTrace();
        }
    }
    
    /**
     * Builds a RegisteredAction with Method arguments.
     * 
     * @param name the name of the method to register
     * @param o the Object holding the method
     * @param cargs the arguments
     */
    public RegisteredAction(String name, Object o, Class cargs[]) {
        Class c = o.getClass();
        try {
            Method method = c.getMethod(name, cargs);
            link(o, method);
        } catch (Exception e) {
            PApplet.println("Could not register "+name+"() for "+o);
            e.printStackTrace();
        }
    }
    
    /**
     * Links the passed Object and Method to this RegisteredAction.
     * 
     * @param o the Object to link
     * @param m the Method to link
     */
    public void link(Object o, Method m) {
        registeredObject = o;
        registeredMethod = m;
    }

    /**
     * Invokes the RegisteredAction without arguments.
     */
    public void invoke() {
        invoke(new Object[] {});
    }
    
    /**
     * Invokes the RegisteredAction with arguments.
     * 
     * @param oargs the arguments as an Object array
     */
    public void invoke(Object oargs[]) {
        try {
            registeredMethod.invoke(registeredObject, oargs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
