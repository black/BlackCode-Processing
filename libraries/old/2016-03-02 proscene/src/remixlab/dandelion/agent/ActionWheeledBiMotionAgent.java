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
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * An {@link remixlab.bias.agent.ActionWheeledMotionAgent} that handles Dandelion motion actions (i.e., actions
 * triggered from a {@link remixlab.bias.event.MotionEvent}). You should not instantiate this class but one of its
 * derived ones: {@link remixlab.dandelion.agent.HIDAgent}, {@link remixlab.dandelion.agent.JoystickAgent} or
 * {@link remixlab.dandelion.agent.MouseAgent}.
 * <p>
 * Dandelion actions can be handled by an {@link remixlab.dandelion.core.AbstractScene}, an
 * {@link remixlab.dandelion.core.InteractiveFrame} or by an {@link remixlab.dandelion.core.InteractiveEyeFrame}. This
 * class implements a generic Agent that represents any Human Interface Device (such as a mouse or a Joystick) and that
 * handles actions to be executed only by an {@link remixlab.dandelion.core.InteractiveFrame} or an
 * {@link remixlab.dandelion.core.InteractiveEyeFrame} (hence the name "bimotion").
 * {@link remixlab.dandelion.core.AbstractScene} actions are handled exclusively by a
 * {@link remixlab.dandelion.agent.KeyboardAgent}.
 * <p>
 * The agent uses its {@link remixlab.bias.agent.profile.Profile}s (see below) to parse the generic
 * {@link remixlab.bias.core.BogusEvent} to obtain a dandelion action, which is then sent to the proper (
 * {@link #inputGrabber()}) Frame (InteractiveFrame or InteractiveEyeFrame) for its final execution. In case the grabber
 * is not an instance of a Frame, but a different object which behavior you implemented (retrieved as with
 * {@link #alienGrabber()}), the agent sends the BogusEvent to it (please refer to ProScene's MouseGrabbers example).
 * <p>
 * This agent holds the following InteractiveFrame {@link remixlab.bias.agent.profile.Profile}s: a
 * {@link #frameProfile()}, a {@link #frameClickProfile()}, and a {@link #frameWheelProfile()}; together with its
 * InteractiveEyeFrame counterparts: a {@link #eyeProfile()}, a {@link #eyeClickProfile()}, and a
 * {@link #eyeWheelProfile()}. Simply retrieve a specific profile to bind an action to a shortcut, to remove it, or to
 * check your current bindings.
 * <p>
 * <b>Note</b> that the {@link remixlab.bias.agent.ActionWheeledMotionAgent} holds only three profiles:
 * {@link remixlab.bias.agent.ActionWheeledMotionAgent#wheelProfile()},
 * {@link remixlab.bias.agent.ActionWheeledMotionAgent#clickProfile()} and
 * {@link remixlab.bias.agent.ActionWheeledMotionAgent#motionProfile()}. The ActionWheeledBiMotionAgent renames this
 * three profiles for the InteractiveFrame and add those of the InteractiveEyeFrame.
 * 
 * @param <P>
 *          MotionProfile parameterized with a Dandelion action
 */
public class ActionWheeledBiMotionAgent<P extends MotionProfile<?>> extends
		ActionWheeledMotionAgent<MotionProfile<DOF1Action>,
		P,
		ClickProfile<ClickAction>> implements Constants {
	protected P													camProfile;
	protected MotionProfile<DOF1Action>	camWheelProfile;
	protected ClickProfile<ClickAction>	camClickProfile;
	protected AbstractScene							scene;

	public ActionWheeledBiMotionAgent(MotionProfile<DOF1Action> fWProfile,
			MotionProfile<DOF1Action> cWProfile,
			P fProfile,
			P cProfile,
			ClickProfile<ClickAction> c,
			ClickProfile<ClickAction> d,
			AbstractScene scn, String n) {
		super(fWProfile, fProfile, c, scn.inputHandler(), n);
		scene = scn;
		setDefaultGrabber(scn.eye().frame());
		camProfile = cProfile;
		camWheelProfile = cWProfile;
		camClickProfile = d;
	}

	/**
	 * Profile defining InteractiveEyeFrame action bindings from {@link remixlab.bias.event.shortcut.ButtonShortcut}s.
	 */
	public P eyeProfile() {
		return camProfile;
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ButtonShortcut}s.
	 */
	public P frameProfile() {
		return profile();
	}

	/**
	 * Sets the {@link #eyeProfile()}.
	 */
	public void setEyeProfile(P profile) {
		camProfile = profile;
	}

	/**
	 * Sets the {@link #frameProfile()}.
	 */
	public void setFrameProfile(P profile) {
		setProfile(profile);
	}

	/**
	 * Profile defining InteractiveEyeFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> eyeClickProfile() {
		return camClickProfile;
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> frameClickProfile() {
		return clickProfile;
	}

	/**
	 * Sets the {@link #eyeClickProfile()}.
	 */
	public void setEyeClickProfile(ClickProfile<ClickAction> profile) {
		camClickProfile = profile;
	}

	/**
	 * Sets the {@link #frameClickProfile()}.
	 */
	public void setFrameClickProfile(ClickProfile<ClickAction> profile) {
		setClickProfile(profile);
	}

	/**
	 * Profile defining InteractiveEyeFrame action bindings from (wheel)
	 * {@link remixlab.bias.event.shortcut.ButtonShortcut}s.
	 */
	public MotionProfile<DOF1Action> eyeWheelProfile() {
		return camWheelProfile;
	}

	/**
	 * Profile defining InteractiveFrame action bindings from (wheel) {@link remixlab.bias.event.shortcut.ButtonShortcut}
	 * s.
	 */
	public MotionProfile<DOF1Action> frameWheelProfile() {
		return wheelProfile;
	}

	/**
	 * Sets the {@link #eyeWheelProfile()}.
	 */
	public void setEyeWheelProfile(MotionProfile<DOF1Action> profile) {
		camWheelProfile = profile;
	}

	/**
	 * Sets the {@link #frameWheelProfile()}.
	 */
	public void setFrameWheelProfile(MotionProfile<DOF1Action> profile) {
		setWheelProfile(profile);
	}

	/**
	 * Calls {@link remixlab.bias.agent.profile.Profile#removeAllBindings()} on all agent profiles.
	 */
	public void resetAllProfiles() {
		eyeClickProfile().removeAllBindings();
		eyeProfile().removeAllBindings();
		eyeWheelProfile().removeAllBindings();
		frameClickProfile().removeAllBindings();
		frameProfile().removeAllBindings();
		frameWheelProfile().removeAllBindings();
	}

	@Override
	public P motionProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeProfile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameProfile();
		return null;
	}

	@Override
	public ClickProfile<ClickAction> clickProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeClickProfile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameClickProfile();
		return null;
	}

	@Override
	public MotionProfile<DOF1Action> wheelProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeWheelProfile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameWheelProfile();
		return null;
	}

	@Override
	protected boolean alienGrabber() {
		return !(inputGrabber() instanceof InteractiveFrame) && !(inputGrabber() instanceof AbstractScene);
	}

	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (eyeClickProfile().description().length() != 0) {
			description += "Eye click shortcuts\n";
			description += eyeClickProfile().description();
		}
		if (frameClickProfile().description().length() != 0) {
			description += "Frame click shortcuts\n";
			description += frameClickProfile().description();
		}
		if (eyeProfile().description().length() != 0) {
			description += "Eye shortcuts\n";
			description += eyeProfile().description();
		}
		if (frameProfile().description().length() != 0) {
			description += "Frame shortcuts\n";
			description += frameProfile().description();
		}
		if (eyeWheelProfile().description().length() != 0) {
			description += "Eye wheel shortcuts\n";
			description += eyeWheelProfile().description();
		}
		if (frameWheelProfile().description().length() != 0) {
			description += "Frame wheel shortcuts\n";
			description += frameWheelProfile().description();
		}
		return description;
	}
}
