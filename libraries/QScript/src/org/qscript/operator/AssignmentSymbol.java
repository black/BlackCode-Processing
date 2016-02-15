/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript.operator;

import org.qscript.Argument;
import org.qscript.Script;
import org.qscript.Token;
import org.qscript.Variable;
import org.qscript.errors.ErrorType;
import org.qscript.errors.EvaluationException;
import org.qscript.events.EvaluationErrorEvent;

/**
 * = assignment symbole (LHS must be a variable)
 * 
 * @author Peter Lager
 *
 */
public class AssignmentSymbol extends Operator {

	public AssignmentSymbol(String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args, Object...objects) 
			throws EvaluationException {
		Argument a0 = args[0];
		// LHS must be be a variable
		if(!a0.isVariable()){
			script.fireEvent(EvaluationErrorEvent.class,  ErrorType.LHS_VARIABLE_REQD, a0);
			script.throwEvaluationException(ErrorType.LHS_VARIABLE_REQD, a0);
		}
		// If RHS is a variable it must have been initialised
		Argument a1 = args[1];	
		testForUninitialisedVar(script, a1);
		
		// Perform assignment and store result
		Variable result = new Variable(((Variable) a0).getIdentifier(), a1.getValue());
		script.storeVariable(result);
		return result;

//		Variable result = (Variable) a0;
//		result.copyArgument(a1);
//		script.storeVariable(result);
//		return result;
	}

}
