/**
 <p>This sketch demonstrates how to add a new constant to the
 QScript library. </p>
 
 <p>It adds an constant called PHI then runs a mall script called hypot that caluclates and 
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


public void setup() {
  size(300, 128);
  background(200, 255, 200);
  fill(0);

  PHI_constant p = new PHI_constant("PHI", 0, 40, Operator.CONSTANT);
  OperatorSet.get().addOperator(p);

  // The END method is used in the script to return
  // the value of PHI in the result.
  String code = "println('Golden Ratio = ' + PHI); END(PHI)";
  Result r = Solver.evaluate(code);

  text("The constant PHI has the value", 10, 20);
  text(r.answer.toString(), 10, 40);
}


public static class PHI_constant extends Operator {

  public PHI_constant(String symbol, int nbrArgs, int priority, int type) {
    super(symbol, nbrArgs, priority, type);
  }

  public Argument resolve(Script script, Token token, Argument[] args, Object... objects) 
  throws EvaluationException {
    return new Argument(new Double(1.618033988749895));
  }
}

