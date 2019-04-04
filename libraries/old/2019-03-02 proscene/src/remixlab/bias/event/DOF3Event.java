/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.event;

import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

/**
 * A {@link remixlab.bias.event.MotionEvent} with three degrees-of-freedom ( {@link #x()},
 * {@link #y()} and {@link #z()} ).
 */
public class DOF3Event extends MotionEvent {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(x).append(dx).append(y).append(dy).append(z)
        .append(dz).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    DOF3Event other = (DOF3Event) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(x, other.x).append(dx, other.dx).append(y, other.y)
        .append(dy, other.dy).append(z, other.z).append(dz, other.dz).isEquals();
  }

  protected float x, dx;
  protected float y, dy;
  protected float z, dz;

  /**
   * Construct an absolute event from the given dof's and modifiers.
   * 
   * @param x
   * @param y
   * @param z
   * @param modifiers
   * @param button
   */
  public DOF3Event(float x, float y, float z, int modifiers, int button) {
    super(modifiers, button);
    this.dx = x;
    this.dy = y;
    this.dz = z;
  }

  /**
   * Construct a relative event from the given previous event, dof's and modifiers.
   * 
   * @param prevEvent
   * @param x
   * @param y
   * @param z
   * @param modifiers
   * @param button
   */
  public DOF3Event(DOF3Event prevEvent, float x, float y, float z, int modifiers, int button) {
    super(modifiers, button);
    this.x = x;
    this.y = y;
    this.z = z;
    setPreviousEvent(prevEvent);
  }

  /**
   * Construct an absolute event from the given dof's.
   * 
   * @param x
   * @param y
   * @param z
   */
  public DOF3Event(float x, float y, float z) {
    super();
    this.dx = x;
    this.dy = y;
    this.dz = z;
  }

  /**
   * Construct a relative event from the given previous event, dof's and modifiers.
   * 
   * @param prevEvent
   * @param x
   * @param y
   * @param z
   */
  public DOF3Event(DOF3Event prevEvent, float x, float y, float z) {
    super();
    this.x = x;
    this.y = y;
    this.z = z;
    setPreviousEvent(prevEvent);
  }

  protected DOF3Event(DOF3Event other) {
    super(other);
    this.x = other.x;
    this.dx = other.dx;
    this.y = other.y;
    this.dy = other.dy;
    this.z = other.z;
    this.dz = other.z;
  }

  @Override
  public DOF3Event get() {
    return new DOF3Event(this);
  }

  @Override
  public DOF3Event flush() {
    return (DOF3Event) super.flush();
  }

  @Override
  public DOF3Event fire() {
    return (DOF3Event) super.fire();
  }

  @Override
  public void setPreviousEvent(MotionEvent prevEvent) {
    super.setPreviousEvent(prevEvent);
    if (prevEvent != null)
      if (prevEvent instanceof DOF3Event) {
        rel = true;
        this.dx = this.x() - ((DOF3Event) prevEvent).x();
        this.dy = this.y() - ((DOF3Event) prevEvent).y();
        this.dz = this.z() - ((DOF3Event) prevEvent).z();
        distance = Util.distance(x, y, z, ((DOF3Event) prevEvent).x(), ((DOF3Event) prevEvent).y(),
            ((DOF3Event) prevEvent).z());
        delay = this.timestamp() - prevEvent.timestamp();
        if (delay == 0)
          speed = distance;
        else
          speed = distance / (float) delay;
      } else {
        this.dx = 0f;
        this.dy = 0f;
        this.dz = 0f;
        delay = 0l;
        speed = 0f;
        distance = 0f;
      }
  }

  /**
   * @return dof-1
   */
  public float x() {
    return x;
  }

  /**
   * @return dof-1 delta
   */
  public float dx() {
    return dx;
  }

  /**
   * @return previous dof-1
   */
  public float prevX() {
    return x() - dx();
  }

  /**
   * 
   * @return dof-2
   */
  public float y() {
    return y;
  }

  /**
   * 
   * @return dof-2 delta
   */
  public float dy() {
    return dy;
  }

  /**
   * @return previous dof-2
   */
  public float prevY() {
    return y() - dy();
  }

  /**
   * @return dof-3
   */
  public float z() {
    return z;
  }

  /**
   * 
   * @return dof-3 delta
   */
  public float dz() {
    return dz;
  }

  /**
   * @return previous dof-3
   */
  public float prevZ() {
    return z() - dz();
  }

  @Override
  public void modulate(float[] sens) {
    if (sens != null)
      if (sens.length >= 3 && this.isAbsolute()) {
        dx = dx * sens[0];
        dy = dy * sens[1];
        dz = dz * sens[2];
      }
  }

  @Override
  public boolean isNull() {
    if (Util.zero(dx()) && Util.zero(dy()) && Util.zero(dz()))
      return true;
    return false;
  }

  /**
   * Reduces the event to a {@link remixlab.bias.event.DOF2Event} (lossy reduction). Keeps
   * dof-1 and dof-2 and discards dof-3.
   */
  public DOF2Event dof2Event() {
    DOF2Event pe2;
    DOF2Event e2;
    if (isRelative()) {
      pe2 = new DOF2Event(null, prevX(), prevY(), modifiers(), id());
      e2 = new DOF2Event(pe2, x(), y(), modifiers(), id());
    } else {
      e2 = new DOF2Event(dx(), dy(), modifiers(), id());
    }
    e2.modifiedTimestamp(this.timestamp());
    e2.delay = this.delay();
    e2.speed = this.speed();
    e2.distance = this.distance();
    if (fired())
      return e2.fire();
    else if (flushed())
      return e2.flush();
    return e2;
  }
}
