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

import remixlab.bias.agent.profile.*;
import remixlab.bias.event.DOF2Event;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * An {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Wheeled mouse and thus only holds 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as most mice.
 */
public class MouseAgent extends ActionWheeledBiMotionAgent<MotionProfile<DOF2Action>> {
	/**
	 * Constructs a MouseAgent. Nothing fancy.
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          Agents name
	 */
	public MouseAgent(AbstractScene scn, String n) {
		super(new MotionProfile<DOF1Action>(),
				new MotionProfile<DOF1Action>(),
				new MotionProfile<DOF2Action>(),
				new MotionProfile<DOF2Action>(),
				new ClickProfile<ClickAction>(),
				new ClickProfile<ClickAction>(), scn, n);
	}

	@Override
	public DOF2Event feed() {
		return null;
	}

	@Override
	public MotionProfile<DOF2Action> eyeProfile() {
		return camProfile;
	}

	@Override
	public MotionProfile<DOF2Action> frameProfile() {
		return profile;
	}

	/**
	 * Sets the mouse translation sensitivity along X.
	 */
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}

	/**
	 * Sets the mouse translation sensitivity along Y.
	 */
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}
}
