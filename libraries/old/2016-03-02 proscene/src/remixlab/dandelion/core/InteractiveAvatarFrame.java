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

import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * The InteractiveAvatarFrame class represents an InteractiveFrame that can be tracked by an Eye, i.e., it implements
 * the Trackable interface.
 * <p>
 * The {@link #eyeFrame()} of the camera that is to be tracking the frame (see the documentation of the Trackable
 * interface) is defined in spherical coordinates ({@link #azimuth()}, {@link #inclination()} and
 * {@link #trackingDistance()}) respect to the {@link #position()} (which defines the
 * {@link remixlab.dandelion.core.Eye#at()} Vec).
 */
public class InteractiveAvatarFrame extends InteractiveFrame implements Trackable, Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(eFrame).
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

		InteractiveAvatarFrame other = (InteractiveAvatarFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(eFrame, other.eFrame)
				.isEquals();
	}

	protected Frame		eFrame;

	private Rotation	q;
	private float			trackingDist;

	/**
	 * Constructs an InteractiveAvatarFrame and sets its {@link #trackingDistance()} to
	 * {@link remixlab.dandelion.core.AbstractScene#radius()}/5, {@link #azimuth()} to 0, and {@link #inclination()} to 0.
	 * 
	 * @see remixlab.dandelion.core.AbstractScene#setAvatar(Trackable)
	 */
	public InteractiveAvatarFrame(AbstractScene scn) {
		super(scn);
		eFrame = new Frame(scene);
		q = scene.is3D() ? new Quat((float) Math.PI / 4, 0, 0) : new Rot((float) Math.PI / 4);
		eFrame.setReferenceFrame(this);
		setTrackingDistance(scene.radius() / 5);
		updateEyeFrame();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param otherFrame
	 *          the other interactive avatar frame
	 */
	protected InteractiveAvatarFrame(InteractiveAvatarFrame otherFrame) {
		super(otherFrame);
		this.eFrame = otherFrame.eyeFrame().get();
		this.q = otherFrame.q.get();
		this.setTrackingDistance(otherFrame.trackingDistance());
	}

	/**
	 * Calls {@link #InteractiveAvatarFrame(InteractiveAvatarFrame)} (which is protected) and returns a copy of
	 * {@code this} object.
	 * 
	 * @see #InteractiveAvatarFrame(InteractiveAvatarFrame)
	 */
	@Override
	public InteractiveAvatarFrame get() {
		return new InteractiveAvatarFrame(this);
	}

	/**
	 * Returns the distance between the frame and the tracking camera.
	 */
	public float trackingDistance() {
		return trackingDist;
	}

	@Override
	public void scale(float s) {
		super.scale(s);
		updateEyeFrame();
	}

	/**
	 * Sets the distance between the frame and the tracking camera.
	 */
	public void setTrackingDistance(float d) {
		trackingDist = d;
		updateEyeFrame();
	}

	/**
	 * Returns the azimuth of the tracking camera measured respect to the frame's {@link #zAxis()}.
	 */
	public float azimuth() {
		// azimuth <-> pitch
		if (scene.is3D())
			return ((Quat) q).taitBryanAngles().vec[1];
		else {
			AbstractScene.showDepthWarning("azimuth");
			return 0;
		}
	}

	/**
	 * Sets the {@link #azimuth()} of the tracking camera.
	 */
	public void setAzimuth(float a) {
		if (scene.is3D()) {
			float roll = ((Quat) q).taitBryanAngles().vec[0];
			((Quat) q).fromTaitBryan(roll, a, 0);
			updateEyeFrame();
		}
		else
			AbstractScene.showDepthWarning("setAzimuth");
	}

	/**
	 * Returns the inclination of the tracking camera measured respect to the frame's {@link #yAxis()}.
	 */
	public float inclination() {
		// inclination <-> roll
		if (scene.is3D())
			return ((Quat) q).taitBryanAngles().vec[0];
		else
			return q.angle();
	}

	/**
	 * Sets the {@link #inclination()} of the tracking camera.
	 */
	public void setInclination(float i) {
		if (scene.is3D()) {
			float pitch = ((Quat) q).taitBryanAngles().vec[1];
			((Quat) q).fromTaitBryan(i, pitch, 0);
		}
		else
			q = new Rot(i);
		updateEyeFrame();
	}

	/**
	 * The {@link #eyeFrame()} of the Eye that is to be tracking the frame (see the documentation of the Trackable
	 * interface) is defined in spherical coordinates by means of the {@link #azimuth()}, the {@link #inclination()} and
	 * {@link #trackingDistance()}) respect to the Frame {@link #position()}.
	 */
	public void updateEyeFrame() {
		if (scene.is3D()) {
			Vec p = q.rotate(new Vec(0, 0, 1));
			p.multiply(trackingDistance() / magnitude());
			eFrame.setTranslation(p);
			eFrame.setYAxis(yAxis());
			eFrame.setZAxis(inverseTransformOf(p));
			eFrame.setScaling(scene.eye().frame().scaling());
		}
		else {
			Vec p = q.rotate(new Vec(0, 1));
			p.multiply(trackingDistance() / magnitude());
			eFrame.setTranslation(p);
			eFrame.setYAxis(yAxis());
			float size = Math.min(scene.width(), scene.height());
			eFrame.setScaling((2.5f * trackingDistance() / size)); // window.fitBall which sets the scaling
		}
	}

	// Interface implementation

	/**
	 * Overloading of {@link remixlab.dandelion.core.Trackable#eyeFrame()}. Returns the world coordinates of the camera
	 * position computed in {@link #updateEyeFrame()}.
	 */
	@Override
	public Frame eyeFrame() {
		return eFrame;
	}
}
