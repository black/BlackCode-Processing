/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

/**
 * Grabbers are means to attach a set of user-defined {@link remixlab.bias.core.Action} groups to application objects.
 * Grabbers are attached to {@link remixlab.bias.core.Agent}s through their API, and may be attached to more than just a
 * single Agent.
 * <p>
 * Each application object willing to subscribe a group of user defined actions should either implement the Grabber
 * interface or extend from the {@link remixlab.bias.core.GrabberObject} class (which provides a default implementation
 * of that interface), and override the following two methods: {@link #checkIfGrabsInput(BogusEvent)}, which defines the
 * rules to set the application object as the agents {@link remixlab.bias.core.Agent#inputGrabber()}; and,
 * {@link #performInteraction(BogusEvent)}, which defines how the application object should behave according to a given
 * bogus event, which may hold a user-defined action.
 */
public interface Grabber {
	/**
	 * Defines the rules to set the application object as an input grabber.
	 */
	boolean checkIfGrabsInput(BogusEvent event);

	/**
	 * Defines how the application object should behave according to a given BogusEvent, which may hold a user-defined
	 * action.
	 */
	void performInteraction(BogusEvent event);

	/**
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	boolean grabsInput(Agent agent);
}
