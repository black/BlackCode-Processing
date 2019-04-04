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

import remixlab.bias.event.*;
import remixlab.bias.core.*;

/**
 * Proscene key-agent. A Processing fully fledged key {@link remixlab.bias.core.Agent}.
 * 
 * @see remixlab.bias.core.Agent
 * @see remixlab.proscene.MouseAgent
 * @see remixlab.proscene.DroidKeyAgent
 * @see remixlab.proscene.DroidTouchAgent
 */
public class KeyAgent extends Agent {
  public static final int LEFT_KEY = 37, RIGHT_KEY = 39, UP_KEY = 38, DOWN_KEY = 40;
  // public static int LEFT_KEY = PApplet.LEFT, RIGHT_KEY = PApplet.RIGHT,
  // UP_KEY = PApplet.UP, DOWN_KEY = PApplet.DOWN;
  protected Scene scene;
  protected boolean press, release, type;
  protected KeyboardEvent currentEvent;

  /**
   * Calls super on (scn,n) and sets default keyboard shortcuts.
   */
  public KeyAgent(Scene scn) {
    super(scn.inputHandler());
    scene = scn;
    addGrabber(scene);
  }

  /**
   * Returns the scene this object belongs to.
   */
  public Scene scene() {
    return scene;
  }

  /**
   * Processing keyEvent method to be registered at the PApplet's instance.
   */
  public void keyEvent(processing.event.KeyEvent e) {
    press = e.getAction() == processing.event.KeyEvent.PRESS;
    release = e.getAction() == processing.event.KeyEvent.RELEASE;
    type = e.getAction() == processing.event.KeyEvent.TYPE;

    if (type)
      currentEvent = new KeyboardEvent(e.getKey());
    else if (press || release)
      currentEvent = new KeyboardEvent(e.getModifiers(), e.getKeyCode());
    if (type || press)
      updateTrackedGrabber(currentEvent);

    handle(release ? currentEvent.flush() : currentEvent.fire());
  }

  // debug

  // protected String printEvent(KeyboardEvent event) {
  // return " mod: " + KeyboardEvent.modifiersText(event.modifiers()) + " vkey: " +
  // event.id() + " char: " + event.key(); }
  //
  // protected String printAction(KeyboardEvent event) {
  // return " scene: " + scene.profile().action(event.shortcut()) + " eye: " +
  // scene.eyeFrame().profile().action(event.shortcut()); }

  /**
   * Same as {@code return java.awt.event.KeyEvent.getExtendedKeyCodeForChar(key)}.
   */
  public static int keyCode(char key) {
    return java.awt.event.KeyEvent.getExtendedKeyCodeForChar(key);
  }
}