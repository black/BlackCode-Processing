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
import org.qscript.Vector;
import org.qscript.errors.EvaluationException;

public class VectorFunction extends Operator {

	public VectorFunction(String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args,
			Object... objects) throws EvaluationException {
		testForUninitialisedVars(script, args);
		Argument a0 = args[0];
		Argument a1 = args[1];
		Argument a2 = args[2];
		if(a0.isNumeric && a1.isNumeric && a2.isNumeric){
			return  new Argument(new Vector(a0.toDouble(), a1.toDouble(), a2.toDouble()));
		}

		// If we get here then the arguments are invalid
		handleInvalidArguments(script, token);
		return null;
	}

}
