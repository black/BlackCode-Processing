/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.event;

import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.shortcut.*;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * Base class of all DOF_n_Events: {@link remixlab.bias.core.BogusEvent}s defined from DOFs (degrees-of-freedom).
 * <p>
 * A MotionEvent encapsulates a {@link remixlab.bias.event.shortcut.ButtonShortcut}. MotionEvents may be relative or
 * absolute (see {@link #isRelative()}, {@link #isAbsolute()}) depending whether or not they're defined from a previous
 * MotionEvent (see {@link #setPreviousEvent(MotionEvent)}). While relative motion events have {@link #distance()},
 * {@link #speed()}, and {@link #delay()}, absolute motion events don't.
 */
public class MotionEvent extends BogusEvent {
	// some motion actions may be performed without any button, e.g., mouse move (instead of drag).
	public static final int	NOBUTTON	= 0;

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
				.append(button)
				.append(delay)
				.append(distance)
				.append(speed)
				.append(rel)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		MotionEvent other = (MotionEvent) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(button, other.button)
				.append(delay, other.delay)
				.append(distance, other.distance)
				.append(speed, other.speed)
				.append(rel, other.rel)
				.isEquals();
	}

	// defaulting to zero:
	// http://stackoverflow.com/questions/3426843/what-is-the-default-initialization-of-an-array-in-java
	protected long		delay;
	protected float		distance, speed;
	protected int			button;
	protected boolean	rel;

	/**
	 * Constructs a MotionEvent with an "empty" {@link remixlab.bias.event.shortcut.ButtonShortcut}.
	 */
	public MotionEvent() {
		super();
		this.button = NOBUTTON;
	}

	/**
	 * Constructs a MotionEvent taking the given {@code modifiers} as a
	 * {@link remixlab.bias.event.shortcut.ButtonShortcut}.
	 */
	public MotionEvent(int modifiers) {
		super(modifiers);
		this.button = NOBUTTON;
	}

	/**
	 * Constructs a MotionEvent taking the given {@code modifiers} and {@code modifiers} as a
	 * {@link remixlab.bias.event.shortcut.ButtonShortcut}.
	 */
	public MotionEvent(int modifiers, int button) {
		super(modifiers);
		this.button = button;
	}

	protected MotionEvent(MotionEvent other) {
		super(other);
		this.button = other.button;
		this.delay = other.delay;
		this.distance = other.distance;
		this.speed = other.speed;
		this.rel = other.rel;
	}

	@Override
	public MotionEvent get() {
		return new MotionEvent(this);
	}

	/**
	 * Modulate the event dofs according to {@code sens}.
	 */
	public void modulate(float[] sens) {
	}

	/**
	 * Returns the button defining the event's {@link remixlab.bias.event.shortcut.ButtonShortcut}.
	 */
	public int button() {
		return button;
	}

	@Override
	public ButtonShortcut shortcut() {
		return new ButtonShortcut(modifiers(), button());
	}

	/**
	 * Returns the delay between two consecutive motion events. Meaningful only if the event {@link #isRelative()}.
	 */
	public long delay() {
		return delay;
	}

	/**
	 * Returns the distance between two consecutive motion events. Meaningful only if the event {@link #isRelative()}.
	 */
	public float distance() {
		return distance;
	}

	/**
	 * Returns the speed between two consecutive motion events. Meaningful only if the event {@link #isRelative()}.
	 */
	public float speed() {
		return speed;
	}

	/**
	 * Returns true if the motion event is relative, i.e., it has been built from a previous motion event.
	 */
	public boolean isRelative() {
		// return distance() != 0;
		return rel;
	}

	/**
	 * Returns true if the motion event is absolute, i.e., it hasn't been built from a previous motion event.
	 */
	public boolean isAbsolute() {
		return !isRelative();
	}

	/**
	 * Sets the event's previous event to build a relative event.
	 */
	public void setPreviousEvent(MotionEvent prevEvent) {
		if (prevEvent == null) {
			delay = 0l;
			speed = 0f;
			distance = 0f;
		} else {
			rel = true;
			delay = this.timestamp() - prevEvent.timestamp();
			if (delay == 0l)
				speed = distance;
			else
				speed = distance / (float) delay;
		}
	}
}
