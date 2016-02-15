package org.qscript.operator;

import org.qscript.Argument;
import org.qscript.Complex;
import org.qscript.Script;
import org.qscript.Token;
import org.qscript.Vector;
import org.qscript.errors.EvaluationException;

public class ComplexFunction extends Operator  {

	public ComplexFunction(String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args, Object... objects) throws EvaluationException {
		testForUninitialisedVars(script, args);
		Argument a0 = args[0];
		Argument a1 = args[1];
		if(a0.isNumeric && a1.isNumeric){
			return  new Argument(new Complex(a0.toDouble(), a1.toDouble()));
		}

		// If we get here then the arguments are invalid
		handleInvalidArguments(script, token);
		return null;
	}

	
}
