/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * An {@link remixlab.bias.agent.ActionKeyboardAgent} that handles Dandelion keyboard actions.
 * <p>
 * Dandelion actions can be handled by the {@link remixlab.dandelion.core.AbstractScene}, or by an
 * {@link remixlab.dandelion.core.InteractiveFrame} or an {@link remixlab.dandelion.core.InteractiveEyeFrame}. This
 * class implements a generic Keyboard Agent that represents a keyboard device that handles actions to be executed only
 * by the AbstractScene (InteractiveFrame and InteractiveEyeFrame actions are handled exclusively by an
 * {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent}).
 * <p>
 * The agent uses its {@link #keyboardProfile()} to parse the {@link remixlab.bias.core.BogusEvent} to obtain a
 * dandelion action, which is then sent to the AbstractScene ({@link #inputGrabber()}) for its final execution. In case
 * the grabber is not an instance of AbstractScene, but a different object which behavior you implemented (
 * {@link #alienGrabber()}), the agent sends the raw BogusEvent to it.
 * <p>
 * Simply retrieve the {@link #keyboardProfile()} to bind an action to a shortcut, to remove it, or to check your
 * current bindings. Default bindings are provided for convenience ({@link #setDefaultShortcuts()}).
 * <p>
 * Note that {@link #keyboardProfile()} shortcuts are {@link remixlab.bias.event.shortcut.KeyboardShortcut}s.
 */
public class KeyboardAgent extends ActionKeyboardAgent<KeyboardProfile<KeyboardAction>> implements
		Constants {
	protected AbstractScene	scene;

	/**
	 * Default constructor. Calls {@link #setDefaultShortcuts()}.
	 */
	public KeyboardAgent(AbstractScene scn, String n) {
		super(new KeyboardProfile<KeyboardAction>(), scn.inputHandler(), n);
		setDefaultGrabber(scn);
		scene = scn;

		// D e f a u l t s h o r t c u t s
		setDefaultShortcuts();
	}

	/**
	 * Set the default keyboard shortcuts as follows:
	 * <p>
	 * {@code 'a' -> KeyboardAction.TOGGLE_AXIS_VISUAL_HINT}<br>
	 * {@code 'f' -> KeyboardAction.TOGGLE_FRAME_VISUAL_HINT}<br>
	 * {@code 'g' -> KeyboardAction.TOGGLE_GRID_VISUAL_HINT}<br>
	 * {@code 'm' -> KeyboardAction.TOGGLE_ANIMATION}<br>
	 * {@code 'e' -> KeyboardAction.TOGGLE_CAMERA_TYPE}<br>
	 * {@code 'h' -> KeyboardAction.DISPLAY_INFO}<br>
	 * {@code 'r' -> KeyboardAction.TOGGLE_PATHS_VISUAL_HINT}<br>
	 * {@code 's' -> KeyboardAction.INTERPOLATE_TO_FIT}<br>
	 * {@code 'S' -> KeyboardAction.SHOW_ALL}<br>
	 */
	public void setDefaultShortcuts() {
		keyboardProfile().removeAllBindings();
		keyboardProfile().setBinding('a', KeyboardAction.TOGGLE_AXES_VISUAL_HINT);
		keyboardProfile().setBinding('f', KeyboardAction.TOGGLE_PICKING_VISUAL_HINT);
		keyboardProfile().setBinding('g', KeyboardAction.TOGGLE_GRID_VISUAL_HINT);
		keyboardProfile().setBinding('m', KeyboardAction.TOGGLE_ANIMATION);

		keyboardProfile().setBinding('e', KeyboardAction.TOGGLE_CAMERA_TYPE);
		keyboardProfile().setBinding('h', KeyboardAction.DISPLAY_INFO);
		keyboardProfile().setBinding('r', KeyboardAction.TOGGLE_PATHS_VISUAL_HINT);

		keyboardProfile().setBinding('s', KeyboardAction.INTERPOLATE_TO_FIT);
		keyboardProfile().setBinding('S', KeyboardAction.SHOW_ALL);
	}

	/**
	 * Sets the default (virtual) key to play eye paths.
	 */
	public void setKeyCodeToPlayPath(int vkey, int path) {
		switch (path) {
		case 1:
			keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_1);
			break;
		case 2:
			keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_2);
			break;
		case 3:
			keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_3);
			break;
		default:
			break;
		}
	}

	@Override
	public KeyboardEvent feed() {
		return null;
	}

	@Override
	public KeyboardProfile<KeyboardAction> keyboardProfile() {
		return profile;
	}
}
