/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

// Thanks to Sebastian Chaparro, url-PENDING and William Rodriguez, url-PENDING
// for providing an initial picking example and searching the documentation for it:
// http://n.clavaud.free.fr/processing/picking/pickcode.htm
// http://content.gpwiki.org/index.php/OpenGL_Selection_Using_Unique_Color_IDs

package remixlab.proscene;

import java.lang.reflect.Method;

import processing.core.*;
import remixlab.bias.core.Agent;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.core.Shortcut;
import remixlab.bias.event.ClickShortcut;
import remixlab.bias.event.KeyboardEvent;
import remixlab.bias.event.KeyboardShortcut;
import remixlab.bias.event.MotionShortcut;
import remixlab.bias.ext.Profile;
import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Eye;
import remixlab.dandelion.core.GenericFrame;
import remixlab.dandelion.core.AbstractScene.Platform;
import remixlab.dandelion.core.GenericFrame.PickingPrecision;
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * A Processing {@link remixlab.dandelion.core.GenericFrame} with a {@link #profile()}
 * instance which allows {@link remixlab.bias.core.Shortcut} to
 * {@link java.lang.reflect.Method} bindings high-level customization. (see all the
 * <b>*Binding*()</b> methods).
 * <p>
 * Visual representations (PShapes or arbitrary graphics procedures) may be related to an
 * interactive-frame in two different ways:
 * <ol>
 * <li>Applying the frame transformation just before the graphics code happens in
 * <b>papplet.draw()</b> (refer to the {@link remixlab.dandelion.core.GenericFrame} API
 * class documentation).
 * <li>Setting a visual representation directly to the frame, either by calling
 * {@link #setShape(PShape)} or {@link #addGraphicsHandler(Object, String)} in
 * <b>papplet.setup()</b>, and then calling {@link remixlab.proscene.Scene#drawFrames()}
 * in <b>papplet.draw()</b>.
 * </ol>
 * When a visual representation is attached to a frame, picking can be performed in an
 * exact manner (using the pixels of the projected visual representation themselves)
 * provided that the {@link #pickingPrecision()} is set to {@link PickingPrecision#EXACT}
 * and the scene {@link remixlab.proscene.Scene#pickingBuffer()} is enabled (see
 * {@link remixlab.proscene.Scene#enablePickingBuffer()}). The strategy doesn't support
 * coloring or texturing operations to take place within the visual representation. Use
 * another {@link #pickingPrecision()} if this is the case or if performance is a concern
 * (using a picking buffer requires the geometry to be drawn twice).
 * <p>
 * If the above conditions are met, the visual representation may be highlighted when
 * picking takes place (see {@link #enableHighlighting()}). Highlighting is enabled by
 * default when {@link remixlab.proscene.Scene#drawFrames()} is called. To implement a
 * different highlighting strategy, {@link #disableHighlighting()} and then iterate
 * through the scene frames using code like this:
 * 
 * <pre>
 * {@code
 * for (InteractiveFrame frame : scene.frames()) {
 *   fill(scene.mouseAgent().inputGrabber() == frame ? highlightedColor : normalColor);
 *   frame.draw();
 * }
 * }
 * </pre>
 * 
 * Note that iterating through the scene frames is not as efficient as simply calling
 * {@link remixlab.proscene.Scene#drawFrames()}.
 * 
 * @see remixlab.dandelion.core.GenericFrame
 */
public class InteractiveFrame extends GenericFrame {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(profile).append(id).toHashCode();
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
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(profile, other.profile).append(id, other.id)
        .isEquals();
  }

  // profile
  protected Profile profile;

  // shape
  protected PShape pshape;
  protected int id;
  protected Vec shift;

  // graphics handler
  protected Object drawHandlerObject;
  protected Method drawHandlerMethod;

  protected boolean highlight = true;

  /**
   * Calls {@code super(eye)}, add the {@link #drawEye(PGraphics)} graphics handler,
   * creates the frame {@link #profile()} and calls {@link #setDefaultMouseBindings()} and
   * {@link #setDefaultKeyBindings()}.
   * 
   * @see #drawEye(PGraphics)
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(Eye)
   */
  public InteractiveFrame(Eye eye) {
    super(eye);
    init();
    addGraphicsHandler(this, "drawEye");
  }

  /**
   * Same as {@code pg.scale(1/magnitude()); scene().drawEye(pg, eye());}.
   * <p>
   * This method is only meaningful when frame {@link #isEyeFrame()}.
   * 
   * @see remixlab.proscene.Scene#drawEye(PGraphics, Eye)
   * @see #isEyeFrame()
   */
  public void drawEye(PGraphics pg) {
    if (isEyeFrame()) {
      // a bit of a hack, but the eye frame scaling should be canceled out
      pg.scale(1 / magnitude());
      scene().drawEye(pg, eye());
    } else
      AbstractScene.showOnlyEyeWarning("drawEye", true);
  }

  /**
   * Constructs an interactive-frame. Calls {@code super(scn}. Sets the
   * {@link #pickingPrecision()} to {@link PickingPrecision#EXACT}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene)
   * @see #shape()
   * @see #setShape(PShape)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn) {
    super(scn);
    init();
    setPickingPrecision(PickingPrecision.EXACT);
  }

  /**
   * Constructs an interactive-frame as a child of reference frame. Calls
   * {@code super(scn, referenceFrame}. Sets the {@link #pickingPrecision()} to
   * {@link PickingPrecision#EXACT}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, GenericFrame)
   * @see #shape()
   * @see #setShape(PShape)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn, GenericFrame referenceFrame) {
    super(scn, referenceFrame);
    init(referenceFrame);
    setPickingPrecision(PickingPrecision.EXACT);
  }

  /**
   * Wraps the pshape into this interactive-frame. Calls {@code super(scn)}. Sets the
   * {@link #pickingPrecision()} to {@link PickingPrecision#EXACT}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene)
   */
  public InteractiveFrame(Scene scn, PShape ps) {
    super(scn);
    init();
    setShape(ps);
    setPickingPrecision(PickingPrecision.EXACT);
  }

  /**
   * Wraps the pshape into this interactive-frame which is created as a child of reference
   * frame. Calls {@code super(scn, referenceFrame)}. Sets the {@link #pickingPrecision()}
   * to {@link PickingPrecision#EXACT}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, GenericFrame)
   */
  public InteractiveFrame(Scene scn, GenericFrame referenceFrame, PShape ps) {
    super(scn, referenceFrame);
    init(referenceFrame);
    setShape(ps);
    setPickingPrecision(PickingPrecision.EXACT);
  }

  /**
   * Wraps the function object procedure into this interactive-frame. Calls
   * {@code super(scn}. Sets the {@link #pickingPrecision()} to
   * {@link PickingPrecision#EXACT}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn, Object obj, String methodName) {
    super(scn);
    init();
    addGraphicsHandler(obj, methodName);
    setPickingPrecision(PickingPrecision.EXACT);
  }

  /**
   * Wraps the the function object procedure into this interactive-frame which is is
   * created as a child of reference frame. Calls {@code super(scn, referenceFrame}. Sets
   * the {@link #pickingPrecision()} to {@link PickingPrecision#EXACT}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, GenericFrame)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn, GenericFrame referenceFrame, Object obj, String methodName) {
    super(scn, referenceFrame);
    init(referenceFrame);
    addGraphicsHandler(obj, methodName);
    setPickingPrecision(PickingPrecision.EXACT);
  }

  protected void init() {
    id = ++Scene.frameCount;
    shift = new Vec();

    setProfile(new Profile(this));
    // TODO
    if (Scene.platform() == Platform.PROCESSING_DESKTOP)
      setDefaultMouseBindings();
    // else
    // setDefaultTouchBindings();
    setDefaultKeyBindings();
  }

  protected void init(GenericFrame referenceFrame) {
    id = ++Scene.frameCount;
    shift = new Vec();

    setProfile(new Profile(this));
    if (referenceFrame instanceof InteractiveFrame)
      this.profile.from(((InteractiveFrame) referenceFrame).profile);
    else {
      // TODO
      if (Scene.platform() == Platform.PROCESSING_DESKTOP)
        setDefaultMouseBindings();
      // else
      // setDefaultTouchBindings();
      setDefaultKeyBindings();
    }
  }

  protected InteractiveFrame(InteractiveFrame otherFrame) {
    super(otherFrame);
    setProfile(new Profile(this));
    this.profile.from(otherFrame.profile);
    this.pshape = otherFrame.pshape;
    this.id = otherFrame.id;
    this.shift = otherFrame.shift.get();
    this.drawHandlerObject = otherFrame.drawHandlerObject;
    this.drawHandlerMethod = otherFrame.drawHandlerMethod;
  }

  @Override
  public InteractiveFrame get() {
    return new InteractiveFrame(this);
  }

  // common api
  @Override
  public Scene scene() {
    return (Scene) gScene;
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
   * @see remixlab.proscene.MouseAgent#setDefaultBindings(InteractiveFrame)
   */
  public void setDefaultMouseBindings() {
    scene().mouseAgent().setDefaultBindings(this);
  }

  // TODO restore me

  // public void setDefaultTouchBindings() {
  // scene().touchAgent().setDefaultBindings(this);
  // }

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
   * Remove all motion bindings. Same as
   * {@code profile.removeBindings(MotionShortcut.class)}.
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
   * Remove all the motion bindings related to the agent. Same as
   * {@code profile.removeBindings(agent, MotionShortcut.class)}.
   * 
   * @see #removeMotionBinding(int)
   * @see remixlab.bias.ext.Profile#removeBindings(Agent, Class)
   */
  public void removeMotionBindings(Agent agent) {
    profile.removeBindings(agent, MotionShortcut.class);
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
   * Remove all key bindings. Same as
   * {@code profile.removeBindings(KeyboardShortcut.class)}.
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
   * Same as
   * {@code for (int i = 1; i < 4; i++) profile.removeBinding(new ClickShortcut(id, i))}.
   * 
   * @param id
   */
  public void removeClickBinding(int id) {
    for (int i = 1; i < 4; i++)
      profile.removeBinding(new ClickShortcut(id, i));
  }

  /**
   * Remove all click bindings. Same as
   * {@code profile.removeBindings(ClickShortcut.class)}.
   * 
   * @see remixlab.bias.ext.Profile#removeBindings(Class)
   */
  public void removeClickBindings() {
    profile.removeBindings(ClickShortcut.class);
  }

  /**
   * Remove the click bindings related to the agent. Same as
   * {@code profile.removeBindings(agent, ClickShortcut.class)}.
   * 
   * @see #removeClickBinding(int, int)
   * @see remixlab.bias.ext.Profile#removeBindings(Agent, Class)
   */
  public void removeClickBindings(Agent agent) {
    profile.removeBindings(agent, ClickShortcut.class);
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
   * @see #setBindings(InteractiveFrame)
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
  public void setBindings(InteractiveFrame otherFrame) {
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
  // end api

  /**
   * Calls {@link remixlab.dandelion.core.GenericFrame#fromFrame(Frame)},
   * {@link #setShape(InteractiveFrame)} and {@link #addGraphicsHandler(InteractiveFrame)}
   * on the other frame instance.
   */
  public void fromFrame(InteractiveFrame otherFrame) {
    super.fromFrame(otherFrame);
    setShape(otherFrame);
    addGraphicsHandler(otherFrame);
  }

  /**
   * Same as {@code ((Scene) scene).applyTransformation(pg, this)}.
   * 
   * @see remixlab.proscene.Scene#applyTransformation(PGraphics, Frame)
   */
  public void applyTransformation(PGraphics pg) {
    scene().applyTransformation(pg, this);
  }

  /**
   * Same as {@code ((Scene) scene).applyWorldTransformation(pg, this)}.
   * 
   * @see remixlab.proscene.Scene#applyWorldTransformation(PGraphics, Frame)
   */
  public void applyWorldTransformation(PGraphics pg) {
    scene().applyWorldTransformation(pg, this);
  }

  /**
   * Enables highlighting of the frame visual representation when picking takes place.
   * 
   * @see #disableHighlighting()
   * @see #toggleHighlighting()
   * @see #isHighlightingEnabled()
   */
  public void enableHighlighting() {
    highlight = true;
  }

  /**
   * Disables highlighting of the frame visual representation when picking takes place.
   * 
   * @see #enableHighlighting()
   * @see #toggleHighlighting()
   * @see #isHighlightingEnabled()
   */
  public void disableHighlighting() {
    highlight = false;
  }

  /**
   * Toggles highlighting of the frame visual representation when picking takes place.
   * 
   * @see #enableHighlighting()
   * @see #disableHighlighting()
   * @see #isHighlightingEnabled()
   */
  public void toggleHighlighting() {
    highlight = !highlight;
  }

  /**
   * Returns true if highlighting of the frame visual representation when picking takes
   * place is enablesd.
   * 
   * @see #enableHighlighting()
   * @see #disableHighlighting()
   * @see #isHighlightingEnabled()
   */
  public boolean isHighlightingEnabled() {
    return highlight;
  }

  protected int highlight(int color) {
    int c = 0;
    float hue, saturation, brightness;
    scene().pApplet().pushStyle();
    scene().pApplet().colorMode(PApplet.HSB, 4);

    hue = scene().pApplet().hue(color);
    saturation = scene().pApplet().saturation(color);
    brightness = scene().pApplet().brightness(color);
    brightness = brightness < 1 ? 2 : brightness < 2 ? 3 : 4;
    c = scene().pApplet().color(hue, saturation, brightness);

    scene().pApplet().popStyle();
    return c;
  }

  protected void highlight(PGraphics pg) {
    if (isEyeFrame()) {
      AbstractScene.showOnlyEyeWarning("highlight", false);
      return;
    }
    pg.scale(1.1f);
    // TODO shapes pending, requires PShape style, stroke* and fill* to be readable
    if (pg.stroke)
      pg.stroke(highlight(pg.strokeColor));
    if (pg.fill)
      pg.fill(highlight(pg.fillColor));
  }

  /**
   * Internal use. Frame graphics color to use in the
   * {@link remixlab.proscene.Scene#pickingBuffer()}.
   */
  protected int id() {
    // see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
    return scene().pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
  }

  /**
   * Shifts the {@link #shape()} respect to the frame {@link #position()}. Default value
   * is zero.
   * <p>
   * This method is only meaningful when frame is not eyeFrame.
   * 
   * @see #graphicsShift()
   * @see #isEyeFrame()
   */
  public void shiftGraphics(Vec shift) {
    if (isEyeFrame())
      AbstractScene.showOnlyEyeWarning("shiftGraphics", true);
    this.shift = shift;
  }

  /**
   * Returns the {@link #shape()} shift.
   * <p>
   * This method is only meaningful when frame is not eyeFrame.
   * 
   * @see #shiftGraphics(Vec)
   * @see #isEyeFrame()
   */
  public Vec graphicsShift() {
    if (isEyeFrame())
      AbstractScene.showOnlyEyeWarning("graphicsShift", true);
    return shift;
  }

  // shape

  /**
   * Returns the shape wrap by this interactive-frame.
   */
  public PShape shape() {
    return pshape;
  }

  /**
   * Replaces previous {@link #shape()} with {@code ps}.
   */
  public void setShape(PShape ps) {
    pshape = ps;
    Scene.GRAPHICS = update();
  }

  /**
   * Sets the frame {@link #shape()} from that of other frame. Useful when sharing the
   * same shape drawing method among different frame instances is desirable.
   * 
   * {@link #addGraphicsHandler(InteractiveFrame)}
   */
  public void setShape(InteractiveFrame otherFrame) {
    setShape(otherFrame.shape());
  }

  /**
   * Unsets the shape which is wrapped by this interactive-frame.
   */
  public PShape unsetShape() {
    PShape prev = pshape;
    pshape = null;
    Scene.GRAPHICS = update();
    return prev;
  }

  /**
   * Internal cache optimization method.
   */
  protected boolean update() {
    if (shape() != null || this.hasGraphicsHandler() && !isEyeFrame())
      return true;
    else
      for (InteractiveFrame frame : scene().frames())
        if (frame.shape() != null || frame.hasGraphicsHandler() && !frame.isEyeFrame())
          return true;
    return false;
  }

  /**
   * Internal cache optimization method.
   */
  protected boolean update(PickingPrecision precision) {
    if (precision == PickingPrecision.EXACT)
      return true;
    for (InteractiveFrame frame : scene().frames())
      if (frame.pickingPrecision() == PickingPrecision.EXACT)
        return true;
    return false;
  }

  @Override
  public void setPickingPrecision(PickingPrecision precision) {
    if (precision == PickingPrecision.EXACT)
      if (!scene().isPickingBufferEnabled())
        System.out.println(
            "Warning: EXACT picking precision will behave like FIXED until the scene.pickingBuffer() is enabled.");
    pkgnPrecision = precision;
    if (isEyeFrame()) {
      AbstractScene.showOnlyEyeWarning("setPickingPrecision", false);
      return;
    }
    Scene.PRECISION = update(pickingPrecision());
  }

  /**
   * An interactive-frame may be picked using
   * <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a> with a
   * color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method
   * compares the color of the {@link remixlab.proscene.Scene#pickingBuffer()} at
   * {@code (x,y)} with {@link #id()}. Returns true if both colors are the same, and false
   * otherwise.
   * <p>
   * This method is only meaningful when frame is not eyeFrame.
   * 
   * @see #setPickingPrecision(PickingPrecision)
   * @see #isEyeFrame()
   */
  @Override
  public final boolean checkIfGrabsInput(float x, float y) {
    if (isEyeFrame()) {
      AbstractScene.showOnlyEyeWarning("highlight", false);
      return false;
    }
    if (pickingPrecision() != PickingPrecision.EXACT || (shape() == null && !this.hasGraphicsHandler())
        || !scene().isPickingBufferEnabled())
      return super.checkIfGrabsInput(x, y);
    scene().pickingBuffer().pushStyle();
    scene().pickingBuffer().colorMode(PApplet.RGB, 255);
    int index = (int) y * gScene.width() + (int) x;
    if ((0 <= index) && (index < scene().pickingBuffer().pixels.length))
      return scene().pickingBuffer().pixels[index] == id();
    scene().pickingBuffer().popStyle();
    return false;
  }

  @Override
  protected boolean checkIfGrabsInput(KeyboardEvent event) {
    return profile.hasBinding(event.shortcut());
  }

  /**
   * Same as {@code draw(scene.pg())}.
   * 
   * @see remixlab.proscene.Scene#drawFrames(PGraphics)
   */
  public void draw() {
    if (shape() == null && !this.hasGraphicsHandler())
      return;
    draw(scene().pg());
  }

  /**
   * Draw the visual representation of the frame into the given PGraphics using the
   * current point of view (see
   * {@link remixlab.proscene.Scene#applyWorldTransformation(PGraphics, Frame)} ).
   * <p>
   * This method is internally called by the scene to
   * {@link remixlab.proscene.Scene#drawFrames(PGraphics)} into the
   * {@link remixlab.proscene.Scene#disablePickingBuffer()} and by {@link #draw()} to draw
   * the frame into the scene main {@link remixlab.proscene.Scene#pg()}.
   */
  public boolean draw(PGraphics pg) {
    if (shape() == null && !this.hasGraphicsHandler())
      return false;
    pg.pushMatrix();
    scene().applyWorldTransformation(pg, this);
    visit(pg);
    pg.popMatrix();
    return true;
  }

  @Override
  public void visit() {
    visit(Scene.targetPGraphics);
  }

  protected void visit(PGraphics pg) {
    pg.pushStyle();
    if (pg == scene().pickingBuffer())
      beginPickingBuffer();
    // TODO shapes pending, requires PShape style, stroke* and fill* to be readable
    if (!isEyeFrame()) {
      pg.pushMatrix();
      if(pg.is3D())
        pg.translate(shift.x(), shift.y(), shift.z());
      else
        pg.translate(shift.x(), shift.y());
      if (isHighlightingEnabled() && this.grabsInput() && pg != scene().pickingBuffer())
        this.highlight(pg);
      if (shape() != null)
        this.shape(pg);// nicer: it allows to draw shape() into an arbitrary pg
      if (this.hasGraphicsHandler())
        this.invokeGraphicsHandler(pg);
      pg.popMatrix();
    }
    if (pg == scene().pickingBuffer())
      endPickingBuffer();
    pg.popStyle();
  }

  /**
   * Internal use. Calls the shape drawing method. Called by {@link #draw(PGraphics)} and
   * by the scene frame hierarchy traversal algorithm.
   * <p>
   * This method is only meaningful when frame is not eyeFrame.
   * 
   * @see #isEyeFrame()
   */
  protected void shape(PGraphics pg) {
    if (shape().isVisible() && !this.isEyeFrame()) { // don't do expensive matrix ops if
                                                     // invisible
      pg.flush();
      if (pg.shapeMode == PApplet.CENTER) {
        pg.pushMatrix();
        translate(-shape().getWidth() / 2, -shape().getHeight() / 2);
      }
      shape().draw(pg); // needs to handle recorder too
      if (pg.shapeMode == PApplet.CENTER) {
        pg.popMatrix();
      }
    }
  }

  protected void beginPickingBuffer() {
    PGraphics pickingBuffer = scene().pickingBuffer();
    if (shape() != null)
      shape().disableStyle();
    pickingBuffer.colorMode(PApplet.RGB, 255);
    pickingBuffer.fill(id());
    pickingBuffer.stroke(id());
  }

  protected void endPickingBuffer() {
    if (shape() != null)
      shape().enableStyle();
  }

  // DRAW METHOD REG

  /**
   * Internal use. Invokes an external drawing method (if registered). Called by
   * {@link #draw(PGraphics)} and by the scene frame hierarchy traversal algorithm.
   * <p>
   * This method is only meaningful when frame is not eyeFrame.
   * 
   * @see #isEyeFrame()
   */
  protected boolean invokeGraphicsHandler(PGraphics pg) {
    if (drawHandlerObject != null && !this.isEyeFrame()) {
      try {
        drawHandlerMethod.invoke(drawHandlerObject, new Object[] { pg });
        return true;
      } catch (Exception e) {
        PApplet.println("Something went wrong when invoking your " + drawHandlerMethod.getName() + " method");
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  /**
   * Attempt to add a graphics handler method to the frame. The default event handler is a
   * method that returns void and has one single PGraphics parameter. Note that the method
   * should only deal with geometry and that not coloring procedure may be specified
   * within it.
   * 
   * @param obj
   *          the object to handle the event
   * @param methodName
   *          the method to execute in the object handler class
   * 
   * @see #removeGraphicsHandler()
   * @see #invokeGraphicsHandler(PGraphics)
   */
  public void addGraphicsHandler(Object obj, String methodName) {
    try {
      drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { PGraphics.class });
      drawHandlerObject = obj;
      Scene.GRAPHICS = update();
    } catch (Exception e) {
      PApplet.println("Something went wrong when registering your " + methodName + " method");
      e.printStackTrace();
    }
  }

  /**
   * Adds the other frame graphics handler to this frame. Useful when sharing the same
   * frame graphics handler among different frame instances is desirable.
   * 
   * @see #setShape(InteractiveFrame)
   */
  public void addGraphicsHandler(InteractiveFrame otherFrame) {
    addGraphicsHandler(otherFrame.drawHandlerObject, otherFrame.drawHandlerMethod.getName());
  }

  /**
   * Unregisters the graphics handler method (if any has previously been added to the
   * Scene).
   * 
   * @see #addGraphicsHandler(Object, String)
   * @see #invokeGraphicsHandler(PGraphics)
   */
  public void removeGraphicsHandler() {
    drawHandlerMethod = null;
    drawHandlerObject = null;
    Scene.GRAPHICS = update();
  }

  /**
   * Returns {@code true} if the user has registered a graphics handler method to the
   * Scene and {@code false} otherwise.
   * 
   * @see #addGraphicsHandler(Object, String)
   * @see #invokeGraphicsHandler(PGraphics)
   */
  public boolean hasGraphicsHandler() {
    if (drawHandlerMethod == null)
      return false;
    return true;
  }
}