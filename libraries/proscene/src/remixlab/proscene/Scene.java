/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2014-2017 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import processing.awt.PGraphicsJava2D;
import processing.core.*;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.opengl.PGL;
import processing.opengl.PGraphics3D;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;
import remixlab.bias.*;
import remixlab.bias.event.ClickShortcut;
import remixlab.bias.event.KeyboardEvent;
import remixlab.bias.event.KeyboardShortcut;
import remixlab.bias.event.MotionShortcut;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// begin: GWT-incompatible
///*
// end: GWT-incompatible
//*/

/**
 * A 2D or 3D interactive, on-screen or off-screen, Processing Scene with a
 * {@link Profile} instance which allows {@link Shortcut} to
 * {@link java.lang.reflect.Method} bindings high-level customization (see all the
 * <b>*Binding*()</b> methods). The Scene is a specialization of the
 * {@link remixlab.dandelion.core.AbstractScene}, providing an interface between Dandelion
 * and Processing.
 * <p>
 * <h3>Usage</h3> To use a Scene you have two main choices:
 * <ol>
 * <li><b>Direct instantiation</b>. In this case you should instantiate your own Scene
 * object at the {@code PApplet.setup()} function. See the example <i>BasicUse</i>.
 * <li><b>Inheritance</b>. In this case, once you declare a Scene derived class, you
 * should implement {@link #proscenium()} which defines the objects in your scene. Just
 * make sure to define the {@code PApplet.draw()} method, even if it's empty. See the
 * example <i>AlternativeUse</i>.
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
 * {@link Agent}) and for which the scene implements a
 * <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a> with a color
 * buffer technique for easy and precise object selection (see {@link #pickingBuffer()}
 * and {@link #drawFrames(PGraphics)}).
 */
public class Scene extends AbstractScene implements PConstants {
  // begin: GWT-incompatible
  // /*
  // Animation
  // The object to handle the animation
  protected Object animateHandlerObject;
  // The method in animateHandlerObject to execute
  protected Method animateHandlerMethod;

  // Timing
  protected boolean javaTiming;
  // end: GWT-incompatible
  // */

  public static final String prettyVersion = "3.0.0";

  public static final String version = "32";

  // P R O C E S S I N G A P P L E T A N D O B J E C T S
  protected PApplet parent;
  protected PGraphics mainPGraphics;

  // iFrames
  protected int iFrameCount;
  // pb : picking buffer
  protected PGraphics pb;
  protected boolean pickingBufferEnabled;
  protected PShader pickingBufferShaderTriangle, pickingBufferShaderLine, pickingBufferShaderPoint;

  protected Profile profile;

  // E X C E P T I O N H A N D L I N G
  protected int beginOffScreenDrawingCalls;

