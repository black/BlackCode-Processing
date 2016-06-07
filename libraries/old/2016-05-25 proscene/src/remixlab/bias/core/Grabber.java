/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.core;

/**
 * Grabbers are means to attach a user-space object to all the
 * {@link remixlab.bias.core.Agent}s (see
 * {@link remixlab.bias.core.Agent#addGrabber(Grabber)}) through which it's going to be
 * handled. For details, refer to the {@link remixlab.bias.core.Agent} documentation.
 */
public interface Grabber {
  /**
   * Defines the rules to set the grabber as an agent input-grabber.
   * 
   * @see remixlab.bias.core.Agent#updateTrackedGrabber(BogusEvent)
   * @see remixlab.bias.core.Agent#inputGrabber()
   */
  boolean checkIfGrabsInput(BogusEvent event);

  /**
   * Defines how the grabber should react according to the given bogus-event.
   * 
   * @see remixlab.bias.core.Agent#handle(BogusEvent)
   */
  void performInteraction(BogusEvent event);
}
