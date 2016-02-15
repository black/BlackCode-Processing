/**
 * This class represents an object that can be controlled
 * using QScript. Each helicopter has its own script and
 * handles its own script events.
 */
public class Helicopter {
  // Forward speed in pixels per second
  final float SPEED = 150;
  // Angular speed in degrees per second
  final float ANGULAR_SPEED = 120;

  // Store for events waiting to be processed
  LinkedList<ScriptEvent> event_queue = new LinkedList<ScriptEvent>();

  // The variable name to be used in the script
  final String identifier;
  Script script;

  // State variables
  PVector pos, targetPos;
  PVector velocity;
  // +1 forward direction
  // -1 reverse direction
  // 0 stationary
  int moveDir = 0;
  float angle = 0, targetAngle = 0;
  // +1 turn left (rotate clockwise)
  // -1 turn right (rotate anti-clockwise)
  // 0 rotation finished
  int rotDir = 0;

  // Images for the helicopter
  PImage rotor, body;

  String activity = "Stopped";

  /**
   * Create the helicopter.
   * @param name the variable identifier used in the script
   * @param bodyFile image file for body
   * @param rotorFile image file for rotor
   */
  Helicopter(String name, String bodyFile, String rotorFile) {
    this.identifier = name;
    rotor = loadImage(rotorFile);
    body = loadImage(bodyFile);
    pos = new PVector(width/2, height/2);
    targetPos = pos.get();
    velocity = new PVector();
    script = new Script("");
    script.storeVariable(new Thing(name, this));
    script.addListener(this);
    registerMethod("pre", this);
  }

  /**
   * Used to set the script (instructions) for the helicopter
   * to follow. The current script is paused while the new 
   * script is being parsed. If the new script has no syntax 
   * errors then it will replace the existing script otherwise
   * the old script will be resumed.
   * 
   * @param lines the instructions for the helicopter
   * @return true if the new script is accepted else  false
   */
  boolean setCode(String[] lines) {
    // Pause the current script and stop listening to it
    script.removeListener(this);
    script.waitFor(0);
    // Lets try the new script
    Script newScript = new Script(lines);
    newScript.storeVariable(new Thing(identifier, this));
    newScript.addListener(this);
    newScript.parse();
    // If the new script parsed then apply it
    if (newScript.isParsed()) {
      script = newScript;
      Solver$.evaluate(script);
      return true;
    }
    // If we get here then the new script is no good
    // so dump it
    newScript.removeListener(this);
    script.addListener(this);
    script.resume();
    return false;
  }

  /**
   * Get the code / instructions for this helicopter
   * @return
   */
  String[] getCode() {
    return script.getCode();
  }

  /**
   * Since events are fired asynchronously this method might be called whilst G4P
   * is drawing its controls causing the program to crash. To avoid this we will
   * add the event to a queue and process it during Processing's event loop.
   */
  @EventHandler
    synchronized public void onScriptEvent(ScriptEvent event) {
    if (event instanceof HaltExecutionEvent && event.etype == ErrorType.STOPPED_BY_USER)
      event_queue.addFirst(event);
    else
      event_queue.addLast(event);
  }

  /**
   * This method has been registered with Processing so will be called just 
   * before the draw method. It will process a maximum of 20 events in the FIFO
   * queue, then allow the draw method to execute.
   * Since the script can generate hundreds of events per frame we have to
   * cap the number processed if we want the GUI to be responsive.
   */
  public void pre() {
    int count = 0;
    while (!event_queue.isEmpty () && count++ < 20)
      performEventAction(event_queue.removeFirst());
  }

