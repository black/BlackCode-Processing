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
 * Default implementation of the Grabber interface which eases implementation by simply overriding
 * {@link #grabsInput(Agent)}.
 */
public abstract class GrabberObject implements Grabber {
	/**
	 * Empty constructor.
	 */
	public GrabberObject() {
	}

	/**
	 * Constructs and adds this grabber to the agent pool.
	 * 
	 * @see remixlab.bias.core.Agent#pool()
	 */
	public GrabberObject(Agent agent) {
		agent.addInPool(this);
	}

	/**
	 * Constructs and adds this grabber to all agents belonging to the input handler.
	 * 
	 * @see remixlab.bias.core.InputHandler#agents()
	 */
	public GrabberObject(InputHandler inputHandler) {
		for (Agent agent : inputHandler.agents())
			agent.addInPool(this);
	}

	@Override
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}
}
