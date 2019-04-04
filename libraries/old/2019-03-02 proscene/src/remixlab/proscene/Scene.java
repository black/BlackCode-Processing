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

import processing.core.*;
import processing.opengl.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.ext.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

// begin: GWT-incompatible
///*
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
// end: GWT-incompatible
//*/

/**
 * A 2D or 3D interactive Processing Scene with a {@link #profile()} instance which allows
 * {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method} bindings
 * high-level customization (see all the <b>*Binding*()</b> methods). The Scene is a
 * specialization of the {@link remixlab.dandelion.core.AbstractScene}, providing an
 * interface between Dandelion and Processing.
 * <p>
 * <h3>Usage</h3> To use a Scene you have three choices:
 * <ol>
 * <li><b>Direct instantiation</b>. In this case you should instantiate your own Scene
 * object at the {@code PApplet.setup()} function. See the example <i>BasicUse</i>.
 * <li><b>Inheritance</b>. In this case, once you declare a Scene derived class, you
 * should implement {@link #proscenium()} which defines the objects in your scene. Just
 * make sure to define the {@code PApplet.draw()} method, even if it's empty. See the
 * example <i>AlternativeUse</i>.
 * <li><b>External draw handler registration</b>. In addition, you can even declare an
 * external drawing method and then register it at the Scene with
 * {@link #addGraphicsHandler(Object, String)}. That method should return {@code void} and
 * have one single {@code Scene} parameter. This strategy may be useful when there are
 * multiple viewers sharing the same drawing code. See the example <i>StandardCamera</i>.
 * </ol>
 * <h3>Interactivity mechanisms</h3> ProScene provides powerful interactivity mechanisms
 * allowing a wide range of scene setups ranging from very simple to complex ones. For
 * convenience, two interaction mechanisms are provided by default:
 * {@link #keyboardAgent()}, and {@link #motionAgent()} (which in the desktop version of
 * proscene defaults to a {@link #mouseAgent()}):
 * <ol>
 * <li><b>The default keyboard agent</b> provides shortcuts to
 * {@link remixlab.proscene.InteractiveFrame}s and scene keyboard actions (such as
 * {@link #drawGrid()} or {@link #drawAxes()}). See {@link #keyboardAgent()}.
 * <li><b>The default mouse agent</b> provides high-level methods to manage the
 * {@link remixlab.dandelion.core.Eye} and {@link remixlab.proscene.InteractiveFrame}
 * motion actions. Please refer to the {@link remixlab.proscene.MouseAgent} and
 * {@link remixlab.proscene.KeyAgent} API's.
 * </ol>
 * <h3>Animation mechanisms</h3> ProScene provides three animation mechanisms to define
 * how your scene evolves over time:
 * <ol>
 * <li><b>Overriding the Dandelion {@link #animate()} method.</b> In this case, once you
 * declare a Scene derived class, you should implement {@link #animate()} which defines
 * how your scene objects evolve over time. See the example <i>Animation</i>.
 * <li><b>By checking if the Dandelion AbstractScene's {@link #timer()} was triggered
 * within the frame.</b> See the example <i>Flock</i>.
 * <li><b>External animation handler registration.</b> In addition (not being part of
 * Dandelion), you can also declare an external animation method and then register it at
 * the Scene with {@link #addAnimationHandler(Object, String)}. That method should return
 * {@code void} and have one single {@code Scene} parameter. See the example
 * <i>AnimationHandler</i>.
 * </ol>
 * <h3>Scene frames</h3> Each scene has a collection of
 * {@link remixlab.proscene.InteractiveFrame}s (see {@link #frames()}). An
 * {@link remixlab.proscene.InteractiveFrame} is a high level
 * {@link remixlab.dandelion.geom.Frame} PSshape wrapper (a coordinate system related to a
 * PShape or an arbitrary graphics procedure) which may be manipulated by any
 * {@link remixlab.bias.core.Agent}) and for which the scene implements a
 * <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a> with a color
 * buffer technique for easy and precise object selection (see {@link #pickingBuffer()}
 * and {@link #drawFrames(PGraphics)}).
 */
public class Scene extends AbstractScene implements PConstants {
  // begin: GWT-incompatible
  // /*
  // Reflection
  // 1. Draw
  protected Object drawHandlerObject;
  // The method in drawHandlerObject to execute
  protected Method drawHandlerMethod;
  // 2. Animation
  // The object to handle the animation
  protected Object animateHandlerObject;
  // The method in animateHandlerObject to execute
  protected Method animateHandlerMethod;

  // Timing
  protected boolean javaTiming;
  // end: GWT-incompatible
  // */

  public static final String prettyVersion = "3.0.0-beta.4";

  public static final String version = "26";

  // P R O C E S S I N G A P P L E T A N D O B J E C T S
  protected PApplet parent;
  protected PGraphics mainPGgraphics;

  // iFrames
  protected static int frameCount;
  protected PGraphics pBuffer;
  protected boolean pBufferEnabled;

  protected Profile profile;

  // E X C E P T I O N H A N D L I N G
  protected int beginOffScreenDrawingCalls;

  // CONSTRUCTORS

  /**
   * Constructor that defines an on-screen Processing Scene. Same as {@code this(p, p.g}.
   * 
   * @see #Scene(PApplet, PGraphics)
   * @see #Scene(PApplet, PGraphics, int, int)
   */
  public Scene(PApplet p) {
    this(p, p.g);
  }

  /**
   * Same as {@code this(p, renderer, 0, 0)}.
   * 
   * @see #Scene(PApplet)
   * @see #Scene(PApplet, PGraphics, int, int)
   */
  public Scene(PApplet p, PGraphics renderer) {
    this(p, renderer, 0, 0);
  }

  /**
   * Main constructor defining a left-handed Processing compatible Scene. Calls
   * {@link #setMatrixHelper(MatrixHelper)} using a customized
   * {@link remixlab.dandelion.core.MatrixHelper} depending on the {@code pg} type (see
   * {@link remixlab.proscene.Java2DMatrixHelper} and
   * {@link remixlab.proscene.GLMatrixHelper}). The constructor instantiates the
   * {@link #inputHandler()} and the {@link #timingHandler()}, sets the AXIS and GRID
   * visual hint flags, instantiates the {@link #eye()} (a
   * {@link remixlab.dandelion.core.Camera} if the Scene {@link #is3D()} or a
   * {@link remixlab.dandelion.core.Window} if the Scene {@link #is2D()}). It also
   * instantiates the {@link #keyboardAgent()} and the {@link #mouseAgent()}, and finally
   * calls {@link #init()}.
   * <p>
   * An off-screen Processing Scene is defined if {@code pg != p.g}. In this case the
   * {@code x} and {@code y} parameters define the position of the upper-left corner where
   * the off-screen Scene is expected to be displayed, e.g., for instance with a call to
   * Processing the {@code image(img, x, y)} function. If {@code pg == p.g}) (which
   * defines an on-screen Scene, see also {@link #isOffscreen()}), the values of x and y
   * are meaningless (both are set to 0 to be taken as dummy values).
   * 
   * @see remixlab.dandelion.core.AbstractScene#AbstractScene()
   * @see #Scene(PApplet)
   * @see #Scene(PApplet, PGraphics)
   */
  public Scene(PApplet p, PGraphics pg, int x, int y) {
    // 1. P5 objects
    parent = p;
    mainPGgraphics = pg;
    offscreen = pg != p.g;
    upperLeftCorner = offscreen ? new Point(x, y) : new Point(0, 0);

    // 2. Matrix helper
    setMatrixHelper(matrixHelper(pg));

    // 3. Frames & picking buffer
    // pBuffer = (pg() instanceof processing.opengl.PGraphicsOpenGL) ?
    // pApplet().createGraphics(pg().width, pg().height, pg() instanceof
    // PGraphics3D ? P3D : P2D) : pApplet().createGraphics(pg().width,
    // pg().height, JAVA2D);
    pBuffer = (pg() instanceof processing.opengl.PGraphicsOpenGL)
        ? pApplet().createGraphics(pg().width, pg().height, pg() instanceof PGraphics3D ? P3D : P2D) : null;
    if (pBuffer != null)
      enablePickingBuffer();

    // 4. Create agents and register P5 methods
    setProfile(new Profile(this));
    // TODO android
    // discard this block one android is restored
    {
      defMotionAgent = new MouseAgent(this);
      defKeyboardAgent = new KeyAgent(this);
      parent.registerMethod("mouseEvent", motionAgent());
    }
    // if (platform() == Platform.PROCESSING_ANDROID) {
    // defMotionAgent = new DroidTouchAgent(this, "proscene_touch");
    // defKeyboardAgent = new DroidKeyAgent(this, "proscene_keyboard");
    // }
    // else {
    // defMotionAgent = new MouseAgent(this, "proscene_mouse");
    // defKeyboardAgent = new KeyAgent(this, "proscene_keyboard");
    // parent.registerMethod("mouseEvent", motionAgent());
    // }

    parent.registerMethod("keyEvent", keyboardAgent());
    this.setDefaultKeyBindings();

    pApplet().registerMethod("pre", this);
    pApplet().registerMethod("draw", this);

    // Android: remove the following 2 lines if needed to compile the project
    // if (platform() == Platform.PROCESSING_ANDROID)
    // disablePickingBuffer();
    if (this.isOffscreen() && (upperLeftCorner.x() != 0 || upperLeftCorner.y() != 0))
      pApplet().registerMethod("post", this);

    // 5. Eye
    setLeftHanded();
    width = pg.width;
    height = pg.height;
    // properly the eye which is a 3 step process:
    eye = is3D() ? new Camera(this) : new Window(this);
    eye.setFrame(new InteractiveFrame(eye));
    setEye(eye());// calls showAll();

    // 6. Misc stuff:
    setDottedGrid(!(platform() == Platform.PROCESSING_ANDROID || is2D()));
    if (platform() == Platform.PROCESSING_DESKTOP || platform() == Platform.PROCESSING_ANDROID)
      this.setNonSeqTimers();
    // pApplet().frameRate(100);

    // 7. Init should be called only once
    init();
  }

  @Override
  public InteractiveFrame eyeFrame() {
    return (InteractiveFrame) eye.frame();
  }

  @Override
  protected boolean checkIfGrabsInput(KeyboardEvent event) {
    return profile.hasBinding(event.shortcut());
  }

  // P5 STUFF

  /**
   * Returns the PApplet instance this Scene is related to.
   */
  public PApplet pApplet() {
    return parent;
  }

  /**
   * Returns the PGraphics instance this Scene is related to. It may be the PApplets one,
   * if the Scene is on-screen or an user-defined if the Scene {@link #isOffscreen()}.
   */
  public PGraphics pg() {
    return mainPGgraphics;
  }

  // PICKING BUFFER

  /**
   * Returns the {@link #frames()}
   * <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a> color
   * buffer.
   * 
   * @see #drawFrames()
   * @see #drawFrames(PGraphics)
   */
  public PGraphics pickingBuffer() {
    return pBuffer;
  }

  /**
   * Enable the {@link #pickingBuffer()}.
   */
  public void enablePickingBuffer() {
    if (!(pBufferEnabled = pBuffer != null))
      System.out.println("PickingBuffer can't be instantiated!");
  }

  /**
   * Disable the {@link #pickingBuffer()}.
   */
  public void disablePickingBuffer() {
    pBufferEnabled = false;
  }

  /**
   * Returns {@code true} if {@link #pickingBuffer()} buffer is enabled and {@code false}
   * otherwise.
   */
  public boolean isPickingBufferEnabled() {
    return pBufferEnabled;
  }

  /**
   * Toggles availability of the {@link #pickingBuffer()}.
   */
  public void togglePickingBuffer() {
    if (isPickingBufferEnabled())
      disablePickingBuffer();
    else
      enablePickingBuffer();
  }

  @Override
  public int width() {
    return pg().width;
  }

  @Override
  public int height() {
    return pg().height;
  }

  // DIM

  @Override
  public boolean is3D() {
    return (mainPGgraphics instanceof PGraphics3D);
  }

  // CHOOSE PLATFORM

