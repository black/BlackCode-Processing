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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.bias.core.Action;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.shortcut.*;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * A mapping between {@link remixlab.bias.event.shortcut.Shortcut}s and user-defined {@link remixlab.bias.core.Action}s,
 * implemented as a parameterized hash-map wrap.
 * 
 * @param <K>
 *          {@link remixlab.bias.event.shortcut.Shortcut}
 * @param <A>
 *          {@link remixlab.bias.core.Action} : User-defined action.
 */
public class Profile<K extends Shortcut, A extends Action<?>> implements Copyable {
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

		Profile<?, ?> other = (Profile<?, ?>) obj;
		return new EqualsBuilder().append(map, other.map).isEquals();
	}

	protected HashMap<K, A>	map;

	/**
	 * Constructs the hash-map based profile.
	 */
	public Profile() {
		map = new HashMap<K, A>();
	}

	/**
	 * Copy constructor. Use {@link #get()} to copy this profile.
	 * 
	 * @param other
	 *          profile to be copied
	 */
	protected Profile(Profile<K, A> other) {
		map = new HashMap<K, A>();
		for (Map.Entry<K, A> entry : other.map().entrySet()) {
			K key = entry.getKey();
			A value = entry.getValue();
			setBinding(key, value);
		}
	}

	/**
	 * Returns a copy of this profile.
	 */
	@Override
	public Profile<K, A> get() {
		return new Profile<K, A>(this);
	}

	/**
	 * Main class method which attempts to define a user-defined action by parsing the event's shortcut.
	 * 
	 * @param event
	 *          {@link remixlab.bias.core.BogusEvent} i.e., Action event to be parsed by this profile.
	 * @return The user-defined action. May be null if no actions was found.
	 */
	public Action<?> handle(BogusEvent event) {
		if (event != null)
			return action(event.shortcut());
		return null;
	}

	/**
	 * Returns the {@code map} (which is simply an instance of {@code HashMap}) encapsulated by this object.
	 */
	public HashMap<K, A> map() {
		return map;
	}

	/**
	 * Returns the {@link remixlab.bias.core.Action} binding for the given {@link remixlab.bias.event.shortcut.Shortcut}
	 * key.
	 */
	public A action(Shortcut key) {
		return map.get(key);
	}

	/**
	 * Defines the shortcut that triggers a given action.
	 * 
	 * @param key
	 *          {@link remixlab.bias.event.shortcut.Shortcut}
	 * @param action
	 *          {@link remixlab.bias.core.Action}
	 */
	public void setBinding(K key, A action) {
		map.put(key, action);
	}

	/**
	 * Removes the shortcut binding.
	 * 
	 * @param key
	 *          {@link remixlab.bias.event.shortcut.Shortcut}
	 */
	public void removeBinding(K key) {
		map.remove(key);
	}

	/**
	 * Removes all the shortcuts from this object.
	 */
	public void removeAllBindings() {
		map.clear();
	}

	/**
	 * Returns true if this object contains a binding for the specified shortcut.
	 * 
	 * @param key
	 *          {@link remixlab.bias.event.shortcut.Shortcut}
	 * @return true if this object contains a binding for the specified shortcut.
	 */
	public boolean hasBinding(K key) {
		return map.containsKey(key);
	}

	/**
	 * Returns true if this object maps one or more shortcuts to the specified action.
	 * 
	 * @param action
	 *          {@link remixlab.bias.core.Action}
	 * @return true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isActionBound(A action) {
		return map.containsValue(action);
	}

	/**
	 * Returns a description of all the bindings this profile holds.
	 */
	public String description() {
		String result = new String();
		for (Entry<K, A> entry : map.entrySet())
			if (entry.getKey() != null && entry.getValue() != null)
				result += entry.getKey().description() + " -> " + entry.getValue().description() + "\n";
		return result;
	}

	// Deprecated

	/**
	 * Use hasBinding instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isBindingInUse(K key) {
		return map.containsKey(key);
	}

	/**
	 * Use the action version with the same parameters instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public A binding(Shortcut key) {
		return map.get(key);
	}

	/**
	 * Use isActionBound instead.
	 * 
	 * @deprecated Please refrain from using this method, it will be removed from future releases.
	 */
	@Deprecated
	public boolean isActionMapped(A action) {
		return map.containsValue(action);
	}
}
