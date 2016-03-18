/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.core;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;
import remixlab.util.*;

/**
 * An InteractiveFrame is a Frame that can be rotated, translated and scaled by user interaction means.
 * <p>
 * An InteractiveFrame converts user gestures into translation, rotation and scaling updates. An InteractiveFrame is
 * used to move an object in the scene (and thus it's tightly-coupled with it). Combined with object selection, its
 * Grabber properties and a dynamic update of the scene, the InteractiveFrame introduces a great reactivity to your
 * dandelion-based applications.
 * <p>
 * The possible actions that can interactively be performed by the InteractiveFrame are
 * {@link remixlab.dandelion.core.Constants.ClickAction}, {@link remixlab.dandelion.core.Constants.DOF1Action},
 * {@link remixlab.dandelion.core.Constants.DOF2Action}, {@link remixlab.dandelion.core.Constants.DOF3Action} and
 * {@link remixlab.dandelion.core.Constants.DOF6Action}. The {@link remixlab.dandelion.core.AbstractScene#motionAgent()}
 * provides high-level methods to handle some of these actions, e.g., a {@link remixlab.dandelion.agent.MouseAgent} can
 * handle up to {@link remixlab.dandelion.core.Constants.DOF2Action}s
 * <p>
 * <b>Note:</b> Once created, the InteractiveFrame is automatically added to the scene
 * {@link remixlab.bias.core.InputHandler#agents()} pool.
 */
