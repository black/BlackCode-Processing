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
 * Generic interface defining user action (sub)groups.
 * <p>
 * (User-defined) global Actions in bias should be defined by a third-party simply using an Enum. This interface allows
 * grouping items of that global action Enum together, thus possibly forming action sub-groups. Each item in the action
 * sub-group should be mapped back to an item in the global Enum set (see {@link #referenceAction()}).
 * <p>
 * <b>Note:</b> User-defined actions subgroups implementing this Interface are used to parameterize
 * {@link remixlab.bias.agent.profile.Profile}s which are then used to parameterize
 * {@link remixlab.bias.agent.ActionAgent}s. The idea being that user-defined actions may be grouped together according
 * to the BogusEvent type needed to implement them (see
 * {@link remixlab.bias.core.Grabber#performInteraction(remixlab.bias.core.BogusEvent)}). Parsing the BogusEvent thus
 * requires the proper {@link remixlab.bias.core.Agent} type.
 * <p>
 * <b>Observation</b> Enums provide an easy (typical) implementation of this Interface. For example, given the following
 * global Action set:
 * 
 * <pre>
 * {@code
 * public enum GlobalAction {
 *   CHANGE_COLOR,
 *   CHANGE_STROKE_WEIGHT,
 *   CHANGE_POSITION,
 *   CHANGE_SHAPE
 * }
 * }
 * </pre>
 * 
 * An implementation of an Action group defined with the CHANGE_POSITION and CHANGE_SHAPE items, would look like this:
 * 
 * <pre>
 * {@code
 * public enum MotionAction implements Action<GlobalAction> {
 *   CHANGE_POSITION(GlobalAction.CHANGE_POSITION), 
 *   CHANGE_SHAPE(GlobalAction.CHANGE_SHAPE);
 *   
 *   public GlobalAction referenceAction() {
 *     return act;
 *   }
 * 
 *   public String description() {
 *     return "A simple motion action";
 *   }
 * 
 *   public int dofs() {
 *     return 2;
 *   }
 * 
 *   GlobalAction act;
 * 
 *   MotionAction(GlobalAction a) {
 *     act = a;
 *   }
 * }
 * }
 * </pre>
 * 
 * @param <E>
 *          Global enum action set.
 */
public interface Action<E extends Enum<E>> {
	/**
	 * Returns group to global action item mappings.
	 */
	E referenceAction();

	/**
	 * Returns a description of the action.
	 */
	String description();

	/**
	 * Returns the degrees-of-freedom needed to perform the action.
	 */
	public int dofs();
}
