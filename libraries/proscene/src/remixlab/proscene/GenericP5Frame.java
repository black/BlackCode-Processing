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

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.ext.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.AbstractScene.Platform;
import remixlab.util.*;

/**
 * Internal class defining the common API among the {@link remixlab.proscene.EyeFrame} and
 * the {@link remixlab.proscene.InteractiveFrame} classes.
 * <p>
 * A GenericFrame with a {@link #profile()} instance which allows
 * {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method} bindings
 * high-level customization.
 */
class GenericP5Frame extends GenericFrame {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(profile).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    GenericP5Frame other = (GenericP5Frame) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(profile, other.profile).isEquals();
  }

  @Override
  public Scene scene() {
    return (Scene) gScene;
  }

  protected Profile profile;

  /**
   * Calls {@code super(scn)}, creates the frame {@link #profile()} and calls
   * {@link #setDefaultMouseBindings()} and {@link #setDefaultKeyBindings()}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene).
   */
  public GenericP5Frame(Scene scn) {
    super(scn);
    setProfile(new Profile(this));
    // TODO
    if (Scene.platform() == Platform.PROCESSING_DESKTOP)
      setDefaultMouseBindings();
    // else
    // setDefaultTouchBindings();
    setDefaultKeyBindings();
  }

  /**
   * Calls {@code super(scn), referenceFrame}, creates the frame {@link #profile()} and
   * calls {@link #setDefaultMouseBindings()} and {@link #setDefaultKeyBindings()}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, GenericFrame).
   */
  public GenericP5Frame(Scene scn, GenericFrame referenceFrame) {
    super(scn, referenceFrame);
    setProfile(new Profile(this));
    if (referenceFrame instanceof GenericP5Frame)
      this.profile.from(((GenericP5Frame) referenceFrame).profile);
    else {
      // TODO
      if (Scene.platform() == Platform.PROCESSING_DESKTOP)
        setDefaultMouseBindings();
      // else
      // setDefaultTouchBindings();
      setDefaultKeyBindings();
    }
  }

  /**
   * Calls {@code super(eye)}, creates the frame {@link #profile()} and calls
   * {@link #setDefaultMouseBindings()} and {@link #setDefaultKeyBindings()}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(Eye).
   */
  public GenericP5Frame(Eye eye) {
    super(eye);
    setProfile(new Profile(this));
    // TODO
    if (Scene.platform() == Platform.PROCESSING_DESKTOP)
      setDefaultMouseBindings();
    // else
    // setDefaultTouchBindings();
    setDefaultKeyBindings();
  }

  protected GenericP5Frame(GenericP5Frame otherFrame) {
    super(otherFrame);
    setProfile(new Profile(this));
    this.profile.from(otherFrame.profile);
  }

  @Override
  public GenericP5Frame get() {
    return new GenericP5Frame(this);
  }

  @Override
  protected boolean checkIfGrabsInput(KeyboardEvent event) {
    return profile.hasBinding(event.shortcut());
  }

  /**
   * Same as {@code if (!bypassKey(event)) profile.handle(event)}.
   * 
   * @see #bypassKey(BogusEvent)
   * @see remixlab.bias.ext.Profile#handle(BogusEvent)
   */
  @Override
  public void performInteraction(BogusEvent event) {
    if (!bypassKey(event))
      profile.handle(event);
  }

  /**
   * Same as {@code profile.removeBindings()}.
   * 
   * @see remixlab.bias.ext.Profile#removeBindings()
   */
  public void removeBindings() {
    profile.removeBindings();
  }

  /**
   * Same as {@code return profile.action(key)}.
   * 
   * @see remixlab.bias.ext.Profile#action(Shortcut)
   */
  public String action(Shortcut key) {
    return profile.action(key);
  }

  /**
   * Same as {@code profile.isActionBound(action)}.
   * 
   * @see remixlab.bias.ext.Profile#isActionBound(String)
   */
  public boolean isActionBound(String action) {
    return profile.isActionBound(action);
  }

  /**
   * Same as {@code scene().mouseAgent().setDefaultBindings(this)}. The default frame muse
   * bindings which may be queried with {@link #info()}.
   * 
   * @see remixlab.proscene.MouseAgent#setDefaultBindings(GenericP5Frame)
   */
  public void setDefaultMouseBindings() {
    scene().mouseAgent().setDefaultBindings(this);
  }

  // TODO restore me
  /*
   * public void setDefaultTouchBindings() {
   * scene().touchAgent().setDefaultBindings(this); }
   */

  /**
   * Calls {@link #removeKeyBindings()} and sets the default frame key bindings which may
   * be queried with {@link #info()}.
   */
  public void setDefaultKeyBindings() {
    removeKeyBindings();
    setKeyBinding('n', "align");
    setKeyBinding('c', "center");
    setKeyBinding(KeyAgent.LEFT_KEY, "translateXNeg");
    setKeyBinding(KeyAgent.RIGHT_KEY, "translateXPos");
    setKeyBinding(KeyAgent.DOWN_KEY, "translateYNeg");
    setKeyBinding(KeyAgent.UP_KEY, "translateYPos");
    setKeyBinding(BogusEvent.SHIFT, KeyAgent.LEFT_KEY, "rotateXNeg");
    setKeyBinding(BogusEvent.SHIFT, KeyAgent.RIGHT_KEY, "rotateXPos");
    setKeyBinding(BogusEvent.SHIFT, KeyAgent.DOWN_KEY, "rotateYNeg");
    setKeyBinding(BogusEvent.SHIFT, KeyAgent.UP_KEY, "rotateYPos");
    setKeyBinding('z', "rotateZNeg");
    setKeyBinding('Z', "rotateZPos");
  }

  // good for all dofs :P

  /**
   * Same as {@code profile.setBinding(new MotionShortcut(id), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setMotionBinding(int id, String action) {
    profile.setBinding(new MotionShortcut(id), action);
  }

  /**
   * Same as {@code profile.setBinding(object, new MotionShortcut(id), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setMotionBinding(Object object, int id, String action) {
    profile.setBinding(object, new MotionShortcut(id), action);
  }

  /**
   * Same as {@code profile.removeBindings(MotionShortcut.class)}.
   * 
   * @see remixlab.bias.ext.Profile#removeBindings(Class)
   */
  public void removeMotionBindings() {
    profile.removeBindings(MotionShortcut.class);
  }

  /**
   * Same as {@code profile.hasBinding(new MotionShortcut(id))}.
   * 
   * @see remixlab.bias.ext.Profile#hasBinding(Shortcut)
   */
  public boolean hasMotionBinding(int id) {
    return profile.hasBinding(new MotionShortcut(id));
  }

  /**
   * Same as {@code profile.removeBinding(new MotionShortcut(id))}.
   * 
   * @see remixlab.bias.ext.Profile#removeBindings(Class)
   */
  public void removeMotionBinding(int id) {
    profile.removeBinding(new MotionShortcut(id));
  }

  /**
   * Same as {@code for(int i=0; i< ids.length; i++) removeMotionBinding(ids[i])}.
   * 
   * @see #removeMotionBinding(int)
   */
  public void removeMotionBindings(int[] ids) {
    for (int i = 0; i < ids.length; i++)
      removeMotionBinding(ids[i]);
  }

  // Key

  /**
   * Same as {@code profile.setBinding(new KeyboardShortcut(vkey), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setKeyBinding(int vkey, String action) {
    profile.setBinding(new KeyboardShortcut(vkey), action);
  }

  /**
   * Same as {@code profile.setBinding(new KeyboardShortcut(key), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(char key, String action) {
    profile.setBinding(new KeyboardShortcut(key), action);
  }

  /**
   * Same as {@code profile.setBinding(object, new KeyboardShortcut(vkey), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setKeyBinding(Object object, int vkey, String action) {
    profile.setBinding(object, new KeyboardShortcut(vkey), action);
  }

  /**
   * Same as {@code profile.setBinding(object, new KeyboardShortcut(key), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, char key, String action) {
    profile.setBinding(object, new KeyboardShortcut(key), action);
  }

  /**
   * Same as {@code return profile.hasBinding(new KeyboardShortcut(vkey))}.
   * 
   * @see remixlab.bias.ext.Profile#hasBinding(Shortcut)
   */
  public boolean hasKeyBinding(int vkey) {
    return profile.hasBinding(new KeyboardShortcut(vkey));
  }

  /**
   * Same as {@code return profile.hasBinding(new KeyboardShortcut(key))}.
   * 
   * @see remixlab.bias.ext.Profile#hasBinding(Shortcut)
   */
  public boolean hasKeyBinding(char key) {
    return profile.hasBinding(new KeyboardShortcut(key));
  }

  /**
   * Same as {@code profile.removeBinding(new KeyboardShortcut(vkey))}.
   * 
   * @see remixlab.bias.ext.Profile#removeBinding(Shortcut)
   */
  public void removeKeyBinding(int vkey) {
    profile.removeBinding(new KeyboardShortcut(vkey));
  }

  /**
   * Same as {@code profile.removeBinding(new KeyboardShortcut(key))}.
   * 
   * @see remixlab.bias.ext.Profile#removeBinding(Shortcut)
   */
  public void removeKeyBinding(char key) {
    profile.removeBinding(new KeyboardShortcut(key));
  }

  /**
   * Same as {@code profile.setBinding(new KeyboardShortcut(mask, vkey), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setKeyBinding(int mask, int vkey, String action) {
    profile.setBinding(new KeyboardShortcut(mask, vkey), action);
  }

  /**
   * Same as {@code profile.setBinding(object, new KeyboardShortcut(mask, vkey), action)}
   * .
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, int mask, int vkey, String action) {
    profile.setBinding(object, new KeyboardShortcut(mask, vkey), action);
  }

  /**
   * Same as {@code return profile.hasBinding(new KeyboardShortcut(mask, vkey))} .
   * 
   * @see remixlab.bias.ext.Profile#hasBinding(Shortcut)
   */
  public boolean hasKeyBinding(int mask, int vkey) {
    return profile.hasBinding(new KeyboardShortcut(mask, vkey));
  }

  /**
   * Same as {@code profile.removeBinding(new KeyboardShortcut(mask, vkey))}.
   * 
   * @see remixlab.bias.ext.Profile#removeBinding(Shortcut)
   */
  public void removeKeyBinding(int mask, int vkey) {
    profile.removeBinding(new KeyboardShortcut(mask, vkey));
  }

  /**
   * Same as {@code setKeyBinding(mask, KeyAgent.keyCode(key), action)}.
   * 
   * @see #setKeyBinding(int, int, String)
   */
  public void setKeyBinding(int mask, char key, String action) {
    setKeyBinding(mask, KeyAgent.keyCode(key), action);
  }

  /**
   * Same as {@code setKeyBinding(object, mask, KeyAgent.keyCode(key), action)}.
   * 
   * @see #setKeyBinding(Object, int, int, String)
   */
  public void setKeyBinding(Object object, int mask, char key, String action) {
    setKeyBinding(object, mask, KeyAgent.keyCode(key), action);
  }

  /**
   * Same as {@code hasKeyBinding(mask, KeyAgent.keyCode(key))}.
   * 
   * @see #hasKeyBinding(int, int)
   */
  public boolean hasKeyBinding(int mask, char key) {
    return hasKeyBinding(mask, KeyAgent.keyCode(key));
  }

  /**
   * Same as {@code removeKeyBinding(mask, KeyAgent.keyCode(key))}.
   * 
   * @see #removeKeyBinding(int, int)
   */
  public void removeKeyBinding(int mask, char key) {
    removeKeyBinding(mask, KeyAgent.keyCode(key));
  }

  /**
   * Same as {@code profile.removeBindings(KeyboardShortcut.class)}.
   * 
   * @see remixlab.bias.ext.Profile#removeBindings(Class)
   */
  public void removeKeyBindings() {
    profile.removeBindings(KeyboardShortcut.class);
  }

  // click

  /**
   * Same as {@code }.
   * 
   * @see remixlab.bias.ext.Profile
   */
  public void setClickBinding(int id, int count, String action) {
    if (count > 0 && count < 4)
      profile.setBinding(new ClickShortcut(id, count), action);
    else
      System.out.println("Warning no click binding set! Count should be between 1 and 3");
  }

  /**
   * Same as {@code profile.setBinding(object, new ClickShortcut(id, count), action)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setClickBinding(Object object, int id, int count, String action) {
    if (count > 0 && count < 4)
      profile.setBinding(object, new ClickShortcut(id, count), action);
    else
      System.out.println("Warning no click binding set! Count should be between 1 and 3");
  }

  /**
   * Same as {@code return profile.hasBinding(new ClickShortcut(id, count))}.
   * 
   * @see remixlab.bias.ext.Profile#hasBinding(Shortcut)
   */
  public boolean hasClickBinding(int id, int count) {
    return profile.hasBinding(new ClickShortcut(id, count));
  }

  /**
   * Same as {@code profile.removeBinding(new ClickShortcut(id, count))}.
   * 
   * @see remixlab.bias.ext.Profile#removeBinding(Shortcut)
   */
  public void removeClickBinding(int id, int count) {
    profile.removeBinding(new ClickShortcut(id, count));
  }

  /**
   * Same as {@code profile.removeBindings(ClickShortcut.class)}.
   * 
   * @see remixlab.bias.ext.Profile#removeBindings(Class)
   */
  public void removeClickBindings() {
    profile.removeBindings(ClickShortcut.class);
  }

  /**
   * Same as {@code for(int i=0; i<ids.length; i++) removeClickBinding(ids[i], count)}.
   * 
   * @see #removeClickBinding(int, int)
   */
  public void removeClickBindings(int[] ids, int count) {
    for (int i = 0; i < ids.length; i++)
      removeClickBinding(ids[i], count);
  }

  /**
   * Returns the frame {@link remixlab.bias.ext.Profile} instance.
   */
  public Profile profile() {
    return profile;
  }

  /**
   * Sets the frame {@link remixlab.bias.ext.Profile} instance. Note that the
   * {@link remixlab.bias.ext.Profile#grabber()} object should equals this scene.
   * 
   * @see #setBindings(GenericP5Frame)
   */
  public void setProfile(Profile p) {
    if (p.grabber() == this)
      profile = p;
    else
      System.out.println("Nothing done, profile grabber is different than this grabber");
  }

  /**
   * Same as {@code profile.from(otherFrame.profile())}.
   * 
   * @see remixlab.bias.ext.Profile#from(Profile)
   * @see #setProfile(Profile)
   */
  public void setBindings(GenericP5Frame otherFrame) {
    profile.from(otherFrame.profile());
  }

  /**
   * Returns a description of all the bindings this frame holds.
   */
  public String info() {
    String result = new String();
    String info = profile().info(KeyboardShortcut.class);
    if (!info.isEmpty()) {
      result = "Key bindings:\n";
      result += Scene.parseKeyInfo(info);
    }
    info = profile().info(MotionShortcut.class);
    if (!info.isEmpty()) {
      result += "Motion bindings:\n";
      result += Scene.parseInfo(info);
    }
    info = profile().info(ClickShortcut.class);
    if (!info.isEmpty()) {
      result += "Click bindings:\n";
      result += Scene.parseInfo(info);
    }
    return result;
  }

  // dandelion <-> Processing

  // public final void setTranslation(PVector t) {
  // setTranslation(Scene.toVec(t));
  // }
  //
  // public final void setTranslationWithConstraint(PVector t) {
  // setTranslationWithConstraint(Scene.toVec(t));
  // }
  //
  // public final void setPosition(PVector t) {
  // setPosition(Scene.toVec(t));
  // }
  //
  // public final void setPositionWithConstraint(PVector t) {
  // setPositionWithConstraint(Scene.toVec(t));
  // }
  //
  // public void translate(PVector t) {
  // translate(Scene.toVec(t));
  // }
  //
  // public void setXAxis(PVector axis) {
  // setYAxis(Scene.toVec(axis));
  // }
  //
  // public PVector xPosAxis() {
  // return Scene.toPVector(xAxis(true));
  // }
  //
  // public PVector xNegAxis() {
  // return Scene.toPVector(xAxis(false));
  // }
  //
  // public void setYAxis(PVector axis) {
  // setXAxis(Scene.toVec(axis));
  // }
  //
  // public PVector yPosAxis() {
  // return Scene.toPVector(yAxis(true));
  // }
  //
  // public PVector yNegAxis() {
  // return Scene.toPVector(yAxis(false));
  // }
  //
  // public void setZAxis(PVector axis) {
  // setZAxis(Scene.toVec(axis));
  // }
  //
  // public PVector zPosAxis() {
  // return Scene.toPVector(zAxis(true));
  // }
  //
  // public PVector zNegAxis() {
  // return Scene.toPVector(zAxis(false));
  // }
  //
  // public final void projectOnLine(PVector origin, PVector direction) {
  // projectOnLine(Scene.toVec(origin), Scene.toVec(direction));
  // }
  //
  // public void rotateAroundPoint(Rotation rotation, PVector point) {
  // rotateAroundPoint(rotation, Scene.toVec(point));
  // }
  //
  // public final void fromMatrix(PMatrix3D pM) {
  // fromMatrix(Scene.toMat(pM));
  // }
  //
  // public final void fromMatrix(PMatrix2D pM) {
  // fromMatrix(Scene.toMat(pM));
  // }
  //
  // public final void fromMatrix(PMatrix3D pM, float s) {
  // fromMatrix(Scene.toMat(pM), s);
  // }
  //
  // public final void fromMatrix(PMatrix2D pM, float s) {
  // fromMatrix(Scene.toMat(pM), s);
  // }
  //
  // public final PVector coordinatesOfFrom(PVector src, Frame from) {
  // return Scene.toPVector(coordinatesOfFrom(Scene.toVec(src), from));
  // }
  //
  // public final PVector coordinatesOfIn(PVector src, Frame in) {
  // return Scene.toPVector(coordinatesOfIn(Scene.toVec(src), in));
  // }
  //
  // public final PVector localCoordinatesOf(PVector src) {
  // return Scene.toPVector(localCoordinatesOf(Scene.toVec(src)));
  // }
  //
  // public final PVector coordinatesOf(PVector src) {
  // return Scene.toPVector(coordinatesOf(Scene.toVec(src)));
  // }
  //
  // public final PVector transformOfFrom(PVector src, Frame from) {
  // return Scene.toPVector(transformOfFrom(Scene.toVec(src), from));
  // }
  //
  // public final PVector transformOfIn(PVector src, Frame in) {
  // return Scene.toPVector(transformOfIn(Scene.toVec(src), in));
  // }
  //
  // public final PVector localInverseCoordinatesOf(PVector src) {
  // return Scene.toPVector(localInverseCoordinatesOf(Scene.toVec(src)));
  // }
  //
  // public final PVector inverseCoordinatesOf(PVector src) {
  // return Scene.toPVector(inverseCoordinatesOf(Scene.toVec(src)));
  // }
  //
  // public final PVector transformOf(PVector src) {
  // return Scene.toPVector(transformOf(Scene.toVec(src)));
  // }
  //
  // public final PVector inverseTransformOf(PVector src) {
  // return Scene.toPVector(inverseTransformOf(Scene.toVec(src)));
  // }
  //
  // public final PVector localTransformOf(PVector src) {
  // return Scene.toPVector(localTransformOf(Scene.toVec(src)));
  // }
  //
  // public final PVector localInverseTransformOf(PVector src) {
  // return Scene.toPVector(localInverseTransformOf(Scene.toVec(src)));
  // }
  //
  // // trickier:
  //
  // public PVector getTranslation() {
  // return Scene.toPVector(translation());
  // }
  //
  // public PVector getPosition() {
  // return Scene.toPVector(position());
  // }
  //
  // public PMatrix getMatrix() {
  // return is2D() ? Scene.toPMatrix2D(matrix()) : Scene.toPMatrix(matrix());
  // }
  //
  // public PMatrix getWorldMatrix() {
  // return is2D() ? Scene.toPMatrix2D(worldMatrix()) :
  // Scene.toPMatrix(worldMatrix());
  // }
}