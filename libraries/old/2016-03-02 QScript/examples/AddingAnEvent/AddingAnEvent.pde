/**
 <p>This sketch demonstrates how to add a new event to the
 QScript library. </p>
 
 <p>It adds an event that is fired when the hypot 
 operator is evaluated. </p>
 
 <p>The hypot method caluclates and returns the hypotenuse
 of a right angled triangle from the length of the other 
 two sides. </p>
 
 <p>For detailed information about how to use this library 
 please visit the 
 <a href="http://www.lagers.org.uk/qscript/">website</a></p>
 
 created 2014 by Peter Lager
 */

import org.qscript.eventsonfire.*;
import org.qscript.events.*;
import org.qscript.editor.*;
import org.qscript.*;
import org.qscript.operator.*;
import org.qscript.errors.*;

Script script;
float a = 1, b = 1, h = 1;
String stopText = "";

public void setup() {
  size(400, 400);
  HypotenuseFunction h;
  h = new HypotenuseFunction("hypot", 2, 30, Operator.FUNCTION);
  OperatorSet.get().addOperator(h);

  String[] code = {
    "WHILE(true)", 
    "  a = rnd(10, 100)", 
    "  b = rnd(10, 100)", 
    "  h = hypot(a, b)", 
    "  WAIT(rnd(2000, 5000))", 
    "WEND"
  };

  script = new Script(code);
  // Script events are to be sent to this object
  script.addListener(this);

  Solver$.evaluate(script);
}

public void keyTyped() {
  if (key == 's' || key == 'S')
    script.stop();
}

public void draw() {
  background(200, 255, 200);
  drawTriangle(a, b, h);
  fill(0);
  if (!script.isStopped()) {
    text("WORKING ...", 20, 20);
    text("press S to stop", 20, 36);
  } else
    text(stopText, 20, 20);
}

@EventHandler
public void onScriptEvent(ScriptEvent event) {
  if (event instanceof HpotCalculatedEvent) {
    a = script.getVariable("a").toFloat();
    b = script.getVariable("b").toFloat();
    h = ((Argument)event.extra[0]).toFloat();
  } else if (event instanceof HaltExecutionEvent) {
    stopText = "Program halted";
  }
}

public void mouseClicked() {
  save("triangle" + millis()%100 + ".png");
}

public void drawTriangle(float a, float b, float h) {
  float s = (400 - 80.0f)/ max(a, b);
  float ap = a*s;
  float bp = b*s;

  stroke(0);
  strokeWeight(1.5f);
  fill(0, 128, 0);
  pushMatrix();
  translate(width-40, height - 40);
  beginShape(TRIANGLES);
  vertex(0, 0);
  vertex(-ap, 0);
  vertex(0, -bp);
  endShape();
  // a length
  pushMatrix();
  translate(-ap/2.5f, 14);
  text(""+a, -textWidth(""+a)/2, 0);
  popMatrix();
  // b length
  pushMatrix();
  translate(6, -bp/2.5f);
  rotate(HALF_PI);
  text(""+b, -textWidth(""+b)/2, 0);
  popMatrix();
  // h length
  pushMatrix();
  translate(-6-ap/2, -6-bp/2);
  rotate(-atan2(b, a));
  text(""+h, -textWidth(""+h)/2, 0);
  popMatrix();
  popMatrix();
}

public static class HypotenuseFunction extends Operator {

  public HypotenuseFunction(String symbol, int nbrArgsNeeded, int priority, int type) {
    super(symbol, nbrArgsNeeded, priority, type);
  }

  public Argument resolve(Script script, Token token, Argument[] args, Object... objects) 
  throws EvaluationException {
    testForUninitialisedVars(script, args);
    Argument a0 = args[0];
    Argument a1 = args[0];
    if (a0.isNumeric && a1.isNumeric) {
      double s0 = a0.toDouble(), s1 = a1.toDouble();
      double hyp = Math.sqrt(s0*s0 + s1*s1);
      script.fireEvent(HpotCalculatedEvent.class, null, token, new Argument(new Double(hyp)));
      return new Argument(new Double(hyp));
    }

    // If we get here then the arguments are invalid
    handleInvalidArguments(script, token);
    return null;
  }
}

public static class HpotCalculatedEvent extends ScriptEvent {

  public HpotCalculatedEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
    super(script, etype, lineNo, pos, width, extra);
    message = "The hypotenuse has been calculated";
  }

  public HpotCalculatedEvent(Script script, ErrorType etype, int lineNo, int pos, int width) {
    this(script, etype, lineNo, pos, width, null);
  }
}
