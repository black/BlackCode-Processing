/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.event;

import remixlab.bias.core.BogusEvent;
import remixlab.bias.core.Shortcut;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * This class represents {@link remixlab.bias.event.ClickEvent} shortcuts.
 * <p>
 * Click shortcuts are defined with a specific number of clicks and can be of one out of
 * two forms: 1. A button; and, 2. A button plus a key-modifier (such as the CTRL key).
 * <p>
 * Note that click shortcuts should have at least one click.
 */
public class ClickShortcut extends Shortcut implements Copyable {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(numberOfClicks).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    ClickShortcut other = (ClickShortcut) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(numberOfClicks, other.numberOfClicks).isEquals();
  }

  protected final int numberOfClicks;

  /**
   * Defines a single click shortcut from the given button.
   * 
   * @param id
   *          id
   */
  public ClickShortcut(int id) {
    this(BogusEvent.NO_MODIFIER_MASK, id, 1);
  }

  /**
   * Defines a click shortcut from the given button and number of clicks.
   * 
   * @param id
   *          id
   * @param c
   *          number of clicks
   */
  public ClickShortcut(int id, int c) {
    this(BogusEvent.NO_MODIFIER_MASK, id, c);
  }

  /**
   * Defines a click shortcut from the given button, modifier mask, and number of clicks.
   * 
   * @param m
   *          modifier mask
   * @param id
   *          id
   * @param c
   *          bumber of clicks
   */
  public ClickShortcut(int m, int id, int c) {
    super(m, id);
    if (c <= 0)
      this.numberOfClicks = 1;
    else
      this.numberOfClicks = c;
  }

  protected ClickShortcut(ClickShortcut other) {
    super(other);
    this.numberOfClicks = other.numberOfClicks;
  }

  @Override
  public ClickShortcut get() {
    return new ClickShortcut(this);
  }

  /**
   * Returns a textual description of this click shortcut.
   * 
   * @return description
   */
  public String description() {
    String r = super.description();
    if (numberOfClicks == 1)
      r += (r.length() > 0) ? "+" + String.valueOf(numberOfClicks) + "_click"
          : String.valueOf(numberOfClicks) + "_click";
    else
      r += (r.length() > 0) ? "+" + String.valueOf(numberOfClicks) + "_clicks"
          : String.valueOf(numberOfClicks) + "_clicks";
    return r;
  }
}
