/**
 <p>This sketch demonstrates how to add a new operator to the
 QScript library. </p>
 
 <p>It adds an operator called hypot that caluclates and 
 returns the hypotenuse of a right angled triangle from 
 the length of the other two sides. </p>
 
 <p>For detailed information about how to use this library 
 please visit the 
 <a href="http://www.lagers.org.uk/qscript/">website</a></p>
 
 created by Peter Lager 2014
 */

import org.qscript.eventsonfire.*;
import org.qscript.events.*;
import org.qscript.editor.*;
import org.qscript.*;
import org.qscript.operator.*;
import org.qscript.errors.*;

import java.util.List;

public void setup() {
  size(300, 128);
  background(200, 255, 200);
  fill(0);
  HypotenuseFunction h;
  h = new HypotenuseFunction("hypot", 2, 30, Operator.FUNCTION);
  OperatorSet.get().addOperator(h);

  String code = "$h = hypot(5, 12); println('Hypotenuse = ' + $h)";
  Result r = Solver.evaluate(code);

  text("The hypotenuse of a right angled triangle", 10, 20);
  text("with sides 5 and 12 is " + r.answer.toString(), 10, 40);
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
      return new Argument(new Double(hyp));
    }

    // If we get here then the arguments are invalid
    handleInvalidArguments(script, token);
    return null;
  }
}
