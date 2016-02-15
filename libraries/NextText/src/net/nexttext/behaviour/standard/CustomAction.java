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

package net.nexttext.behaviour.standard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import processing.core.PApplet;
import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;

/**
 * An action that calls back a function defined in the main Processing sketch.
 */
public class CustomAction extends AbstractAction {
    
    PApplet p = null;
    Integer id = null;
    String methodName = null;
    Method method = null;
  
    /**
     * Constructs a default CustomAction.
     */
    public CustomAction(PApplet p) {
    	this(p, "customAction");
    }
    
    public CustomAction(PApplet p, String meth) {
      this.p = p;
      this.methodName = meth;
      
      Class<?> c = p.getClass();
      try {
        Class[] args = new Class[1];
        args[0] = TextObject.class;
        method = c.getMethod(methodName, args);
      } catch (NoSuchMethodException nsme) {
        p.die("There is no public " + methodName + "() method in the class " + p.getClass().getName());
      } catch (Exception e) {
        p.die("Could not register " + methodName + " + () for " + p, e);
      }
    }
    
    public ActionResult behave(TextObject to) {
      if (method == null)
        return new ActionResult(false, false, false);
        
      try {
        Object[] passedArgs = {to};
        return (ActionResult)method.invoke(p, passedArgs);
      } catch(IllegalAccessException e) {
        p.die("IllegalAccessException " + methodName + " + () for " + p, e);
        return new ActionResult(false, false, false);
      } catch(InvocationTargetException e) {
        p.die("InvocationTargetException " + methodName + " + () for " + p, e);
        return new ActionResult(false, false, false);
      }      
    }
}