  // off-screen scenes:
  protected static Scene lastScene;
  protected long lastDisplay;
  protected boolean autofocus;
  // just to make it compatible with previous versions of proscene
  protected static int offScreenScenes;

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
   * are meaningless (both are set to 0 to be taken as dummy values). Render into an
   * off-screen scene requires the drawing code to be enclose by {@link #beginDraw()} and
   * {@link #endDraw()}. To display an off-screen scene call {@link #display()}.
   *
   * @see remixlab.dandelion.core.AbstractScene#AbstractScene()
   * @see #Scene(PApplet)
   * @see #Scene(PApplet, PGraphics)
   */
  public Scene(PApplet p, PGraphics pg, int x, int y) {
    // 1. P5 objects
    parent = p;
    Profile.context = pApplet();
    mainPGraphics = pg;
    offscreen = pg != p.g;
    upperLeftCorner = offscreen ? new Point(x, y) : new Point(0, 0);

    // 2. Matrix helper
    setMatrixHelper(matrixHelper(pg));

    // 3. Frames & picking buffer
    pb = (pg() instanceof processing.opengl.PGraphicsOpenGL) ?
        pApplet().createGraphics(pg().width, pg().height, pg() instanceof PGraphics3D ? P3D : P2D) :
        null;
    if (pb != null) {
      enablePickingBuffer();
      pickingBufferShaderTriangle = pApplet().loadShader("PickingBuffer.frag");
      pickingBufferShaderLine = pApplet().loadShader("PickingBuffer.frag");
      pickingBufferShaderPoint = pApplet().loadShader("PickingBuffer.frag");
    }

    // 4. Create agents and register P5 methods
    profile = new Profile(this);
    if (platform() == Platform.PROCESSING_ANDROID) {
      defMotionAgent = new DroidTouchAgent(this);
      defKeyboardAgent = new DroidKeyAgent(this);
    } else {
      defMotionAgent = new MouseAgent(this);
      defKeyboardAgent = new KeyAgent(this);
      parent.registerMethod("mouseEvent", motionAgent());
      // TODO DROID broke in Android so moved here
      parent.registerMethod("keyEvent", keyboardAgent());
      this.setDefaultKeyBindings();
    }
    // TODO DROID broke in Android
    // parent.registerMethod("keyEvent", keyboardAgent());
    // this.setDefaultKeyBindings();
    if (!isOffscreen()) {
      pApplet().registerMethod("pre", this);
      pApplet().registerMethod("draw", this);
    } else {
      offScreenScenes++;
      enableAutoFocus();
    }
    // TODO DROID buggy in desktop, should be tested in Android
    if (platform() != Platform.PROCESSING_ANDROID)
      pApplet().registerMethod("dispose", this);

    // 5. Eye
    setLeftHanded();
    width = pg.width;
    height = pg.height;
    // properly set the eye which is a 3 step process:
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

  /**
   * Same as {@code return super.checkIfGrabsInput(event)}.
   * <p>
   * To use it, create a custom InteractiveFrame class, implement a
   * {@code checkIfGrabsInput(CustomEvent)} method and override the
   * {@link #checkIfGrabsInput(BogusEvent)} as follows:
   * <pre>
   * {@code
   * @Override
   * public boolean checkIfGrabsInput(BogusEvent event) {
   *   if(event instanceof CustomEvent)
   *     return checkIfGrabsInput(event);
   *   else
   *     return supercheckIfGrabsInput(event);
   * }
   * }
   * </pre>
   *
   * @see remixlab.dandelion.core.GenericFrame#checkIfGrabsInput(BogusEvent)
   * @see #checkIfGrabsInput(BogusEvent)
   */
  protected boolean supercheckIfGrabsInput(BogusEvent event) {
    return super.checkIfGrabsInput(event);
  }

  /**
   * Checks for the existence of the
   * {@link Grabber#checkIfGrabsInput(BogusEvent)} condition at the
   * {@link #pApplet()}, having
   * {@code public boolean checkIfGrabsInput(Scene, CustomEvent)} as method
   * prototype. If it doesn't find it there, looks for the condition at this instance,
   * with a similar method prototype, but without the Scene parameter.
   * You don't need to call this. Automatically called by agents handling this scene.
   * <p>
   * <b>Note: </b> Call {@link #supercheckIfGrabsInput(BogusEvent)} at your scene
   * derived class, if you prefer to use inheritance to override the scene picking
   * condition on a custom-event.
   *
   * @see #supercheckIfGrabsInput(BogusEvent)
   */
  @Override
  public boolean checkIfGrabsInput(BogusEvent event) {
    Method mth = null;
    Object obj = pApplet();
    boolean sceneParam = false;
    // 1. Retrieving
    try {
      mth = obj.getClass().getMethod("checkIfGrabsInput", new Class<?>[]{Scene.class, event.getClass()});
      sceneParam = true;
    } catch (Exception e1) {
      obj = this;
      try {
        mth = obj.getClass().getMethod("checkIfGrabsInput", new Class<?>[]{event.getClass()});
      } catch (Exception e2) {
        PApplet.println("Error: no picking condition for " + event.getClass().getName());
      }
    }
    // 2. Invocation
    try {
      if (sceneParam)
        return (boolean) mth.invoke(obj, new Object[]{this, event});
      else
        return (boolean) mth.invoke(obj, new Object[]{event});
    } catch (Exception e) {
      PApplet.println("Error: no picking condition found");
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean checkIfGrabsInput(KeyboardEvent event) {
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
    return mainPGraphics;
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
    return pb;
  }

  /**
   * Enable the {@link #pickingBuffer()}.
   */
  public void enablePickingBuffer() {
    if (!(pickingBufferEnabled = pb != null))
      System.out.println("PickingBuffer can't be instantiated!");
  }

  /**
   * Disable the {@link #pickingBuffer()}.
   */
  public void disablePickingBuffer() {
    pickingBufferEnabled = false;
  }

  /**
   * Returns {@code true} if {@link #pickingBuffer()} buffer is enabled and {@code false}
   * otherwise.
   */
  public boolean isPickingBufferEnabled() {
    return pickingBufferEnabled;
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
    return (mainPGraphics instanceof PGraphics3D);
  }

  // CHOOSE PLATFORM

  @Override
  protected void setPlatform() {
    String value = System.getProperty("java.vm.vendor").toString();
    if (Pattern.compile(Pattern.quote("Android"), Pattern.CASE_INSENSITIVE).matcher(value).find())
      platform = Platform.PROCESSING_ANDROID;
    else
      platform = Platform.PROCESSING_DESKTOP;
  }

  /**
   * Either returns {@link remixlab.proscene.KeyAgent#keyCode(char)} or
   * {@link remixlab.proscene.DroidKeyAgent#keyCode(char)} depending on
   * {@link #platform()}.
   */
  public static int keyCode(char key) {
    return platform() == Platform.PROCESSING_ANDROID ? DroidKeyAgent.keyCode(key) : KeyAgent.keyCode(key);
  }

  // P5-WRAPPERS

  /**
   * Same as {@code vertex(pg(), v)}.
   *
   * @see #vertex(PGraphics, float[])
   */
  public void vertex(float[] v) {
    vertex(pg(), v);
  }

  /**
   * Wrapper for PGraphics.vertex(v)
   */
  public static void vertex(PGraphics pg, float[] v) {
    pg.vertex(v);
  }

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
   * Same as
   * {@code if (this.is2D()) vertex(pg(), x, y, u, v); else vertex(pg(), x, y, z, u, v);}.
   *
   * @see #vertex(PGraphics, float, float, float, float)
   * @see #vertex(PGraphics, float, float, float, float, float)
   */
  public void vertex(float x, float y, float z, float u, float v) {
    if (this.is2D())
      vertex(pg(), x, y, u, v);
    else
      vertex(pg(), x, y, z, u, v);
  }

  /**
   * Wrapper for PGraphics.vertex(x,y,z,u,v)
   */
  public static void vertex(PGraphics pg, float x, float y, float z, float u, float v) {
    if (pg instanceof PGraphics3D)
      pg.vertex(x, y, z, u, v);
    else
      pg.vertex(x, y, u, v);
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
   * Same as {@code vertex(pg(), x, y, u, v)}.
   *
   * @see #vertex(PGraphics, float, float, float, float)
   */
  public void vertex(float x, float y, float u, float v) {
    vertex(pg(), x, y, u, v);
  }

  /**
   * Wrapper for PGraphics.vertex(x,y,u,v)
   */
  public static void vertex(PGraphics pg, float x, float y, float u, float v) {
    pg.vertex(x, y, u, v);
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
    if (platform() == Platform.PROCESSING_ANDROID)
      return disableDroidTouchAgent();
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
    if (platform() == Platform.PROCESSING_ANDROID)
      return disableDroidKeyAgent();
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
      throw new RuntimeException("Proscene mouseAgent() is not available in Android mode. Use droidTouchAgent() instead");
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
    if (platform() == Platform.PROCESSING_ANDROID)
      throw new RuntimeException("Proscene keyAgent() is not available in Android mode. Use droidKeyAgent() instead");
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
    if (platform() == Platform.PROCESSING_ANDROID)
      throw new RuntimeException(
          "Proscene enableKeyAgent() is not available in Android mode. Use enableDroidKeyAgent() instead");
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
    if (platform() == Platform.PROCESSING_ANDROID)
      throw new RuntimeException(
          "Proscene disableKeyAgent() is not available in Android mode. Use disableDroidKeyAgent() instead");
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
    if (platform() == Platform.PROCESSING_ANDROID)
      throw new RuntimeException(
          "Proscene isKeyAgentEnabled() is not available in Android mode. Use isDroidKeyAgentEnabled() instead");
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
  public DroidTouchAgent droidTouchAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      throw new RuntimeException("Proscene droidTouchAgent() is not available in Desktop mode. Use mouseAgent() instead");
    return (DroidTouchAgent) motionAgent();
  }

  /**
   * Enables motion handling through the {@link #droidTouchAgent()}.
   *
   * @see #droidTouchAgent()
   * @see #isDroidTouchAgentEnabled()
   * @see #disableDroidTouchAgent()
   * @see #enableDroidKeyAgent()
   */
  public void enableDroidTouchAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      throw new RuntimeException(
          "Proscene enableDroidTouchAgent() is not available in Desktop mode. Use enableMouseAgent() instead");
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
  public boolean disableDroidTouchAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      throw new RuntimeException(
          "Proscene disableDroidTouchAgent() is not available in Desktop mode. Use disableMouseAgent() instead");
    if (isMotionAgentEnabled())
      return inputHandler().unregisterAgent(motionAgent());
    return false;
  }

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
    if (platform() == Platform.PROCESSING_DESKTOP)
      throw new RuntimeException(
          "Proscene isDroidTouchAgentEnabled() is not available in Android mode. Use isDroidKeyAgentEnabled() instead");
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
  public DroidKeyAgent droidKeyAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      throw new RuntimeException("Proscene droidKeyAgent() is not available in Desktop mode. Use keyAgent() instead");
    return (DroidKeyAgent) defKeyboardAgent;
  }

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
  public boolean disableDroidKeyAgent() {
    if (platform() == Platform.PROCESSING_DESKTOP)
      throw new RuntimeException(
          "Proscene disableDroidKeyAgent() is not available in Desktop mode. Use disableKeyAgent() instead");
    if (inputHandler().isAgentRegistered(keyboardAgent())) {
      // TODO DROID broke in Android
      // parent.unregisterMethod("keyEvent", keyboardAgent());
      return inputHandler().unregisterAgent(keyboardAgent());
    }
    return false;
  }

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

  @Override
  public String info() {
    String result = new String();
    String info = profile.info();
    if (!info.isEmpty()) {
      result = "1. Scene\n";
      result += info;
    }
    info = eyeFrame().info(); // frame already parses info :P
    if (!info.isEmpty()) {
      result += "2. Eye\n";
      result += info;
    }
    if (this.leadingFrames().size() > 1)
      result += "3. For a specific frame bindings use: frame.info():\n";
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
   * timers are (single-threaded) {@link remixlab.fpstiming.SeqTimer}(s)).
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

  // ANIMATION METHOD REG

  @Override
  public boolean invokeAnimationHandler() {
    if (animateHandlerObject != null) {
      try {
        animateHandlerMethod.invoke(animateHandlerObject, new Object[]{this});
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
   * @param obj        the object to handle the event
   * @param methodName the method to execute in the object handler class
   * @see #animate()
   * @see #removeAnimationHandler()
   */
  public void addAnimationHandler(Object obj, String methodName) {
    try {
      animateHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[]{Scene.class});
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
    disableDepthTest(pg());
  }

  /**
   * Disables depth test on the PGraphics instance.
   *
   * @see #enableDepthTest(PGraphics)
   */
  public void disableDepthTest(PGraphics p) {
    p.hint(PApplet.DISABLE_DEPTH_TEST);
  }

  @Override
  public void enableDepthTest() {
    enableDepthTest(pg());
  }

  /**
   * Enables depth test on the PGraphics instance.
   *
   * @see #disableDepthTest(PGraphics)
   */
  public void enableDepthTest(PGraphics p) {
    p.hint(PApplet.ENABLE_DEPTH_TEST);
  }

  // end: GWT-incompatible
  // */

  // 3. Drawing methods

  /**
   * Called before your main drawing and performs the following:
   * <ol>
   * <li>Handles the {@link #avatar()}</li>
   * <li>Calls {@link #bindMatrices()}</li>
   * <li>Calls {@link remixlab.dandelion.core.Eye#updateBoundaryEquations()} if
   * {@link #areBoundaryEquationsEnabled()}</li>
   * <li>Calls {@link #proscenium()}</li>
   * </ol>
   * <p>
   * <b>Note</b> that this method overloads
   * {@link remixlab.dandelion.core.AbstractScene#preDraw()} where a call to
   * {@link #displayVisualHints()} is done. Here, however, it needs to be bypassed for the
   * PApplet.background() method not to hide the display of the {@link #visualHints()}.
   * The {@link #displayVisualHints()} mostly happens then at the {@link #draw()} method,
   * if the scene is on-screen, or at the {@link #endDraw()} if it is off-screen.
   *
   * @see #postDraw()
   */
  @Override
  public void preDraw() {
    // 1. Avatar
    if (avatar() != null && (!eye().anyInterpolationStarted()))
      eye().frame().setWorldMatrix(avatar().trackingEyeFrame());
    // 2. Eye
    bindMatrices();
    if (areBoundaryEquationsEnabled() && (eye().lastUpdate() > lastEqUpdate || lastEqUpdate == 0)) {
      eye().updateBoundaryEquations();
      lastEqUpdate = frameCount;
    }
  }

  /**
   * Paint method which is called just before your {@code PApplet.draw()} method. Simply
   * calls {@link #preDraw()}. This method is registered at the PApplet and hence you
   * don't need to call it. Only meaningful if the scene is on-screen (it the scene
   * {@link #isOffscreen()} it even doesn't get registered at the PApplet.
   * <p>
   * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and
   * {@link #height()}, and calls
   * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
   * <p>
   *
   * @see #draw()
   * @see #preDraw()
   * @see #postDraw()
   * @see #beginDraw()
   * @see #endDraw()
   * @see #isOffscreen()
   */
  public void pre() {
    if ((width != pg().width) || (height != pg().height)) {
      width = pg().width;
      height = pg().height;
      eye().setScreenWidthAndHeight(width, height);
    }
    preDraw();
    pushModelView();
  }

  /**
   * Paint method which is called just after your {@code PApplet.draw()} method. Calls
   * {@link #proscenium()}, {@link #displayVisualHints()}, draws the scene into the
   * {@link #pickingBuffer()} and {@link #postDraw()}. This method is registered at the
   * PApplet and hence you don't need to call it. Only meaningful if the scene is
   * on-screen (it the scene {@link #isOffscreen()} it even doesn't get registered at the
   * PApplet.
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
    proscenium();
    popModelView();
    displayVisualHints();
    handlePickingBuffer();
    postDraw();
  }

  // Off-screen

  /**
   * Same as {@code showOnlyOffScreenWarning(method, true)}.
   *
   * @see #showOnlyOffScreenWarning(String, boolean)
   */
  static public void showOnlyOffScreenWarning(String method) {
    showOnlyOffScreenWarning(method, true);
  }

  /**
   * Display a warning that the specified method is only available for off-screen scenes
   * if {@code offscreen} is {@code true}, or (on-screen scenes if if {@code offscreen} is
   * {@code false}).
   */
  static public void showOnlyOffScreenWarning(String method, boolean offscreen) {
    if (offscreen)
      showWarning(method + "() is only meaningful for offscreen scenes.");
    else
      showWarning(method + "() is only meaningful for onscreen scenes.");
  }

  /**
   * Only if the Scene {@link #isOffscreen()}. Calls {@code pg().beginDraw()} (hence
   * there's no need to explicitly call it) and then {@link #preDraw()} .
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
   * @see #pg()
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
    // open off-screen pgraphics for drawing:
    pg().beginDraw();
    preDraw();
    pushModelView();
  }

  /**
   * Only if the Scene {@link #isOffscreen()}. Calls
   * <p>
   * <ol>
   * <li>{@link #proscenium()}</li>
   * <li>{@link #displayVisualHints()}</li>
   * <li>{@code pg().endDraw()} and hence there's no need to explicitly call it</li>
   * <li>{@link #handlePickingBuffer()}</li>
   * <li>{@link #handleFocus()} if {@link #hasAutoFocus()} is {@code true}</li>
   * <li>{@link #postDraw()}</li>
   * </ol>
   * <p>
   * {@link #postDraw()}.
   *
   * @see #draw()
   * @see #preDraw()
   * @see #postDraw()
   * @see #beginDraw()
   * @see #pre()
   * @see #isOffscreen()
   * @see #pg()
   */
  public void endDraw() {
    if (!isOffscreen())
      throw new RuntimeException(
          "(begin/)endDraw() should be used only within offscreen scenes. Check your implementation!");
    beginOffScreenDrawingCalls--;
    if (beginOffScreenDrawingCalls != 0)
      throw new RuntimeException("There should be exactly one beginDraw() call followed by a "
          + "endDraw() and they cannot be nested. Check your implementation!");
    proscenium();
    popModelView();
    displayVisualHints();
    pg().endDraw();
    handlePickingBuffer();
    if (hasAutoFocus())
      handleFocus();
    postDraw();
  }

  /**
   * Internal use. Draws the contents of the scene (its {@link #frames()}) into the
   * {@link #pickingBuffer()} to perform picking on the scene {@link #frames()}.
   * <p>
   * Called by {@link #draw()} (on-screen scenes) and {@link #endDraw()} (off-screen
   * scenes).
   */
  protected void handlePickingBuffer() {
    if (!this.isPickingBufferEnabled() || !unchachedBuffer)
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
   * Same as {@code display(pg())}. Only meaningful if the scene {@link #isOffscreen()}.
   *
   * @see #display(PGraphics)
   * @see #pg()
   */
  public void display() {
    display(pg());
  }

  /**
   * Same as {@code pApplet().image(pgraphics, originCorner().x(), originCorner().y())}.
   * Only meaningful if the scene {@link #isOffscreen()}.
   * <p>
   * Displays the contents of the pgraphics (typically {@link #pg()} or the
   * {@link #pickingBuffer()}) into the scene {@link #pApplet()}.
   */
  public void display(PGraphics pgraphics) {
    if (!isOffscreen())
      showOnlyOffScreenWarning("display");
    pApplet().image(pgraphics, originCorner().x(), originCorner().y());
    lastDisplay = pApplet().frameCount;
  }

  /**
   * Implementation of the "Focus follows mouse" policy. Used by {@link #hasFocus()}.
   */
  protected boolean hasMouseFocus() {
    return originCorner().x() < pApplet().mouseX && pApplet().mouseX < originCorner().x() + this.width()
        && originCorner().y() < pApplet().mouseY && pApplet().mouseY < originCorner().y() + this.height();
  }

  /**
   * Main condition evaluated by the {@link #handleFocus()} algorithm, which defaults to
   * {@link #hasMouseFocus()}.
   * <p>
   * Override this method to define a focus policy different than "focus follows mouse".
   */
  protected boolean hasFocus() {
    return hasMouseFocus();
  }

  /**
   * Macro used by {@link #handleFocus()}.
   */
  protected boolean displayed() {
    return lastDisplay == pApplet().frameCount - 1;
  }

  /**
   * Called by {@link #endDraw()} if {@link #hasAutoFocus()} is {@code true}.
   */
  protected void handleFocus() {
    if (offScreenScenes < 2)
      return;
    // Handling focus of non-overlapping scenes is trivial.
    // Suppose scn1 and scn2 overlap and also that scn2 is displayed on top of scn1, i.e.,
    // scn2.display() is to be called after scn1.display() (which is the key observation).
    // Then, for a given frame either only scn1 hasFocus() (which is handled trivially);
    // or, both, scn1 and scn2 hasFocus(), which means only scn2 should retain focus
    // (while scn1 lose it).
    boolean available = true;
    if (lastScene != null)
      if (lastScene != this)
        // Note that lastScene.displayed() returns true only if the lastScene was assigned
        // in the previous frame and false otherwise (particularly, if it was assigned in
        // the current frame) which means both: 1. If scn1 gained focus on the current
        // frame it will lose it when the routine is run on scn2 in the current frame;
        // and, 2. If scn2 has gained focus in the previous frame, it will prevent scn1
        // from having it back in the current frame.
        if (lastScene.hasFocus() && lastScene.displayed())
          available = false;
    if (hasFocus() && displayed() && available) {
      enableMotionAgent();
      enableKeyboardAgent();
      lastScene = this;
    } else {
      disableMotionAgent();
      disableKeyboardAgent();
    }
  }

  /**
   * When having multiple off-screen scenes displayed at once, one should decide which
   * scene will grab input from both, the {@link #motionAgent()} and the
   * {@link #keyboardAgent()}, so that code like this:
   * <p>
   * <pre>
   * {@code
   * scene1.beginDraw();
   * drawScene1();
   * scene.endDraw();
   * scene.display();
   * scene2.beginDraw();
   * drawScene2();
   * scene2.endDraw();
   * scene2.display();
   * }
   * </pre>
   * <p>
   * will behave according to a given focus policy. This property is enabled by default
   * and it implements a "focus follows mouse" policy, so that the scene under the cursor
   * will grab input. If multiple scenes overlaps the scene on top will grab the input as
   * expected.
   * <p>
   * To implement a different policy either:
   * <p>
   * <ol>
   * <li>Override the {@link #hasFocus()} Scene object; or,</li>
   * <li>Call {@link #disableAutoFocus()} and implement your own focus policy at the
   * sketch space.</li>
   * </ol>
   * <p>
   * <b>Note</b> that for this policy to work you should call {@link #display()} instead
   * of the papplet image() function on the {@link #pg()}.
   *
   * @see #beginDraw()
   * @see #endDraw()
   * @see #display()
   * @see #enableAutoFocus()
   * @see #disableAutoFocus()
   * @see #toggleAutoFocus()
   */
  public boolean hasAutoFocus() {
    if (!isOffscreen())
      showOnlyOffScreenWarning("hasAutoFocus");
    return autofocus;
  }

  /**
   * Toggles the off-screen scene auto-focus property.
   *
   * @see #hasAutoFocus()
   * @see #enableAutoFocus(boolean)
   * @see #enableAutoFocus()
   * @see #disableAutoFocus()
   */
  public void toggleAutoFocus() {
    if (!isOffscreen()) {
      showOnlyOffScreenWarning("toggleAutoFocus");
      return;
    }
    if (hasAutoFocus())
      disableAutoFocus();
    else
      enableAutoFocus();
  }

  /**
   * Disables the off-screen scene auto-focus property.
   *
   * @see #hasAutoFocus()
   * @see #enableAutoFocus(boolean)
   * @see #enableAutoFocus()
   * @see #toggleAutoFocus()
   */
  public void disableAutoFocus() {
    if (!isOffscreen()) {
      showOnlyOffScreenWarning("disableAutoFocus");
      return;
    }
    enableAutoFocus(false);
  }

  /**
   * Enables the off-screen scene auto-focus property.
   *
   * @see #hasAutoFocus()
   * @see #enableAutoFocus(boolean)
   * @see #disableAutoFocus()
   * @see #toggleAutoFocus()
   */
  public void enableAutoFocus() {
    if (!isOffscreen()) {
      showOnlyOffScreenWarning("enableAutoFocus");
      return;
    }
    enableAutoFocus(true);
  }

  /**
   * Turns on or off the off-screen scene auto-focus property according to {@code flag}.
   * <p>
   * The {@link #hasAutoFocus()} property for off-screen scenes is {@code true} by
   * default.
   *
   * @see #hasAutoFocus()
   * @see #enableAutoFocus()
   * @see #disableAutoFocus()
   * @see #toggleAutoFocus()
   */
  public void enableAutoFocus(boolean flag) {
    if (!isOffscreen()) {
      showOnlyOffScreenWarning("enableAutoFocus");
      return;
    }
    autofocus = flag;
  }

  // TODO: Future work should include the eye and scene profiles.
  // Probably related with iFrame.fromFrame

  /**
   * Same as {@link #saveConfig()}.
   * <p>
   * Should be called automatically by P5, but it is currently broken. See:
   * https://github.com/processing/processing/issues/4445
   *
   * @see #saveConfig()
   * @see #saveConfig(String)
   * @see #loadConfig()
   * @see #loadConfig(String)
   */
  public void dispose() {
    System.out.println("Debug: saveConfig() (i.e., dispose()) called!");
    if (!this.isOffscreen())
      saveConfig();
  }

  /**
   * Same as {@code saveConfig("data/config.json")}.
   * <p>
   * Note that off-screen scenes require {@link #saveConfig(String)} instead.
   *
   * @see #saveConfig(String)
   * @see #loadConfig()
   * @see #loadConfig(String)
   */
  public void saveConfig() {
    if (this.isOffscreen())
      System.out
          .println("Warning: no config saved! Off-screen scene config requires saveConfig(String fileName) to be called");
    else
      saveConfig("data/config.json");
  }

  /**
   * Saves the {@link #eye()}, the {@link #radius()}, the {@link #visualHints()}, the
   * {@link remixlab.dandelion.core.Camera#type()} and the
   * {@link remixlab.dandelion.core.Camera#keyFrameInterpolatorArray()} into
   * {@code fileName}.
   *
   * @see #saveConfig()
   * @see #loadConfig()
   * @see #loadConfig(String)
   */
  public void saveConfig(String fileName) {
    JSONObject json = new JSONObject();
    json.setFloat("radius", radius());
    json.setInt("visualHints", visualHints());
    json.setBoolean("ortho", is2D() ? true : camera().type() == Camera.Type.ORTHOGRAPHIC ? true : false);
    json.setJSONObject("eye", toJSONObject(eyeFrame()));
    JSONArray jsonPaths = new JSONArray();
    // keyFrames
    int i = 0;
    for (int id : eye().keyFrameInterpolatorMap().keySet()) {
      JSONObject jsonPath = new JSONObject();
      jsonPath.setInt("key", id);
      jsonPath.setJSONArray("keyFrames", toJSONArray(id));
      jsonPaths.setJSONObject(i++, jsonPath);
    }
    json.setJSONArray("paths", jsonPaths);
    pApplet().saveJSONObject(json, fileName);
  }

  /**
   * Same as {@code loadConfig("data/config.json")}.
   * <p>
   * Note that off-screen scenes require {@link #loadConfig(String)} instead.
   *
   * @see #loadConfig(String)
   * @see #saveConfig()
   * @see #saveConfig(String)
   */
  public void loadConfig() {
    if (this.isOffscreen())
      System.out
          .println("Warning: no config loaded! Off-screen scene config requires loadConfig(String fileName) to be called");
    else
      loadConfig("config.json");
  }

  /**
   * Loads the {@link #eye()}, the {@link #radius()}, the {@link #visualHints()}, the
   * {@link remixlab.dandelion.core.Camera#type()} and the
   * {@link remixlab.dandelion.core.Camera#keyFrameInterpolatorArray()} from
   * {@code fileName}.
   *
   * @see #saveConfig()
   * @see #saveConfig(String)
   * @see #loadConfig()
   */
  public void loadConfig(String fileName) {
    JSONObject json = null;
    try {
      json = pApplet().loadJSONObject(fileName);
    } catch (Exception e) {
      System.out.println("No such " + fileName + " found!");
    }
    if (json != null) {
      setRadius(json.getFloat("radius"));
      setVisualHints(json.getInt("visualHints"));
      if (is3D())
        camera().setType(json.getBoolean("ortho") ? Camera.Type.ORTHOGRAPHIC : Camera.Type.PERSPECTIVE);
      eyeFrame().setWorldMatrix(toFrame(json.getJSONObject("eye")));
      // keyFrames
      JSONArray paths = json.getJSONArray("paths");
      for (int i = 0; i < paths.size(); i++) {
        JSONObject path = paths.getJSONObject(i);
        int id = path.getInt("key");
        eye().deletePath(id);
        JSONArray keyFrames = path.getJSONArray("keyFrames");
        for (int j = 0; j < keyFrames.size(); j++) {
          InteractiveFrame keyFrame = new InteractiveFrame(this);
          pruneBranch(keyFrame);
          keyFrame.setWorldMatrix(toFrame(keyFrames.getJSONObject(j)));
          keyFrame.setPickingPrecision(GenericFrame.PickingPrecision.FIXED);
          keyFrame.setGrabsInputThreshold(AbstractScene.platform() == Platform.PROCESSING_ANDROID ? 50 : 20);
          if (pathsVisualHint())
            inputHandler().addGrabber(keyFrame);
          if (!eye().keyFrameInterpolatorMap().containsKey(id))
            eye().setKeyFrameInterpolator(id, new KeyFrameInterpolator(this, eyeFrame()));
          eye().keyFrameInterpolator(id).addKeyFrame(keyFrame, keyFrames.getJSONObject(j).getFloat("time"));
        }
      }
    }
  }

  /**
   * Used internally by {@link #saveConfig(String)}. Converts the {@code id} eye path into
   * a P5 JSONArray.
   */
  protected JSONArray toJSONArray(int id) {
    JSONArray jsonKeyFrames = new JSONArray();
    for (int i = 0; i < eye().keyFrameInterpolator(id).numberOfKeyFrames(); i++) {
      JSONObject jsonKeyFrame = toJSONObject(eye().keyFrameInterpolator(id).keyFrame(i));
      jsonKeyFrame.setFloat("time", eye().keyFrameInterpolator(id).keyFrameTime(i));
      jsonKeyFrames.setJSONObject(i, jsonKeyFrame);
    }
    return jsonKeyFrames;
  }

  /**
   * Used internally by {@link #loadConfig(String)}. Converts the P5 JSONObject into a
   * {@code frame}.
   */
  protected Frame toFrame(JSONObject jsonFrame) {
    Frame frame = new Frame(is3D());
    float x, y, z;
    x = jsonFrame.getJSONArray("position").getFloat(0);
    y = jsonFrame.getJSONArray("position").getFloat(1);
    z = jsonFrame.getJSONArray("position").getFloat(2);
    Vec pos = new Vec(x, y, z);
    frame.setPosition(pos);
    if (is2D())
      frame.setOrientation(new Rot(jsonFrame.getJSONArray("orientation").getFloat(0)));
    else {
      x = jsonFrame.getJSONArray("orientation").getFloat(0);
      y = jsonFrame.getJSONArray("orientation").getFloat(1);
      z = jsonFrame.getJSONArray("orientation").getFloat(2);
      float w = jsonFrame.getJSONArray("orientation").getFloat(3);
      frame.setOrientation(new Quat(x, y, z, w));
    }
    frame.setMagnitude(jsonFrame.getFloat("magnitude"));
    return frame;
  }

  /**
   * Used internally by {@link #saveConfig(String)}. Converts {@code frame} into a P5
   * JSONObject.
   */
  protected JSONObject toJSONObject(Frame frame) {
    JSONObject jsonFrame = new JSONObject();
    jsonFrame.setFloat("magnitude", frame.magnitude());
    jsonFrame.setJSONArray("position", toJSONArray(frame.position()));
    jsonFrame.setJSONArray("orientation", toJSONArray(frame.orientation()));
    return jsonFrame;
  }

  /**
   * Used internally by {@link #saveConfig(String)}. Converts {@code vec} into a P5
   * JSONArray.
   */
  protected JSONArray toJSONArray(Vec vec) {
    JSONArray jsonVec = new JSONArray();
    jsonVec.setFloat(0, vec.x());
    jsonVec.setFloat(1, vec.y());
    jsonVec.setFloat(2, vec.z());
    return jsonVec;
  }

  /**
   * Used internally by {@link #saveConfig(String)}. Converts {@code rot} into a P5
   * JSONArray.
   */
  protected JSONArray toJSONArray(Rotation rot) {
    JSONArray jsonRot = new JSONArray();
    if (is3D()) {
      Quat quat = (Quat) rot;
      jsonRot.setFloat(0, quat.x());
      jsonRot.setFloat(1, quat.y());
      jsonRot.setFloat(2, quat.z());
      jsonRot.setFloat(3, quat.w());
    } else
      jsonRot.setFloat(0, rot.angle());
    return jsonRot;
  }

  /**
   * Internal used. Applies the {@link #pickingBuffer()} shaders needed by iFrame picking.
   */
  protected void applyPickingBufferShaders() {
    pickingBuffer().shader(pickingBufferShaderTriangle);
    pickingBuffer().shader(pickingBufferShaderLine, LINES);
    pickingBuffer().shader(pickingBufferShaderPoint, POINTS);
  }

  protected boolean unchachedBuffer;
  protected PGraphics targetPGraphics;

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
   * Returns the collection of interactive frames the scene handles, including eye-frames.
   * <p>
   * Note that iterating through the scene frames is not as efficient as simply calling
   * {@link #drawFrames()}.
   */
  public ArrayList<InteractiveFrame> frames() {
    ArrayList<InteractiveFrame> iFrames = new ArrayList<InteractiveFrame>();
    for (GenericFrame frame : frames(true))
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
   * {@link remixlab.dandelion.core.AbstractScene#traverseTree()}.
   * <p>
   * <b>Attention:</b> this method should be called after {@link #bindMatrices()} (i.e.,
   * eye update which happens at {@link #preDraw()}) and before any other transformation
   * of the modelview takes place.
   *
   * @see #frames()
   * @see #pg()
   * @see #drawFrames(PGraphics)
   * @see remixlab.proscene.InteractiveFrame#draw(PGraphics)
   */
  public void drawFrames() {
    targetPGraphics = pg();
    traverseTree();
  }

  /**
   * Draw all {@link #frames()} into the given pgraphics. No
   * {@code pgraphics.beginDraw()/endDraw()} calls take place. This method allows shader
   * chaining.
   * <p>
   * Note that {@code drawFrames(pickingBuffer())} (which enables 'picking' of the frames
   * using a <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
   * technique is called by {@link #postDraw()}.
   * <p>
   * <b>Attention:</b> this method should be called after {@link #bindMatrices(PGraphics)}
   * (i.e., manual eye update) and before any other transformation of the modelview takes
   * place.
   *
   * @param pgraphics
   * @see #frames()
   * @see #drawFrames()
   * @see remixlab.proscene.InteractiveFrame#draw(PGraphics)
   */
  public void drawFrames(PGraphics pgraphics) {
    // 1. Set pgraphics matrices using a custom MatrixHelper
    bindMatrices(pgraphics);
    // 2. Draw all frames into pgraphics
    targetPGraphics = pgraphics;
    traverseTree();
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
    return (pgraphics instanceof processing.opengl.PGraphicsOpenGL) ?
        new GLMatrixHelper(this, (PGraphicsOpenGL) pgraphics) :
        new Java2DMatrixHelper(this, pgraphics);
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
  public static void applyTransformation(PGraphics pgraphics, Frame frame) {
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
  public static void applyWorldTransformation(PGraphics pgraphics, Frame frame) {
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
    beginScreenDrawing(pg());
  }

  /**
   * Begins screen drawing on an arbitrary PGraphics instance using {@link #eye()}
   * parameters. Don't forget to call {@link #endScreenDrawing(PGraphics)} after screen
   * drawing ends.
   *
   * @see #endScreenDrawing(PGraphics)
   * @see #beginScreenDrawing()
   */
  public void beginScreenDrawing(PGraphics p) {
    if (startCoordCalls != 0)
      throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
          + "endScreenDrawing() and they cannot be nested. Check your implementation!");
    startCoordCalls++;
    p.hint(PApplet.DISABLE_OPTIMIZED_STROKE);// -> new line not present in AbstractScene.bS
    disableDepthTest(p);
    // if-else same as:
    // matrixHelper(p).beginScreenDrawing();
    // but perhaps a bit more efficient
    if (p == pg())
      matrixHelper().beginScreenDrawing();
    else
      matrixHelper(p).beginScreenDrawing();
  }

  /**
   * Need to override it because of this issue:
   * https://github.com/remixlab/proscene/issues/1
   */
  @Override
  public void endScreenDrawing() {
    endScreenDrawing(pg());
  }

  /**
   * Ends screen drawing on the arbitrary PGraphics instance using {@link #eye()}
   * parameters. The screen drawing should happen between
   * {@link #beginScreenDrawing(PGraphics)} and this method.
   *
   * @see #beginScreenDrawing(PGraphics)
   * @see #endScreenDrawing()
   */
  public void endScreenDrawing(PGraphics p) {
    startCoordCalls--;
    if (startCoordCalls != 0)
      throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
          + "endScreenDrawing() and they cannot be nested. Check your implementation!");
    // if-else same as:
    // matrixHelper(p).endScreenDrawing();
    // but perhaps a bit more efficient
    if (p == pg())
      matrixHelper().endScreenDrawing();
    else
      matrixHelper(p).endScreenDrawing();
    enableDepthTest(p);
    p.hint(PApplet.ENABLE_OPTIMIZED_STROKE);// -> new line not present in AbstractScene.eS
  }

  // DRAWING

  @Override
  public void drawCylinder(float w, float h) {
    drawCylinder(pg(), w, h);
  }

  /**
   * Same as {@code drawCylinder(pg, radius()/6, radius()/3)}.
   * <p>
   * Note that this method is useful for
   * {@link remixlab.proscene.InteractiveFrame#setShape(String)}.
   */
  public void drawCylinder(PGraphics pg) {
    drawCylinder(pg, radius() / 6, radius() / 3);
  }

  /**
   * Low-level version of {@link #drawCylinder(float, float)}.
   * <p>
   * Calls {@link #drawCylinder(float, float)} on {@code pg}.
   */
  public static void drawCylinder(PGraphics pg, float w, float h) {
    if (!(pg instanceof PGraphics3D)) {
      AbstractScene.showDepthWarning("drawCylinder");
      return;
    }
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
    drawHollowCylinder(pg(), detail, w, h, m, n);
  }

  /**
   * Low-level version of {@link #drawHollowCylinder(int, float, float, Vec, Vec)}.
   * <p>
   * Calls {@link #drawHollowCylinder(int, float, float, Vec, Vec)} on {@code pg}.
   */
  public static void drawHollowCylinder(PGraphics pg, int detail, float w, float h, Vec m, Vec n) {
    if (!(pg instanceof PGraphics3D)) {
      AbstractScene.showDepthWarning("drawHollowCylinder");
      return;
    }
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

  // Cone v1

  @Override
  public void drawCone(int detail, float x, float y, float r, float h) {
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
   * Same as {@code drawCone(pg, 12, 0, 0, radius()/4, sqrt(3) * radius()/4)}.
   * <p>
   * Note that this method is useful for
   * {@link remixlab.proscene.InteractiveFrame#setShape(String)}.
   */
  public void drawCone(PGraphics pg) {
    float r = radius() / 4;
    drawCone(pg, 12, 0, 0, r, (float) Math.sqrt((float) 3) * r);
  }

  /**
   * Low-level version of {@link #drawCone(int, float, float, float, float)}.
   * <p>
   * Calls {@link #drawCone(int, float, float, float, float)} on {@code pg}.
   */
  public static void drawCone(PGraphics pg, int detail, float x, float y, float r, float h) {
    if (!(pg instanceof PGraphics3D)) {
      AbstractScene.showDepthWarning("drawCone");
      return;
    }
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

  // Cone v2

  /**
   * Same as {@code cone(pg, det, 0, 0, r1, r2, h)}
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

  @Override
  public void drawCone(int detail, float x, float y, float r1, float r2, float h) {
    drawCone(pg(), detail, x, y, r1, r2, h);
  }

  /**
   * Low-level version of {@link #drawCone(int, float, float, float, float, float)}.
   * <p>
   * Calls {@link #drawCone(int, float, float, float, float, float)} on {@code pg}.
   */
  public static void drawCone(PGraphics pg, int detail, float x, float y, float r1, float r2, float h) {
    if (!(pg instanceof PGraphics3D)) {
      AbstractScene.showDepthWarning("drawCone");
      return;
    }
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
    drawAxes(pg(), length);
  }

  /**
   * Same as {@code drawAxes(pg, radius()/5)}.
   * <p>
   * Note that this method is useful for
   * {@link remixlab.proscene.InteractiveFrame#setShape(String)}.
   */
  public void drawAxes(PGraphics pg) {
    drawAxes(pg, radius() / 5);
  }

  /**
   * Low-level version of {@link #drawAxes(float)}.
   * <p>
   * Calls {@link #drawAxes(float)} on {@code pg}.
   */
  public void drawAxes(PGraphics pg, float length) {
    pg.pushStyle();
    pg.colorMode(PApplet.RGB, 255);
    float charWidth = length / 40.0f;
    float charHeight = length / 30.0f;
    float charShift = 1.04f * length;

    pg.pushStyle();
    pg.beginShape(PApplet.LINES);
    pg.strokeWeight(2);
    if (is2D()) {
      // The X
      pg.stroke(200, 0, 0);
      pg.vertex(charShift + charWidth, -charHeight);
      pg.vertex(charShift - charWidth, charHeight);
      pg.vertex(charShift - charWidth, -charHeight);
      pg.vertex(charShift + charWidth, charHeight);

      // The Y
      charShift *= 1.02;
      pg.stroke(0, 200, 0);
      pg.vertex(charWidth, charShift + (isRightHanded() ? charHeight : -charHeight));
      pg.vertex(0.0f, charShift + 0.0f);
      pg.vertex(-charWidth, charShift + (isRightHanded() ? charHeight : -charHeight));
      pg.vertex(0.0f, charShift + 0.0f);
      pg.vertex(0.0f, charShift + 0.0f);
      pg.vertex(0.0f, charShift + -(isRightHanded() ? charHeight : -charHeight));
    } else {
      // The X
      pg.stroke(200, 0, 0);
      pg.vertex(charShift, charWidth, -charHeight);
      pg.vertex(charShift, -charWidth, charHeight);
      pg.vertex(charShift, -charWidth, -charHeight);
      pg.vertex(charShift, charWidth, charHeight);
      // The Y
      pg.stroke(0, 200, 0);
      pg.vertex(charWidth, charShift, (isLeftHanded() ? charHeight : -charHeight));
      pg.vertex(0.0f, charShift, 0.0f);
      pg.vertex(-charWidth, charShift, (isLeftHanded() ? charHeight : -charHeight));
      pg.vertex(0.0f, charShift, 0.0f);
      pg.vertex(0.0f, charShift, 0.0f);
      pg.vertex(0.0f, charShift, -(isLeftHanded() ? charHeight : -charHeight));
      // The Z
      pg.stroke(0, 100, 200);
      pg.vertex(-charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
      pg.vertex(charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
      pg.vertex(charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
      pg.vertex(-charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
      pg.vertex(-charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
      pg.vertex(charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
    }
    pg.endShape();
    pg.popStyle();

    // X Axis
    pg.stroke(200, 0, 0);
    if (is2D())
      pg.line(0, 0, length, 0);
    else
      pg.line(0, 0, 0, length, 0, 0);
    // Y Axis
    pg.stroke(0, 200, 0);
    if (is2D())
      pg.line(0, 0, 0, length);
    else
      pg.line(0, 0, 0, 0, length, 0);

    // Z Axis
    if (is3D()) {
      pg.stroke(0, 100, 200);
      pg.line(0, 0, 0, 0, 0, length);
    }
    pg.popStyle();
  }

  @Override
  public void drawGrid(float size, int nbSubdivisions) {
    drawGrid(pg(), size, nbSubdivisions);
  }

  /**
   * Same as {@code drawGrid(size, 10)}.
   */
  public void drawGrid(float size) {
    drawGrid(size, 10);
  }

  /**
   * Same as {@code drawGrid(pg, radius()/4, 10)}.
   * <p>
   * Note that this method is useful for
   * {@link remixlab.proscene.InteractiveFrame#setShape(String)}.
   */
  public void drawGrid(PGraphics pg) {
    drawGrid(pg, radius() / 4, 10);
  }

  /**
   * Low-level version of {@link #drawGrid(float)}.
   * <p>
   * Calls {@link #drawGrid(float)} on {@code pg}.
   */
  public void drawGrid(PGraphics pg, float size, int nbSubdivisions) {
    pg.pushStyle();
    pg.beginShape(LINES);
    for (int i = 0; i <= nbSubdivisions; ++i) {
      final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
      vertex(pg, pos, -size);
      vertex(pg, pos, +size);
      vertex(pg, -size, pos);
      vertex(pg, size, pos);
    }
    pg.endShape();
    pg.popStyle();
  }

  @Override
  public void drawDottedGrid(float size, int nbSubdivisions) {
    drawDottedGrid(pg(), size, nbSubdivisions);
  }

  /**
   * Same as {@code drawDottedGrid(pg, radius()/4, 10)}.
   * <p>
   * Note that this method is useful for
   * {@link remixlab.proscene.InteractiveFrame#setShape(String)}.
   */
  public void drawDottedGrid(PGraphics pg) {
    drawDottedGrid(pg, radius() / 4, 10);
  }

  /**
   * Low-level version of {@link #drawDottedGrid(float, int)}.
   * <p>
   * Calls {@link #drawDottedGrid(float, int)} on {@code pg}.
   */
  public void drawDottedGrid(PGraphics pg, float size, int nbSubdivisions) {
    pg.pushStyle();
    float posi, posj;
    pg.beginShape(POINTS);
    for (int i = 0; i <= nbSubdivisions; ++i) {
      posi = size * (2.0f * i / nbSubdivisions - 1.0f);
      for (int j = 0; j <= nbSubdivisions; ++j) {
        posj = size * (2.0f * j / nbSubdivisions - 1.0f);
        vertex(pg, posi, posj);
      }
    }
    pg.endShape();
    int internalSub = 5;
    int subSubdivisions = nbSubdivisions * internalSub;
    float currentWeight = pg.strokeWeight;
    pg.colorMode(HSB, 255);
    float hue = pg.hue(pg.strokeColor);
    float saturation = pg.saturation(pg.strokeColor);
    float brightness = pg.brightness(pg.strokeColor);
    pg.stroke(hue, saturation, brightness * 10f / 17f);
    pg.strokeWeight(currentWeight / 2);
    pg.beginShape(POINTS);
    for (int i = 0; i <= subSubdivisions; ++i) {
      posi = size * (2.0f * i / subSubdivisions - 1.0f);
      for (int j = 0; j <= subSubdivisions; ++j) {
        posj = size * (2.0f * j / subSubdivisions - 1.0f);
        if (((i % internalSub) != 0) || ((j % internalSub) != 0))
          vertex(pg, posi, posj);
      }
    }
    pg.endShape();
    pg.popStyle();
  }

  @Override
  public void drawEye(Eye eye) {
    drawEye(eye, false);
  }

  /**
   * Applies the {@code eye.frame()} transformation and then calls
   * {@link #drawEye(PGraphics, Eye, boolean)} on the scene {@link #pg()}. If
   * {@code texture} draws the projected scene on the near plane.
   *
   * @see #applyTransformation(Frame)
   * @see #drawEye(PGraphics, Eye, boolean)
   */
  public void drawEye(Eye eye, boolean texture) {
    pg().pushMatrix();
    applyTransformation(eye.frame());
    drawEye(pg(), eye, texture);
    pg().popMatrix();
  }

  /**
   * Same as {@code drawEye(pg, eye, false)}.
   *
   * @see #drawEye(PGraphics, Eye, boolean)
   */
  public void drawEye(PGraphics pg, Eye eye) {
    drawEye(pg, eye, false);
  }

  /**
   * Implementation of {@link #drawEye(Eye)}. If {@code texture} draws the projected scene
   * on the near plane.
   * <p>
   * Warning: texture only works with opengl renderers.
   * <p>
   * Note that if {@code eye.scene()).pg() == pg} this method has not effect at all.
   */
  public void drawEye(PGraphics pg, Eye eye, boolean texture) {
    // Key here is to represent the eye getBoundaryWidthHeight, zNear and zFar params
    // (which are is given in world units) in eye units.
    // Hence they should be multiplied by: 1 / eye.frame().magnitude()
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
      points[0].setX(wh[0] * 1 / eye.frame().magnitude());
      points[1].setX(wh[0] * 1 / eye.frame().magnitude());
      points[0].setY(wh[1] * 1 / eye.frame().magnitude());
      points[1].setY(wh[1] * 1 / eye.frame().magnitude());
    }

    if (is3D()) {
      points[0].setZ(((Camera) eye).zNear() * 1 / eye.frame().magnitude());
      points[1].setZ(((Camera) eye).zFar() * 1 / eye.frame().magnitude());
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

    // Up arrow
    float arrowHeight = 1.5f * points[0].y();
    float baseHeight = 1.2f * points[0].y();
    float arrowHalfWidth = 0.5f * points[0].x();
    float baseHalfWidth = 0.3f * points[0].x();

    pg.noStroke();
    // Arrow base
    if (texture) {
      pg.pushStyle();// end at arrow
      pg.colorMode(PApplet.RGB, 255);
      float r = pg.red(pg.fillColor);
      float g = pg.green(pg.fillColor);
      float b = pg.blue(pg.fillColor);
      pg.fill(r, g, b, 126);// same transparency as near plane texture
    }
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
    if (texture)
      pg.popStyle();// begin at arrow base
    pg.endShape();

    // Planes
    // far plane
    drawPlane(pg, eye, points[1], new Vec(0, 0, -1), false);
    // near plane
    drawPlane(pg, eye, points[0], new Vec(0, 0, 1), texture);

    pg.popStyle();
  }

  public void drawEyeNearPlane(Eye eye) {
    drawEyeNearPlane(eye, false);
  }

  /**
   * Applies the {@code eye.frame()} transformation and then calls
   * {@link #drawEye(PGraphics, Eye, boolean)} on the scene {@link #pg()}. If
   * {@code texture} draws the projected scene on the near plane.
   *
   * @see #applyTransformation(Frame)
   * @see #drawEye(PGraphics, Eye, boolean)
   */
  public void drawEyeNearPlane(Eye eye, boolean texture) {
    pg().pushMatrix();
    applyTransformation(eye.frame());
    drawEyeNearPlane(pg(), eye, texture);
    pg().popMatrix();
  }

  /**
   * Same as {@code drawEyeNearPlane(pg, eye, false)}.
   *
   * @see #drawEyeNearPlane(PGraphics, Eye, boolean)
   */
  public void drawEyeNearPlane(PGraphics pg, Eye eye) {
    drawEyeNearPlane(pg, eye, false);
  }

  /**
   * Draws the eye near plane. If {@code texture} draws the projected scene on the plane.
   * <p>
   * Warning: texture only works with opengl renderers.
   * <p>
   * Note that if {@code eye.scene()).pg() == pg} this method has not effect at all.
   */
  public void drawEyeNearPlane(PGraphics pg, Eye eye, boolean texture) {
    // Key here is to represent the eye getBoundaryWidthHeight and zNear params
    // (which are is given in world units) in eye units.
    // Hence they should be multiplied by: 1 / eye.frame().magnitude()
    if (eye.scene() instanceof Scene)
      if (((Scene) eye.scene()).pg() == pg) {
        System.out.println("Warning: No drawEyeNearPlane done, eye.scene()).pg() and pg are the same!");
        return;
      }
    pg.pushStyle();
    boolean ortho = false;
    if (is3D())
      if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC)
        ortho = true;
    // 0 is the upper left coordinates of the near corner, 1 for the far one
    Vec corner = new Vec();
    if (is2D() || ortho) {
      float[] wh = eye.getBoundaryWidthHeight();
      corner.setX(wh[0] * 1 / eye.frame().magnitude());
      corner.setY(wh[1] * 1 / eye.frame().magnitude());
    }
    if (is3D()) {
      corner.setZ(((Camera) eye).zNear() * 1 / eye.frame().magnitude());
      if (((Camera) eye).type() == Camera.Type.PERSPECTIVE) {
        corner.setY(corner.z() * PApplet.tan(((Camera) eye).fieldOfView() / 2.0f));
        corner.setX(corner.y() * ((Camera) eye).aspectRatio());
      }
    }
    drawPlane(pg, eye, corner, new Vec(0, 0, 1), texture);
  }

  protected void drawPlane(PGraphics pg, Eye eye, Vec corner, Vec normal, boolean texture) {
    pg.pushStyle();
    // near plane
    pg.beginShape(PApplet.QUAD);
    pg.normal(normal.x(), normal.y(), normal.z());
    if (pg instanceof PGraphicsOpenGL && texture) {
      pg.textureMode(NORMAL);
      pg.tint(255, 126); // Apply transparency without changing color
      pg.texture(((Scene) eye.scene()).pg());
      Scene.vertex(pg, corner.x(), corner.y(), -corner.z(), 1, 1);
      Scene.vertex(pg, -corner.x(), corner.y(), -corner.z(), 0, 1);
      Scene.vertex(pg, -corner.x(), -corner.y(), -corner.z(), 0, 0);
      Scene.vertex(pg, corner.x(), -corner.y(), -corner.z(), 1, 0);
    } else {
      Scene.vertex(pg, corner.x(), corner.y(), -corner.z());
      Scene.vertex(pg, -corner.x(), corner.y(), -corner.z());
      Scene.vertex(pg, -corner.x(), -corner.y(), -corner.z());
      Scene.vertex(pg, corner.x(), -corner.y(), -corner.z());
    }
    pg.endShape();
    pg.popStyle();
  }

  /**
   * Calls {@link #drawProjector(PGraphics, Eye, Vec)} on the scene {@link #pg()}.
   * <p>
   * Since this method uses the eye origin and zNear plane to draw the other end of the
   * projector it should be used in conjunction with {@link #drawEye(PGraphics, Eye)}.
   *
   * @see #drawProjector(PGraphics, Eye, Vec)
   * @see #drawProjectors(Eye, List)
   */
  public void drawProjector(Eye eye, Vec src) {
    drawProjector(pg(), eye, src);
  }

  /**
   * Draws as a line (or point in 2D) the projection of {@code src} (given in the world
   * coordinate system) onto the near plane.
   * <p>
   * Since this method uses the eye origin and zNear plane to draw the other end of the
   * projector it should be used in conjunction with
   * {@link #drawEye(PGraphics, Eye, boolean)}.
   * <p>
   * Note that if {@code eye.scene()).pg() == pg} this method has not effect at all.
   *
   * @see #drawProjector(PGraphics, Eye, Vec)
   * @see #drawProjectors(PGraphics, Eye, List)
   */
  public void drawProjector(PGraphics pg, Eye eye, Vec src) {
    drawProjectors(pg, eye, Arrays.asList(src));
  }

  /**
   * Calls {@link #drawProjectors(PGraphics, Eye, List)} on the scene {@link #pg()}.
   * <p>
   * Since this method uses the eye origin and zNear plane to draw the other end of the
   * projector it should be used in conjunction with {@link #drawEye(PGraphics, Eye)}.
   *
   * @see #drawProjectors(PGraphics, Eye, List)
   * @see #drawProjector(Eye, Vec)
   */
  public void drawProjectors(Eye eye, List<Vec> src) {
    drawProjectors(pg(), eye, src);
  }

  /**
   * Draws as lines (or points in 2D) the projection of each vector in {@code src} (all of
   * which should be given in the world coordinate system) onto the near plane.
   * <p>
   * Since this method uses the eye origin and zNear plane to draw the other end of the
   * projector it should be used in conjunction with
   * {@link #drawEye(PGraphics, Eye, boolean)}.
   * <p>
   * Note that if {@code eye.scene()).pg() == pg} this method has not effect at all.
   *
   * @see #drawProjectors(PGraphics, Eye, List)
   * @see #drawProjector(PGraphics, Eye, Vec)
   */
  public void drawProjectors(PGraphics pg, Eye eye, List<Vec> src) {
    if (eye.scene() instanceof Scene)
      if (((Scene) eye.scene()).pg() == pg) {
        System.out.println("Warning: No drawProjectors done, eye.scene()).pg() and pg are the same!");
        return;
      }
    pg.pushStyle();
    if (is2D()) {
      pg.beginShape(PApplet.POINTS);
      for (Vec s : src)
        Scene.vertex(pg, s.x(), s.y());
      pg.endShape();
    } else {
      // if ORTHOGRAPHIC: do it in the eye coordinate system
      // if PERSPECTIVE: do it in the world coordinate system
      Vec o = new Vec();
      if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC) {
        pg.pushMatrix();
        applyTransformation(eye.frame());
      }
      // in PERSPECTIVE cache the transformed origin
      else
        o = eye.frame().inverseCoordinatesOf(new Vec());
      pg.beginShape(PApplet.LINES);
      for (Vec s : src) {
        if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC) {
          Vec v = eye.frame().coordinatesOf(s);
          Scene.vertex(pg, v.x(), v.y(), v.z());
          // Key here is to represent the eye zNear param (which is given in world units)
          // in eye units.
          // Hence it should be multiplied by: 1 / eye.frame().magnitude()
          // The neg sign is because the zNear is positive but the eye view direction is
          // the negative Z-axis
          Scene.vertex(pg, v.x(), v.y(), -((Camera) eye).zNear() * 1 / eye.frame().magnitude());
        } else {
          Scene.vertex(pg, s.x(), s.y(), s.z());
          Scene.vertex(pg, o.x(), o.y(), o.z());
        }
      }
      pg.endShape();
      if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC)
        pg.popMatrix();
    }
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
    // draw the picking targets:
    for (int index = 0; index < kfi.numberOfKeyFrames(); index++)
      drawPickingTarget(kfi.keyFrame(index));
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
    drawCross(pg(), px, py, size);
  }

  public void drawCross(PGraphics pg, float px, float py, float size) {
    float half_size = size / 2f;
    pg.pushStyle();
    beginScreenDrawing(pg);
    pg.noFill();
    pg.beginShape(LINES);
    vertex(pg, px - half_size, py);
    vertex(pg, px + half_size, py);
    vertex(pg, px, py - half_size);
    vertex(pg, px, py + half_size);
    pg.endShape();
    endScreenDrawing(pg);
    pg.popStyle();
  }

  @Override
  public void drawFilledCircle(int subdivisions, Vec center, float radius) {
    drawFilledCircle(pg(), subdivisions, center, radius);
  }

  public void drawFilledCircle(PGraphics pg, int subdivisions, Vec center, float radius) {
    pg.pushStyle();
    float precision = PApplet.TWO_PI / subdivisions;
    float x = center.x();
    float y = center.y();
    float angle, x2, y2;
    beginScreenDrawing(pg);
    pg.noStroke();
    pg.beginShape(TRIANGLE_FAN);
    vertex(pg, x, y);
    for (angle = 0.0f; angle <= PApplet.TWO_PI + 1.1 * precision; angle += precision) {
      x2 = x + PApplet.sin(angle) * radius;
      y2 = y + PApplet.cos(angle) * radius;
      vertex(pg, x2, y2);
    }
    pg.endShape();
    endScreenDrawing(pg);
    pg.popStyle();
  }

  @Override
  public void drawFilledSquare(Vec center, float edge) {
    drawFilledSquare(pg(), center, edge);
  }

  public void drawFilledSquare(PGraphics pg, Vec center, float edge) {
    float half_edge = edge / 2f;
    pg.pushStyle();
    float x = center.x();
    float y = center.y();
    beginScreenDrawing(pg);
    pg.noStroke();
    pg.beginShape(QUADS);
    vertex(pg, x - half_edge, y + half_edge);
    vertex(pg, x + half_edge, y + half_edge);
    vertex(pg, x + half_edge, y - half_edge);
    vertex(pg, x - half_edge, y - half_edge);
    pg.endShape();
    endScreenDrawing(pg);
    pg.popStyle();
  }

  @Override
  public void drawShooterTarget(Vec center, float length) {
    drawShooterTarget(pg(), center, length);
  }

  public void drawShooterTarget(PGraphics pg, Vec center, float length) {
    float half_length = length / 2f;
    pg.pushStyle();
    float x = center.x();
    float y = center.y();
    beginScreenDrawing(pg);
    pg.noFill();

    pg.beginShape();
    vertex(pg, (x - half_length), (y - half_length) + (0.6f * half_length));
    vertex(pg, (x - half_length), (y - half_length));
    vertex(pg, (x - half_length) + (0.6f * half_length), (y - half_length));
    pg.endShape();

    pg.beginShape();
    vertex(pg, (x + half_length) - (0.6f * half_length), (y - half_length));
    vertex(pg, (x + half_length), (y - half_length));
    vertex(pg, (x + half_length), ((y - half_length) + (0.6f * half_length)));
    pg.endShape();

    pg.beginShape();
    vertex(pg, (x + half_length), ((y + half_length) - (0.6f * half_length)));
    vertex(pg, (x + half_length), (y + half_length));
    vertex(pg, ((x + half_length) - (0.6f * half_length)), (y + half_length));
    pg.endShape();

    pg.beginShape();
    vertex(pg, (x - half_length) + (0.6f * half_length), (y + half_length));
    vertex(pg, (x - half_length), (y + half_length));
    vertex(pg, (x - half_length), ((y + half_length) - (0.6f * half_length)));
    pg.endShape();
    endScreenDrawing(pg);
    drawCross(center.x(), center.y(), 0.6f * length);
    pg.popStyle();
  }

  @Override
  public void drawPickingTarget(GenericFrame iFrame) {
    if (iFrame.isEyeFrame()) {
      System.err.println("eye frames don't have a picking target");
      return;
    }
    if (!iFrame.isVisualHintEnabled())
      return;
    // if (!inputHandler().hasGrabber(iFrame)) {
    // System.err.println("add iFrame to motionAgent before drawing picking target");
    // return;
    // }
    Vec center = projectedCoordinatesOf(iFrame.position());
    if (inputHandler().isInputGrabber(iFrame)) {
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
   * Same as {@code profile.handle(event)}.
   *
   * @see Profile#handle(BogusEvent)
   */
  @Override
  public void performInteraction(BogusEvent event) {
    profile.handle(event);
  }

  // Key

  /**
   * Same as {@code setBinding(new KeyboardShortcut(vkey), methodName)}.
   *
   * @see #setBinding(Shortcut, String)
   */
  public void setKeyBinding(int vkey, String methodName) {
    setBinding(new KeyboardShortcut(vkey), methodName);
  }

  /**
   * Same as {@code setBinding(new KeyboardShortcut(key), methodName)}.
   *
   * @see #setBinding(Shortcut, String)
   */
  public void setKeyBinding(char key, String methodName) {
    setBinding(new KeyboardShortcut(key), methodName);
  }

  /**
   * Same as {@code setBinding(object, new KeyboardShortcut(vkey), methodName)}.
   *
   * @see #setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, int vkey, String methodName) {
    setBinding(object, new KeyboardShortcut(vkey), methodName);
  }

  /**
   * Same as {@code setBinding(object, new KeyboardShortcut(key), methodName)}.
   *
   * @see #setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, char key, String methodName) {
    setBinding(object, new KeyboardShortcut(key), methodName);
  }

  /**
   * Same as {@code return hasBinding(new KeyboardShortcut(vkey))}.
   *
   * @see #hasBinding(Shortcut)
   */
  public boolean hasKeyBinding(int vkey) {
    return hasBinding(new KeyboardShortcut(vkey));
  }

  /**
   * Same as {@code return hasBinding(new KeyboardShortcut(key))}.
   *
   * @see #hasBinding(Shortcut)
   */
  public boolean hasKeyBinding(char key) {
    return hasBinding(new KeyboardShortcut(key));
  }

  /**
   * Same as {@code removeBinding(new KeyboardShortcut(vkey))}.
   *
   * @see #removeBinding(Shortcut)
   */
  public void removeKeyBinding(int vkey) {
    removeBinding(new KeyboardShortcut(vkey));
  }

  /**
   * Same as {@code removeBinding(new KeyboardShortcut(key))}.
   *
   * @see #removeBinding(Shortcut)
   */
  public void removeKeyBinding(char key) {
    removeBinding(new KeyboardShortcut(key));
  }

  //

  /**
   * Same as {@code setBinding(new KeyboardShortcut(mask, vkey), methodName)}.
   *
   * @see #setBinding(Shortcut, String)
   */
  public void setKeyBinding(int mask, int vkey, String methodName) {
    setBinding(new KeyboardShortcut(mask, vkey), methodName);
  }

  /**
   * Same as {@code setBinding(object, new KeyboardShortcut(mask, vkey), methodName)} .
   *
   * @see #setBinding(Object, Shortcut, String)
   */
  public void setKeyBinding(Object object, int mask, int vkey, String methodName) {
    setBinding(object, new KeyboardShortcut(mask, vkey), methodName);
  }

  /**
   * Same as {@code return hasBinding(new KeyboardShortcut(mask, vkey))} .
   *
   * @see #hasBinding(Shortcut)
   */
  public boolean hasKeyBinding(int mask, int vkey) {
    return hasBinding(new KeyboardShortcut(mask, vkey));
  }

  /**
   * Same as {@code removeBinding(new KeyboardShortcut(mask, vkey))}.
   *
   * @see #removeBinding(Shortcut)
   */
  public void removeKeyBinding(int mask, int vkey) {
    removeBinding(new KeyboardShortcut(mask, vkey));
  }

  /**
   * Same as {@code setKeyBinding(mask, keyCode(key), methodName)}.
   *
   * @see #setKeyBinding(int, int, String)
   */
  public void setKeyBinding(int mask, char key, String methodName) {
    setKeyBinding(mask, keyCode(key), methodName);
  }

  /**
   * Same as {@code setKeyBinding(object, mask, keyCode(key), methodName)}.
   *
   * @see #setKeyBinding(Object, int, int, String)
   */
  public void setKeyBinding(Object object, int mask, char key, String methodName) {
    setKeyBinding(object, mask, keyCode(key), methodName);
  }

  /**
   * Same as {@code return hasKeyBinding(mask, keyCode(key))}.
   *
   * @see #hasKeyBinding(int, int)
   */
  public boolean hasKeyBinding(int mask, char key) {
    return hasKeyBinding(mask, keyCode(key));
  }

  /**
   * Same as {@code removeKeyBinding(mask, keyCode(key))}.
   *
   * @see #removeKeyBinding(int, int)
   */
  public void removeKeyBinding(int mask, char key) {
    removeKeyBinding(mask, keyCode(key));
  }

  /**
   * Same as {@code removeBindings(KeyboardShortcut.class)}.
   *
   * @see #removeBindings(Class)
   */
  public void removeKeyBindings() {
    removeBindings(KeyboardShortcut.class);
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

  // low-level

  /**
   * Hack to fix <a href="https://github.com/processing/processing/issues/1693">Processing MouseEvent.getModifiers()
   * issue</a>
   *
   * @param shortcut
   * @return shortcut fixed
   */
  protected Shortcut p5Java2DModifiersFix(Shortcut shortcut) {
    if (platform() == Platform.PROCESSING_DESKTOP)
      if ((shortcut instanceof MotionShortcut || shortcut instanceof ClickShortcut) && pg() instanceof PGraphicsJava2D && (shortcut.id() == PApplet.CENTER || shortcut.id() == PApplet.RIGHT))
        if (shortcut instanceof MotionShortcut)
          shortcut = new MotionShortcut(shortcut.id() == PApplet.CENTER ? (BogusEvent.ALT | shortcut.modifiers()) : (BogusEvent.META | shortcut.modifiers()), shortcut.id());
        else
          shortcut = new ClickShortcut(shortcut.id() == PApplet.CENTER ? (BogusEvent.ALT | shortcut.modifiers()) : (BogusEvent.META | shortcut.modifiers()), shortcut.id(), ((ClickShortcut) shortcut).clickCount());
    return shortcut;
  }

  /**
   * Same as {@code profile.setBinding(shortcut, action)}.
   * <p>
   * Low-level profile handling routine. Call this method to set a binding for a custom bogus event, like this:
   * {@code scene.setBinding(new CustomShortcut(mask, CustomAgent.CUSTOM_ID), "customBehavior")}.
   *
   * @see Profile#setBinding(Shortcut, String)
   * @see BogusEvent
   * @see Shortcut
   */
  public void setBinding(Shortcut shortcut, String action) {
    profile.setBinding(p5Java2DModifiersFix(shortcut), action);
  }

  /**
   * Same as {@code profile.setBinding(object, shortcut, action)}.
   * <p>
   * Low-level profile handling routine. Call this method to set a binding for a custom bogus event, like this:
   * {@code scene.setBinding(object, new CustomShortcut(mask, CustomAgent.CUSTOM_ID), "customBehavior")}.
   *
   * @see Profile#setBinding(Object, Shortcut, String)
   * @see BogusEvent
   * @see Shortcut
   */
  public void setBinding(Object object, Shortcut shortcut, String action) {
    profile.setBinding(object, p5Java2DModifiersFix(shortcut), action);
  }

  /**
   * Same as {@code profile.set(otherScene.profile)}.
   *
   * @see Profile#set(Profile)
   */
  public void setBindings(Scene otherScene) {
    profile.set(otherScene.profile);
  }

  /**
   * Same as {@code return profile.hasBinding(shortcut)}.
   * <p>
   * <p>
   * Low-level profile handling routine. Call this method to query for a binding from a custom bogus event, like this:
   * {@code scene.hasBinding(object, new CustomShortcut(mask, CustomAgent.CUSTOM_ID)}.
   *
   * @see Profile#hasBinding(Shortcut)
   * @see BogusEvent
   * @see Shortcut
   */
  public boolean hasBinding(Shortcut shortcut) {
    return profile.hasBinding(p5Java2DModifiersFix(shortcut));
  }

  /**
   * Same as {@code profile.removeBinding(shortcut)}.
   * <p>
   * Low-level profile handling routine. Call this method to remove a binding for a custom bogus event, like this:
   * {@code scene.removeBinding(new CustomShortcut(mask, CustomAgent.CUSTOM_ID)}.
   *
   * @see Profile#removeBinding(Shortcut)
   * @see BogusEvent
   * @see Shortcut
   */
  public void removeBinding(Shortcut shortcut) {
    profile.removeBinding(p5Java2DModifiersFix(shortcut));
  }

  /**
   * Same as {@code profile.removeBindings()}.
   *
   * @see Profile#removeBindings()
   */
  public void removeBindings() {
    profile.removeBindings();
  }

  /**
   * Same as {@code profile.removeBindings(cls)}.
   *
   * @see Profile#removeBindings(Class)
   */
  public void removeBindings(Class<? extends Shortcut> cls) {
    profile.removeBindings(cls);
  }

  /**
   * Same as {@code profile.info(cls)}.
   *
   * @see Profile#info(Class)
   */
  public String info(Class<? extends Shortcut> cls) {
    return profile.info(cls);
  }

  /**
   * Same as {@code return profile.action(key)}.
   *
   * @see Profile#action(Shortcut)
   */
  public String action(Shortcut shortcut) {
    return profile.action(p5Java2DModifiersFix(shortcut));
  }

  /**
   * Same as {@code return profile.isActionBound(action)}.
   *
   * @see Profile#isActionBound(String)
   */
  public boolean isActionBound(String action) {
    return profile.isActionBound(action);
  }

  // end low-level
}
