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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * The InputHandler object is the high level package handler which holds a collection of {@link #agents()}, and an event
 * dispatcher queue of {@link remixlab.bias.core.EventGrabberTuple}s ({@link #eventTupleQueue()}). Such tuple represents
 * a message passing to application objects, allowing an object to be instructed to perform a particular user-defined
 * {@link remixlab.bias.core.Action} from a given {@link remixlab.bias.core.BogusEvent}. For an introduction to BIAS
 * please refer to <a href="http://nakednous.github.io/projects/bias">this</a>.
 * <p>
 * At runtime, the input handler should continuously run the two loops defined in {@link #handle()}. Therefore, simply
 * attach a call to {@link #handle()} at the end of your main event (drawing) loop for that to take effect (like it's
 * done in </b>dandelion</b> by the <b>AbstractScene.postDraw()</b> method).
 */
public class InputHandler {
	// D E V I C E S & E V E N T S
	protected HashMap<String, Agent>				agents;
	protected LinkedList<EventGrabberTuple>	eventTupleQueue;

	public InputHandler() {
		// agents
		agents = new HashMap<String, Agent>();
		// events
		eventTupleQueue = new LinkedList<EventGrabberTuple>();
	}

	/**
	 * Main handler method. Call it at the end of your main event (drawing) loop (like it's done in </b>dandelion</b> by
	 * the <b>AbstractScene.postDraw()</b> method)
	 * <p>
	 * The handle comprises the following two loops:
	 * <p>
	 * 1. {@link remixlab.bias.core.EventGrabberTuple} producer loop which for each registered agent calls:
	 * {@link remixlab.bias.core.Agent#handle(BogusEvent)}. Note that the bogus event is obtained from the agents callback
	 * {@link remixlab.bias.core.Agent#feed()} method.<br>
	 * 2. User-defined action consumer loop: which for each {@link remixlab.bias.core.EventGrabberTuple} calls
	 * {@link remixlab.bias.core.EventGrabberTuple#perform()}.<br>
	 */
	public void handle() {
		// 1. Agents
		for (Agent agent : agents.values())
			agent.handle(agent.feed());

		// 2. Low level events
		while (!eventTupleQueue.isEmpty())
			eventTupleQueue.remove().perform();
	}

	/**
	 * Returns a description of all registered agents' bindings and shortcuts as a String
	 */
	public String info() {
		String description = new String();
		description += "Agents' info\n";
		int index = 1;
		for (Agent agent : agents()) {
			description += index;
			description += ". ";
			description += agent.info();
			index++;
		}
		return description;
	}

	/**
	 * Returns an array of the registered agents.
	 * 
	 * @see #agents()
	 */
	public Agent[] agentsArray() {
		return agents.values().toArray(new Agent[0]);
	}

	/**
	 * Returns a list of the registered agents.
	 * 
	 * @see #agentsArray()
	 */
	public List<Agent> agents() {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent agent : agents.values())
			list.add(agent);

		return list;
	}

	/**
	 * Registers the given agent.
	 */
	public void registerAgent(Agent agent) {
		if (!isAgentRegistered(agent))
			agents.put(agent.name(), agent);
		else {
			System.out.println("Nothing done. An agent with the same name is already registered. Current agent names are:");
			for (Agent ag : agents.values())
				System.out.println(ag.name());
		}
	}

	/**
	 * Returns true if the given agent is registered.
	 */
	public boolean isAgentRegistered(Agent agent) {
		return agents.containsKey(agent.name());
	}

	/**
	 * Returns true if the agent (given by its name) is registered.
	 */
	public boolean isAgentRegistered(String name) {
		return agents.containsKey(name);
	}

	/**
	 * Returns the agent by its name. The agent mus be {@link #isAgentRegistered(Agent)}.
	 */
	public Agent agent(String name) {
		return agents.get(name);
	}

	/**
	 * Unregisters the given agent and returns it.
	 */
	public Agent unregisterAgent(Agent agent) {
		return agents.remove(agent.name());
	}

	/**
	 * Unregisters the given agent by its name and returns it.
	 */
	public Agent unregisterAgent(String name) {
		return agents.remove(name);
	}

	/**
	 * Unregisters all agents from the handler.
	 */
	public void unregisterAllAgents() {
		agents.clear();
	}

	/**
	 * Returns the event tuple queue. Rarely needed.
	 */
	public LinkedList<EventGrabberTuple> eventTupleQueue() {
		return eventTupleQueue;
	}

	/**
	 * Enqueues the eventTuple for later execution which happens at the end of {@link #handle()}. Returns {@code true} if
	 * succeeded and {@code false} otherwise.
	 * 
	 * @see #handle()
	 */
	public boolean enqueueEventTuple(EventGrabberTuple eventTuple) {
		if (!eventTupleQueue.contains(eventTuple))
			if (!eventTuple.event().isNull()) {
				eventTupleQueue.add(eventTuple);
				return true;
			}
		return false;
	}

	/**
	 * Removes the given event from the event queue. No action is executed.
	 * 
	 * @param event
	 *          to be removed.
	 */
	public void removeEventTuple(BogusEvent event) {
		eventTupleQueue.remove(event);
	}

	/**
	 * Clears the event queue. Nothing is executed.
	 */
	public void removeAllEventTuples() {
		eventTupleQueue.clear();
	}

	/**
	 * Returns {@code true} if the given {@code grabber} is in the {@code agent} pool and {@code false} otherwise.
	 */
	public boolean isInAgentPool(Grabber grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.isInPool(grabber);
	}

	/**
	 * Adds {@code grabber} to the {@code agent} {@link remixlab.bias.core.Agent#pool()}.
	 */
	public boolean addInAgentPool(Grabber grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.addInPool(grabber);
	}

	/**
	 * Removes {@code grabber} from the {@code agent} {@link remixlab.bias.core.Agent#pool()}.
	 */
	public boolean removeFromAgentPool(Grabber grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.removeFromPool(grabber);
	}

	/**
	 * Clears the {@code agent} {@link remixlab.bias.core.Agent#pool()}.
	 */
	public void clearAgentPool(Agent agent) {
		agent.clearPool();
	}

	/**
	 * Adds {@code grabber} into all registered agents.
	 */
	public void addInAllAgentPools(Grabber grabber) {
		for (Agent agent : agents.values())
			if (!agent.isInPool(grabber))
				agent.addInPool(grabber);
	}

	/**
	 * Returns {@code true} if the grabber {@link remixlab.bias.core.Grabber#grabsInput(Agent)} from any registered agent.
	 */
	public boolean grabsAnyAgentInput(Grabber grabber) {
		for (Agent agent : agents()) {
			if (grabber.grabsInput(agent))
				return true;
		}
		return false;
	}

	/**
	 * Removes {@code grabber} from all registered agents.
	 */
	public void removeFromAllAgentPools(Grabber grabber) {
		for (Agent agent : agents.values())
			agent.removeFromPool(grabber);
	}

	/**
	 * Clears all registered agent's {@link remixlab.bias.core.Agent#pool()}.
	 */
	public void clearAllAgentPools() {
		for (Agent agent : agents.values())
			agent.clearPool();
	}

	/**
	 * Returns a list containing all Grabber objects registered at all agents.
	 */
	public List<Grabber> globalGrabberList() {
		List<Grabber> msGrabberPool = new ArrayList<Grabber>();
		for (Agent device : agents.values())
			for (Grabber grabber : device.pool())
				if (!msGrabberPool.contains(grabber))
					msGrabberPool.add(grabber);

		return msGrabberPool;
	}
}
