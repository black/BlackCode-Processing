/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.event.shortcut;

import remixlab.bias.core.BogusEvent;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * Shortcuts are BogusEvent means to bind user-defined actions.
 * <p>
 * Shortcuts can represent, for instance, the button being dragged and the modifier key pressed at the very moment an
 * user interaction takes place, such as when she/he drags a giving mouse button while pressing the 'CTRL' modifier key.
 */
public class Shortcut implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				append(mask).
				toHashCode();
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
		return new EqualsBuilder()
				.append(mask, other.mask)
				.isEquals();
	}

	protected final Integer	mask;

	/**
	 * @param m
	 *          modifier mask defining the shortcut
	 */
	public Shortcut(Integer m) {
		mask = m;
	}

	/**
	 * Constructs an "empty" shortcut. Same as: {@link #Shortcut(Integer)} with the integer parameter being
	 * B_NOMODIFIER_MASK.
	 */
	public Shortcut() {
		mask = BogusEvent.NOMODIFIER_MASK;
	}

	protected Shortcut(Shortcut other) {
		this.mask = new Integer(other.mask);
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
		return BogusEvent.modifiersText(mask);
	}
}
