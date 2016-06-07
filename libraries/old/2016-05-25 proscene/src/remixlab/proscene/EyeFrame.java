/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.dandelion.core.*;

/**
 * A Processing {@link remixlab.dandelion.core.GenericFrame} with a {@link #profile()}
 * instance which allows {@link remixlab.bias.core.Shortcut} to
 * {@link java.lang.reflect.Method} bindings high-level customization.
 * 
 * @see remixlab.dandelion.core.GenericFrame
 * @see remixlab.bias.ext.Profile
 */
public class EyeFrame extends GenericP5Frame {
  public EyeFrame(Eye eye) {
    super(eye);
  }

  protected EyeFrame(EyeFrame otherFrame) {
    super(otherFrame);
  }

  @Override
  public EyeFrame get() {
    return new EyeFrame(this);
  }
}