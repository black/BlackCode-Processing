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

import remixlab.bias.core.Shortcut;
import remixlab.util.Copyable;

/**
 * This class represents {@link remixlab.bias.event.MotionEvent} shortcuts.
 * <p>
 * Button shortcuts can be of one of two forms: 1. Buttons (e.g., 'LEFT' , or even
 * 'B_NOBUTTON'); 2. Button + modifier key combinations (e.g., 'RIGHT' + 'CTRL').
 * <p>
 * Note that the shortcut may be empty: the no-button (B_NOBUTTON) and no-modifier-mask
 * (B_NOMODIFIER_MASK) combo may also defined a shortcut. Empty shortcuts may bind
 * button-less motion interactions (e.g., mouse move without any button pressed).
 */
public final class MotionShortcut extends Shortcut implements Copyable {
  /**
   * Constructs an "empty" shortcut by conveniently calling
   * {@code this(B_NOMODIFIER_MASK, B_NOBUTTON);}
   */
  public MotionShortcut() {
    super();
  }

  /**
   * Defines a shortcut from the given button.
   * 
   * @param id
   *          button
   */
  public MotionShortcut(int id) {
    super(id);
  }

  /**
   * Defines a shortcut from the given modifier mask and button combination.
   * 
   * @param m
   *          the mask
   * @param id
   *          button
   */
  public MotionShortcut(int m, int id) {
    super(m, id);
  }

  protected MotionShortcut(MotionShortcut other) {
    super(other);
  }

  @Override
  public MotionShortcut get() {
    return new MotionShortcut(this);
  }
}