  @Override
  protected void setPlatform() {
    Properties p = System.getProperties();
    Enumeration<?> keys = p.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = (String) p.get(key);
      if (key.contains("java.vm.vendor")) {
        if (Pattern.compile(Pattern.quote("Android"), Pattern.CASE_INSENSITIVE).matcher(value).find())
          platform = Platform.PROCESSING_ANDROID;
        else
          platform = Platform.PROCESSING_DESKTOP;
        break;
      }
    }
  }

  // P5-WRAPPERS

  /**
   * Same as {@code if (this.is2D()) vertex(pg(), x, y); elsevertex(pg(), x, y, z)}.
   * 
   * @see #vertex(PGraphics, float, float, float)
   */
  public void vertex(float x, float y, float z) {
    if (this.is2D())
      vertex(pg(), x, y);
    else
      vertex(pg(), x, y, z);
  }

  /**
   * Wrapper for PGraphics.vertex(x,y,z)
   */
  public static void vertex(PGraphics pg, float x, float y, float z) {
    if (pg instanceof PGraphics3D)
      pg.vertex(x, y, z);
    else
      pg.vertex(x, y);
  }

  /**
   * Same as {@code vertex(pg(), x, y)}.
   * 
   * @see #vertex(PGraphics, float, float)
   */
  public void vertex(float x, float y) {
    vertex(pg(), x, y);
  }

  /**
   * Wrapper for PGraphics.vertex(x,y)
   */
  public static void vertex(PGraphics pg, float x, float y) {
    pg.vertex(x, y);
  }

  /**
   * Same as
   * {@code if (this.is2D()) line(pg(), x1, y1, x2, y2); else line(pg(), x1, y1, z1, x2, y2, z2);}
   * .
   * 
   * @see #line(PGraphics, float, float, float, float, float, float)
   */
  public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
    if (this.is2D())
      line(pg(), x1, y1, x2, y2);
    else
      line(pg(), x1, y1, z1, x2, y2, z2);
  }

  /**
   * Wrapper for PGraphics.line(x1, y1, z1, x2, y2, z2)
   */
  public static void line(PGraphics pg, float x1, float y1, float z1, float x2, float y2, float z2) {
    if (pg instanceof PGraphics3D)
      pg.line(x1, y1, z1, x2, y2, z2);
    else
      pg.line(x1, y1, x2, y2);
  }

  /**
   * Same as {@code pg().line(x1, y1, x2, y2)}.
   * 
   * @see #line(PGraphics, float, float, float, float)
   */
  public void line(float x1, float y1, float x2, float y2) {
    line(pg(), x1, y1, x2, y2);
  }

  /**
   * Wrapper for PGraphics.line(x1, y1, x2, y2)
   */
  public static void line(PGraphics pg, float x1, float y1, float x2, float y2) {
    pg.line(x1, y1, x2, y2);
  }

  /**
   * Converts a {@link remixlab.dandelion.geom.Vec} to a PVec.
   */
  public static PVector toPVector(Vec v) {
    return new PVector(v.x(), v.y(), v.z());
  }

  /**
   * Converts a PVec to a {@link remixlab.dandelion.geom.Vec}.
   */
  public static Vec toVec(PVector v) {
    return new Vec(v.x, v.y, v.z);
  }

  /**
   * Converts a {@link remixlab.dandelion.geom.Mat} to a PMatrix3D.
   */
  public static PMatrix3D toPMatrix(Mat m) {
    float[] a = m.getTransposed(new float[16]);
    return new PMatrix3D(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14],
        a[15]);
  }

  /**
   * Converts a PMatrix3D to a {@link remixlab.dandelion.geom.Mat}.
   */
  public static Mat toMat(PMatrix3D m) {
    return new Mat(m.get(new float[16]), true);
  }

  /**
   * Converts a PMatrix2D to a {@link remixlab.dandelion.geom.Mat}.
   */
  public static Mat toMat(PMatrix2D m) {
    return toMat(new PMatrix3D(m));
  }

  /**
   * Converts a {@link remixlab.dandelion.geom.Mat} to a PMatrix2D.
   */
  public static PMatrix2D toPMatrix2D(Mat m) {
    float[] a = m.getTransposed(new float[16]);
    return new PMatrix2D(a[0], a[1], a[3], a[4], a[5], a[7]);
  }

  // firstly, of course, dirty things that I used to love :P

  // DEFAULT MOTION-AGENT

  /**
   * Enables Proscene mouse handling through the {@link #mouseAgent()}.
   * 
   * @see #isMotionAgentEnabled()
   * @see #disableMotionAgent()
   * @see #enableKeyboardAgent()
   */
  @Override
  public void enableMotionAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      enableMouseAgent();
    if (platform() == Platform.PROCESSING_ANDROID)
      enableDroidTouchAgent();
  }

  /**
   * Disables the default mouse agent and returns it.
   * 
   * @see #isMotionAgentEnabled()
   * @see #enableMotionAgent()
   * @see #enableKeyboardAgent()
   * @see #disableKeyboardAgent()
   */
  @Override
  public boolean disableMotionAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      return disableMouseAgent();
    // TODO android
    /*
     * if (platform() == Platform.PROCESSING_ANDROID) return disableDroidTouchAgent();
     */
    return false;
  }

  // KEYBOARD

  /**
   * Enables Proscene keyboard handling through the {@link #keyboardAgent()}.
   * 
   * @see #isKeyboardAgentEnabled()
   * @see #disableKeyboardAgent()
   * @see #enableMotionAgent()
   */
  @Override
  public void enableKeyboardAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      enableKeyAgent();
    if (platform() == Platform.PROCESSING_ANDROID)
      enableDroidKeyAgent();
  }

  /**
   * Disables the default keyboard agent and returns it.
   * 
   * @see #isKeyboardAgentEnabled()
   * @see #enableKeyboardAgent()
   * @see #disableMotionAgent()
   */
  @Override
  public boolean disableKeyboardAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      return disableKeyAgent();
    // TODO android
    /*
     * if (platform() == Platform.PROCESSING_ANDROID) return disableDroidKeyAgent();
     */
    return false;
  }

  // Mouse

  /**
   * Returns the default mouse agent handling Processing mouse events. If you plan to
   * customize your mouse use this method.
   * 
   * @see #enableMouseAgent()
   * @see #isMouseAgentEnabled()
   * @see #disableMouseAgent()
   * @see #keyAgent()
   */
  public MouseAgent mouseAgent() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene mouseAgent() is not available in Android mode. Use droidTouchAgent() instead");
    }
    return (MouseAgent) motionAgent();
  }

  /**
   * Enables motion handling through the {@link #mouseAgent()}.
   * 
   * @see #mouseAgent()
   * @see #isMouseAgentEnabled()
   * @see #disableMouseAgent()
   * @see #enableKeyAgent()
   */
  public void enableMouseAgent() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene enableMouseAgent() is not available in Android mode. Use enableDroidTouchAgent() instead");
    }
    if (!isMotionAgentEnabled()) {
      inputHandler().registerAgent(motionAgent());
      parent.registerMethod("mouseEvent", motionAgent());
    }
  }

  /**
   * Disables the default mouse agent and returns it.
   * 
   * @see #mouseAgent()
   * @see #isMouseAgentEnabled()
   * @see #enableMouseAgent()
   * @see #disableKeyAgent()
   */
  public boolean disableMouseAgent() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene disableMouseAgent() is not available in Android mode. Use disableDroidTouchAgent() instead");
    }
    if (isMotionAgentEnabled()) {
      parent.unregisterMethod("mouseEvent", motionAgent());
      return inputHandler().unregisterAgent(motionAgent());
    }
    return false;
  }

  /**
   * Returns {@code true} if the {@link #mouseAgent()} is enabled and {@code false}
   * otherwise.
   * 
   * @see #mouseAgent()
   * @see #enableMouseAgent()
   * @see #disableMouseAgent()
   * @see #enableKeyAgent()
   */
  public boolean isMouseAgentEnabled() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene isMouseAgentEnabled() is not available in Android mode. Use isDroidTouchAgentEnabled() instead");
    }
    return isMotionAgentEnabled();
  }

  // keyAgent

  /**
   * Returns the default key agent handling Processing key events. If you plan to
   * customize your keyboard use this method.
   * 
   * @see #enableKeyAgent()
   * @see #isKeyAgentEnabled()
   * @see #disableKeyAgent()
   * @see #mouseAgent()
   */
  public KeyAgent keyAgent() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException("Proscene keyAgent() is not available in Android mode. Use droidKeyAgent() instead");
    }
    return (KeyAgent) defKeyboardAgent;
  }

  /**
   * Enables keyboard handling through the {@link #keyAgent()}.
   * 
   * @see #keyAgent()
   * @see #isKeyAgentEnabled()
   * @see #disableKeyAgent()
   * @see #enableMouseAgent()
   */
  public void enableKeyAgent() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene enableKeyAgent() is not available in Android mode. Use enableDroidKeyAgent() instead");
    }
    if (!isKeyboardAgentEnabled()) {
      inputHandler().registerAgent(keyboardAgent());
      parent.registerMethod("keyEvent", keyboardAgent());
    }
  }

  /**
   * Disables the key agent and returns it.
   * 
   * @see #keyAgent()
   * @see #isKeyAgentEnabled()
   * @see #enableKeyAgent()
   * @see #disableMouseAgent()
   */
  public boolean disableKeyAgent() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene disableKeyAgent() is not available in Android mode. Use disableDroidKeyAgent() instead");
    }
    if (inputHandler().isAgentRegistered(keyboardAgent())) {
      parent.unregisterMethod("keyEvent", keyboardAgent());
      return inputHandler().unregisterAgent(keyboardAgent());
    }
    return false;
  }

  /**
   * Returns {@code true} if the {@link #keyAgent()} is enabled and {@code false}
   * otherwise.
   * 
   * @see #keyAgent()
   * @see #enableKeyAgent()
   * @see #disableKeyAgent()
   * @see #enableKeyAgent()
   */
  public boolean isKeyAgentEnabled() {
    if (platform() == Platform.PROCESSING_ANDROID) {
      throw new RuntimeException(
          "Proscene isKeyAgentEnabled() is not available in Android mode. Use isDroidKeyAgentEnabled() instead");
    }
    return isKeyboardAgentEnabled();
  }

  // droid touch

  /**
   * Returns the default droid touch agent handling touch events. If you plan to customize
   * your touch use this method.
   * 
   * @see #enableMouseAgent()
   * @see #isMouseAgentEnabled()
   * @see #disableMouseAgent()
   * @see #droidKeyAgent()
   */
  // TODO android
  /*
   * public DroidTouchAgent droidTouchAgent() { if (platform() ==
   * Platform.PROCESSING_DESKTOP) { throw new RuntimeException(
   * "Proscene droidTouchAgent() is not available in Desktop mode. Use mouseAgent() instead"
   * ); } return (DroidTouchAgent) motionAgent(); }
   */

  /**
   * Enables motion handling through the {@link #droidTouchAgent()}.
   * 
   * @see #droidTouchAgent()
   * @see #isDroidTouchAgentEnabled()
   * @see #disableDroidTouchAgent()
   * @see #enableDroidKeyAgent()
   */
  public void enableDroidTouchAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP) {
      throw new RuntimeException(
          "Proscene enableDroidTouchAgent() is not available in Desktop mode. Use enableMouseAgent() instead");
    }
    super.enableMotionAgent();
  }

  /**
   * Disables the default droid touch agent and returns it.
   * 
   * @see #droidTouchAgent()
   * @see #isDroidTouchAgentEnabled()
   * @see #enableDroidTouchAgent()
   * @see #disableDroidKeyAgent()
   */
  // TODO android
  /*
   * public DroidTouchAgent disableDroidTouchAgent() { if (platform() ==
   * Platform.PROCESSING_DESKTOP) { throw new RuntimeException(
   * "Proscene disableDroidTouchAgent() is not available in Desktop mode. Use disableMouseAgent() instead"
   * ); } return (DroidTouchAgent)motionAgent(); }
   */

  /**
   * Returns {@code true} if the {@link #droidTouchAgent()} is enabled and {@code false}
   * otherwise.
   * 
   * @see #droidTouchAgent()
   * @see #enableDroidTouchAgent()
   * @see #disableDroidTouchAgent()
   * @see #enableDroidKeyAgent()
   */
  public boolean isDroidTouchAgentEnabled() {
    if (platform() == Platform.PROCESSING_DESKTOP) {
      throw new RuntimeException(
          "Proscene isDroidTouchAgentEnabled() is not available in Android mode. Use isDroidKeyAgentEnabled() instead");
    }
    return isMotionAgentEnabled();
  }

  // droid key

  /**
   * Returns the default droid key agent handling touch events. If you plan to customize
   * your touch use this method.
   * 
   * @see #enableDroidKeyAgent()
   * @see #isDroidKeyAgentEnabled()
   * @see #disableDroidKeyAgent()
   * @see #droidTouchAgent()
   */
  // TODO android
  /*
   * public DroidKeyAgent droidKeyAgent() { if (platform() == Platform.PROCESSING_DESKTOP)
   * { throw new RuntimeException(
   * "Proscene droidKeyAgent() is not available in Desktop mode. Use keyAgent() instead"
   * ); } return (DroidKeyAgent)defKeyboardAgent; }
   */

  /**
   * Enables keyboard handling through the {@link #droidKeyAgent()}.
   * 
   * @see #droidKeyAgent()
   * @see #isDroidKeyAgentEnabled()
   * @see #disableDroidKeyAgent()
   * @see #enableDroidTouchAgent()
   */
  public void enableDroidKeyAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP) {
      throw new RuntimeException(
          "Proscene enableDroidKeyAgent() is not available in Desktop mode. Use enableKeyAgent() instead");
    }
    super.enableKeyboardAgent();
  }

  /**
   * Disables the droid key agent and returns it.
   * 
   * @see #droidKeyAgent()
   * @see #isDroidKeyAgentEnabled()
   * @see #enableDroidKeyAgent()
   * @see #disableDroidTouchAgent()
   */
  // TODO android
  /*
   * public DroidKeyAgent disableDroidKeyAgent() { if (platform() ==
   * Platform.PROCESSING_DESKTOP) { throw new RuntimeException(
   * "Proscene disableDroidKeyAgent() is not available in Desktop mode. Use disableKeyAgent() instead"
   * ); } return (DroidKeyAgent)keyboardAgent(); }
   */

  /**
   * Returns {@code true} if the {@link #droidKeyAgent()} is enabled and {@code false}
   * otherwise.
   * 
   * @see #keyAgent()
   * @see #enableKeyAgent()
   * @see #disableKeyAgent()
   * @see #enableKeyAgent()
   */
  public boolean isDroidKeyAgentEnabled() {
    if (platform() == Platform.PROCESSING_DESKTOP) {
      throw new RuntimeException(
          "Proscene isDroidKeyAgentEnabled() is not available in Android mode. Use isDroidKeyAgentEnabled() instead");
    }
    return isKeyboardAgentEnabled();
  }

  // INFO

  protected static String parseInfo(String info) {
    // mouse:
    String l = "ID_" + String.valueOf(MouseAgent.LEFT_ID);
    String r = "ID_" + String.valueOf(MouseAgent.RIGHT_ID);
    String c = "ID_" + String.valueOf(MouseAgent.CENTER_ID);
    String w = "ID_" + String.valueOf(MouseAgent.WHEEL_ID);
    String n = "ID_" + String.valueOf(MouseAgent.NO_BUTTON);

    // ... and replace it with proper descriptions:

    info = info.replace(l, "LEFT_BUTTON").replace(r, "RIGHT_BUTTON").replace(c, "CENTER_BUTTON").replace(w, "WHEEL")
        .replace(n, "NO_BUTTON");

    // add other agents here:
    return info;
  }

  protected static String parseKeyInfo(String info) {
    // parse...
    // the left-right-up-down keys:
    String vk_l = "VKEY_" + String.valueOf(37);
    String vk_u = "VKEY_" + String.valueOf(38);
    String vk_r = "VKEY_" + String.valueOf(39);
    String vk_d = "VKEY_" + String.valueOf(40);
    // the function keys
    String vk_f1 = "VKEY_" + String.valueOf(112);
    String vk_f2 = "VKEY_" + String.valueOf(113);
    String vk_f3 = "VKEY_" + String.valueOf(114);
    String vk_f4 = "VKEY_" + String.valueOf(115);
    String vk_f5 = "VKEY_" + String.valueOf(116);
    String vk_f6 = "VKEY_" + String.valueOf(117);
    String vk_f7 = "VKEY_" + String.valueOf(118);
    String vk_f8 = "VKEY_" + String.valueOf(119);
    String vk_f9 = "VKEY_" + String.valueOf(120);
    String vk_f10 = "VKEY_" + String.valueOf(121);
    String vk_f11 = "VKEY_" + String.valueOf(122);
    String vk_f12 = "VKEY_" + String.valueOf(123);
    // other common keys
    String vk_cancel = "VKEY_" + String.valueOf(3);
    String vk_insert = "VKEY_" + String.valueOf(155);
    String vk_delete = "VKEY_" + String.valueOf(127);
    String vk_scape = "VKEY_" + String.valueOf(27);
    String vk_enter = "VKEY_" + String.valueOf(10);
    String vk_pageup = "VKEY_" + String.valueOf(33);
    String vk_pagedown = "VKEY_" + String.valueOf(34);
    String vk_end = "VKEY_" + String.valueOf(35);
    String vk_home = "VKEY_" + String.valueOf(36);
    String vk_begin = "VKEY_" + String.valueOf(65368);

    // ... and replace it with proper descriptions:

    info = info.replace(vk_l, "LEFT_vkey").replace(vk_u, "UP_vkey").replace(vk_r, "RIGHT_vkey")
        .replace(vk_d, "DOWN_vkey").replace(vk_f1, "F1_vkey").replace(vk_f2, "F2_vkey").replace(vk_f3, "F3_vkey")
        .replace(vk_f4, "F4_vkey").replace(vk_f5, "F5_vkey").replace(vk_f6, "F6_vkey").replace(vk_f7, "F7_vkey")
        .replace(vk_f8, "F8_vkey").replace(vk_f9, "F9_vkey").replace(vk_f10, "F10_vkey").replace(vk_f11, "F11_vkey")
        .replace(vk_f12, "F12_vkey").replace(vk_cancel, "CANCEL_vkey").replace(vk_insert, "INSERT_vkey")
        .replace(vk_delete, "DELETE_vkey").replace(vk_scape, "SCAPE_vkey").replace(vk_enter, "ENTER_vkey")
        .replace(vk_pageup, "PAGEUP_vkey").replace(vk_pagedown, "PAGEDOWN_vkey").replace(vk_end, "END_vkey")
        .replace(vk_home, "HOME_vkey").replace(vk_begin, "BEGIN_vkey");
    // */

    return info;
  }

  @Override
  public String info() {
    String result = new String();
    String info = profile().info(KeyboardShortcut.class);
    if (!info.isEmpty()) {
      result = "1. Scene key bindings:\n";
      result += parseKeyInfo(info);
    }
    info = eyeFrame().info(); // frame already parses info :P
    if (!info.isEmpty()) {
      result += "2. Eye bindings:\n";
      result += info;
    }
    if (this.leadingFrames().size() > 0)
      result += "3. For a specific frame bindings use: frame.info():\n";
    /*
     * result += "Frames' info\n"; for (InteractiveFrame frame : frames()) { result +=
     * frame.info(); } //
     */
    return result;
  }

  @Override
  public void displayInfo(boolean onConsole) {
    if (onConsole)
      System.out.println(info());
    else { // on applet
      pg().textFont(parent.createFont("Arial", 12));
      beginScreenDrawing();
      pg().fill(0, 255, 0);
      pg().textLeading(20);
      pg().text(info(), 10, 10, (pg().width - 20), (pg().height - 20));
      endScreenDrawing();
    }
  }

  // begin: GWT-incompatible
  // /*

  // TIMING

  @Override
  public void registerTimingTask(TimingTask task) {
    if (areTimersSeq())
      timingHandler().registerTask(task);
    else
      timingHandler().registerTask(task, new NonSeqTimer(this, task));
  }

  /**
   * Sets all {@link #timingHandler()} timers as (single-threaded)
   * {@link remixlab.fpstiming.SeqTimer}(s).
   * 
   * @see #setNonSeqTimers()
   * @see #shiftTimers()
   * @see #areTimersSeq()
   */
  public void setSeqTimers() {
    if (areTimersSeq())
      return;

    javaTiming = false;
    timingHandler().restoreTimers();
  }

  /**
   * Sets all {@link #timingHandler()} timers as (multi-threaded) java.util.Timer(s).
   * 
   * @see #setSeqTimers()
   * @see #shiftTimers()
   * @see #areTimersSeq()
   */
  public void setNonSeqTimers() {
    if (!areTimersSeq())
      return;

    boolean isActive;

    for (TimingTask task : timingHandler().timerPool()) {
      long period = 0;
      boolean rOnce = false;
      isActive = task.isActive();
      if (isActive) {
        period = task.period();
        rOnce = task.timer().isSingleShot();
      }
      task.stop();
      task.setTimer(new NonSeqTimer(this, task));
      if (isActive) {
        if (rOnce)
          task.runOnce(period);
        else
          task.run(period);
      }
    }

    javaTiming = true;
    PApplet.println("java util timers set");
  }

  /**
   * @return true, if timing is handling sequentially (i.e., all {@link #timingHandler()}
   *         timers are (single-threaded) {@link remixlab.fpstiming.SeqTimer}(s)).
   * 
   * @see #setSeqTimers()
   * @see #setNonSeqTimers()
   * @see #shiftTimers()
   */
  public boolean areTimersSeq() {
    return !javaTiming;
  }

  /**
   * If {@link #areTimersSeq()} calls {@link #setNonSeqTimers()}, otherwise call
   * {@link #setSeqTimers()}.
   */
  public void shiftTimers() {
    if (areTimersSeq())
      setNonSeqTimers();
    else
      setSeqTimers();
  }

  // DRAW METHOD REG

  @Override
  protected boolean invokeGraphicsHandler() {
    // 3. Draw external registered method
    if (drawHandlerObject != null) {
      try {
        drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this });
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
   * Attempt to add a 'draw' handler method to the Scene. The default event handler is a
   * method that returns void and has one single Scene parameter.
   * 
   * @param obj
   *          the object to handle the event
   * @param methodName
   *          the method to execute in the object handler class
   * 
   * @see #removeGraphicsHandler()
   * @see #invokeGraphicsHandler()
   */
  public void addGraphicsHandler(Object obj, String methodName) {
    try {
      drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { Scene.class });
      drawHandlerObject = obj;
    } catch (Exception e) {
      PApplet.println("Something went wrong when registering your " + methodName + " method");
      e.printStackTrace();
    }
  }

  /**
   * Unregisters the 'draw' handler method (if any has previously been added to the
   * Scene).
   * 
   * @see #addGraphicsHandler(Object, String)
   * @see #invokeGraphicsHandler()
   */
  public void removeGraphicsHandler() {
    drawHandlerMethod = null;
    drawHandlerObject = null;
  }

  /**
   * Returns {@code true} if the user has registered a 'draw' handler method to the Scene
   * and {@code false} otherwise.
   * 
   * @see #addGraphicsHandler(Object, String)
   * @see #invokeGraphicsHandler()
   */
  public boolean hasGraphicsHandler() {
    if (drawHandlerMethod == null)
      return false;
    return true;
  }

  // ANIMATION METHOD REG

  @Override
  public boolean invokeAnimationHandler() {
    if (animateHandlerObject != null) {
      try {
        animateHandlerMethod.invoke(animateHandlerObject, new Object[] { this });
        return true;
      } catch (Exception e) {
        PApplet.println("Something went wrong when invoking your " + animateHandlerMethod.getName() + " method");
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  /**
   * Attempt to add an 'animation' handler method to the Scene. The default event handler
   * is a method that returns void and has one single Scene parameter.
   * 
   * @param obj
   *          the object to handle the event
   * @param methodName
   *          the method to execute in the object handler class
   * 
   * @see #animate()
   * @see #removeAnimationHandler()
   */
  public void addAnimationHandler(Object obj, String methodName) {
    try {
      animateHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { Scene.class });
      animateHandlerObject = obj;
    } catch (Exception e) {
      PApplet.println("Something went wrong when registering your " + methodName + " method");
      e.printStackTrace();
    }
  }

  /**
   * Unregisters the 'animation' handler method (if any has previously been added to the
   * Scene).
   * 
   * @see #addAnimationHandler(Object, String)
   */
  public void removeAnimationHandler() {
    animateHandlerMethod = null;
    animateHandlerObject = null;
  }

  /**
   * Returns {@code true} if the user has registered an 'animation' handler method to the
   * Scene and {@code false} otherwise.
   * 
   * @see #addAnimationHandler(Object, String)
   * @see #removeAnimationHandler()
   */
  public boolean hasAnimationHandler() {
    if (animateHandlerMethod == null)
      return false;
    return true;
  }

  // OPENGL

  @Override
  public float pixelDepth(Point pixel) {
    PGraphicsOpenGL pggl;
    if (pg() instanceof PGraphicsOpenGL)
      pggl = (PGraphicsOpenGL) pg();
    else
      throw new RuntimeException("pg() is not instance of PGraphicsOpenGL");
    float[] depth = new float[1];
    PGL pgl = pggl.beginPGL();
    pgl.readPixels(pixel.x(), (camera().screenHeight() - pixel.y()), 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT,
        FloatBuffer.wrap(depth));
    pggl.endPGL();
    return depth[0];
  }

  @Override
  public void disableDepthTest() {
    pg().hint(PApplet.DISABLE_DEPTH_TEST);
  }

  @Override
  public void enableDepthTest() {
    pg().hint(PApplet.ENABLE_DEPTH_TEST);
  }

  // end: GWT-incompatible
  // */

  // 3. Drawing methods

  /**
   * Paint method which is called just before your {@code PApplet.draw()} method. Simply
   * calls {@link #preDraw()}. This method is registered at the PApplet and hence you
   * don't need to call it.
   * <p>
   * If {@link #isOffscreen()} does nothing.
   * <p>
   * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and
   * {@link #height()}, and calls
   * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
   * 
   * @see #draw()
   * @see #preDraw()
   * @see #postDraw()
   * @see #beginDraw()
   * @see #endDraw()
   * @see #isOffscreen()
   */
  public void pre() {
    if (isOffscreen())
      return;

    if ((width != pg().width) || (height != pg().height)) {
      width = pg().width;
      height = pg().height;
      eye().setScreenWidthAndHeight(width, height);
    }

    preDraw();
  }

  /**
   * Paint method which is called just after your {@code PApplet.draw()} method. Simply
   * calls {@link #postDraw()}. This method is registered at the PApplet and hence you
   * don't need to call it.
   * <p>
   * If {@link #isOffscreen()} does nothing.
   * 
   * @see #pre()
   * @see #preDraw()
   * @see #postDraw()
   * @see #beginDraw()
   * @see #endDraw()
   * @see #isOffscreen()
   */
  public void draw() {
    if (isOffscreen())
      return;
    postDraw();
  }

  /**
   * Only if the Scene {@link #isOffscreen()}. This method should be called just after the
   * {@link #pg()} beginDraw() method. Simply calls {@link #preDraw()} .
   * <p>
   * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and
   * {@link #height()}, and calls
   * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
   * 
   * @see #draw()
   * @see #preDraw()
   * @see #postDraw()
   * @see #pre()
   * @see #endDraw()
   * @see #isOffscreen()
   */
  public void beginDraw() {
    if (!isOffscreen())
      throw new RuntimeException(
          "begin(/end)Draw() should be used only within offscreen scenes. Check your implementation!");

    if (beginOffScreenDrawingCalls != 0)
      throw new RuntimeException("There should be exactly one beginDraw() call followed by a "
          + "endDraw() and they cannot be nested. Check your implementation!");

    beginOffScreenDrawingCalls++;

    if ((width != pg().width) || (height != pg().height)) {
      width = pg().width;
      height = pg().height;
      eye().setScreenWidthAndHeight(width, height);
    }

    preDraw();
  }

  /**
   * Only if the Scene {@link #isOffscreen()}. This method should be called just before
   * {@link #pg()} endDraw() method. Simply calls {@link #postDraw()}.
   * 
   * @see #draw()
   * @see #preDraw()
   * @see #postDraw()
   * @see #beginDraw()
   * @see #pre()
   * @see #isOffscreen()
   */
  public void endDraw() {
    if (!isOffscreen())
      throw new RuntimeException(
          "(begin/)endDraw() should be used only within offscreen scenes. Check your implementation!");

    beginOffScreenDrawingCalls--;

    if (beginOffScreenDrawingCalls != 0)
      throw new RuntimeException("There should be exactly one beginDraw() call followed by a "
          + "endDraw() and they cannot be nested. Check your implementation!");

    postDraw();
  }

  @Override
  public void postDraw() {
    super.postDraw();
    if (!(this.isOffscreen() && (upperLeftCorner.x() != 0 || upperLeftCorner.y() != 0)))
      post();
  }

  // TODO WARNING: hack: as drawing should never happen here
  // but that's the only way to draw visual hints correctly
  // into an off-screen scene which is shifted from the papplet origin
  public void post() {
    // draw into picking buffer
    // TODO experimental, should be tested when no pshape nor graphics are
    // created, but yet there exists some 'frames'
    if (!this.isPickingBufferEnabled() || !PRECISION || !GRAPHICS)
      return;
    pickingBuffer().beginDraw();
    pickingBuffer().pushStyle();
    pickingBuffer().background(0);
    drawFrames(pickingBuffer());
    pickingBuffer().popStyle();
    pickingBuffer().endDraw();
    // if (frames().size() > 0)
    pickingBuffer().loadPixels();
  }

  /**
   * Same as {@code return Profile.registerMotionID(id, agent.getClass(), dof)}.
   * 
   * @see #registerMotionID(int, Agent, int)
   * @see remixlab.bias.ext.Profile#registerMotionID(int, Class, int)
   */
  public int registerMotionID(int id, Agent agent, int dof) {
    return Profile.registerMotionID(id, agent.getClass(), dof);
  }

  /**
   * Same as {@code return Profile.registerMotionID(agent.getClass(), dof)}.
   *
   * @see #registerMotionID(int, Agent, int)
   * @see remixlab.bias.ext.Profile#registerMotionID(Class, int)
   */
  public int registerMotionID(Agent agent, int dof) {
    return Profile.registerMotionID(agent.getClass(), dof);
  }

  /**
   * Same as {@code return Profile.registerClickID(id, agent.getClass())}.
   * 
   * @see #registerClickID(Agent)
   * @see remixlab.bias.ext.Profile#registerClickID(int, Class)
   */
  public int registerClickID(int id, Agent agent) {
    return Profile.registerClickID(id, agent.getClass());
  }

  /**
   * Same as {@code return Profile.registerClickID(agent.getClass())}.
   * 
   * @see #registerClickID(Agent)
   * @see remixlab.bias.ext.Profile#registerClickID(Class)
   */
  public int registerClickID(Agent agent) {
    return Profile.registerClickID(agent.getClass());
  }

  protected static boolean PRECISION, GRAPHICS;
  protected static PGraphics targetPGraphics;

  @Override
  protected boolean addLeadingFrame(GenericFrame gFrame) {
    boolean result = super.addLeadingFrame(gFrame);
    if (result)
      if (gFrame instanceof InteractiveFrame)
        // a bit weird but otherwise checkifgrabsinput throws a npe at sketch startup
        // if(gFrame instanceof InteractiveFrame)// this line throws the npe too
        if (isPickingBufferEnabled())
        pickingBuffer().loadPixels();
    return result;
  }

  /**
   * Returns the collection of interactive frames the scene handles.
   */
  public ArrayList<InteractiveFrame> frames() {
    ArrayList<InteractiveFrame> iFrames = new ArrayList<InteractiveFrame>();
    for (GenericFrame frame : frames(false))
      if (frame instanceof InteractiveFrame)
        iFrames.add((InteractiveFrame) frame);
    return iFrames;
  }

  /**
   * Collects {@code frame} and all its descendant frames. When {@code eyeframes} is
   * {@code true} eye-frames will also be collected. Note that for a frame to be collected
   * it must be reachable.
   * 
   * @see #isFrameReachable(GenericFrame)
   */
  public ArrayList<InteractiveFrame> branch(GenericFrame frame) {
    ArrayList<InteractiveFrame> iFrames = new ArrayList<InteractiveFrame>();
    for (GenericFrame gFrame : branch(frame, false))
      if (gFrame instanceof InteractiveFrame)
        iFrames.add((InteractiveFrame) gFrame);
    return iFrames;
  }

  /**
   * Draw all scene {@link #frames()} into the {@link #pg()} buffer. A similar (but
   * slightly less efficient) effect may be achieved with
   * {@code for (InteractiveFrame frame : frames()) frame.draw(pg());}.
   * <p>
   * Note that {@code drawFrames()} is typically called from within your sketch
   * {@link #pApplet()} draw() loop.
   * <p>
   * This method is implementing by simply calling
   * {@link remixlab.dandelion.core.AbstractScene#traverseGraph()}.
   * 
   * @see #frames()
   * @see #pg()
   * @see #drawFrames(PGraphics)
   * @see remixlab.proscene.InteractiveFrame#draw(PGraphics)
   */
  /// *
  public void drawFrames() {
    targetPGraphics = pg();
    traverseGraph();
  }

  /**
   * Draw all {@link #frames()} into the given pgraphics. No
   * {@code pgraphics.beginDraw()/endDraw()} calls take place. This method allows shader
   * chaining.
   * <p>
   * Note that {@code drawFrames(pickingBuffer())} (which enables 'picking' of the frames
   * using a <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
   * technique is called by {@link #postDraw()}.
   * 
   * @param pgraphics
   * 
   * @see #frames()
   * @see #drawFrames()
   * @see remixlab.proscene.InteractiveFrame#draw(PGraphics)
   */
  public void drawFrames(PGraphics pgraphics) {
    // 1. Set pgraphics matrices using a custom MatrixHelper
    bindMatrices(pgraphics);
    // 2. Draw all frames into pgraphics
    targetPGraphics = pgraphics;
    traverseGraph();
  }

  /**
   * Returns a new matrix helper for the given {@code pgraphics}. Rarely needed.
   * <p>
   * Note that the current scene matrix helper may be retrieved by {@link #matrixHelper()}
   * .
   * 
   * @see #matrixHelper()
   * @see #setMatrixHelper(MatrixHelper)
   * @see #drawFrames()
   * @see #drawFrames(PGraphics)
   * @see #applyWorldTransformation(PGraphics, Frame)
   */
  public MatrixHelper matrixHelper(PGraphics pgraphics) {
    return (pgraphics instanceof processing.opengl.PGraphicsOpenGL)
        ? new GLMatrixHelper(this, (PGraphicsOpenGL) pgraphics) : new Java2DMatrixHelper(this, pgraphics);
  }

  /**
   * Same as {@code matrixHelper(pgraphics).bind(false)}. Set the {@code pgraphics}
   * matrices by calling
   * {@link remixlab.dandelion.core.MatrixHelper#loadProjection(boolean)} and
   * {@link remixlab.dandelion.core.MatrixHelper#loadModelView(boolean)} (only makes sense
   * when {@link #pg()} is different than {@code pgraphics}).
   * <p>
   * This method doesn't perform any computation, but simple retrieve the current matrices
   * whose actual computation has been updated in {@link #preDraw()}.
   */
  public void bindMatrices(PGraphics pgraphics) {
    if (this.pg() == pgraphics)
      return;
    matrixHelper(pgraphics).bind(false);
  }

  @Override
  protected void visitFrame(GenericFrame frame) {
    targetPGraphics.pushMatrix();
    applyTransformation(targetPGraphics, frame);
    if (frame instanceof GenericFrame)
      frame.visitCallback();
    for (GenericFrame child : frame.children())
      visitFrame(child);
    targetPGraphics.popMatrix();
  }

  /**
   * Apply the local transformation defined by the given {@code frame} on the given
   * {@code pgraphics}. This method doesn't call {@link #bindMatrices(PGraphics)} which
   * should be called manually (only makes sense when {@link #pg()} is different than
   * {@code pgraphics}). Needed by {@link #applyWorldTransformation(PGraphics, Frame)}.
   * 
   * @see #applyWorldTransformation(PGraphics, Frame)
   * @see #bindMatrices(PGraphics)
   */
  public void applyTransformation(PGraphics pgraphics, Frame frame) {
    if (pgraphics instanceof PGraphics3D) {
      pgraphics.translate(frame.translation().vec[0], frame.translation().vec[1], frame.translation().vec[2]);
      pgraphics.rotate(frame.rotation().angle(), ((Quat) frame.rotation()).axis().vec[0],
          ((Quat) frame.rotation()).axis().vec[1], ((Quat) frame.rotation()).axis().vec[2]);
      pgraphics.scale(frame.scaling(), frame.scaling(), frame.scaling());
    } else {
      pgraphics.translate(frame.translation().x(), frame.translation().y());
      pgraphics.rotate(frame.rotation().angle());
      pgraphics.scale(frame.scaling(), frame.scaling());
    }
  }

  /**
   * Apply the global transformation defined by the given {@code frame} on the given
   * {@code pgraphics}. This method doesn't call {@link #bindMatrices(PGraphics)} which
   * should be called manually (only makes sense when {@link #pg()} is different than
   * {@code pgraphics}). Needed by
   * {@link remixlab.proscene.InteractiveFrame#draw(PGraphics)}
   * 
   * @see remixlab.proscene.InteractiveFrame#draw(PGraphics)
   * @see #applyTransformation(PGraphics, Frame)
   * @see #bindMatrices(PGraphics)
   */
  public void applyWorldTransformation(PGraphics pgraphics, Frame frame) {
    Frame refFrame = frame.referenceFrame();
    if (refFrame != null) {
      applyWorldTransformation(pgraphics, refFrame);
      applyTransformation(pgraphics, frame);
    } else {
      applyTransformation(pgraphics, frame);
    }
  }

  // SCREENDRAWING

  /**
   * Need to override it because of this issue:
   * https://github.com/remixlab/proscene/issues/1
   */
  @Override
  public void beginScreenDrawing() {
    if (startCoordCalls != 0)
      throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
          + "endScreenDrawing() and they cannot be nested. Check your implementation!");

    startCoordCalls++;

    pg().hint(PApplet.DISABLE_OPTIMIZED_STROKE);// -> new line not present in
    // AbstractScene.bS
    disableDepthTest();
    matrixHelper.beginScreenDrawing();
  }

  /**
   * Need to override it because of this issue:
   * https://github.com/remixlab/proscene/issues/1
   */
  @Override
  public void endScreenDrawing() {
    startCoordCalls--;
    if (startCoordCalls != 0)
      throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
          + "endScreenDrawing() and they cannot be nested. Check your implementation!");

    matrixHelper.endScreenDrawing();
    enableDepthTest();
    pg().hint(PApplet.ENABLE_OPTIMIZED_STROKE);// -> new line not present in
    // AbstractScene.bS
  }

  // DRAWING

  @Override
  public void drawCylinder(float w, float h) {
    if (is2D()) {
      AbstractScene.showDepthWarning("drawCylinder");
      return;
    }
    drawCylinder(pg(), w, h);
  }

  /**
   * {@link #drawCylinder(float, float)} on {@code pg}.
   */
  public static void drawCylinder(PGraphics pg, float w, float h) {
    pg.pushStyle();
    float px, py;

    pg.beginShape(PApplet.QUAD_STRIP);
    for (float i = 0; i < 13; i++) {
      px = (float) Math.cos(PApplet.radians(i * 30)) * w;
      py = (float) Math.sin(PApplet.radians(i * 30)) * w;
      vertex(pg, px, py, 0);
      vertex(pg, px, py, h);
    }
    pg.endShape();

    pg.beginShape(PApplet.TRIANGLE_FAN);
    vertex(pg, 0, 0, 0);
    for (float i = 12; i > -1; i--) {
      px = (float) Math.cos(PApplet.radians(i * 30)) * w;
      py = (float) Math.sin(PApplet.radians(i * 30)) * w;
      vertex(pg, px, py, 0);
    }
    pg.endShape();

    pg.beginShape(PApplet.TRIANGLE_FAN);
    vertex(pg, 0, 0, h);
    for (float i = 0; i < 13; i++) {
      px = (float) Math.cos(PApplet.radians(i * 30)) * w;
      py = (float) Math.sin(PApplet.radians(i * 30)) * w;
      vertex(pg, px, py, h);
    }
    pg.endShape();
    pg.popStyle();
  }

  @Override
  public void drawHollowCylinder(int detail, float w, float h, Vec m, Vec n) {
    if (is2D()) {
      AbstractScene.showDepthWarning("drawHollowCylinder");
      return;
    }
    drawHollowCylinder(pg(), detail, w, h, m, n);
  }

  /**
   * {@link #drawHollowCylinder(int, float, float, Vec, Vec)} on {@code pg}.
   */
  public static void drawHollowCylinder(PGraphics pg, int detail, float w, float h, Vec m, Vec n) {
    pg.pushStyle();
    // eqs taken from: http://en.wikipedia.org/wiki/Line-plane_intersection
    Vec pm0 = new Vec(0, 0, 0);
    Vec pn0 = new Vec(0, 0, h);
    Vec l0 = new Vec();
    Vec l = new Vec(0, 0, 1);
    Vec p = new Vec();
    float x, y, d;

    pg.noStroke();
    pg.beginShape(PApplet.QUAD_STRIP);

    for (float t = 0; t <= detail; t++) {
      x = w * PApplet.cos(t * PApplet.TWO_PI / detail);
      y = w * PApplet.sin(t * PApplet.TWO_PI / detail);
      l0.set(x, y, 0);

      d = (m.dot(Vec.subtract(pm0, l0))) / (l.dot(m));
      p = Vec.add(Vec.multiply(l, d), l0);
      vertex(pg, p.x(), p.y(), p.z());

      l0.setZ(h);
      d = (n.dot(Vec.subtract(pn0, l0))) / (l.dot(n));
      p = Vec.add(Vec.multiply(l, d), l0);
      vertex(pg, p.x(), p.y(), p.z());
    }
    pg.endShape();
    pg.popStyle();
  }

  @Override
  public void drawCone(int detail, float x, float y, float r, float h) {
    if (is2D()) {
      AbstractScene.showDepthWarning("drawCone");
      return;
    }
    drawCone(pg(), detail, x, y, r, h);
  }

  /**
   * Same as {@code cone(pg, det, 0, 0, r, h);}
   * 
   * @see #drawCone(PGraphics, int, float, float, float, float)
   */
  public static void drawCone(PGraphics pg, int det, float r, float h) {
    drawCone(pg, det, 0, 0, r, h);
  }

  /**
   * Same as {@code cone(pg, 12, 0, 0, r, h);}
   * 
   * @see #drawCone(PGraphics, int, float, float, float, float)
   */
  public static void drawCone(PGraphics pg, float r, float h) {
    drawCone(pg, 12, 0, 0, r, h);
  }

  /**
   * Same as {@code cone(pg, det, 0, 0, r1, r2, h);}
   * 
   * @see #drawCone(PGraphics, int, float, float, float, float, float)
   */
  public static void drawCone(PGraphics pg, int det, float r1, float r2, float h) {
    drawCone(pg, det, 0, 0, r1, r2, h);
  }

  /**
   * Same as {@code cone(pg, 18, 0, 0, r1, r2, h);}
   * 
   * @see #drawCone(PGraphics, int, float, float, float, float, float)
   */
  public static void drawCone(PGraphics pg, float r1, float r2, float h) {
    drawCone(pg, 18, 0, 0, r1, r2, h);
  }

  /**
   * {@link #drawCone(int, float, float, float, float)} on {@code pg}.
   */
  public static void drawCone(PGraphics pg, int detail, float x, float y, float r, float h) {
    pg.pushStyle();
    float unitConeX[] = new float[detail + 1];
    float unitConeY[] = new float[detail + 1];

    for (int i = 0; i <= detail; i++) {
      float a1 = PApplet.TWO_PI * i / detail;
      unitConeX[i] = r * (float) Math.cos(a1);
      unitConeY[i] = r * (float) Math.sin(a1);
    }

    pg.pushMatrix();
    pg.translate(x, y);
    pg.beginShape(PApplet.TRIANGLE_FAN);
    vertex(pg, 0, 0, h);
    for (int i = 0; i <= detail; i++) {
      vertex(pg, unitConeX[i], unitConeY[i], 0.0f);
    }
    pg.endShape();
    pg.popMatrix();
    pg.popStyle();
  }

  @Override
  public void drawCone(int detail, float x, float y, float r1, float r2, float h) {
    if (is2D()) {
      AbstractScene.showDepthWarning("drawCone");
      return;
    }
    drawCone(pg(), detail, x, y, r1, r2, h);
  }

  /**
   * {@link #drawCone(int, float, float, float, float, float)} on {@code pg}.
   */
  public static void drawCone(PGraphics pg, int detail, float x, float y, float r1, float r2, float h) {
    pg.pushStyle();
    float firstCircleX[] = new float[detail + 1];
    float firstCircleY[] = new float[detail + 1];
    float secondCircleX[] = new float[detail + 1];
    float secondCircleY[] = new float[detail + 1];

    for (int i = 0; i <= detail; i++) {
      float a1 = PApplet.TWO_PI * i / detail;
      firstCircleX[i] = r1 * (float) Math.cos(a1);
      firstCircleY[i] = r1 * (float) Math.sin(a1);
      secondCircleX[i] = r2 * (float) Math.cos(a1);
      secondCircleY[i] = r2 * (float) Math.sin(a1);
    }

    pg.pushMatrix();
    pg.translate(x, y);
    pg.beginShape(PApplet.QUAD_STRIP);
    for (int i = 0; i <= detail; i++) {
      vertex(pg, firstCircleX[i], firstCircleY[i], 0);
      vertex(pg, secondCircleX[i], secondCircleY[i], h);
    }
    pg.endShape();
    pg.popMatrix();
    pg.popStyle();
  }

  @Override
  public void drawAxes(float length) {
    pg().pushStyle();
    pg().colorMode(PApplet.RGB, 255);
    float charWidth = length / 40.0f;
    float charHeight = length / 30.0f;
    float charShift = 1.04f * length;

    pg().pushStyle();
    pg().beginShape(PApplet.LINES);
    pg().strokeWeight(2);
    if (is2D()) {
      // The X
      pg().stroke(200, 0, 0);
      vertex(charShift + charWidth, -charHeight);
      vertex(charShift - charWidth, charHeight);
      vertex(charShift - charWidth, -charHeight);
      vertex(charShift + charWidth, charHeight);

      // The Y
      charShift *= 1.02;
      pg().stroke(0, 200, 0);
      vertex(charWidth, charShift + (isRightHanded() ? charHeight : -charHeight));
      vertex(0.0f, charShift + 0.0f);
      vertex(-charWidth, charShift + (isRightHanded() ? charHeight : -charHeight));
      vertex(0.0f, charShift + 0.0f);
      vertex(0.0f, charShift + 0.0f);
      vertex(0.0f, charShift + -(isRightHanded() ? charHeight : -charHeight));
    } else {
      // The X
      pg().stroke(200, 0, 0);
      vertex(charShift, charWidth, -charHeight);
      vertex(charShift, -charWidth, charHeight);
      vertex(charShift, -charWidth, -charHeight);
      vertex(charShift, charWidth, charHeight);
      // The Y
      pg().stroke(0, 200, 0);
      vertex(charWidth, charShift, (isLeftHanded() ? charHeight : -charHeight));
      vertex(0.0f, charShift, 0.0f);
      vertex(-charWidth, charShift, (isLeftHanded() ? charHeight : -charHeight));
      vertex(0.0f, charShift, 0.0f);
      vertex(0.0f, charShift, 0.0f);
      vertex(0.0f, charShift, -(isLeftHanded() ? charHeight : -charHeight));
      // The Z
      pg().stroke(0, 100, 200);
      vertex(-charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
      vertex(charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
      vertex(charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
      vertex(-charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
      vertex(-charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
      vertex(charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
    }
    pg().endShape();
    pg().popStyle();

    // X Axis
    pg().stroke(200, 0, 0);
    line(0, 0, 0, length, 0, 0);
    // Y Axis
    pg().stroke(0, 200, 0);
    line(0, 0, 0, 0, length, 0);

    // Z Axis
    if (is3D()) {
      pg().stroke(0, 100, 200);
      line(0, 0, 0, 0, 0, length);
    }
    pg().popStyle();
  }

  @Override
  public void drawGrid(float size, int nbSubdivisions) {
    pg().pushStyle();
    pg().beginShape(LINES);
    for (int i = 0; i <= nbSubdivisions; ++i) {
      final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
      vertex(pos, -size);
      vertex(pos, +size);
      vertex(-size, pos);
      vertex(size, pos);
    }
    pg().endShape();
    pg().popStyle();
  }

  @Override
  public void drawDottedGrid(float size, int nbSubdivisions) {
    pg().pushStyle();
    float posi, posj;
    pg().beginShape(POINTS);
    for (int i = 0; i <= nbSubdivisions; ++i) {
      posi = size * (2.0f * i / nbSubdivisions - 1.0f);
      for (int j = 0; j <= nbSubdivisions; ++j) {
        posj = size * (2.0f * j / nbSubdivisions - 1.0f);
        vertex(posi, posj);
      }
    }
    pg().endShape();
    int internalSub = 5;
    int subSubdivisions = nbSubdivisions * internalSub;
    float currentWeight = pg().strokeWeight;
    pg().colorMode(HSB, 255);
    float hue = pg().hue(pg().strokeColor);
    float saturation = pg().saturation(pg().strokeColor);
    float brightness = pg().brightness(pg().strokeColor);
    pg().stroke(hue, saturation, brightness * 10f / 17f);
    pg().strokeWeight(currentWeight / 2);
    pg().beginShape(POINTS);
    for (int i = 0; i <= subSubdivisions; ++i) {
      posi = size * (2.0f * i / subSubdivisions - 1.0f);
      for (int j = 0; j <= subSubdivisions; ++j) {
        posj = size * (2.0f * j / subSubdivisions - 1.0f);
        if (((i % internalSub) != 0) || ((j % internalSub) != 0))
          vertex(posi, posj);
      }
    }
    pg().endShape();
    pg().popStyle();
  }

  @Override
  public void drawEye(Eye eye) {
    pg().pushMatrix();

    // applyMatrix(camera.frame().worldMatrix());
    // same as the previous line, but maybe more efficient

    // Frame tmpFrame = new Frame(is3D());
    // tmpFrame.fromMatrix(eye.frame().worldMatrix());
    // applyTransformation(tmpFrame);
    // same as above but easier
    // scene().applyTransformation(camera.frame());

    // fails due to scaling!

    // take into account the whole hierarchy:
    if (is2D()) {
      // applyWorldTransformation(eye.frame());
      pg().translate(eye.frame().position().vec[0], eye.frame().position().vec[1]);
      pg().rotate(eye.frame().orientation().angle());
    } else {
      pg().translate(eye.frame().position().vec[0], eye.frame().position().vec[1], eye.frame().position().vec[2]);
      pg().rotate(eye.frame().orientation().angle(), ((Quat) eye.frame().orientation()).axis().vec[0],
          ((Quat) eye.frame().orientation()).axis().vec[1], ((Quat) eye.frame().orientation()).axis().vec[2]);
    }
    drawEye(pg(), eye);
    pg().popMatrix();
  }

  /**
   * Implementation of {@link #drawEye(Eye)}.
   * <p>
   * Note that if {@code eye.scene()).pg() == pg} this method has not effect at all.
   */
  public void drawEye(PGraphics pg, Eye eye) {
    if (eye.scene() instanceof Scene)
      if (((Scene) eye.scene()).pg() == pg) {
        System.out.println("Warning: No drawEye done, eye.scene()).pg() and pg are the same!");
        return;
      }
    pg.pushStyle();
    // boolean drawFarPlane = true;
    // int farIndex = drawFarPlane ? 1 : 0;
    int farIndex = is3D() ? 1 : 0;
    boolean ortho = false;
    if (is3D())
      if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC)
        ortho = true;

    // 0 is the upper left coordinates of the near corner, 1 for the far one
    Vec[] points = new Vec[2];
    points[0] = new Vec();
    points[1] = new Vec();

    if (is2D() || ortho) {
      float[] wh = eye.getBoundaryWidthHeight();
      points[0].setX(wh[0]);
      points[1].setX(wh[0]);
      points[0].setY(wh[1]);
      points[1].setY(wh[1]);
    }

    if (is3D()) {
      points[0].setZ(((Camera) eye).zNear());
      points[1].setZ(((Camera) eye).zFar());

      if (((Camera) eye).type() == Camera.Type.PERSPECTIVE) {
        points[0].setY(points[0].z() * PApplet.tan(((Camera) eye).fieldOfView() / 2.0f));
        points[0].setX(points[0].y() * ((Camera) eye).aspectRatio());
        float ratio = points[1].z() / points[0].z();
        points[1].setY(ratio * points[0].y());
        points[1].setX(ratio * points[0].x());
      }

      // Frustum lines
      switch (((Camera) eye).type()) {
      case PERSPECTIVE: {
        pg.beginShape(PApplet.LINES);
        Scene.vertex(pg, 0.0f, 0.0f, 0.0f);
        Scene.vertex(pg, points[farIndex].x(), points[farIndex].y(), -points[farIndex].z());
        Scene.vertex(pg, 0.0f, 0.0f, 0.0f);
        Scene.vertex(pg, -points[farIndex].x(), points[farIndex].y(), -points[farIndex].z());
        Scene.vertex(pg, 0.0f, 0.0f, 0.0f);
        Scene.vertex(pg, -points[farIndex].x(), -points[farIndex].y(), -points[farIndex].z());
        Scene.vertex(pg, 0.0f, 0.0f, 0.0f);
        Scene.vertex(pg, points[farIndex].x(), -points[farIndex].y(), -points[farIndex].z());
        pg.endShape();
        break;
      }
      case ORTHOGRAPHIC: {
        // if (drawFarPlane) {
        pg.beginShape(PApplet.LINES);
        Scene.vertex(pg, points[0].x(), points[0].y(), -points[0].z());
        Scene.vertex(pg, points[1].x(), points[1].y(), -points[1].z());
        Scene.vertex(pg, -points[0].x(), points[0].y(), -points[0].z());
        Scene.vertex(pg, -points[1].x(), points[1].y(), -points[1].z());
        Scene.vertex(pg, -points[0].x(), -points[0].y(), -points[0].z());
        Scene.vertex(pg, -points[1].x(), -points[1].y(), -points[1].z());
        Scene.vertex(pg, points[0].x(), -points[0].y(), -points[0].z());
        Scene.vertex(pg, points[1].x(), -points[1].y(), -points[1].z());
        pg.endShape();
        // }
        break;
      }
      }
    }

    // Near and (optionally) far plane(s)
    pg.noStroke();
    pg.beginShape(PApplet.QUADS);
    for (int i = farIndex; i >= 0; --i) {
      pg.normal(0.0f, 0.0f, (i == 0) ? 1.0f : -1.0f);
      Scene.vertex(pg, points[i].x(), points[i].y(), -points[i].z());
      Scene.vertex(pg, -points[i].x(), points[i].y(), -points[i].z());
      Scene.vertex(pg, -points[i].x(), -points[i].y(), -points[i].z());
      Scene.vertex(pg, points[i].x(), -points[i].y(), -points[i].z());
    }
    pg.endShape();

    // Up arrow
    float arrowHeight = 1.5f * points[0].y();
    float baseHeight = 1.2f * points[0].y();
    float arrowHalfWidth = 0.5f * points[0].x();
    float baseHalfWidth = 0.3f * points[0].x();

    // pg3d().noStroke();
    // Arrow base
    pg.beginShape(PApplet.QUADS);
    if (isLeftHanded()) {
      Scene.vertex(pg, -baseHalfWidth, -points[0].y(), -points[0].z());
      Scene.vertex(pg, baseHalfWidth, -points[0].y(), -points[0].z());
      Scene.vertex(pg, baseHalfWidth, -baseHeight, -points[0].z());
      Scene.vertex(pg, -baseHalfWidth, -baseHeight, -points[0].z());
    } else {
      Scene.vertex(pg, -baseHalfWidth, points[0].y(), -points[0].z());
      Scene.vertex(pg, baseHalfWidth, points[0].y(), -points[0].z());
      Scene.vertex(pg, baseHalfWidth, baseHeight, -points[0].z());
      Scene.vertex(pg, -baseHalfWidth, baseHeight, -points[0].z());
    }
    pg.endShape();

    // Arrow
    pg.beginShape(PApplet.TRIANGLES);
    if (isLeftHanded()) {
      Scene.vertex(pg, 0.0f, -arrowHeight, -points[0].z());
      Scene.vertex(pg, -arrowHalfWidth, -baseHeight, -points[0].z());
      Scene.vertex(pg, arrowHalfWidth, -baseHeight, -points[0].z());
    } else {
      Scene.vertex(pg, 0.0f, arrowHeight, -points[0].z());
      Scene.vertex(pg, -arrowHalfWidth, baseHeight, -points[0].z());
      Scene.vertex(pg, arrowHalfWidth, baseHeight, -points[0].z());
    }
    pg.endShape();
    // pg.popMatrix();
    pg.popStyle();
  }

  @Override
  public void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames, float scale) {
    pg().pushStyle();
    if (mask != 0) {
      int nbSteps = 30;
      pg().strokeWeight(2 * pg().strokeWeight);
      pg().noFill();

      List<Frame> path = kfi.path();
      if (((mask & 1) != 0) && path.size() > 1) {
        pg().beginShape();
        for (Frame myFr : path)
          vertex(myFr.position().x(), myFr.position().y(), myFr.position().z());
        pg().endShape();
      }
      if ((mask & 6) != 0) {
        int count = 0;
        if (nbFrames > nbSteps)
          nbFrames = nbSteps;
        float goal = 0.0f;

        for (Frame myFr : path)
          if ((count++) >= goal) {
            goal += nbSteps / (float) nbFrames;
            pushModelView();

            applyTransformation(myFr);

            if ((mask & 2) != 0)
              drawKFIEye(scale);
            if ((mask & 4) != 0)
              drawAxes(scale / 10.0f);

            popModelView();
          }
      }
      pg().strokeWeight(pg().strokeWeight / 2f);
    }
    pg().popStyle();
  }

  @Override
  protected void drawKFIEye(float scale) {
    pg().pushStyle();
    float halfHeight = scale * (is2D() ? 1.2f : 0.07f);
    float halfWidth = halfHeight * 1.3f;
    float dist = halfHeight / (float) Math.tan(PApplet.PI / 8.0f);

    float arrowHeight = 1.5f * halfHeight;
    float baseHeight = 1.2f * halfHeight;
    float arrowHalfWidth = 0.5f * halfWidth;
    float baseHalfWidth = 0.3f * halfWidth;

    // Frustum outline
    pg().noFill();
    pg().beginShape();
    vertex(-halfWidth, halfHeight, -dist);
    vertex(-halfWidth, -halfHeight, -dist);
    vertex(0.0f, 0.0f, 0.0f);
    vertex(halfWidth, -halfHeight, -dist);
    vertex(-halfWidth, -halfHeight, -dist);
    pg().endShape();
    pg().noFill();
    pg().beginShape();
    vertex(halfWidth, -halfHeight, -dist);
    vertex(halfWidth, halfHeight, -dist);
    vertex(0.0f, 0.0f, 0.0f);
    vertex(-halfWidth, halfHeight, -dist);
    vertex(halfWidth, halfHeight, -dist);
    pg().endShape();

    // Up arrow
    pg().noStroke();
    pg().fill(pg().strokeColor);
    // Base
    pg().beginShape(PApplet.QUADS);

    if (isLeftHanded()) {
      vertex(baseHalfWidth, -halfHeight, -dist);
      vertex(-baseHalfWidth, -halfHeight, -dist);
      vertex(-baseHalfWidth, -baseHeight, -dist);
      vertex(baseHalfWidth, -baseHeight, -dist);
    } else {
      vertex(-baseHalfWidth, halfHeight, -dist);
      vertex(baseHalfWidth, halfHeight, -dist);
      vertex(baseHalfWidth, baseHeight, -dist);
      vertex(-baseHalfWidth, baseHeight, -dist);
    }

    pg().endShape();
    // Arrow
    pg().beginShape(PApplet.TRIANGLES);

    if (isLeftHanded()) {
      vertex(0.0f, -arrowHeight, -dist);
      vertex(arrowHalfWidth, -baseHeight, -dist);
      vertex(-arrowHalfWidth, -baseHeight, -dist);
    } else {
      vertex(0.0f, arrowHeight, -dist);
      vertex(-arrowHalfWidth, baseHeight, -dist);
      vertex(arrowHalfWidth, baseHeight, -dist);
    }
    pg().endShape();
    pg().popStyle();
  }

  @Override
  public void drawCross(float px, float py, float size) {
    float half_size = size / 2f;
    pg().pushStyle();
    beginScreenDrawing();
    pg().noFill();
    pg().beginShape(LINES);
    vertex(px - half_size, py);
    vertex(px + half_size, py);
    vertex(px, py - half_size);
    vertex(px, py + half_size);
    pg().endShape();
    endScreenDrawing();
    pg().popStyle();
  }

  @Override
  public void drawFilledCircle(int subdivisions, Vec center, float radius) {
    pg().pushStyle();
    float precision = PApplet.TWO_PI / subdivisions;
    float x = center.x();
    float y = center.y();
    float angle, x2, y2;
    beginScreenDrawing();
    pg().noStroke();
    pg().beginShape(TRIANGLE_FAN);
    vertex(x, y);
    for (angle = 0.0f; angle <= PApplet.TWO_PI + 1.1 * precision; angle += precision) {
      x2 = x + PApplet.sin(angle) * radius;
      y2 = y + PApplet.cos(angle) * radius;
      vertex(x2, y2);
    }
    pg().endShape();
    endScreenDrawing();
    pg().popStyle();
  }

  @Override
  public void drawFilledSquare(Vec center, float edge) {
    float half_edge = edge / 2f;
    pg().pushStyle();
    float x = center.x();
    float y = center.y();
    beginScreenDrawing();
    pg().noStroke();
    pg().beginShape(QUADS);
    vertex(x - half_edge, y + half_edge);
    vertex(x + half_edge, y + half_edge);
    vertex(x + half_edge, y - half_edge);
    vertex(x - half_edge, y - half_edge);
    pg().endShape();
    endScreenDrawing();
    pg().popStyle();
  }

  @Override
  public void drawShooterTarget(Vec center, float length) {
    float half_length = length / 2f;
    pg().pushStyle();
    float x = center.x();
    float y = center.y();
    beginScreenDrawing();
    pg().noFill();

    pg().beginShape();
    vertex((x - half_length), (y - half_length) + (0.6f * half_length));
    vertex((x - half_length), (y - half_length));
    vertex((x - half_length) + (0.6f * half_length), (y - half_length));
    pg().endShape();

    pg().beginShape();
    vertex((x + half_length) - (0.6f * half_length), (y - half_length));
    vertex((x + half_length), (y - half_length));
    vertex((x + half_length), ((y - half_length) + (0.6f * half_length)));
    pg().endShape();

    pg().beginShape();
    vertex((x + half_length), ((y + half_length) - (0.6f * half_length)));
    vertex((x + half_length), (y + half_length));
    vertex(((x + half_length) - (0.6f * half_length)), (y + half_length));
    pg().endShape();

    pg().beginShape();
    vertex((x - half_length) + (0.6f * half_length), (y + half_length));
    vertex((x - half_length), (y + half_length));
    vertex((x - half_length), ((y + half_length) - (0.6f * half_length)));
    pg().endShape();
    endScreenDrawing();
    drawCross(center.x(), center.y(), 0.6f * length);
    pg().popStyle();
  }

  @Override
  public void drawPickingTarget(GenericFrame iFrame) {
    if (iFrame.isEyeFrame()) {
      System.err.println("eye frames don't have a picking target");
      return;
    }
    if (!motionAgent().hasGrabber(iFrame)) {
      System.err.println("add iFrame to motionAgent before drawing picking target");
      return;
    }
    Vec center = projectedCoordinatesOf(iFrame.position());
    if (motionAgent().isInputGrabber(iFrame)) {
      pg().pushStyle();
      pg().strokeWeight(2 * pg().strokeWeight);
      pg().colorMode(HSB, 255);
      float hue = pg().hue(pg().strokeColor);
      float saturation = pg().saturation(pg().strokeColor);
      float brightness = pg().brightness(pg().strokeColor);
      pg().stroke(hue, saturation * 1.4f, brightness * 1.4f);
      drawShooterTarget(center, (iFrame.grabsInputThreshold() + 1));
      pg().popStyle();
    } else {
      pg().pushStyle();
      pg().colorMode(HSB, 255);
      float hue = pg().hue(pg().strokeColor);
      float saturation = pg().saturation(pg().strokeColor);
      float brightness = pg().brightness(pg().strokeColor);
      pg().stroke(hue, saturation * 1.4f, brightness);
      drawShooterTarget(center, iFrame.grabsInputThreshold());
      pg().popStyle();
    }
  }

  /**
   * Code contributed by Jacques Maire (http://www.alcys.com/) See also:
   * http://www.mathcurve.com/courbes3d/solenoidtoric/solenoidtoric.shtml
   * http://crazybiocomputing.blogspot.fr/2011/12/3d-curves-toric-solenoids.html
   */
  @Override
  public void drawTorusSolenoid(int faces, int detail, float insideRadius, float outsideRadius) {
    drawTorusSolenoid(pg(), faces, detail, insideRadius, outsideRadius);
  }

  /**
   * Convenience function that simply calls {@code drawTorusSolenoid(pg, 6)}.
   * 
   * @see #drawTorusSolenoid(PGraphics, int, int, float, float)
   */
  public static void drawTorusSolenoid(PGraphics pg) {
    drawTorusSolenoid(pg, 6);
  }

  /**
   * Convenience function that simply calls {@code drawTorusSolenoid(pg, 6, insideRadius)}
   * .
   * 
   * @see #drawTorusSolenoid(PGraphics, int, int, float, float)
   */
  public static void drawTorusSolenoid(PGraphics pg, float insideRadius) {
    drawTorusSolenoid(pg, 6, insideRadius);
  }

  /**
   * Convenience function that simply calls
   * {@code drawTorusSolenoid(pg, faces, 100, insideRadius, insideRadius * 1.3f)} .
   * 
   * @see #drawTorusSolenoid(int, int, float, float)
   */
  public static void drawTorusSolenoid(PGraphics pg, int faces, float insideRadius) {
    drawTorusSolenoid(pg, faces, 100, insideRadius, insideRadius * 1.3f);
  }

  /**
   * {@link #drawTorusSolenoid(PGraphics, int, int, float, float)} pn {@code pg} .
   */
  public static void drawTorusSolenoid(PGraphics pg, int faces, int detail, float insideRadius, float outsideRadius) {
    pg.pushStyle();
    pg.noStroke();
    Vec v1, v2;
    int b, ii, jj, a;
    float eps = PApplet.TWO_PI / detail;
    for (a = 0; a < faces; a += 2) {
      pg.beginShape(PApplet.TRIANGLE_STRIP);
      b = (a <= (faces - 1)) ? a + 1 : 0;
      for (int i = 0; i < (detail + 1); i++) {
        ii = (i < detail) ? i : 0;
        jj = ii + 1;
        float ai = eps * jj;
        float alpha = a * PApplet.TWO_PI / faces + ai;
        v1 = new Vec((outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.cos(ai),
            (outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.sin(ai), insideRadius * PApplet.sin(alpha));
        alpha = b * PApplet.TWO_PI / faces + ai;
        v2 = new Vec((outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.cos(ai),
            (outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.sin(ai), insideRadius * PApplet.sin(alpha));
        vertex(pg, v1.x(), v1.y(), v1.z());
        vertex(pg, v2.x(), v2.y(), v2.z());
      }
      pg.endShape();
    }
    pg.popStyle();
  }

  /*
   * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
   */
  @Override
  protected void drawAxesHint() {
    pg().pushStyle();
    pg().strokeWeight(2);
    drawAxes(eye().sceneRadius());
    pg().popStyle();
  }

  /*
   * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
   */
  @Override
  protected void drawGridHint() {
    pg().pushStyle();
    pg().stroke(170);
    if (gridIsDotted()) {
      pg().strokeWeight(2);
      drawDottedGrid(eye().sceneRadius());
    } else {
      pg().strokeWeight(1);
      drawGrid(eye().sceneRadius());
    }
    pg().popStyle();
  }

  /*
   * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
   */
  @Override
  protected void drawPathsHint() {
    pg().pushStyle();
    pg().colorMode(PApplet.RGB, 255);
    pg().strokeWeight(1);
    pg().stroke(0, 220, 220);
    drawPaths();
    pg().popStyle();
  }

  /*
   * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
   */
  @Override
  protected void drawPickingHint() {
    pg().pushStyle();
    pg().colorMode(PApplet.RGB, 255);
    pg().strokeWeight(1);
    pg().stroke(220, 220, 220);
    drawPickingTargets();
    pg().popStyle();
  }

  @Override
  protected void drawAnchorHint() {
    pg().pushStyle();
    Vec p = eye().projectedCoordinatesOf(anchor());
    pg().stroke(255);
    pg().strokeWeight(3);
    drawCross(p.vec[0], p.vec[1]);
    pg().popStyle();
  }

  @Override
  protected void drawPointUnderPixelHint() {
    pg().pushStyle();
    Vec v = eye().projectedCoordinatesOf(eye().pupVec);
    pg().stroke(255);
    pg().strokeWeight(3);
    drawCross(v.vec[0], v.vec[1], 30);
    pg().popStyle();
  }

  @Override
  protected void drawScreenRotateHint() {
    if (!(motionAgent() instanceof MouseAgent))
      return;
    if (!(motionAgent().inputGrabber() instanceof InteractiveFrame))
      return;

    pg().pushStyle();
    float p1x = mouseAgent().currentEvent.x() /*- originCorner().x()*/;
    float p1y = mouseAgent().currentEvent.y() /*- originCorner().y()*/;

    Vec p2 = new Vec();
    if (motionAgent().inputGrabber() instanceof GenericFrame) {
      if (((GenericFrame) motionAgent().inputGrabber()).isEyeFrame())
        p2 = eye().projectedCoordinatesOf(anchor());
      else
        p2 = eye().projectedCoordinatesOf(((GenericFrame) mouseAgent().inputGrabber()).position());
    }
    beginScreenDrawing();
    pg().stroke(255, 255, 255);
    pg().strokeWeight(2);
    pg().noFill();
    line(p2.x(), p2.y(), p1x, p1y);
    endScreenDrawing();
    pg().popStyle();
  }

  @Override
  protected void drawZoomWindowHint() {
    if (!(motionAgent() instanceof MouseAgent))
      return;
    if (!(motionAgent().inputGrabber() instanceof InteractiveFrame))
      return;
    InteractiveFrame iFrame = (InteractiveFrame) motionAgent().inputGrabber();
    pg().pushStyle();
    float p1x = iFrame.initEvent.x() /*- originCorner().x()*/;
    float p1y = iFrame.initEvent.y() /*- originCorner().y()*/;
    float p2x = mouseAgent().currentEvent.x() /*- originCorner().x()*/;
    float p2y = mouseAgent().currentEvent.y() /*- originCorner().y()*/;
    beginScreenDrawing();
    pg().stroke(255, 255, 255);
    pg().strokeWeight(2);
    pg().noFill();
    pg().beginShape();
    vertex(p1x, p1y);
    vertex(p2x, p1y);
    vertex(p2x, p2y);
    vertex(p1x, p2y);
    pg().endShape(CLOSE);
    endScreenDrawing();
    pg().popStyle();
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
   * Same as {@code return profile.action(key)}.
   * 
   * @see remixlab.bias.ext.Profile#action(Shortcut)
   */
  public String action(Shortcut key) {
    return profile.action(key);
  }

  /**
   * Same as {@code return profile.isActionBound(action)}.
   * 
   * @see remixlab.bias.ext.Profile#isActionBound(String)
   */
  public boolean isActionBound(String action) {
    return profile.isActionBound(action);
  }

  // Key

  /**
   * Same as {@code profile.setBinding(new KeyboardShortcut(vkey), methodName)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setKeyBinding(int vkey, String methodName) {
    profile.setBinding(new KeyboardShortcut(vkey), methodName);
  }

  /**
   * Same as {@code profile.setBinding(new KeyboardShortcut(key), methodName)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setKeyBinding(char key, String methodName) {
    profile.setBinding(new KeyboardShortcut(key), methodName);
  }

  /**
   * Same as {@code profile.setBinding(object, new KeyboardShortcut(vkey), methodName)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, int vkey, String methodName) {
    profile.setBinding(object, new KeyboardShortcut(vkey), methodName);
  }

  /**
   * Same as {@code profile.setBinding(object, new KeyboardShortcut(key), methodName)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, char key, String methodName) {
    profile.setBinding(object, new KeyboardShortcut(key), methodName);
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
   * Same as {@code }.
   * 
   * @see remixlab.bias.ext.Profile
   */
  public void removeKeyBinding(char key) {
    profile.removeBinding(new KeyboardShortcut(key));
  }

  //

  /**
   * Same as {@code profile.setBinding(new KeyboardShortcut(mask, vkey), methodName)}.
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Shortcut, String)
   */
  public void setKeyBinding(int mask, int vkey, String methodName) {
    profile.setBinding(new KeyboardShortcut(mask, vkey), methodName);
  }

  /**
   * Same as
   * {@code profile.setBinding(object, new KeyboardShortcut(mask, vkey), methodName)} .
   * 
   * @see remixlab.bias.ext.Profile#setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, int mask, int vkey, String methodName) {
    profile.setBinding(object, new KeyboardShortcut(mask, vkey), methodName);
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
   * Same as {@code setKeyBinding(mask, KeyAgent.keyCode(key), methodName)}.
   * 
   * @see #setKeyBinding(int, int, String)
   */
  public void setKeyBinding(int mask, char key, String methodName) {
    setKeyBinding(mask, KeyAgent.keyCode(key), methodName);
  }

  /**
   * Same as {@code setKeyBinding(object, mask, KeyAgent.keyCode(key), methodName)}.
   * 
   * @see #setKeyBinding(Object, int, int, String)
   */
  public void setKeyBinding(Object object, int mask, char key, String methodName) {
    setKeyBinding(object, mask, KeyAgent.keyCode(key), methodName);
  }

  /**
   * Same as {@code return hasKeyBinding(mask, KeyAgent.keyCode(key))}.
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

  /**
   * Same as {@code profile.from(otherScene.profile())}.
   * 
   * @see remixlab.bias.ext.Profile#from(Profile)
   * @see #setProfile(Profile)
   */
  public void setBindings(Scene otherScene) {
    profile.from(otherScene.profile());
  }

  /**
   * Restores the default keyboard shortcuts:
   * <p>
   * {@code 'a' -> KeyboardAction.TOGGLE_AXES_VISUAL_HINT}<br>
   * {@code 'f' -> KeyboardAction.TOGGLE_FRAME_VISUAL_HINT}<br>
   * {@code 'g' -> KeyboardAction.TOGGLE_GRID_VISUAL_HINT}<br>
   * {@code 'm' -> KeyboardAction.TOGGLE_ANIMATION}<br>
   * {@code 'e' -> KeyboardAction.TOGGLE_CAMERA_TYPE}<br>
   * {@code 'h' -> KeyboardAction.DISPLAY_INFO}<br>
   * {@code 'r' -> KeyboardAction.TOGGLE_PATHS_VISUAL_HINT}<br>
   * {@code 's' -> KeyboardAction.INTERPOLATE_TO_FIT}<br>
   * {@code 'S' -> KeyboardAction.SHOW_ALL}<br>
   * {@code left_arrow -> KeyboardAction.MOVE_LEFT}<br>
   * {@code right_arrow -> KeyboardAction.MOVE_RIGHT}<br>
   * {@code up_arrow -> KeyboardAction.MOVE_UP}<br>
   * {@code down_arrow -> KeyboardAction.MOVE_DOWN }<br>
   * {@code 'CTRL' + '1' -> KeyboardAction.ADD_KEYFRAME_TO_PATH_1}<br>
   * {@code 'ALT' + '1' -> KeyboardAction.DELETE_PATH_1}<br>
   * {@code '1' -> KeyboardAction.PLAY_PATH_1}<br>
   * {@code 'CTRL' + '2' -> KeyboardAction.ADD_KEYFRAME_TO_PATH_2}<br>
   * {@code 'ALT' + '2' -> KeyboardAction.DELETE_PATH_2}<br>
   * {@code '2' -> KeyboardAction.PLAY_PATH_2}<br>
   * {@code 'CTRL' + '3' -> KeyboardAction.ADD_KEYFRAME_TO_PATH_3}<br>
   * {@code 'ALT' + '3' -> KeyboardAction.DELETE_PATH_3}<br>
   * {@code '3' -> KeyboardAction.PLAY_PATH_3}<br>
   */

  /**
   * Calls {@link #removeKeyBindings()} and sets the default frame key bindings which may
   * be queried with {@link #info()}.
   */
  public void setDefaultKeyBindings() {
    removeKeyBindings();
    setKeyBinding('a', "toggleAxesVisualHint");
    setKeyBinding('e', "toggleCameraType");
    setKeyBinding('f', "togglePickingVisualhint");
    setKeyBinding('g', "toggleGridVisualHint");
    setKeyBinding('h', "displayInfo");
    setKeyBinding('m', "toggleAnimation");
    setKeyBinding('r', "togglePathsVisualHint");
    setKeyBinding('s', "interpolateToFitScene");
    setKeyBinding('S', "showAll");
    setKeyBinding(BogusEvent.CTRL, '1', "addKeyFrameToPath1");
    setKeyBinding(BogusEvent.ALT, '1', "deletePath1");
    setKeyBinding('1', "playPath1");
    setKeyBinding(BogusEvent.CTRL, '2', "addKeyFrameToPath2");
    setKeyBinding(BogusEvent.ALT, '2', "deletePath2");
    setKeyBinding('2', "playPath2");
    setKeyBinding(BogusEvent.CTRL, '3', "addKeyFrameToPath3");
    setKeyBinding(BogusEvent.ALT, '3', "deletePath3");
    setKeyBinding('3', "playPath3");
  }

  /**
   * Returns the frame {@link remixlab.bias.ext.Profile} instance.
   */
  public Profile profile() {
    return profile;
  }

  /**
   * Sets the scene {@link remixlab.bias.ext.Profile} instance. Note that the
   * {@link remixlab.bias.ext.Profile#grabber()} object should equals this scene.
   * 
   * @see #setBindings(Scene)
   */
  public void setProfile(Profile p) {
    if (p.grabber() == this)
      profile = p;
    else
      System.out.println("Nothing done, profile grabber is different than this scene");
  }

  // PVector <-> toVec

  // /**
  // * Same as {@code drawHollowCylinder(pg(), detail, w, h, m, n)}.
  // *
  // * @see #drawHollowCylinder(PGraphics, int, float, float, PVector, PVector)
  // */
  // public void drawHollowCylinder(int detail, float w, float h, PVector m,
  // PVector n) {
  // drawHollowCylinder(pg(), detail, w, h, m, n);
  // }
  //
  // /**
  // * Same as {@code drawHollowCylinder(pg, detail, w, h, Scene.toVec(m),
  // Scene.toVec(n))}.
  // *
  // * @see #drawHollowCylinder(PGraphics, int, float, float, Vec, Vec)
  // */
  // public static void drawHollowCylinder(PGraphics pg, int detail, float w,
  // float h, PVector m, PVector n) {
  // drawHollowCylinder(pg, detail, w, h, Scene.toVec(m), Scene.toVec(n));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #drawArrow(Vec, Vec, float)
  // */
  // public void drawArrow(PVector from, PVector to, float radius) {
  // drawArrow(toVec(from), toVec(to), radius);
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #drawFilledCircle(Vec, float)
  // */
  // public void drawFilledCircle(PVector center, float radius) {
  // drawFilledCircle(toVec(center), radius);
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #drawFilledSquare(Vec, float)
  // */
  // public void drawFilledSquare(PVector center, float edge) {
  // drawFilledSquare(toVec(center), edge);
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #drawShooterTarget(Vec, float)
  // */
  // public void drawShooterTarget(PVector center, float length) {
  // drawShooterTarget(toVec(center), length);
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #isPointVisible(Vec)
  // */
  // public boolean isPointVisible(PVector point) {
  // return isPointVisible(toVec(point));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #ballVisibility(Vec, float)
  // */
  // public Eye.Visibility ballVisibility(PVector center, float radius) {
  // return ballVisibility(toVec(center), radius);
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #boxVisibility(Vec, Vec)
  // */
  // public Eye.Visibility boxVisibility(PVector p1, PVector p2) {
  // return boxVisibility(toVec(p1), toVec(p2));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #isFaceBackFacing(Vec, Vec, Vec)
  // */
  // public boolean isFaceBackFacing(PVector a, PVector b, PVector c) {
  // return isFaceBackFacing(toVec(a), toVec(b), toVec(c));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #pointUnderPixel(Point)
  // */
  // public PVector pointUnderPixel(float x, float y) {
  // return toPVector(pointUnderPixel(new Point(x, y)));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #projectedCoordinatesOf(Vec)
  // */
  // public PVector projectedCoordinatesOf(PVector src) {
  // return toPVector(projectedCoordinatesOf(toVec(src)));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #unprojectedCoordinatesOf(Vec)
  // */
  // public PVector unprojectedCoordinatesOf(PVector src) {
  // return toPVector(unprojectedCoordinatesOf(toVec(src)));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setCenter(Vec)
  // */
  // public void setCenter(PVector center) {
  // setCenter(toVec(center));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setAnchor(Vec)
  // */
  // public void setAnchor(PVector anchor) {
  // setAnchor(toVec(anchor));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setBoundingBox(Vec, Vec)
  // */
  // public void setBoundingBox(PVector min, PVector max) {
  // setBoundingBox(toVec(min), toVec(max));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setBoundingRect(Vec, Vec)
  // */
  // public void setBoundingRect(PVector min, PVector max) {
  // setBoundingRect(toVec(min), toVec(max));
  // }
  //
  // // PMatrix <-> toMat
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #applyModelView(PMatrix2D)
  // */
  // public void applyModelView(PMatrix2D source) {
  // applyModelView(toMat(source));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #applyModelView(PMatrix3D)
  // */
  // public void applyModelView(PMatrix3D source) {
  // applyModelView(toMat(source));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #applyProjection(PMatrix3D)
  // */
  // public void applyProjection(PMatrix3D source) {
  // applyProjection(toMat(source));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setModelView(PMatrix2D)
  // */
  // public void setModelView(PMatrix2D source) {
  // setModelView(toMat(source));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setModelView(PMatrix3D)
  // */
  // public void setModelView(PMatrix3D source) {
  // setModelView(toMat(source));
  // }
  //
  // /**
  // * Processing version of the abstract-scene method with the same name.
  // *
  // * @see #setProjection(PMatrix3D)
  // */
  // public void setProjection(PMatrix3D source) {
  // setProjection(toMat(source));
  // }
  //
  // // trickier:
  //
  // /**
  // * Same as {@link #modelView()} but returning a PMatrix.
  // */
  // public PMatrix getModelView() {
  // return is2D() ? toPMatrix2D(modelView()) : toPMatrix(modelView());
  // }
  //
  // /**
  // * Same as {@link #projection()} but returning a PMatrix.
  // */
  // public PMatrix3D getProjection() {
  // return toPMatrix(projection());
  // }
  //
  // // Quat stuff, even trickier:
  //
  // public static final PVector multiply(Quat q1, PVector v) {
  // return toPVector(Quat.multiply(q1, toVec(v)));
  // }
  //
  // public static Quat quat(PVector axis, float angle) {
  // return new Quat(toVec(axis), angle);
  // }
  //
  // public static Quat quat(PVector from, PVector to) {
  // return new Quat(toVec(from), toVec(to));
  // }
  //
  // public static Quat quat(PVector X, PVector Y, PVector Z) {
  // return new Quat(toVec(X), toVec(Y), toVec(Z));
  // }
  //
  // public static Quat quat(PMatrix3D matrix) {
  // return new Quat(toMat(matrix));
  // }
}