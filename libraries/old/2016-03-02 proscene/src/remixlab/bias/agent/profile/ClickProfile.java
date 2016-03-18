/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.agent.profile;

import remixlab.bias.core.Action;
import remixlab.bias.event.shortcut.ClickShortcut;

/**
 * A {@link remixlab.bias.agent.profile.Profile} defining a mapping between
 * {@link remixlab.bias.event.shortcut.ClickShortcut} s and user-defined {@link remixlab.bias.core.Action}s.
 * 
 * @param <A>
 *          {@link remixlab.bias.core.Action} : User-defined action.
 */

public class ClickProfile<A extends Action<?>> extends Profile<ClickShortcut, A> {
	/**
	 * Returns true if the given binding binds a click-action.
	 * 
	 * @param button
	 *          binding
	 */
	public boolean hasBinding(Integer button) {
		return hasBinding(new ClickShortcut(button));
	}

	/**
	 * Returns true if the given binding binds a click-action.
	 * 
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks defining the binding
	 */
	public boolean hasBinding(Integer button, Integer nc) {
		return hasBinding(new ClickShortcut(button, nc));
	}

	/**
	 * Returns true if the given binding binds a click-action.
	 * 
	 * @param mask
	 *          modifier mask defining the binding
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks defining the binding
	 */
	public boolean hasBinding(Integer mask, Integer button, Integer nc) {
		return hasBinding(new ClickShortcut(mask, button, nc));
	}

	/**
	 * Binds the click-action to the given binding.
	 * 
	 * @param button
	 *          binding
	 * @param action
	 *          action to be bound
	 */
	public void setBinding(Integer button, A action) {
		if (hasBinding(button)) {
			Action<?> a = action(button);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ClickShortcut(button), action);
	}

	/**
	 * Binds the click-action to the given binding.
	 * 
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks that defines the binding
	 * @param action
	 *          action to be bound
	 */
	public void setBinding(Integer button, Integer nc, A action) {
		if (hasBinding(button, nc)) {
			Action<?> a = action(button, nc);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ClickShortcut(button, nc), action);
	}

	/**
	 * Binds the click-action to the given binding.
	 * 
	 * @param mask
	 *          modifier mask defining the binding
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks that defines the binding
	 * @param action
	 *          action to be bound
	 */
	public void setBinding(Integer mask, Integer button, Integer nc, A action) {
		if (hasBinding(mask, button, nc)) {
			Action<?> a = action(mask, button, nc);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ClickShortcut(mask, button, nc), action);
	}

	/**
	 * Removes the click binding.
	 * 
	 * @param button
	 *          binding
	 */
	public void removeBinding(Integer button) {
		removeBinding(new ClickShortcut(button));
	}

	/**
	 * Removes the click binding.
	 * 
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks defining the binding
	 */
	public void removeBinding(Integer button, Integer nc) {
		removeBinding(new ClickShortcut(button, nc));
	}

	/**
	 * Removes the click binding.
	 * 
	 * @param mask
	 *          modifier mask defining the binding
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks defining the binding
	 */
	public void removeBinding(Integer mask, Integer button, Integer nc) {
		removeBinding(new ClickShortcut(mask, button, nc));
	}

	/**
	 * Returns the click-action associated to the given binding.
	 * 
	 * @param button
	 *          binding
	 */
	public Action<?> action(Integer button) {
		return action(new ClickShortcut(button));
	}

	/**
	 * Returns the click-action associated to the given binding.
	 * 
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks defining the binding
	 */
	public Action<?> action(Integer button, Integer nc) {
		return action(new ClickShortcut(button, nc));
	}

	/**
	 * Returns the click-action associated to the given binding.
	 * 
	 * @param mask
	 *          modifier mask defining the binding
	 * @param button
	 *          button defining the binding
	 * @param nc
	 *          number of clicks defining the binding
	 */
	public Action<?> action(Integer mask, Integer button, Integer nc) {
		return action(new ClickShortcut(mask, button, nc));
	}

	// Deprecated

	/**
	 * Use the hasBinding version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isBindingInUse(Integer button) {
		return hasBinding(new ClickShortcut(button));
	}

	/**
	 * Use the hasBinding version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isBindingInUse(Integer button, Integer nc) {
		return hasBinding(new ClickShortcut(button, nc));
	}

	/**
	 * Use the hasBinding version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isBindingInUse(Integer mask, Integer button, Integer nc) {
		return hasBinding(new ClickShortcut(mask, button, nc));
	}

	/**
	 * Use the action version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public Action<?> binding(Integer button) {
		return action(new ClickShortcut(button));
	}

	/**
	 * Use the action version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public Action<?> binding(Integer button, Integer nc) {
		return action(new ClickShortcut(button, nc));
	}

	/**
	 * Use the action version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public Action<?> binding(Integer mask, Integer button, Integer nc) {
		return action(new ClickShortcut(mask, button, nc));
	}

	/**
	 * Use the isBindingInUse version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isClickBindingInUse(Integer button) {
		return isBindingInUse(new ClickShortcut(button));
	}

	/**
	 * Use the isBindingInUse version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isClickBindingInUse(Integer button, Integer nc) {
		return isBindingInUse(new ClickShortcut(button, nc));
	}

	/**
	 * Use the isBindingInUse version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isClickBindingInUse(Integer mask, Integer button, Integer nc) {
		return isBindingInUse(new ClickShortcut(mask, button, nc));
	}

	/**
	 * Use isActionBound instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isClickActionBound(A action) {
		return isActionBound(action);
	}

	/**
	 * Use the setBinding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public void setClickBinding(Integer button, A action) {
		if (isClickBindingInUse(button)) {
			Action<?> a = clickBinding(button);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ClickShortcut(button), action);
	}

	/**
	 * Use the setBinding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public void setClickBinding(Integer button, Integer nc, A action) {
		if (isClickBindingInUse(button, nc)) {
			Action<?> a = clickBinding(button, nc);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ClickShortcut(button, nc), action);
	}

	/**
	 * Use the setBinding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public void setClickBinding(Integer mask, Integer button, Integer nc, A action) {
		if (isClickBindingInUse(mask, button, nc)) {
			Action<?> a = clickBinding(mask, button, nc);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ClickShortcut(mask, button, nc), action);
	}

	/**
	 * Use the removeBinding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public void removeClickBinding(Integer button) {
		removeBinding(new ClickShortcut(button));
	}

	/**
	 * Use the removeBinding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public void removeClickBinding(Integer button, Integer nc) {
		removeBinding(new ClickShortcut(button, nc));
	}

	/**
	 * Use the removeBinding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public void removeClickBinding(Integer mask, Integer button, Integer nc) {
		removeBinding(new ClickShortcut(mask, button, nc));
	}

	/**
	 * Use the binding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public Action<?> clickBinding(Integer button) {
		return binding(new ClickShortcut(button));
	}

	/**
	 * Use the binding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public Action<?> clickBinding(Integer button, Integer nc) {
		return binding(new ClickShortcut(button, nc));
	}

	/**
	 * Use the binding version with the same parameters instead
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public Action<?> clickBinding(Integer mask, Integer button, Integer nc) {
		return binding(new ClickShortcut(mask, button, nc));
	}
}