public class InteractiveFrame extends Frame implements Grabber, Copyable, Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(grabsInputThreshold).
				append(isInCamPath).
				append(rotSensitivity).
				append(spngRotation).
				append(spngSensitivity).
				append(dampFriction).
				append(sFriction).
				append(transSensitivity).
				append(wheelSensitivity).
				append(flyDisp).
				append(flySpd).
				append(scnUpVec).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		InteractiveFrame other = (InteractiveFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(grabsInputThreshold, other.grabsInputThreshold)
				.append(isInCamPath, other.isInCamPath)
				.append(dampFriction, other.dampFriction)
				.append(sFriction, other.sFriction)
				.append(rotSensitivity, other.rotSensitivity)
				.append(spngRotation, other.spngRotation)
				.append(spngSensitivity, other.spngSensitivity)
				.append(transSensitivity, other.transSensitivity)
				.append(wheelSensitivity, other.wheelSensitivity)
				.append(flyDisp, other.flyDisp)
				.append(flySpd, other.flySpd)
				.append(scnUpVec, other.scnUpVec)
				.isEquals();
	}

	private int									grabsInputThreshold;
	private float								rotSensitivity;
	private float								transSensitivity;
	private float								wheelSensitivity;

	// spinning stuff:
	protected float							eventSpeed;
	private float								spngSensitivity;
	private TimingTask					spinningTimerTask;
	private Rotation						spngRotation;
	protected float							dampFriction;							// new
	// toss and spin share the damp var:
	private float								sFriction;									// new

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or not.
	public boolean							dirIsFixed;
	private boolean							horiz								= true; // Two simultaneous InteractiveFrame require two mice!

	protected boolean						isInCamPath;

	// " D R I V A B L E " S T U F F :
	protected Vec								tDir;
	protected float							flySpd;
	protected TimingTask				flyTimerTask;
	protected Vec								scnUpVec;
	protected Vec								flyDisp;
	protected static final long	FLY_UPDATE_PERDIOD	= 10;

	/**
	 * Default constructor.
	 * <p>
	 * The {@link #translation()} is set to 0, with an identity {@link #rotation()} and no {@link #scaling()} (see Frame
	 * constructor for details). The different sensitivities are set to their default values (see
	 * {@link #rotationSensitivity()} , {@link #translationSensitivity()}, {@link #spinningSensitivity()} and
	 * {@link #wheelSensitivity()}). {@link #dampingFriction()} is set to 0.5.
	 * <p>
	 * <b>Note:</b> the InteractiveFrame is automatically added to the {@link remixlab.bias.core.InputHandler#agents()}
	 * pool.
	 */
	public InteractiveFrame(AbstractScene scn) {
		super(scn);

		scene.inputHandler().addInAllAgentPools(this);
		isInCamPath = false;

		setGrabsInputThreshold(10);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);

		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);

		spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		scene.registerTimingTask(spinningTimerTask);

		scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);

		if (!(this instanceof InteractiveEyeFrame))
			setFlySpeed(0.01f * scene.radius());

		flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		scene.registerTimingTask(flyTimerTask);
	}

	/**
	 * Same as {@code this(scn)} and then calls {@link #setReferenceFrame(Frame)} on {@code referenceFrame}.
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame) {
		this(scn);
		this.setReferenceFrame(referenceFrame);
	}

	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);

		for (Agent element : this.scene.inputHandler().agents()) {
			if (this.scene.inputHandler().isInAgentPool(otherFrame, element))
				this.scene.inputHandler().addInAgentPool(this, element);
		}

		this.isInCamPath = otherFrame.isInCamPath;

		this.setGrabsInputThreshold(otherFrame.grabsInputThreshold());
		this.setRotationSensitivity(otherFrame.rotationSensitivity());
		this.setTranslationSensitivity(otherFrame.translationSensitivity());
		this.setWheelSensitivity(otherFrame.wheelSensitivity());

		this.setSpinningSensitivity(otherFrame.spinningSensitivity());
		this.setDampingFriction(otherFrame.dampingFriction());

		this.spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		this.scene.registerTimingTask(spinningTimerTask);

		this.scnUpVec = new Vec();
		this.scnUpVec.set(otherFrame.sceneUpVector());
		this.flyDisp = new Vec();
		this.flyDisp.set(otherFrame.flyDisp);
		this.setFlySpeed(otherFrame.flySpeed());

		this.flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		this.scene.registerTimingTask(flyTimerTask);
	}

	@Override
	public InteractiveFrame get() {
		return new InteractiveFrame(this);
	}

	/**
	 * Ad-hoc constructor needed to make editable an Eye path defined by a KeyFrameInterpolator.
	 * <p>
	 * Constructs a Frame from the the {@code iFrame} {@link #translation()}, {@link #rotation()} and {@link #scaling()}
	 * and immediately adds it to the scene {@link remixlab.bias.core.InputHandler#agents()} pool.
	 * <p>
	 * A call on {@link #isInEyePath()} on this Frame will return {@code true}.
	 * 
	 * <b>Attention:</b> Internal use. You should not call this constructor in your own applications.
	 * 
	 * @see remixlab.dandelion.core.Eye#addKeyFrameToPath(int)
	 */
	protected InteractiveFrame(AbstractScene scn, InteractiveEyeFrame iFrame) {
		super(scn, iFrame.translation().get(), iFrame.rotation().get(), iFrame.scaling());

		isInCamPath = true;

		setGrabsInputThreshold(10);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);

		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);

		spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		scene.registerTimingTask(spinningTimerTask);

		scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);
		setFlySpeed(0.0f);
		flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		scene.registerTimingTask(flyTimerTask);
	}

	/**
	 * Returns {@code true} if the InteractiveFrame forms part of an Eye path and {@code false} otherwise.
	 * 
	 */
	public boolean isInEyePath() {
		return isInCamPath;
	}

	/**
	 * Returns the grabs input threshold which is used by this interactive frame to {@link #checkIfGrabsInput(BogusEvent)}
	 * .
	 * 
	 * @see #setGrabsInputThreshold(int)
	 */
	public int grabsInputThreshold() {
		return grabsInputThreshold;
	}

	/**
	 * Sets the number of pixels that defined the {@link #checkIfGrabsInput(BogusEvent)} condition.
	 * 
	 * @param threshold
	 *          number of pixels that defined the {@link #checkIfGrabsInput(BogusEvent)} condition. Default value is 10
	 *          pixels (which is set in the constructor). Negative values are silently ignored.
	 * 
	 * @see #grabsInputThreshold()
	 * @see #checkIfGrabsInput(BogusEvent)
	 */
	public void setGrabsInputThreshold(int threshold) {
		if (threshold >= 0)
			grabsInputThreshold = threshold;
	}

	/**
	 * Implementation of the Grabber main method.
	 * <p>
	 * The InteractiveFrame {@link #grabsInput(Agent)} when the event coordinates is within a
	 * {@link #grabsInputThreshold()} pixels region around its
	 * {@link remixlab.dandelion.core.Eye#projectedCoordinatesOf(Vec)} {@link #position()}.
	 */
	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		DOF2Event event2 = null;

		if ((!(event instanceof MotionEvent)) || (event instanceof DOF1Event)) {
			throw new RuntimeException("Grabbing an interactive frame requires at least a DOF2 event");
		}

		if (event instanceof DOF2Event)
			event2 = ((DOF2Event) event).get();
		else if (event instanceof DOF3Event)
			event2 = ((DOF3Event) event).dof2Event();
		else if (event instanceof DOF6Event)
			event2 = ((DOF6Event) event).dof3Event().dof2Event();

		Vec proj = scene.eye().projectedCoordinatesOf(position());

		return ((Math.abs(event2.x() - proj.vec[0]) < grabsInputThreshold()) && (Math.abs(event2.y() - proj.vec[1]) < grabsInputThreshold()));
	}

	/**
	 * Returns {@code true} when this frame grabs the Scene's {@code agent}.
	 * 
	 * @see #checkIfGrabsInput(BogusEvent)
	 */
	@Override
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	/**
	 * Returns {@code agent.isInPool(this)}.
	 * 
	 * @see remixlab.bias.core.Agent#isInPool(Grabber)
	 */
	public boolean isInAgentPool(Agent agent) {
		return agent.isInPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {agent.addInPool(this)}.
	 * 
	 * @see remixlab.bias.core.Agent#addInPool(Grabber)
	 */
	public void addInAgentPool(Agent agent) {
		agent.addInPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {@code agent.removeFromPool(this)}.
	 * 
	 * @see remixlab.bias.core.Agent#removeFromPool(Grabber)
	 */
	public void removeFromAgentPool(Agent agent) {
		agent.removeFromPool(this);
	}

	/**
	 * Defines the {@link #rotationSensitivity()}.
	 */
	public final void setRotationSensitivity(float sensitivity) {
		rotSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #translationSensitivity()}.
	 */
	public final void setTranslationSensitivity(float sensitivity) {
		transSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #spinningSensitivity()}.
	 */
	public final void setSpinningSensitivity(float sensitivity) {
		spngSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public final void setWheelSensitivity(float sensitivity) {
		wheelSensitivity = sensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the InteractiveFrame rotation.
	 * <p>
	 * Default value is 1.0 (which matches an identical mouse displacement), a higher value will generate a larger
	 * rotation (and inversely for lower values). A 0.0 value will forbid rotation (see also {@link #constraint()}).
	 * 
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float rotationSensitivity() {
		return rotSensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the InteractiveFrame translation.
	 * <p>
	 * Default value is 1.0 which in the case of a mouse interaction makes the InteractiveFrame precisely stays under the
	 * mouse cursor.
	 * <p>
	 * With an identical gesture displacement, a higher value will generate a larger translation (and inversely for lower
	 * values). A 0.0 value will forbid translation (see also {@link #constraint()}).
	 * 
	 * @see #setTranslationSensitivity(float)
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float translationSensitivity() {
		return transSensitivity;
	}

	/**
	 * Returns the minimum gesture speed required to make the InteractiveFrame {@link #spin()}. Spinning requires to set
	 * to {@link #dampingFriction()} to 0.
	 * <p>
	 * See {@link #spin()}, {@link #spinningRotation()} and {@link #startSpinning(MotionEvent)} for details.
	 * <p>
	 * Gesture speed is expressed in pixels per milliseconds. Default value is 0.3 (300 pixels per second). Use
	 * {@link #setSpinningSensitivity(float)} to tune this value. A higher value will make spinning more difficult (a
	 * value of 100.0 forbids spinning in practice).
	 * 
	 * @see #setSpinningSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #wheelSensitivity()
	 * @see #setDampingFriction(float)
	 */
	public final float spinningSensitivity() {
		return spngSensitivity;
	}

	/**
	 * Returns the wheel sensitivity.
	 * <p>
	 * Default value is 20.0. A higher value will make the wheel action more efficient (usually meaning a faster zoom).
	 * Use a negative value to invert the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 */
	public float wheelSensitivity() {
		return wheelSensitivity;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is spinning.
	 * <p>
	 * During spinning, {@link #spin()} rotates the InteractiveFrame by its {@link #spinningRotation()} at a frequency
	 * defined when the InteractiveFrame {@link #startSpinning(MotionEvent)}.
	 * <p>
	 * Use {@link #startSpinning(MotionEvent)} and {@link #stopSpinning()} to change this state. Default value is
	 * {@code false}.
	 * 
	 * @see #isTossing()
	 */
	public final boolean isSpinning() {
		return spinningTimerTask.isActive();
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is tossing.
	 * <p>
	 * During tossing, {@link #toss()} translates the InteractiveFrame by its {@link #tossingDirection()} at a frequency
	 * defined when the InteractiveFrame {@link #startTossing(MotionEvent)}.
	 * <p>
	 * Use {@link #startTossing(MotionEvent)} and {@link #stopTossing()} to change this state. Default value is
	 * {@code false}.
	 * 
	 * {@link #isSpinning()}
	 */
	public final boolean isTossing() {
		return flyTimerTask.isActive();
	}

	/**
	 * Returns the incremental rotation that is applied by {@link #spin()} to the InteractiveFrame orientation when it
	 * {@link #isSpinning()}.
	 * <p>
	 * Default value is a {@code null} rotation. Use {@link #setSpinningRotation(Rotation)} to change this value.
	 * <p>
	 * The {@link #spinningRotation()} axis is defined in the InteractiveFrame coordinate system. You can use
	 * {@link remixlab.dandelion.core.Frame#transformOfFrom(Vec, Frame)} to convert this axis from another Frame
	 * coordinate system.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #tossingDirection()
	 */
	public final Rotation spinningRotation() {
		return spngRotation;
	}

	/**
	 * Returns the incremental translation that is applied by {@link #toss()} to the InteractiveFrame position when it
	 * {@link #isTossing()}.
	 * <p>
	 * Default value is no translation. Use {@link #setTossingDirection(Vec)} to change this value.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #spinningRotation()
	 */
	public final Vec tossingDirection() {
		return tDir;
	}

	/**
	 * Defines the {@link #spinningRotation()}. Its axis is defined in the InteractiveFrame coordinate system.
	 * 
	 * @see #setTossingDirection(Vec)
	 */
	public final void setSpinningRotation(Rotation spinningRotation) {
		spngRotation = spinningRotation;
	}

	/**
	 * Defines the {@link #tossingDirection()} in the InteractiveFrame coordinate system.
	 * 
	 * @see #setSpinningRotation(Rotation)
	 */
	public final void setTossingDirection(Vec dir) {
		tDir = dir;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is being manipulated with an agent.
	 */
	public boolean isInInteraction() {
		return currentAction != null;
	}

	/**
	 * Stops the spinning motion started using {@link #startSpinning(MotionEvent)}. {@link #isSpinning()} will return
	 * {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #spin()}, since spinning may be decelerated according to
	 * {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public final void stopSpinning() {
		spinningTimerTask.stop();
	}

	/**
	 * Stops the tossing motion started using {@link #startTossing(MotionEvent)}. {@link #isTossing()} will return
	 * {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #toss()}, since tossing may be decelerated according to
	 * {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #spin()
	 */
	public final void stopTossing() {
		flyTimerTask.stop();
	}

	/**
	 * Starts the spinning of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #spin()} every {@code updateInterval} milliseconds. The
	 * InteractiveFrame {@link #isSpinning()} until you call {@link #stopSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public void startSpinning(MotionEvent e) {
		eventSpeed = e.speed();
		int updateInterval = (int) e.delay();
		if (updateInterval > 0)
			spinningTimerTask.run(updateInterval);
	}

	/**
	 * Starts the tossing of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #toss()} every FLY_UPDATE_PERDIOD milliseconds. The
	 * InteractiveFrame {@link #isTossing()} until you call {@link #stopTossing()}.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #spin()
	 */
	public void startTossing(MotionEvent e) {
		eventSpeed = e.speed();
		flyTimerTask.run(FLY_UPDATE_PERDIOD);
	}

	/**
	 * Rotates the InteractiveFrame by its {@link #spinningRotation()}. Called by a timer when the InteractiveFrame
	 * {@link #isSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public void spin() {
		if (Util.nonZero(dampingFriction())) {
			if (eventSpeed == 0) {
				stopSpinning();
				return;
			}
			rotate(spinningRotation());
			recomputeSpinningRotation();
		}
		else
			rotate(spinningRotation());
	}

	/**
	 * Translates the InteractiveFrame by its {@link #tossingDirection()}. Invoked by a timer when the InteractiveFrame is
	 * performing the DRIVE, MOVE_BACKWARD or MOVE_FORWARD dandelion actions.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #spin()
	 */
	public void toss() {
		if (Util.nonZero(dampingFriction())) {
			if (eventSpeed == 0) {
				stopTossing();
				return;
			}
			translate(tossingDirection());
			recomputeTossingDirection();
		}
		else
			translate(tossingDirection());
	}

	/**
	 * Defines the {@link #dampingFriction()}. Values must be in the range [0..1].
	 */
	public void setDampingFriction(float f) {
		if (f < 0 || f > 1)
			return;
		dampFriction = f;
		setDampingFrictionFx(dampFriction);
	}

	/**
	 * Defines the spinning deceleration.
	 * <p>
	 * Default value is 0.5. Use {@link #setDampingFriction(float)} to tune this value. A higher value will make damping
	 * more difficult (a value of 1.0 forbids damping).
	 */
	public float dampingFriction() {
		return dampFriction;
	}

	/**
	 * Internal use.
	 * <p>
	 * Computes and caches the value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected void setDampingFrictionFx(float spinningFriction) {
		sFriction = spinningFriction * spinningFriction * spinningFriction;
	}

	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected float dampingFrictionFx() {
		return sFriction;
	}

	/**
	 * Internal method. Recomputes the {@link #spinningRotation()} according to {@link #dampingFriction()}.
	 * 
	 * @see #recomputeTossingDirection()
	 */
	protected void recomputeSpinningRotation() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFrictionFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;
		// float currSpeed = eventSpeed;
		if (scene.is3D())
			((Quat) spinningRotation()).fromAxisAngle(((Quat) spinningRotation()).axis(), spinningRotation().angle()
					* (eventSpeed / prevSpeed));
		else
			this.setSpinningRotation(new Rot(spinningRotation().angle() * (eventSpeed / prevSpeed)));
	}

	/**
	 * Internal method. Recomputes the {@link #tossingDirection()} according to {@link #dampingFriction()}.
	 * 
	 * @see #recomputeSpinningRotation()
	 */
	protected void recomputeTossingDirection() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFrictionFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;

		flyDisp.setZ(flyDisp.z() * (eventSpeed / prevSpeed));

		if (scene.is2D())
			setTossingDirection(localInverseTransformOf(flyDisp));
		else
			setTossingDirection(rotation().rotate(flyDisp));
	}

	/**
	 * Returns the fly speed, expressed in virtual scene units.
	 * <p>
	 * It corresponds to the incremental displacement that is periodically applied to the InteractiveFrame position when a
	 * MOVE_FORWARD or MOVE_BACKWARD action is proceeded.
	 * <p>
	 * <b>Attention:</b> When the InteractiveFrame is set as the {@link remixlab.dandelion.core.Eye#frame()} or when it is
	 * set as the {@link remixlab.dandelion.core.AbstractScene#avatar()} (which indeed is an instance of the
	 * InteractiveAvatarFrame class), this value is set according to the
	 * {@link remixlab.dandelion.core.AbstractScene#radius()} by
	 * {@link remixlab.dandelion.core.AbstractScene#setRadius(float)}.
	 */
	public float flySpeed() {
		return flySpd;
	}

	/**
	 * Sets the {@link #flySpeed()}, defined in virtual scene units.
	 * <p>
	 * Default value is 0.0, but it is modified according to the {@link remixlab.dandelion.core.AbstractScene#radius()}
	 * when the InteractiveFrame is set as the {@link remixlab.dandelion.core.Eye#frame()} (which indeed is an instance of
	 * the InteractiveEyeFrame class) or when the InteractiveFrame is set as the
	 * {@link remixlab.dandelion.core.AbstractScene#avatar()} (which indeed is an instance of the InteractiveAvatarFrame
	 * class).
	 */
	public void setFlySpeed(float speed) {
		flySpd = speed;
	}

	/**
	 * Returns the up vector used in fly mode, expressed in the world coordinate system.
	 * <p>
	 * Fly mode corresponds to the MOVE_FORWARD and MOVE_BACKWARD action bindings. In these modes, horizontal
	 * displacements of the mouse rotate the InteractiveFrame around this vector. Vertical displacements rotate always
	 * around the frame {@code X} axis.
	 * <p>
	 * This value is also used within the CAD_ROTATE action to define the up vector (and incidentally the 'horizon' plane)
	 * around which the camera will rotate.
	 * <p>
	 * Default value is (0,1,0), but it is updated by the Eye when set as its {@link remixlab.dandelion.core.Eye#frame()}.
	 * {@link remixlab.dandelion.core.Eye#setOrientation(Rotation)} and
	 * {@link remixlab.dandelion.core.Eye#setUpVector(Vec)} modify this value and should be used instead.
	 */
	public Vec sceneUpVector() {
		return scnUpVec;
	}

	/**
	 * Sets the {@link #sceneUpVector()}, defined in the world coordinate system.
	 * <p>
	 * Default value is (0,1,0), but it is updated by the Eye when set as its {@link remixlab.dandelion.core.Eye#frame()}.
	 * Use {@link remixlab.dandelion.core.Eye#setUpVector(Vec)} instead in that case.
	 */
	public void setSceneUpVector(Vec up) {
		scnUpVec = up;
	}

	/**
	 * This method will be called by the Eye when its orientation is changed, so that the {@link #sceneUpVector()} is
	 * changed accordingly. You should not need to call this method.
	 */
	public final void updateSceneUpVector() {
		scnUpVec = orientation().rotate(new Vec(0.0f, 1.0f, 0.0f));
	}

	/**
	 * <a href="http://en.wikipedia.org/wiki/Euler_angles#Extrinsic_rotations">Extrinsic rotation</a> about the
	 * {@link remixlab.dandelion.core.AbstractScene#eye()} {@link remixlab.dandelion.core.InteractiveEyeFrame} axes.
	 * 
	 * @param roll
	 *          Rotation angle in radians around the Eye x-Axis
	 * @param pitch
	 *          Rotation angle in radians around the Eye y-Axis
	 * @param yaw
	 *          Rotation angle in radians around the Eye z-Axis
	 * 
	 * @see remixlab.dandelion.geom.Quat#fromEulerAngles(float, float, float)
	 */
	public void rotateAroundEyeAxes(float roll, float pitch, float yaw) {
		if (scene.is2D()) {
			AbstractScene.showDepthWarning("rotateAroundEyeAxes");
			return;
		}
		Vec trans = new Vec();
		Quat q = new Quat(scene.isLeftHanded() ? roll : -roll, -pitch, scene.isLeftHanded() ? yaw : -yaw);
		// trans = scene.camera().projectedCoordinatesOf(position());
		trans.set(-q.x(), -q.y(), -q.z());
		trans = scene.camera().frame().orientation().rotate(trans);
		trans = transformOf(trans);
		q.setX(trans.x());
		q.setY(trans.y());
		q.setZ(trans.z());
		rotate(q);
	}

	@Override
	public void performInteraction(BogusEvent e) {
		// TODO following line prevents spinning when frameRate is low (as P5 default)
		// if( isSpinning() && Util.nonZero(dampingFriction()) ) stopSpinning();
		stopTossing();
		if (e == null)
			return;
		if (e instanceof KeyboardEvent) {
			scene.performInteraction(e);
			return;
		}
		// new
		if (e instanceof ClickEvent) {
			ClickEvent clickEvent = (ClickEvent) e;
			if (clickEvent.action() == null)
				return;
			if (clickEvent.action() != ClickAction.CENTER_FRAME &&
					clickEvent.action() != ClickAction.ALIGN_FRAME &&
					clickEvent.action() != ClickAction.ZOOM_ON_PIXEL &&
					clickEvent.action() != ClickAction.ANCHOR_FROM_PIXEL &&
					clickEvent.action() != ClickAction.CUSTOM) {
				scene.performInteraction(e); // ;)
				return;
			}
			if ((scene.is2D()) && (((DandelionAction) clickEvent.action().referenceAction()).is2D())) {
				cEvent = (ClickEvent) e.get();
				execAction2D(((DandelionAction) clickEvent.action().referenceAction()));
				return;
			}
			else if (scene.is3D()) {
				cEvent = (ClickEvent) e.get();
				execAction3D(((DandelionAction) clickEvent.action().referenceAction()));
				return;
			}
		}
		// end
		// then it's a MotionEvent
		MotionEvent motionEvent;
		if (e instanceof MotionEvent)
			motionEvent = (MotionEvent) e;
		else
			return;
		// same as no action
		if (motionEvent.action() == null)
			return;
		if (scene.is2D())
			if ((((DandelionAction) motionEvent.action().referenceAction()).is2D()))
				execAction2D(reduceEvent(motionEvent));
			else
				AbstractScene.showDepthWarning((DandelionAction) motionEvent.action().referenceAction());
		else if (scene.is3D())
			execAction3D(reduceEvent(motionEvent));
	}

	// MotionEvent currentEvent;
	ClickEvent			cEvent;
	DOF1Event				e1;
	DOF2Event				e2;
	DOF3Event				e3;
	DOF6Event				e6;
	DandelionAction	currentAction;

	/**
	 * Internal use. Utility routine for reducing the bogus motion event into a dandelion action.
	 */
	protected DandelionAction reduceEvent(MotionEvent e) {
		currentAction = (DandelionAction) ((BogusEvent) e).action().referenceAction();
		if (currentAction == null)
			return null;

		int dofs = currentAction.dofs();
		boolean fromY = currentAction == DandelionAction.ROTATE_X || currentAction == DandelionAction.TRANSLATE_Y
				|| currentAction == DandelionAction.TRANSLATE_Z || currentAction == DandelionAction.SCALE
				|| currentAction == DandelionAction.ZOOM;

		switch (dofs) {
		case 1:
			if (e instanceof DOF1Event)
				e1 = (DOF1Event) e.get();
			else if (e instanceof DOF2Event)
				e1 = fromY ? ((DOF2Event) e).dof1Event(false) : ((DOF2Event) e).dof1Event();
			else if (e instanceof DOF3Event)
				e1 = fromY ? ((DOF3Event) e).dof2Event().dof1Event(false) : ((DOF3Event) e).dof2Event().dof1Event();
			else if (e instanceof DOF6Event)
				e1 = fromY ? ((DOF6Event) e).dof3Event().dof2Event().dof1Event(false) : ((DOF6Event) e).dof3Event().dof2Event()
						.dof1Event();
			break;
		case 2:
			if (e instanceof DOF2Event)
				e2 = ((DOF2Event) e).get();
			else if (e instanceof DOF3Event)
				e2 = ((DOF3Event) e).dof2Event();
			else if (e instanceof DOF6Event)
				e2 = ((DOF6Event) e).dof3Event().dof2Event();
			break;
		case 3:
			if (e instanceof DOF3Event)
				e3 = ((DOF3Event) e).get();
			else if (e instanceof DOF6Event)
				e3 = ((DOF6Event) e).dof3Event();
			if (scene.is2D())
				e2 = e3.dof2Event();
			break;
		case 6:
			if (e instanceof DOF6Event)
				e6 = ((DOF6Event) e).get();
			break;
		default:
			break;
		}
		return currentAction;
	}

	public void performCustomAction() {
		AbstractScene.showMissingImplementationWarning(DandelionAction.CUSTOM, this.getClass().getName());
	}

	/**
	 * Internal use. Main driver implementing all 2D dandelion motion actions.
	 */
	protected void execAction2D(DandelionAction a) {
		if (a == null)
			return;
		Vec trans;
		float deltaX, deltaY, delta;
		Rotation rot;
		switch (a) {
		case CUSTOM:
			performCustomAction();
			break;
		case TRANSLATE_X:
			translateFromEye(new Vec(delta1(), 0), (e1.action() != null) ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Y:
			translateFromEye(new Vec(0, scene.isRightHanded() ? -delta1() : delta1()), (e1.action() != null) ? 1
					: translationSensitivity());
			break;
		case ROTATE_Z:
			rot = new Rot(scene.isRightHanded() ? computeAngle() : -computeAngle());
			rotate(rot);
			setSpinningRotation(rot);
			break;
		case ROTATE:
		case SCREEN_ROTATE:
			rot = computeRot(scene.window().projectedCoordinatesOf(position()));
			if (e2.isRelative()) {
				setSpinningRotation(rot);
				if (Util.nonZero(dampingFriction()))
					startSpinning(e2);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rot);
			break;
		case MOVE_FORWARD:
			rotate(computeRot(scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(flySpeed(), 0.0f, 0.0f);
			trans = localInverseTransformOf(flyDisp);
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case MOVE_BACKWARD:
			rotate(computeRot(scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(-flySpeed(), 0.0f, 0.0f);
			trans = localInverseTransformOf(flyDisp);
			translate(trans);
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case SCREEN_TRANSLATE:
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if (e2.isRelative())
				deltaY = scene.isRightHanded() ? e2.dy() : -e2.dy();
			else
				deltaY = scene.isRightHanded() ? e2.y() : -e2.y();
			int dir = originalDirection(e2);
			if (dir == 1)
				translateFromEye(new Vec(deltaX, 0.0f, 0.0f));
			else if (dir == -1)
				translateFromEye(new Vec(0.0f, -deltaY, 0.0f));
			break;
		case TRANSLATE:
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if (e2.isRelative())
				deltaY = scene.isRightHanded() ? e2.dy() : -e2.dy();
			else
				deltaY = scene.isRightHanded() ? e2.y() : -e2.y();
			translateFromEye(new Vec(deltaX, -deltaY, 0.0f));
			break;
		case SCALE:
			delta = delta1();
			float s = 1 + Math.abs(delta) / (float) scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case CENTER_FRAME:
			projectOnLine(scene.window().position(), scene.window().viewDirection());
			break;
		case ALIGN_FRAME:
			alignWithFrame(scene.window().frame());
			break;
		default:
			AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	/**
	 * Internal use. Main driver implementing all 3D dandelion motion actions.
	 */
	protected void execAction3D(DandelionAction a) {
		if (a == null)
			return;
		Quat rot;
		Vec trans;
		float delta;
		float angle;
		switch (a) {
		case CUSTOM:
			performCustomAction();
			break;
		case TRANSLATE_X:
			trans = new Vec(delta1(), 0.0f, 0.0f);
			scale2Fit(trans);
			translateFromEye(trans, (e1.action() != null) ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Y:
			trans = new Vec(0.0f, scene.isRightHanded() ? -delta1() : delta1(), 0.0f);
			scale2Fit(trans);
			translateFromEye(trans, (e1.action() != null) ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Z:
			trans = new Vec(0.0f, 0.0f, delta1());
			scale2Fit(trans);
			translateFromEye(trans, (e1.action() != null) ? 1 : translationSensitivity());
			break;
		case ROTATE_X:
			rotateAroundEyeAxes(computeAngle(), 0, 0);
			break;
		case ROTATE_Y:
			rotateAroundEyeAxes(0, -computeAngle(), 0);
			break;
		case ROTATE_Z:
			rotateAroundEyeAxes(0, 0, -computeAngle());
			break;
		case DRIVE:
			rotate(turnQuaternion(e2.dof1Event(), scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			trans = rotation().rotate(flyDisp);
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case LOOK_AROUND:
			rotate(rollPitchQuaternion(e2, scene.camera()));
			break;
		case MOVE_BACKWARD:
			rotate(rollPitchQuaternion(e2, scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			trans = rotation().rotate(flyDisp);
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case MOVE_FORWARD:
			rotate(rollPitchQuaternion(e2, scene.camera()));
			flyDisp.set(0.0f, 0.0f, -flySpeed());
			trans = rotation().rotate(flyDisp);
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case ROTATE:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = scene.camera().projectedCoordinatesOf(position());
			rot = deformedBallQuaternion(e2, trans.x(), trans.y(), scene.camera());
			trans = rot.axis();
			trans = scene.camera().frame().orientation().rotate(trans);
			trans = transformOf(trans);
			rot = new Quat(trans, -rot.angle());
			setSpinningRotation(rot);
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			break;
		case ROTATE_XYZ:
			// trans = scene.camera().projectedCoordinatesOf(position());
			if (e3.isAbsolute())
				rotateAroundEyeAxes(e3.x(), -e3.y(), -e3.z());
			else
				rotateAroundEyeAxes(e3.dx(), -e3.dy(), -e3.dz());
			break;
		case SCREEN_ROTATE:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = scene.camera().projectedCoordinatesOf(position());
			float prev_angle = (float) Math.atan2(e2.prevY() - trans.vec[1], e2.prevX() - trans.vec[0]);
			angle = (float) Math.atan2(e2.y() - trans.vec[1], e2.x() - trans.vec[0]);
			Vec axis = transformOf(scene.camera().frame().orientation().rotate(new Vec(0.0f, 0.0f, -1.0f)));
			if (scene.isRightHanded())
				rot = new Quat(axis, angle - prev_angle);
			else
				rot = new Quat(axis, prev_angle - angle);
			setSpinningRotation(rot);
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			break;
		case SCREEN_TRANSLATE:
			int dir = originalDirection(e2);
			trans = new Vec();
			if (dir == 1)
				if (e2.isAbsolute())
					trans.set(e2.x(), 0.0f, 0.0f);
				else
					trans.set(e2.dx(), 0.0f, 0.0f);
			else if (dir == -1)
				if (e2.isAbsolute())
					trans.set(0.0f, scene.isRightHanded() ? -e2.y() : e2.y(), 0.0f);
				else
					trans.set(0.0f, scene.isRightHanded() ? -e2.dy() : e2.dy(), 0.0f);
			scale2Fit(trans);
			translateFromEye(trans);
			break;
		case TRANSLATE:
			if (e2.isRelative())
				trans = new Vec(e2.dx(), scene.isRightHanded() ? -e2.dy() : e2.dy(), 0.0f);
			else
				trans = new Vec(e2.x(), scene.isRightHanded() ? -e2.y() : e2.y(), 0.0f);
			scale2Fit(trans);
			translateFromEye(trans);
			break;
		case TRANSLATE_XYZ:
			if (e3.isRelative())
				trans = new Vec(e3.dx(), scene.isRightHanded() ? -e3.dy() : e3.dy(), e3.dz());
			else
				trans = new Vec(e3.x(), scene.isRightHanded() ? -e3.y() : e3.y(), e3.z());
			scale2Fit(trans);
			translateFromEye(trans);
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// A. Translate the iFrame
			if (e6.isRelative())
				trans = new Vec(e6.dx(), scene.isRightHanded() ? -e6.dy() : e6.dy(), e6.dz());
			else
				trans = new Vec(e6.x(), scene.isRightHanded() ? -e6.y() : e6.y(), e6.z());
			scale2Fit(trans);
			translateFromEye(trans);
			// B. Rotate the iFrame
			if (e6.isAbsolute())
				rotateAroundEyeAxes(e6.roll(), -e6.pitch(), -e6.yaw());
			else
				rotateAroundEyeAxes(e6.drx(), -e6.dry(), -e6.drz());
			break;
		case SCALE:
			delta = delta1();
			float s = 1 + Math.abs(delta) / (float) scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case ZOOM:
			// its a wheel wheel :P
			if (e1.action() != null) {
				delta = e1.x() * wheelSensitivity();
				translateFromEye(new Vec(0.0f, 0.0f, Vec.subtract(scene.camera().position(), position()).magnitude() * delta
						/ scene.camera().screenHeight()), 1);
			}
			else {
				delta = e1.isAbsolute() ? e1.x() : e1.dx();
				translateFromEye(new Vec(0.0f, 0.0f, Vec.subtract(scene.camera().position(), position()).magnitude() * delta
						/ scene.camera().screenHeight()));
			}
			break;
		case CENTER_FRAME:
			projectOnLine(scene.camera().position(), scene.camera().viewDirection());
			break;
		case ALIGN_FRAME:
			alignWithFrame(scene.camera().frame());
			break;
		default:
			AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	// micro-actions procedures

	protected void scale2Fit(Vec trans) {
		// Scale to fit the screen relative event displacement
		switch (scene.camera().type()) {
		case PERSPECTIVE:
			trans.multiply(2.0f
					* (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
					* Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2] * scene.camera().frame().magnitude())
					/ scene.camera().screenHeight());
			break;
		case ORTHOGRAPHIC:
			float[] wh = scene.camera().getBoundaryWidthHeight();
			trans.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();
			trans.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
			break;
		}
	}

	protected float delta1() {
		float delta;
		if (e1.action() != null) // its a wheel wheel :P
			delta = e1.x() * wheelSensitivity();
		else if (e1.isAbsolute())
			delta = e1.x();
		else
			delta = e1.dx();
		return delta;
	}

	protected Rot computeRot(Vec trans) {
		Rot rot;
		if (e2.isRelative()) {
			Point prevPos = new Point(e2.prevX(), e2.prevY());
			Point curPos = new Point(e2.x(), e2.y());
			rot = new Rot(new Point(trans.x(), trans.y()), prevPos, curPos);
			rot = new Rot(rot.angle() * rotationSensitivity());
		}
		else
			rot = new Rot(e2.x() * rotationSensitivity());
		if (scene.isRightHanded())
			rot.negate();
		return rot;
	}

	protected float computeAngle() {
		float angle;
		if (e1.action() != null) // its a wheel wheel :P
			angle = (float) Math.PI * e1.x() * wheelSensitivity() / scene.eye().screenWidth();
		else if (e1.isAbsolute())
			angle = (float) Math.PI * e1.x() / scene.eye().screenWidth();
		else
			angle = (float) Math.PI * e1.dx() / scene.eye().screenWidth();
		return angle;
	}

	protected void translateFromEye(Vec trans) {
		translateFromEye(trans, translationSensitivity());
	}

	protected void translateFromEye(Vec trans, float sens) {
		// Transform from eye to world coordinate system.
		trans = scene.is2D() ? scene.window().frame().inverseTransformOf(Vec.multiply(trans, sens))
				: scene.camera().frame().orientation().rotate(Vec.multiply(trans, sens));

		// And then down to frame
		if (referenceFrame() != null)
			trans = referenceFrame().transformOf(trans);
		translate(trans);
	}

	/**
	 * Returns a Quaternion computed according to the mouse motion. Mouse positions are projected on a deformed ball,
	 * centered on ({@code cx}, {@code cy}).
	 */
	protected Quat deformedBallQuaternion(DOF2Event event, float cx, float cy, Camera camera) {
		// TODO absolute events!?
		float x = event.x();
		float y = event.y();
		float prevX = event.prevX();
		float prevY = event.prevY();
		// Points on the deformed ball
		float px = rotationSensitivity() * ((int) prevX - cx) / camera.screenWidth();
		float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int) prevY - cy) : (cy - (int) prevY))
				/ camera.screenHeight();
		float dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
		float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();

		Vec p1 = new Vec(px, py, projectOnBall(px, py));
		Vec p2 = new Vec(dx, dy, projectOnBall(dx, dy));
		// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
		Vec axis = p2.cross(p1);
		float angle = 2.0f * (float) Math.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));
		return new Quat(axis, angle);
	}

	/**
	 * Returns "pseudo-distance" from (x,y) to ball of radius size. For a point inside the ball, it is proportional to the
	 * euclidean distance to the ball. For a point outside the ball, it is proportional to the inverse of this distance
	 * (tends to zero) on the ball, the function is continuous.
	 */
	protected static float projectOnBall(float x, float y) {
		// If you change the size value, change angle computation in deformedBallQuaternion().
		float size = 1.0f;
		float size2 = size * size;
		float size_limit = size2 * 0.5f;

		float d = x * x + y * y;
		return d < size_limit ? (float) Math.sqrt(size2 - d) : size_limit / (float) Math.sqrt(d);
	}

	/**
	 * Returns a Quaternion that is a rotation around current camera Y, proportional to the horizontal mouse position.
	 */
	protected final Quat turnQuaternion(DOF1Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		return new Quat(new Vec(0.0f, 1.0f, 0.0f), rotationSensitivity() * (-deltaX) / camera.screenWidth());
	}

	/**
	 * Returns a Quaternion that is the composition of two rotations, inferred from the mouse roll (X axis) and pitch (
	 * {@link #sceneUpVector()} axis).
	 */
	protected final Quat rollPitchQuaternion(DOF2Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		float deltaY = event.isAbsolute() ? event.y() : event.dy();

		if (scene.isRightHanded())
			deltaY = -deltaY;

		Quat rotX = new Quat(new Vec(1.0f, 0.0f, 0.0f), rotationSensitivity() * deltaY / camera.screenHeight());
		Quat rotY = new Quat(transformOf(sceneUpVector()), rotationSensitivity() * (-deltaX) / camera.screenWidth());
		return Quat.multiply(rotY, rotX);
	}

	/**
	 * Return 1 if mouse motion was started horizontally and -1 if it was more vertical. Returns 0 if this could not be
	 * determined yet (perfect diagonal motion, rare).
	 */
	protected int originalDirection(DOF2Event event) {
		if (!dirIsFixed) {
			Point delta;
			if (event.isAbsolute())
				delta = new Point(event.x(), event.y());
			else
				delta = new Point(event.dx(), event.dy());
			dirIsFixed = Math.abs(delta.x()) != Math.abs(delta.y());
			horiz = Math.abs(delta.x()) > Math.abs(delta.y());
		}

		if (dirIsFixed)
			if (horiz)
				return 1;
			else
				return -1;
		else
			return 0;
	}
}
