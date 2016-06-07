/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.core;

import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * Shortcuts are {@link remixlab.bias.core.BogusEvent} means to bind user-defined actions
 * from a {@link remixlab.bias.core.BogusEvent}.
 * <p>
 * Every {@link remixlab.bias.core.BogusEvent} instance has a shortcut which can
 * represent, for instance, the button being dragged and the modifier key pressed at the
 * very moment an user interaction takes place, such as when she/he drags a giving mouse
 * button while pressing the 'CTRL' modifier key (see
 * {@link remixlab.bias.core.BogusEvent#shortcut()}).
 * <p>
 * Different bogus event types are related to different shortcuts. The current
 * implementation supports the following event/shortcut types:
 * <ol>
 * <li>{@link remixlab.bias.event.MotionEvent} /
 * {@link remixlab.bias.event.MotionShortcut}. Note that motion-event derived classes:
 * {@link remixlab.bias.event.DOF1Event}, {@link remixlab.bias.event.DOF2Event},
 * {@link remixlab.bias.event.DOF3Event}, {@link remixlab.bias.event.DOF6Event}, are also
 * related to motion shortcuts.</li>
 * <li>{@link remixlab.bias.event.ClickEvent} / {@link remixlab.bias.event.ClickShortcut}
 * </li>
 * <li>{@link remixlab.bias.event.KeyboardEvent} /
 * {@link remixlab.bias.event.KeyboardShortcut}</li>
 * </ol>
 */
public class Shortcut implements Copyable {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(mask).append(id).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    Shortcut other = (Shortcut) obj;
    return new EqualsBuilder().append(mask, other.mask).append(id, other.id).isEquals();
  }

  protected final int mask;
  protected final int id;

  /**
   * Constructs an "empty" shortcut. Same as: {@link #Shortcut(int)} with the integer
   * parameter being B_NOMODIFIER_MASK.
   */
  public Shortcut() {
    mask = BogusEvent.NO_MODIFIER_MASK;
    id = BogusEvent.NO_ID;
  }

  /**
   * Defines a shortcut from the given id.
   * 
   * @param _id
   *          button
   */
  public Shortcut(int _id) {
    mask = BogusEvent.NO_MODIFIER_MASK;
    id = _id;
  }

  /**
   * @param m
   *          modifier mask defining the shortcut
   */
  public Shortcut(int m, int i) {
    mask = m;
    id = i;
  }

  protected Shortcut(Shortcut other) {
    this.mask = other.mask;
    this.id = other.id;
  }

  @Override
  public Shortcut get() {
    return new Shortcut(this);
  }

  /**
   * Shortcut description.
   * 
   * @return description as a String
   */
  public String description() {
    String m = BogusEvent.modifiersText(mask);
    return ((m.length() > 0) ? m + "+ID_" : "ID_") + String.valueOf(id);
  }

  public int modifiers() {
    return mask;
  }

  public int id() {
    return id;
  }
}