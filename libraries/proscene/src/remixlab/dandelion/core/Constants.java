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

import remixlab.bias.core.Action;

public interface Constants {
	/**
	 * Which object is performing the motion action.
	 */
	public enum Target {
		EYE, FRAME
	}

	/**
	 * Dandelion global action enum. All enum sub-groups point-out to this one.
	 */
	public enum DandelionAction {
		// KEYfRAMES
		/**
		 * Add keyframe to path 1
		 */
		ADD_KEYFRAME_TO_PATH_1("Add keyframe to path 1", true, 0),
		/**
		 * Play path 1
		 */
		PLAY_PATH_1("Play path 1", true, 0),
		/**
		 * Delete path 1
		 */
		DELETE_PATH_1("Delete path 1", true, 0),
		/**
		 * Add keyframe to path 2
		 */
		ADD_KEYFRAME_TO_PATH_2("Add keyframe to path 2", true, 0),
		/**
		 * Play path 2
		 */
		PLAY_PATH_2("Play path 2", true, 0),
		/**
		 * Delete path 2
		 */
		DELETE_PATH_2("Delete path 2", true, 0),
		/**
		 * Add keyframe to path 3
		 */
		ADD_KEYFRAME_TO_PATH_3("Add keyframe to path 3", true, 0),
		/**
		 * Play path 3
		 */
		PLAY_PATH_3("Play path 3", true, 0),
		/**
		 * Delete path 3
		 */
		DELETE_PATH_3("Delete path 3", true, 0),

		// CLICk ACTIONs
		/**
		 * Center frame
		 */
		CENTER_FRAME("Center frame", true, 0),
		/**
		 * Align frame with world
		 */
		ALIGN_FRAME("Align frame with world", true, 0),

		// Click actions require cursor pos:
		/**
		 * Interpolate the eye to zoom on pixel
		 */
		ZOOM_ON_PIXEL("Interpolate the eye to zoom on pixel", true, 0),
		/**
		 * Set the anchor from the pixel under the pointer
		 */
		ANCHOR_FROM_PIXEL("Set the anchor from the pixel under the pointer", true, 0),

		// GENERAL KEYBOARD ACTIONs
		/**
		 * Toggles axes visual hint
		 */
		TOGGLE_AXES_VISUAL_HINT("Toggles axes visual hint", true, 0),
		/**
		 * Toggles grid visual hint
		 */
		TOGGLE_GRID_VISUAL_HINT("Toggles grid visual hint", true, 0),
		/**
		 * Toggles paths visual hint
		 */
		TOGGLE_PATHS_VISUAL_HINT("Toggles paths visual hint", true, 0),
		/**
		 * Toggles frame visual hint
		 */
		TOGGLE_PICKING_VISUAL_HINT("Toggles frame visual hint", true, 0),
		/**
		 * Toggles animation
		 */
		TOGGLE_ANIMATION("Toggles animation", true, 0),
		/**
		 * Toggles camera type
		 */
		TOGGLE_CAMERA_TYPE("Toggles camera type", false, 0),
		/**
		 * Displays the global help
		 */
		DISPLAY_INFO("Displays the global help", true, 0),
		/**
		 * Zoom to fit the scene
		 */
		INTERPOLATE_TO_FIT("Zoom to fit the scene", true, 0),
		/**
		 * Reset the anchor to the world origin
		 */
		RESET_ANCHOR("Reset the anchor to the world origin", true, 0),
		/**
		 * Show the whole scene
		 */
		SHOW_ALL("Show the whole scene", true, 0),

		// CAMERA KEYBOARD ACTIONs
		/**
		 * Move eye to the left
		 */
		MOVE_LEFT("Move eye to the left", true, 0),
		/**
		 * Move eye to the right
		 */
		MOVE_RIGHT("Move eye to the right", true, 0),
		/**
		 * Move eye up
		 */
		MOVE_UP("Move eye up", true, 0),
		/**
		 * Move eye down
		 */
		MOVE_DOWN("Move eye down", true, 0),
		/**
		 * Increase frame rotation sensitivity
		 */
		INCREASE_ROTATION_SENSITIVITY("Increase frame rotation sensitivity", true, 0),
		/**
		 * Decrease frame rotation sensitivity
		 */
		DECREASE_ROTATION_SENSITIVITY("Decrease frame rotation sensitivity", true, 0),
		/**
		 * Increase eye fly speed
		 */
		INCREASE_FLY_SPEED("Increase eye fly speed", true, 0),
		/**
		 * Decrease eye fly speed
		 */
		DECREASE_FLY_SPEED("Decrease eye fly speed", true, 0),

