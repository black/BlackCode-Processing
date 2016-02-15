package org.qscript.operator;

import org.qscript.Argument;
import org.qscript.Script;
import org.qscript.Token;
import org.qscript.errors.EvaluationException;

/**
 * floor() function
 * 
 * @author Peter Lager
 *
 */
public class FloorFuction extends Operator {

	public FloorFuction(String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args, Object...objects) 
			throws EvaluationException {
		testForUninitialisedVars(script, args);
		Argument a0 = args[0];
		if(a0.isNumeric)
			return new Argument(Math.floor(a0.toDouble()));

		// If we get here then the arguments are invalid		
		handleInvalidArguments(script, token);
		return null;
	}
}
