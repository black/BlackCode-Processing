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
import remixlab.dandelion.core.GenericFrame;
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
public class InteractiveFrame extends GenericP5Frame {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(id).toHashCode();
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
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, other.id).isEquals();
  }

  // shape
  protected PShape pshape;
  protected int id;
  protected Vec shift;

  // graphics handler
  protected Object drawHandlerObject;
  protected Method drawHandlerMethod;

  protected boolean highlight = true;

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
    id = ++Scene.frameCount;
    shift = new Vec();
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
    id = ++Scene.frameCount;
    shift = new Vec();
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
    id = ++Scene.frameCount;
    shift = new Vec();
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
    id = ++Scene.frameCount;
    shift = new Vec();
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
    id = ++Scene.frameCount;
    shift = new Vec();
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
    id = ++Scene.frameCount;
    shift = new Vec();
    addGraphicsHandler(obj, methodName);
    setPickingPrecision(PickingPrecision.EXACT);
  }

  protected InteractiveFrame(InteractiveFrame otherFrame) {
    super(otherFrame);
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

  /**
   * Same as {@code ((Scene) scene).applyTransformation(pg, this)}.
   * 
   * @see remixlab.proscene.Scene#applyTransformation(PGraphics, Frame)
   */
  public void applyTransformation(PGraphics pg) {
    ((Scene) gScene).applyTransformation(pg, this);
  }

  /**
   * Same as {@code ((Scene) scene).applyWorldTransformation(pg, this)}.
   * 
   * @see remixlab.proscene.Scene#applyWorldTransformation(PGraphics, Frame)
   */
  public void applyWorldTransformation(PGraphics pg) {
    ((Scene) gScene).applyWorldTransformation(pg, this);
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
    ((Scene) gScene).pApplet().pushStyle();
    ((Scene) gScene).pApplet().colorMode(PApplet.HSB, 4);

    hue = ((Scene) gScene).pApplet().hue(color);
    saturation = ((Scene) gScene).pApplet().saturation(color);
    brightness = ((Scene) gScene).pApplet().brightness(color);
    brightness = brightness < 1 ? 2 : brightness < 2 ? 3 : 4;
    c = ((Scene) gScene).pApplet().color(hue, saturation, brightness);

    ((Scene) gScene).pApplet().popStyle();
    return c;
  }

  protected void highlight(PGraphics pg) {
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
    return ((Scene) gScene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
  }

  /**
   * Shifts the {@link #shape()} respect to the frame {@link #position()}. Default value
   * is zero.
   * 
   * @see #graphicsShift()
   */
  public void shiftGraphics(Vec shift) {
    this.shift = shift;
  }

  /**
   * Returns the {@link #shape()} shift.
   * 
   * @see #shiftGraphics(Vec)
   */
  public Vec graphicsShift() {
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
    if (shape() != null || this.hasGraphicsHandler())
      return true;
    else
      for (InteractiveFrame frame : ((Scene) gScene).frames())
        if (frame.shape() != null || frame.hasGraphicsHandler())
          return true;
    return false;
  }

  /**
   * Internal cache optimization method.
   */
  protected boolean update(PickingPrecision precision) {
    if (precision == PickingPrecision.EXACT)
      return true;
    for (InteractiveFrame frame : ((Scene) gScene).frames())
      if (frame.pickingPrecision() == PickingPrecision.EXACT)
        return true;
    return false;
  }

  @Override
  public void setPickingPrecision(PickingPrecision precision) {
    if (precision == PickingPrecision.EXACT)
      if (!((Scene) gScene).isPickingBufferEnabled())
        System.out.println(
            "Warning: EXACT picking precision will behave like FIXED until the scene.pickingBuffer() is enabled.");
    pkgnPrecision = precision;
    Scene.PRECISION = update(pickingPrecision());
  }

  /**
   * An interactive-frame may be picked using
   * <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a> with a
   * color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method
   * compares the color of the {@link remixlab.proscene.Scene#pickingBuffer()} at
   * {@code (x,y)} with {@link #id()}. Returns true if both colors are the same, and false
   * otherwise.
   * 
   * @see #setPickingPrecision(PickingPrecision)
   */
  @Override
  public final boolean checkIfGrabsInput(float x, float y) {
    if (pickingPrecision() != PickingPrecision.EXACT || (shape() == null && !this.hasGraphicsHandler())
        || !((Scene) gScene).isPickingBufferEnabled())
      return super.checkIfGrabsInput(x, y);
    ((Scene) gScene).pickingBuffer().pushStyle();
    ((Scene) gScene).pickingBuffer().colorMode(PApplet.RGB, 255);
    int index = (int) y * gScene.width() + (int) x;
    if ((0 <= index) && (index < ((Scene) gScene).pickingBuffer().pixels.length))
      return ((Scene) gScene).pickingBuffer().pixels[index] == id();
    ((Scene) gScene).pickingBuffer().popStyle();
    return false;
  }

  /**
   * Same as {@code draw(scene.pg())}.
   * 
   * @see remixlab.proscene.Scene#drawFrames(PGraphics)
   */
  public void draw() {
    if (shape() == null && !this.hasGraphicsHandler())
      return;
    PGraphics pg = ((Scene) gScene).pg();
    draw(pg);
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
    ((Scene) gScene).applyWorldTransformation(pg, this);
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
    if (pg == ((Scene) gScene).pickingBuffer())
      beginPickingBuffer();
    pg.pushMatrix();
    pg.translate(shift.x(), shift.y(), shift.z());
    // TODO shapes pending, requires PShape style, stroke* and fill* to be readable
    if (isHighlightingEnabled() && this.grabsInput() && pg != ((Scene) gScene).pickingBuffer())
      highlight(pg);
    if (shape() != null)
      pg.shape(shape());
    if (this.hasGraphicsHandler())
      this.invokeGraphicsHandler(pg);
    pg.popMatrix();
    if (pg == ((Scene) gScene).pickingBuffer())
      endPickingBuffer();
    pg.popStyle();
  }

  protected void beginPickingBuffer() {
    PGraphics pickingBuffer = ((Scene) gScene).pickingBuffer();
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
   * {@link #draw(PGraphics)}.
   */
  protected boolean invokeGraphicsHandler(PGraphics pg) {
    if (drawHandlerObject != null) {
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