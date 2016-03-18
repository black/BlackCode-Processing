/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.ext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.util.*;

/**
 * A {@link remixlab.bias.core.Grabber} extension which allows to define
 * {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method} bindings. See
 * {@link #setBinding(Shortcut, String)} and {@link #setBinding(Object, Shortcut, String)}
 * .
 * <p>
 * To attach a profile to a grabber first override your
 * {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)} method like this:
 * 
 * <pre>
 * {@code
 *   public void performInteraction(BogusEvent event) {
 *     profile.handle(event);
 *   }
 * }
 * </pre>
 * 
 * (see {@link #handle(BogusEvent)}) and then simply pass the grabber instance to the
 * {@link #Profile(Grabber)} constructor.
 */
public class Profile {
  class ObjectMethodTuple {
    Object object;
    Method method;

    ObjectMethodTuple(Object o, Method m) {
      object = o;
      method = m;
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(map).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    Profile other = (Profile) obj;
    return new EqualsBuilder().append(map, other.map).isEquals();
  }

  protected static HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
  protected HashMap<Shortcut, ObjectMethodTuple> map;
  protected Grabber grabber;

  /**
   * Attaches a profile to the given grabber.
   */
  public Profile(Grabber g) {
    map = new HashMap<Shortcut, ObjectMethodTuple>();
    grabber = g;
  }

  /**
   * Registers a {@link remixlab.bias.event.MotionEvent#id()} to the Profile.
   * 
   * @see #registerMotionID(int)
   * 
   * @param id
   *          the intended {@link remixlab.bias.event.MotionEvent#id()} to be registered
   * @param dof
   *          Motion id degrees-of-freedom.. Either 1,2,3, or 6.
   * @return the id or an exception if the id exists.
   */
  public static int registerMotionID(int id, int dof) {
    if (idMap.containsKey(id))
      throw new RuntimeException(
          "Nothing done! id already present in Profile. Call Profile.unregisterMotionID first or use an id different than: "
              + (new ArrayList<Integer>(idMap.keySet())).toString());
    if (dof == 1 || dof == 2 || dof == 3 || dof == 6) {
      idMap.put(id, dof);
      return id;
    } else
      throw new RuntimeException("Nothing done! dofs in Profile.registerMotionID should be either 1, 2, 3 or 6.");
  }

  /**
   * Registers a {@link remixlab.bias.event.MotionEvent#id()} to the Profile.
   * 
   * @see #registerMotionID(int, int)
   * 
   * @param dof
   *          Motion id degrees-of-freedom.. Either 1,2,3, or 6.
   * @return the id.
   */
  public static int registerMotionID(int dof) {
    if (dof != 1 && dof != 2 && dof != 3 && dof != 6)
      throw new RuntimeException(
          "Warning: Nothing done! dofs in Profile.registerMotionID should be either 1, 2, 3 or 6.");
    ArrayList<Integer> ids = new ArrayList<Integer>(idMap.keySet());
    int key = 1;
    if (ids.size() > 0)
      key = Collections.max(ids) + 1;
    idMap.put(key, dof);
    return key;
  }

  /**
   * Instantiates this profile from another profile. Both Profile {@link #grabber()}
   * should be of the same type.
   */
  public void from(Profile p) {
    if (grabber.getClass() != p.grabber.getClass()) {
      System.err.println("Profile grabbers should be of the same type");
      return;
    }
    map = new HashMap<Shortcut, ObjectMethodTuple>();
    for (Map.Entry<Shortcut, ObjectMethodTuple> entry : p.actionMap().entrySet()) {
      if (entry.getValue().object == p.grabber)
        map.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
      else
        map.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
    }
  }

  /**
   * Returns the grabber to which this profile is attached.
   */
  public Grabber grabber() {
    return grabber;
  }

  /**
   * Internal use. Shortcut to action map.
   */
  protected HashMap<Shortcut, ObjectMethodTuple> actionMap() {
    return map;
  }

  /**
   * Returns the {@link java.lang.reflect.Method} binding for the given
   * {@link remixlab.bias.core.Shortcut} key.
   * 
   * @see #action(Shortcut)
   */
  public Method method(Shortcut key) {
    return map.get(key) == null ? null : map.get(key).method;
  }

  /**
   * Returns the {@link java.lang.reflect.Method} binding for the given
   * {@link remixlab.bias.core.Shortcut} key.
   * 
   * @see #method(Shortcut)
   */
  public String action(Shortcut key) {
    Method m = method(key);
    if (m == null)
      return null;
    return m.getName();
  }

  /**
   * Internal macro. Returns the action performing object. Either the {@link #grabber()}
   * or an external object.
   */
  protected Object object(Shortcut key) {
    return map.get(key) == null ? null : map.get(key).object;
  }

