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

import remixlab.bias.agent.profile.ClickProfile;
import remixlab.bias.agent.profile.MotionProfile;
import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants.*;

/**
 * An {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Human Interface Device with 3
 * Degrees-Of-Freedom (e.g., three translations or three rotations), such as some Joysticks.
 */
public class JoystickAgent extends ActionWheeledBiMotionAgent<MotionProfile<DOF3Action>> {
	/**
	 * Default constructor. Nothing fancy.
	 */
	public JoystickAgent(AbstractScene scn, String n) {
		super(new MotionProfile<DOF1Action>(),
				new MotionProfile<DOF1Action>(),
				new MotionProfile<DOF3Action>(),
				new MotionProfile<DOF3Action>(),
				new ClickProfile<ClickAction>(),
				new ClickProfile<ClickAction>(), scn, n);
	}

	@Override
	public MotionProfile<DOF3Action> eyeProfile() {
		return camProfile;
	}

	@Override
	public MotionProfile<DOF3Action> frameProfile() {
		return profile;
	}

	/**
	 * Sets the translation sensitivity along X.
	 */
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}

	/**
	 * Sets the translation sensitivity along Y.
	 */
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}

	/**
	 * Sets the translation sensitivity along Z.
	 */
	public void setZTranslationSensitivity(float s) {
		sens[2] = s;
	}
}