		// Wheel
		/**
		 * Scale frame
		 */
		SCALE("Scale frame", true, 1),
		/**
		 * Zoom eye
		 */
		ZOOM("Zoom eye", false, 1),
		/**
		 * Zoom eye on anchor
		 */
		ZOOM_ON_ANCHOR("Zoom eye on anchor", false, 1),
		/**
		 * Translate along screen X axis
		 */
		TRANSLATE_X("Translate along screen X axis", true, 1),
		/**
		 * Translate along screen Y axis
		 */
		TRANSLATE_Y("Translate along screen Y axis", true, 1),
		/**
		 * Translate along screen Z axis
		 */
		TRANSLATE_Z("Translate along screen Z axis", false, 1),
		/**
		 * Rotate frame around screen x axis (eye or interactive frame)
		 */
		ROTATE_X("Rotate frame around screen x axis (eye or interactive frame)", false, 1),
		/**
		 * Rotate frame around screen y axis (eye or interactive frame)
		 */
		ROTATE_Y("Rotate frame around screen y axis (eye or interactive frame)", false, 1),
		/**
		 * Rotate frame around screen z axis (eye or interactive frame)
		 */
		ROTATE_Z("Rotate frame around screen z axis (eye or interactive frame)", true, 1),
		/**
		 * Drive (camera or interactive frame)
		 */
		DRIVE("Drive (camera or interactive frame)", false, 2),

		// 2 DOFs ACTIONs
		/**
		 * Frame (eye or interactive frame) arcball rotate
		 */
		ROTATE("Frame (eye or interactive frame) arcball rotate", true, 2),
		/**
		 * Rotate camera frame as in CAD applications
		 */
		ROTATE_CAD("Rotate camera frame as in CAD applications", false, 2),
		/**
		 * Translate frame (eye or interactive frame)
		 */
		TRANSLATE("Translate frame (eye or interactive frame)", true, 2),
		/**
		 * Move forward frame (camera or interactive frame)
		 */
		MOVE_FORWARD("Move forward frame (camera or interactive frame)", true, 2),
		/**
		 * Move backward frame (camera or interactive frame)
		 */
		MOVE_BACKWARD("Move backward frame (camera or interactive frame)", true, 2),
		/**
		 * Look around with frame (camera or interactive frame)
		 */
		LOOK_AROUND("Look around with frame (camera or interactive frame)", false, 2),
		/**
		 * Screen rotate (eye or interactive frame)
		 */
		SCREEN_ROTATE("Screen rotate (eye or interactive frame)", true, 2),
		/**
		 * Screen translate frame (eye or interactive frame)
		 */
		SCREEN_TRANSLATE("Screen translate frame (eye or interactive frame)", true, 2),
		/**
		 * Zoom on region (eye or interactive frame)
		 */
		ZOOM_ON_REGION("Zoom on region (eye or interactive frame)", true, 2),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz simultaneously
		 */
		TRANSLATE_XYZ("Translate frame (camera or interactive frame) from dx, dy, dz simultaneously", false, 3),
		/**
		 * Rotate frame (camera or interactive frame) from Euler angles
		 */
		ROTATE_XYZ("Rotate frame (camera or interactive frame) from Euler angles", false, 3),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz and rotate it from Euler angles simultaneously
		 */
		TRANSLATE_XYZ_ROTATE_XYZ(
				"Translate frame (camera or interactive frame) from dx, dy, dz and rotate it from Euler angles simultaneously",
				false, 6),
		/**
		 * Move camera on the surface of a sphere using 5-DOF's: 2 rotations around scene anchor, 1 rotation around scene-up
		 * vector and 1 translation along it, and 1 rotation around eye X-axis.
		 */
		HINGE("Move camera on the surface of a sphere using 5-DOF's", false, 6),

