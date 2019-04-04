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

//import java.util.Arrays;

import remixlab.bias.core.*;
import remixlab.bias.event.*;

/**
 * Proscene mouse-agent. A Processing fully fledged mouse {@link remixlab.bias.core.Agent}
 * .
 *
 * @see remixlab.bias.core.Agent
 * @see remixlab.proscene.KeyAgent
 * @see remixlab.proscene.DroidKeyAgent
 * @see remixlab.proscene.DroidTouchAgent
 */
public class MouseAgent extends Agent {
  public static int LEFT_ID, CENTER_ID, RIGHT_ID, WHEEL_ID, NO_BUTTON;
  protected float xSens = 1f;
  protected float ySens = 1f;
  protected Scene scene;
  protected DOF2Event currentEvent, prevEvent;
  protected boolean move, press, drag, release;
  protected PickingMode pMode;

  public enum PickingMode {
    MOVE, CLICK
  };

  /**
   * Calls super on (scn,n) and sets {@link #pickingMode()} to {@link PickingMode#MOVE}.
   * 
   * @see #setPickingMode(PickingMode)
   */
  public MouseAgent(Scene scn) {
    super(scn.inputHandler());
    scene = scn;
    LEFT_ID = scene().registerMotionID(37, this, 2);
    CENTER_ID = scene().registerMotionID(3, this, 2);
    RIGHT_ID = scene().registerMotionID(39, this, 2);
    WHEEL_ID = scene().registerMotionID(8, this, 1);
    NO_BUTTON = scene().registerMotionID(BogusEvent.NO_ID, this, 2);
    // click ids are anonymous (since they are the same as motions)
    scene().registerClickID(LEFT_ID, this);
    scene().registerClickID(CENTER_ID, this);
    scene().registerClickID(RIGHT_ID, this);
    setPickingMode(PickingMode.MOVE);
  }

  /**
   * Returns the scene this object belongs to.
   */
  public Scene scene() {
    return scene;
  }

  /**
   * Sets the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or
   * {@link PickingMode#CLICK}.
   * 
   * @see #pickingMode()
   */
  public void setPickingMode(PickingMode mode) {
    pMode = mode;
  }

  /**
   * Returns the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or
   * {@link PickingMode#CLICK}.
   * 
   * @see #setPickingMode(PickingMode)
   */
  public PickingMode pickingMode() {
    return pMode;
  }

  /**
   * Processing mouseEvent method to be registered at the PApplet's instance.
   */
  public void mouseEvent(processing.event.MouseEvent e) {
    move = e.getAction() == processing.event.MouseEvent.MOVE;
    press = e.getAction() == processing.event.MouseEvent.PRESS;
    drag = e.getAction() == processing.event.MouseEvent.DRAG;
    release = e.getAction() == processing.event.MouseEvent.RELEASE;
    if (move || press || drag || release) {
      currentEvent = new DOF2Event(prevEvent, e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(),
          /* e.getModifiers() */BogusEvent.NO_MODIFIER_MASK, move ? BogusEvent.NO_ID : e.getButton());
      if (move && (pickingMode() == PickingMode.MOVE))
        updateTrackedGrabber(currentEvent);
      handle(press ? currentEvent.fire() : release ? currentEvent.flush() : currentEvent);
      prevEvent = currentEvent.get();
      return;
    }
    if (e.getAction() == processing.event.MouseEvent.WHEEL) {
      handle(new DOF1Event(e.getCount(), /* e.getModifiers() */BogusEvent.NO_MODIFIER_MASK, WHEEL_ID));
      return;
    }
    if (e.getAction() == processing.event.MouseEvent.CLICK) {
      ClickEvent bogusClickEvent = new ClickEvent(e.getX() - scene.originCorner().x(),
          e.getY() - scene.originCorner().y(), /* e.getModifiers() */BogusEvent.NO_MODIFIER_MASK, e.getButton(),
          e.getCount());
      if (pickingMode() == PickingMode.CLICK)
        updateTrackedGrabber(bogusClickEvent);
      handle(bogusClickEvent);
      return;
    }
  }

  @Override
  public float[] sensitivities(MotionEvent event) {
    if (event instanceof DOF2Event)
      return new float[] { xSens, ySens, 1f, 1f, 1f, 1f };
    else
      return super.sensitivities(event);
  }

  /**
   * Defines the {@link #xSensitivity()}.
   */
  public void setXSensitivity(float sensitivity) {
    xSens = sensitivity;
  }

  /**
   * Returns the x sensitivity.
   * <p>
   * Default value is 1. A higher value will make the event more efficient (usually
   * meaning a faster motion). Use a negative value to invert the along x-Axis motion
   * direction.
   */
  public float xSensitivity() {
    return xSens;
  }

  /**
   * Defines the {@link #ySensitivity()}.
   */
  public void setYSensitivity(float sensitivity) {
    ySens = sensitivity;
  }

  /**
   * Returns the y sensitivity.
   * <p>
   * Default value is 1. A higher value will make the event more efficient (usually
   * meaning a faster motion). Use a negative value to invert the along y-Axis motion
   * direction.
   */
  public float ySensitivity() {
    return ySens;
  }

  /**
   * Internal use. Other Agents should follow a similar pattern: 1. Register some motion
   * ids within the Profile; and, 2. Define the default bindings on the frame parameter.
   * 
   * @see remixlab.bias.ext.Profile#registerMotionID(int)
   * @see remixlab.bias.ext.Profile#registerMotionID(int, int)
   */
  protected void setDefaultBindings(InteractiveFrame frame) {
    frame.removeMotionBindings(this);
    frame.removeClickBindings(this);

    frame.setMotionBinding(LEFT_ID, "rotate");
    frame.setMotionBinding(CENTER_ID, frame.isEyeFrame() ? "zoomOnRegion" : "screenRotate");
    frame.setMotionBinding(RIGHT_ID, "translate");
    frame.setMotionBinding(WHEEL_ID, scene().is3D() ? frame.isEyeFrame() ? "translateZ" : "scale" : "scale");

    frame.setClickBinding(LEFT_ID, 2, "align");
    frame.setClickBinding(RIGHT_ID, 2, "center");
  }
}