  /**
   * Internal macro. Sort of a shortcut to event reverse mapping. Override this method if
   * you intend to implement your own event class.
   */
  protected Class<?> cls(Shortcut key) {
    Class<?> eventClass = BogusEvent.class;
    if (key instanceof KeyboardShortcut)
      eventClass = KeyboardEvent.class;
    else if (key instanceof ClickShortcut)
      eventClass = ClickEvent.class;
    else if (key instanceof MotionShortcut) {
      switch (idMap.get(key.id())) {
      case 1:
        eventClass = DOF1Event.class;
        break;
      case 2:
        eventClass = DOF2Event.class;
        break;
      case 3:
        eventClass = DOF3Event.class;
        break;
      case 6:
        eventClass = DOF2Event.class;
        break;
      }
    }
    return eventClass;
  }

  /**
   * Main class method to be called from
   * {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)}. Calls an action
   * handler if the {@link remixlab.bias.core.BogusEvent#shortcut()} is bound.
   * 
   * @see #setBinding(Shortcut, String)
   * @see #setBinding(Object, Shortcut, String)
   */
  public boolean handle(BogusEvent event) {
    Method iHandlerMethod = method(event.shortcut());
    if (iHandlerMethod != null) {
      try {
        if (object(event.shortcut()) == grabber)
          iHandlerMethod.invoke(object(event.shortcut()), new Object[] { event });
        else
          iHandlerMethod.invoke(object(event.shortcut()), new Object[] { grabber, event });
        return true;
      } catch (Exception e) {
        try {
          if (object(event.shortcut()) == grabber)
            iHandlerMethod.invoke(object(event.shortcut()), new Object[] {});
          else
            iHandlerMethod.invoke(object(event.shortcut()), new Object[] { grabber });
          return true;
        } catch (Exception empty) {
          System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
          empty.printStackTrace();
        }
        System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Internal macro.
   */
  protected boolean printWarning(Shortcut key, String action) {
    if (action == null) {
      this.removeBinding(key);
      System.out.println(key.description() + " removed");
      return true;
    }
    if (hasBinding(key)) {
      Method a = method(key);
      if (a.getName().equals(action)) {
        System.out.println("Warning: shortcut already bound to " + a.getName());
        return true;
      } else {
        System.out.println("Warning: overwritting shortcut which was previously bound to " + a.getName());
        return false;
      }
    }
    return false;
  }

  /**
   * Defines the shortcut that triggers the given action.
   * <p>
   * The action is a method implemented by the {@link #grabber()} that returns void and
   * may have a {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all.
   * A {@link remixlab.bias.event.MotionEvent} or a <b>DOFnEvent()</b> that matches the
   * {@link remixlab.bias.core.Shortcut#id()} dofs may be passed to the action when
   * binding a {@link remixlab.bias.event.MotionShortcut}. A
   * {@link remixlab.bias.event.KeyboardEvent} and a
   * {@link remixlab.bias.event.ClickEvent} should always be passed to the action when
   * binding a {@link remixlab.bias.event.KeyboardShortcut} and a
   * {@link remixlab.bias.event.ClickShortcut}, respectively.
   * 
   * @param key
   *          {@link remixlab.bias.core.Shortcut}
   * @param action
   *          {@link java.lang.String}
   * 
   * @see #setBinding(Object, Shortcut, String)
   */
  public void setBinding(Shortcut key, String action) {
    if (printWarning(key, action))
      return;
    Method method = null;
    String message = "Check that the " + grabber().getClass().getSimpleName() + "." + action
        + " method exists, is public and returns void, and that it takes no parameters or a "
        + ((key instanceof MotionShortcut) ? cls(key).getSimpleName() + " or MotionEvent" : cls(key).getSimpleName())
        + " parameter";
    try {
      method = grabber.getClass().getMethod(action, new Class<?>[] { cls(key) });
    } catch (Exception clazz) {
      boolean print = true;
      try {
        method = grabber.getClass().getMethod(action, new Class<?>[] {});
        print = false;
      } catch (Exception empty) {
        if (key instanceof MotionShortcut)
          try {
            method = grabber.getClass().getMethod(action, new Class<?>[] { MotionEvent.class });
            print = false;
          } catch (Exception motion) {
            System.out.println(message);
            motion.printStackTrace();
          }
        else {
          System.out.println(message);
          empty.printStackTrace();
        }
      }
      if (print) {
        System.out.println(message);
        clazz.printStackTrace();
      }
    }
    map.put(key, new ObjectMethodTuple(grabber, method));
  }

  /**
   * Defines the shortcut that triggers the given action.
   * <p>
   * The action is a method implemented by the {@code object} that returns void and may
   * have a {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all. A
   * {@link remixlab.bias.event.MotionEvent} or a <b>DOFnEvent()</b> that matches the
   * {@link remixlab.bias.core.Shortcut#id()} dofs may be passed to the action when
   * binding a {@link remixlab.bias.event.MotionShortcut}. A
   * {@link remixlab.bias.event.KeyboardEvent} and a
   * {@link remixlab.bias.event.ClickEvent} should always be passed to the action when
   * binding a {@link remixlab.bias.event.KeyboardShortcut} and a
   * {@link remixlab.bias.event.ClickShortcut}, respectively.
   * 
   * @param object
   *          {@link java.lang.Object}
   * @param key
   *          {@link remixlab.bias.core.Shortcut}
   * @param action
   *          {@link java.lang.String}
   * 
   * @see #setBinding(Object, Shortcut, String)
   */
  public void setBinding(Object object, Shortcut key, String action) {
    if (printWarning(key, action))
      return;
    Method method = null;
    String message = "Check that the " + object.getClass().getSimpleName() + "." + action
        + " method exists, is public and returns void, and that it takes a " + grabber().getClass().getSimpleName()
        + " parameter and, optionally, a "
        + ((key instanceof MotionShortcut) ? cls(key).getSimpleName() + " or MotionEvent" : cls(key).getSimpleName())
        + " parameter";
    try {
      method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), cls(key) });
    } catch (Exception clazz) {
      boolean print = true;
      try {
        method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass() });
        print = false;
      } catch (Exception empty) {
        if (key instanceof MotionShortcut)
          try {
            method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), MotionEvent.class });
            print = false;
          } catch (Exception motion) {
            System.out.println(message);
            motion.printStackTrace();
          }
        else {
          System.out.println(message);
          empty.printStackTrace();
        }
      }
      if (print) {
        System.out.println(message);
        clazz.printStackTrace();
      }
    }
    map.put(key, new ObjectMethodTuple(object, method));
  }

  /**
   * Removes the shortcut binding.
   * 
   * @param key
   *          {@link remixlab.bias.core.Shortcut}
   */
  public void removeBinding(Shortcut key) {
    map.remove(key);
  }

  /**
   * Removes all the shortcuts from this object.
   */
  public void removeBindings() {
    map.clear();
  }

  /**
   * Removes all the shortcuts from the given event class.
   */
  public void removeBindings(Class<?> cls) {
    Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
      if (cls.isInstance(pair.getKey()))
        it.remove();
    }
  }

  /**
   * Returns a description of all the bindings this profile holds from the given event
   * class.
   */
  public String info(Class<?> cls) {
    String result = new String();
    for (Entry<Shortcut, ObjectMethodTuple> entry : map.entrySet())
      if (entry.getKey() != null && entry.getValue() != null)
        if (cls.isInstance(entry.getKey()))
          result += entry.getKey().description() + " -> " + entry.getValue().method.getName() + "\n";
    return result;
  }

  /**
   * Returns a description of all the bindings this profile holds.
   */
  public String info() {
    String result = new String();
    boolean title = false;
    for (Entry<Shortcut, ObjectMethodTuple> entry : map.entrySet())
      if (entry.getKey() != null && entry.getValue() != null) {
        if (!title) {
          result += entry.getKey().getClass().getSimpleName() + "s:\n";
          title = true;
        }
        result += entry.getKey().description() + " -> " + entry.getValue().method.getName() + "\n";
      }
    return result;
  }

  /**
   * Returns true if this object contains a binding for the specified shortcut.
   * 
   * @param key
   *          {@link remixlab.bias.core.Shortcut}
   * @return true if this object contains a binding for the specified shortcut.
   */
  public boolean hasBinding(Shortcut key) {
    return map.containsKey(key);
  }

  /**
   * Returns true if this object maps one or more shortcuts to the action specified by the
   * {@link #grabber()}.
   * 
   * @param action
   *          {@link java.lang.String}
   * @return true if this object maps one or more shortcuts to the specified action.
   */
  public boolean isActionBound(String action) {
    for (ObjectMethodTuple tuple : map.values()) {
      if (grabber == tuple.object && tuple.method.getName().equals(action))
        return true;
    }
    return false;
  }

  /**
   * Returns true if this object maps one or more shortcuts to method specified by the
   * {@link #grabber()}.
   * 
   * @param method
   *          {@link java.lang.reflect.Method}
   * @return true if this object maps one or more shortcuts to the specified action.
   */
  public boolean isMethodBound(Method method) {
    return isMethodBound(grabber, method);
  }

  /**
   * Returns true if this object maps one or more shortcuts to the {@code method}
   * specified by the {@code object}.
   * 
   * @param object
   *          {@link java.lang.Object}
   * @param method
   *          {@link java.lang.reflect.Method}
   * @return true if this object maps one or more shortcuts to the specified action.
   */
  public boolean isMethodBound(Object object, Method method) {
    return map.containsValue(new ObjectMethodTuple(object, method));
  }
}