		// CUSTOM ACTIONs
		/**
		 * User defined action
		 */
		CUSTOM("User defined action");

		String	description;
		boolean	twoD;
		int			dofs;

		DandelionAction(String description, boolean td, int ds) {
			this.description = description;
			this.twoD = td;
			this.dofs = ds;
		}

		DandelionAction(String description, int ds) {
			this.description = description;
			this.twoD = true;
			this.dofs = ds;
		}

		DandelionAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
			this.dofs = 2;
		}

		DandelionAction(String description) {
			this.description = description;
			this.twoD = true;
			this.dofs = 0;
		}

		/**
		 * Returns a description of the action item.
		 */
		public String description() {
			return description;
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return twoD;
		}

		/**
		 * Returns the degrees-of-freedom needed to perform the action item.
		 */
		public int dofs() {
			return dofs;
		}
	}

	/**
	 * Click action sub-group.
	 */
	public enum ClickAction implements Action<DandelionAction> {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1(DandelionAction.ADD_KEYFRAME_TO_PATH_1),
		PLAY_PATH_1(DandelionAction.PLAY_PATH_1),
		DELETE_PATH_1(DandelionAction.DELETE_PATH_1),
		ADD_KEYFRAME_TO_PATH_2(DandelionAction.ADD_KEYFRAME_TO_PATH_2),
		PLAY_PATH_2(DandelionAction.PLAY_PATH_2),
		DELETE_PATH_2(DandelionAction.DELETE_PATH_2),
		ADD_KEYFRAME_TO_PATH_3(DandelionAction.ADD_KEYFRAME_TO_PATH_3),
		PLAY_PATH_3(DandelionAction.PLAY_PATH_3),
		DELETE_PATH_3(DandelionAction.DELETE_PATH_3),

		// CLICk ACTIONs
		INTERPOLATE_TO_FIT(DandelionAction.INTERPOLATE_TO_FIT),
		CENTER_FRAME(DandelionAction.CENTER_FRAME),
		ALIGN_FRAME(DandelionAction.ALIGN_FRAME),

		// Click actions require cursor pos:
		ZOOM_ON_PIXEL(DandelionAction.ZOOM_ON_PIXEL),
		ANCHOR_FROM_PIXEL(DandelionAction.ANCHOR_FROM_PIXEL),

		// GENERAL KEYBOARD ACTIONs
		TOGGLE_AXES_VISUAL_HINT(DandelionAction.TOGGLE_AXES_VISUAL_HINT),
		TOGGLE_GRID_VISUAL_HINT(DandelionAction.TOGGLE_GRID_VISUAL_HINT),
		TOGGLE_CAMERA_TYPE(DandelionAction.TOGGLE_CAMERA_TYPE),
		TOGGLE_ANIMATION(DandelionAction.TOGGLE_ANIMATION),
		RESET_ANCHOR(DandelionAction.RESET_ANCHOR),
		DISPLAY_INFO(DandelionAction.DISPLAY_INFO),
		TOGGLE_PATHS_VISUAL_HINT(DandelionAction.TOGGLE_PATHS_VISUAL_HINT),
		TOGGLE_PICKING_VISUAL_HINT(DandelionAction.TOGGLE_PICKING_VISUAL_HINT),
		SHOW_ALL(DandelionAction.SHOW_ALL),

		// CAMERA KEYBOARD ACTIONs
		MOVE_LEFT(DandelionAction.MOVE_LEFT),
		MOVE_RIGHT(DandelionAction.MOVE_RIGHT),
		MOVE_UP(DandelionAction.MOVE_UP),
		MOVE_DOWN(DandelionAction.MOVE_DOWN),
		INCREASE_ROTATION_SENSITIVITY(DandelionAction.INCREASE_ROTATION_SENSITIVITY),
		DECREASE_ROTATION_SENSITIVITY(DandelionAction.DECREASE_ROTATION_SENSITIVITY),
		INCREASE_FLY_SPEED(DandelionAction.INCREASE_FLY_SPEED),
		DECREASE_FLY_SPEED(DandelionAction.DECREASE_FLY_SPEED),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		ClickAction(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * Keyboard action sub-group.
	 */
	public enum KeyboardAction implements Action<DandelionAction> {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1(DandelionAction.ADD_KEYFRAME_TO_PATH_1),
		PLAY_PATH_1(DandelionAction.PLAY_PATH_1),
		DELETE_PATH_1(DandelionAction.DELETE_PATH_1),
		ADD_KEYFRAME_TO_PATH_2(DandelionAction.ADD_KEYFRAME_TO_PATH_2),
		PLAY_PATH_2(DandelionAction.PLAY_PATH_2),
		DELETE_PATH_2(DandelionAction.DELETE_PATH_2),
		ADD_KEYFRAME_TO_PATH_3(DandelionAction.ADD_KEYFRAME_TO_PATH_3),
		PLAY_PATH_3(DandelionAction.PLAY_PATH_3),
		DELETE_PATH_3(DandelionAction.DELETE_PATH_3),

		// CLICk ACTIONs
		INTERPOLATE_TO_FIT(DandelionAction.INTERPOLATE_TO_FIT),

		// GENERAL KEYBOARD ACTIONs
		TOGGLE_AXES_VISUAL_HINT(DandelionAction.TOGGLE_AXES_VISUAL_HINT),
		TOGGLE_GRID_VISUAL_HINT(DandelionAction.TOGGLE_GRID_VISUAL_HINT),
		TOGGLE_CAMERA_TYPE(DandelionAction.TOGGLE_CAMERA_TYPE),
		TOGGLE_ANIMATION(DandelionAction.TOGGLE_ANIMATION),
		RESET_ANCHOR(DandelionAction.RESET_ANCHOR),
		DISPLAY_INFO(DandelionAction.DISPLAY_INFO),
		TOGGLE_PATHS_VISUAL_HINT(DandelionAction.TOGGLE_PATHS_VISUAL_HINT),
		TOGGLE_PICKING_VISUAL_HINT(DandelionAction.TOGGLE_PICKING_VISUAL_HINT),
		SHOW_ALL(DandelionAction.SHOW_ALL),

		// CAMERA KEYBOARD ACTIONs
		MOVE_LEFT(DandelionAction.MOVE_LEFT),
		MOVE_RIGHT(DandelionAction.MOVE_RIGHT),
		MOVE_UP(DandelionAction.MOVE_UP),
		MOVE_DOWN(DandelionAction.MOVE_DOWN),
		INCREASE_ROTATION_SENSITIVITY(DandelionAction.INCREASE_ROTATION_SENSITIVITY),
		DECREASE_ROTATION_SENSITIVITY(DandelionAction.DECREASE_ROTATION_SENSITIVITY),
		INCREASE_FLY_SPEED(DandelionAction.INCREASE_FLY_SPEED),
		DECREASE_FLY_SPEED(DandelionAction.DECREASE_FLY_SPEED),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		KeyboardAction(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF1 action sub-group.
	 */
	public enum DOF1Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ZOOM_ON_ANCHOR(DandelionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(DandelionAction.TRANSLATE_X),
		TRANSLATE_Y(DandelionAction.TRANSLATE_Y),
		TRANSLATE_Z(DandelionAction.TRANSLATE_Z),
		ROTATE_X(DandelionAction.ROTATE_X),
		ROTATE_Y(DandelionAction.ROTATE_Y),
		ROTATE_Z(DandelionAction.ROTATE_Z),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF1Action(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * Wheel action sub-group.
	 * 
	 * @deprecated Please refrain from using this type, it will be removed from future releases. Use DOF1Action instead.
	 */
	@Deprecated
	public enum WheelAction implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ZOOM_ON_ANCHOR(DandelionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(DandelionAction.TRANSLATE_X),
		TRANSLATE_Y(DandelionAction.TRANSLATE_Y),
		TRANSLATE_Z(DandelionAction.TRANSLATE_Z),
		ROTATE_X(DandelionAction.ROTATE_X),
		ROTATE_Y(DandelionAction.ROTATE_Y),
		ROTATE_Z(DandelionAction.ROTATE_Z),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		WheelAction(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF2 action sub-group.
	 */
	public enum DOF2Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ZOOM_ON_ANCHOR(DandelionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(DandelionAction.TRANSLATE_X),
		TRANSLATE_Y(DandelionAction.TRANSLATE_Y),
		TRANSLATE_Z(DandelionAction.TRANSLATE_Z),
		ROTATE_X(DandelionAction.ROTATE_X),
		ROTATE_Y(DandelionAction.ROTATE_Y),
		ROTATE_Z(DandelionAction.ROTATE_Z),

		// DOF_2
		DRIVE(DandelionAction.DRIVE),
		ROTATE(DandelionAction.ROTATE),
		ROTATE_CAD(DandelionAction.ROTATE_CAD),
		TRANSLATE(DandelionAction.TRANSLATE),
		MOVE_FORWARD(DandelionAction.MOVE_FORWARD),
		MOVE_BACKWARD(DandelionAction.MOVE_BACKWARD),
		LOOK_AROUND(DandelionAction.LOOK_AROUND),
		SCREEN_ROTATE(DandelionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(DandelionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(DandelionAction.ZOOM_ON_REGION),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF2Action(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF3 action sub-group.
	 */
	public enum DOF3Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ZOOM_ON_ANCHOR(DandelionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(DandelionAction.TRANSLATE_X),
		TRANSLATE_Y(DandelionAction.TRANSLATE_Y),
		TRANSLATE_Z(DandelionAction.TRANSLATE_Z),
		ROTATE_X(DandelionAction.ROTATE_X),
		ROTATE_Y(DandelionAction.ROTATE_Y),
		ROTATE_Z(DandelionAction.ROTATE_Z),

		// DOF_2
		DRIVE(DandelionAction.DRIVE),
		ROTATE(DandelionAction.ROTATE),
		ROTATE_CAD(DandelionAction.ROTATE_CAD),
		TRANSLATE(DandelionAction.TRANSLATE),
		MOVE_FORWARD(DandelionAction.MOVE_FORWARD),
		MOVE_BACKWARD(DandelionAction.MOVE_BACKWARD),
		LOOK_AROUND(DandelionAction.LOOK_AROUND),
		SCREEN_ROTATE(DandelionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(DandelionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(DandelionAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(DandelionAction.TRANSLATE_XYZ),
		ROTATE_XYZ(DandelionAction.ROTATE_XYZ),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF3Action(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF6 action sub-group.
	 */
	public enum DOF6Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ZOOM_ON_ANCHOR(DandelionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(DandelionAction.TRANSLATE_X),
		TRANSLATE_Y(DandelionAction.TRANSLATE_Y),
		TRANSLATE_Z(DandelionAction.TRANSLATE_Z),
		ROTATE_X(DandelionAction.ROTATE_X),
		ROTATE_Y(DandelionAction.ROTATE_Y),
		ROTATE_Z(DandelionAction.ROTATE_Z),

		// DOF_2
		ROTATE(DandelionAction.ROTATE),
		DRIVE(DandelionAction.DRIVE),
		ROTATE_CAD(DandelionAction.ROTATE_CAD),
		TRANSLATE(DandelionAction.TRANSLATE),
		MOVE_FORWARD(DandelionAction.MOVE_FORWARD),
		MOVE_BACKWARD(DandelionAction.MOVE_BACKWARD),
		LOOK_AROUND(DandelionAction.LOOK_AROUND),
		SCREEN_ROTATE(DandelionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(DandelionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(DandelionAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(DandelionAction.TRANSLATE_XYZ),
		ROTATE_XYZ(DandelionAction.ROTATE_XYZ),

		// DOF_4
		HINGE(DandelionAction.HINGE),

		// DOF_6
		TRANSLATE_XYZ_ROTATE_XYZ(DandelionAction.TRANSLATE_XYZ_ROTATE_XYZ),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF6Action(DandelionAction a) {
			act = a;
		}
	}
}