  /**
   * Process an event. This method only processes those events we
   * want to, other events are simply ignored.
   */
  public void performEventAction(ScriptEvent event) {
    if (event instanceof WaitEvent) {
      activity = "Waiting for " + " " + event.extra[0];
    } else if (event instanceof TurnEvent) {
      // extra[0] = is the helicopter : extra[1] is the angle in degrees
      // Helicopter thisOne = (Helicopter) event.extra[0];
      activity = event.getMessage();
      float angle = (Float) event.extra[1];
      turn(angle);
    } else if (event instanceof MoveEvent) {
      // extra[0] = is the helicopter : extra[1] is the distance in pixels
      // Helicopter thisOne = (Helicopter) event.extra[0];
      float dist = (Float) event.extra[1];
      move(dist);
      activity = event.getMessage();
    } else if (event instanceof ResetEvent) {
      // extra[0] = is the helicopter but in this script we don't use it
      // Helicopter thisOne = (Helicopter) event.extra[0];
      float px = (Float) event.extra[1];
      float py = (Float) event.extra[2];
      float ang = (Float) event.extra[3];
      activity = event.getMessage();
      targetPos.set(px, py);
      pos.set(px, py);
      angle = targetAngle = ang;
      rotDir = 0;
      // Resume script immediately
      script.resume();
    } else if (event instanceof SyntaxErrorEvent || event instanceof EvaluationErrorEvent) {
      errorStatus.setText(event.getMessage());
      editor.clearStyles();
      editor.addStyle(G4P.BACKGROUND, 0xFFFF0000, event.lineNo, event.pos, event.pos + event.width);
      editor.addStyle(G4P.FOREGROUND, 0xFFFFFFFF, event.lineNo, event.pos, event.pos + event.width);
      editor.addStyle(G4P.WEIGHT, 4, event.lineNo, event.pos, event.pos + event.width);
      editor.moveCaretTo(event.lineNo, event.pos);
    } else if (event instanceof ScriptFinishedEvent) {
      event_queue.clear();
      activity = "Stopped";
    }
  }

  /**
   * Calculate the target angle and rotation direction 
   * for this helicopter.
   * Positive values >> clockwise rotation
   * Negative degrees >> anti-clockwise rotation
   * @param degrees number of degrees to turn
   */
  void turn(float degrees) {
    targetAngle += degrees;
    if (targetAngle == angle)
      rotDir = 0;
    else
      rotDir = (targetAngle - angle < 0) ? -1 : 1;
  }

  /**
   * Calculate the target position for this helicopter.
   * if dist is negative then the target position is 
   * 'behind' the helocopter
   * @param dist distance to move in pixels
   */
  void move(float dist) {
    if (dist != 0) {
      moveDir = (dist < 0) ? -1 : 1;
      dist = abs(dist);
      targetPos.set(cos(radians(angle)), sin(radians(angle)));
      targetPos.mult(moveDir * dist);
      targetPos.add(pos);
    }
  }


  /**
   * Update the position and/or rotation of the helicopter
   * @param elapsedTime the elapsed time in seconds
   */
  void update(float elapsedTime) {
    updateAngle(elapsedTime);
    updatePos(elapsedTime);
  }

  /**
   * Update the rotation of the helicopter
   * @param elapsedTime the elapsed time in seconds
   */
  void updateAngle(float elapsedTime) {
    if (rotDir != 0) {
      float nextAng = angle + rotDir * ANGULAR_SPEED * elapsedTime;
      if ( (targetAngle - angle) * ( targetAngle - nextAng)  <= 0) {
        // Turn complete
        nextAng = targetAngle;
        rotDir = 0;
        script.resume();
      }
      angle = nextAng;
    }
  }
  
  /**
   * Update the position of the helicopter
   * @param elapsedTime the elapsed time in seconds
   */
  void updatePos(float elapsedTime) {
    if (!pos.equals(targetPos)) {
      velocity.set(cos(radians(angle)), sin(radians(angle)));
      velocity.mult(moveDir * SPEED * elapsedTime);
      if (pos.dist(targetPos) > velocity.mag()) {
        // Target not reached
        pos.add(velocity);
      } else {
        // Target reached
        pos.set(targetPos);
        moveDir = 0;
        script.resume();
      }
    }
  }

  // Draw the helicopter
  void draw() {
    pushStyle();
    imageMode(CENTER);
    rectMode(CENTER);
    pushMatrix();  
    translate(pos.x, pos.y);
    rotate(radians(angle));
    // Helicopter body;
    image(body, -32, 0);
    // Tail rotor
    fill(0, 32);
    noStroke();
    rect(-66, 8, 24, 4);
    float l = 24 * sin(0.02f * millis());
    fill(0, 72);
    rect(-66, 8, l, 4);
    // Main rotor
    rotate(SPEED * 0.001f * millis());
    image(rotor, 0, 0);
    popMatrix();
    popStyle();
  }
}
